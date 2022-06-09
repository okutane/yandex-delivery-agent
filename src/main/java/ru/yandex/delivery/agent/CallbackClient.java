package ru.yandex.delivery.agent;

import java.util.Map;

import feign.QueryMap;
import feign.RequestLine;

public interface CallbackClient {
    @RequestLine("POST")
	void notify(@QueryMap Map<String, String> event);
}
