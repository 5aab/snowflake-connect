package com.example.cqrs.persistence;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUserType extends AbstractJacksonClobMapper {

    private static final long serialVersionUID = 123456789L;
    private static final ObjectMapper mapper = new ObjectMapper();

    public JsonUserType() {
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return mapper;
    }
}
