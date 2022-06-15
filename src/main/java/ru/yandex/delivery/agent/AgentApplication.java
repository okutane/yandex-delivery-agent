package ru.yandex.delivery.agent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import ru.yandex.delivery.agent.data.State;
import ru.yandex.delivery.agent.data.StateRepository;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories
@ComponentScan
@EntityScan
public class AgentApplication {
	static Logger LOGGER = LoggerFactory.getLogger(AgentApplication.class);

	@Autowired
	StateRepository repository;

	@Autowired
	B2bClient b2bClient;

	@Autowired
	CallbackClient callbackClient;

	@Scheduled(fixedDelay = 1000, initialDelay = 1000)
	@Autowired
	public void pollForJournalUpdates() throws InterruptedException {
		Optional<State> state = repository.findById("claims_cursor");

		final JournalRequest request = new JournalRequest();
		request.cursor = state.map(s -> s.value).orElseGet(() -> {
			LOGGER.warn("No previous cursor value, first run?");

			return null;
		});

		JournalResponse response;
		try {
			response = b2bClient.readJournal(request);
		} catch (Exception e) {
			LOGGER.error("Can't get journal update", e);
			Thread.sleep(60000);

			return;
		}

		LOGGER.info("Got " + response.events.length + " new events.");

		for (JournalEvent event : response.events) {
			if (!"status_changed".equals(event.change_type)) {
				continue; // only notify about status changes.
			}

			Map<String, String> map = new LinkedHashMap<>();
			map.put("claim_id", event.claim_id);
			map.put("change_type", event.change_type);
			map.put("new_status", event.new_status);
			map.put("updated_ts", event.updated_ts);

			try {
				callbackClient.notify(map);
			} catch (Exception e) {
				LOGGER.error("Hook notification error", e);
				Thread.sleep(60000);

				return;
			}
		}

		if (Objects.equals(request.cursor, response.cursor)) {
			LOGGER.info("Cursor wasn't changed.");
			Thread.sleep(60000);

			return;
		}

		State newState = new State();
		newState.key = "claims_cursor";
		newState.value = response.cursor;
		repository.save(newState);
	}

	public static void main(String[] args) {
		SpringApplication.run(AgentApplication.class, args);
	}
}
