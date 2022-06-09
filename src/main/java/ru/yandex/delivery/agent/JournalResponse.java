package ru.yandex.delivery.agent;

public class JournalResponse {
    String cursor;

    JournalEvent[] events;
}

class JournalEvent {
    public String change_type;
    public String claim_id;
    public String claim_origin;
    public String client_id;
    public Integer current_point_id;
    public String new_currency;
    public String new_price;
    public String new_status;
    public Integer operation_id;
    public String resolution;
    public Integer revision;
    public String updated_ts;
}