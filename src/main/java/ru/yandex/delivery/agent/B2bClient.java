package ru.yandex.delivery.agent;

import feign.RequestLine;

public interface B2bClient {
	@RequestLine("POST /b2b/cargo/integration/v1/claims/journal")
	JournalResponse readJournal(JournalRequest request);
}