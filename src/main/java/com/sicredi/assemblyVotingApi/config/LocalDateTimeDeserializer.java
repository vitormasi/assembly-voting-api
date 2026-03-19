package com.sicredi.assemblyVotingApi.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    private final DateTimeFormatter formatter;

    public LocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class);
        this.formatter = formatter;
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String value = p.getText().trim();
        return LocalDateTime.parse(value, formatter);
    }
}
