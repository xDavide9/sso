package com.xdavide9.sso.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Utility class that uses Jackson's {@link ObjectMapper} to convert java objects to json and vice-versa.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Service
public class JsonParserService {

    private final ObjectMapper mapper;

    @Autowired
    public JsonParserService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    private static final Logger log = LoggerFactory.getLogger(JsonParserService.class);

    /**
     * converts java objects to json. In case of failure an error message in capital letters is thrown.
     * @param o the object to be converted
     * @return json of the object passed
     * @since 0.0.1-SNAPSHOT
     */
    public String json(Object o) {
        try {
            String json = mapper.writeValueAsString(o);
            log.info("JACKSON PARSING OBJECT " + o + " TO JSON " + json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("JACKSON CANNOT CONVERT JAVA OBJECT " + o + " TO JSON");
            throw new RuntimeException("JACKSON CANNOT CONVERT JAVA OBJECT " + o + " TO JSON");
        }
    }

    /**
     * converts a json string to a java object whom type must be passed in input. In case of failure an error message
     * in capital letters is thrown.
     * @param json the json string to convert
     * @param type the type of the object you want to convert the json string to
     * @return the object converted from json
     * @since 0.0.1-SNAPSHOT
     */
    public<T> T java(String json, Class<T> type) {
        try {
            log.info("PARSING JSON " + json + " TO A JAVA OBJECT OF TYPE " + type);
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            log.error("JACKSON CANNOT CONVERT JSON " + json + " TO JAVA OBJECT OF TYPE " + type);
            throw new RuntimeException("JACKSON CANNOT CONVERT JSON " + json + " TO JAVA OBJECT OF TYPE " + type);
        }
    }
}
