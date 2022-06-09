package ru.yandex.delivery.agent.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class State {
    @Id
    public String key;

    public String value;
}
