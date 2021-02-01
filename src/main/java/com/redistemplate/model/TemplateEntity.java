package com.redistemplate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("TemplateEntity")
public class TemplateEntity implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("value")
    private String value;

    public TemplateEntity(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TemplateEntity{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}