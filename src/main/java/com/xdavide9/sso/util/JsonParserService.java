package com.xdavide9.sso.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
     * Converts java objects to json.
     * @param o the object to be converted
     * @throws JsonProcessingException as well a custom message
     * @return json of the object passed
     */
    public String json(Object o) throws JsonProcessingException {
        try {
            String json = mapper.writeValueAsString(o);
            log.info("JACKSON PARSING OBJECT " + o + " TO JSON " + json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("JACKSON CANNOT CONVERT JAVA OBJECT " + o + " TO JSON");
            throw e;
        }
    }

    /**
     * Converts a json string to a java object whom type (Class instance) must be passed in input.
     * @param json the json string to convert
     * @param type instance of Class that holds the type of the object you want to convert the json string to
     * @throws JsonProcessingException as well as a custom message
     * @return the object converted from json
     */
    public<T> T java(String json, Class<T> type) throws JsonProcessingException {
        try {
            log.info("PARSING JSON " + json + " TO A JAVA OBJECT OF TYPE " + type);
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            log.error("JACKSON CANNOT CONVERT JSON " + json + " TO JAVA OBJECT OF TYPE " + type);
            throw e;
        }
    }

    /**
     * Converts a json string to a java object whom type (anonymous subclass of TypeReference to get
     * around java's generic type erasure at runtime) must be passed in input.
     * In case of failure an error message in capital letters is thrown.
     * @param json the json string to convert
     * @param type anonymous subclass of TypeReference that holds the type of the object you want to convert the json string to
     * @throws JsonProcessingException as well as a custom message
     * @return the object converted from json
     */
    public<T> T java(String json, TypeReference<T> type) throws JsonProcessingException {
        try {
            log.info("PARSING JSON " + json + " TO A JAVA OBJECT OF TYPE " + type);
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            log.error("JACKSON CANNOT CONVERT JSON " + json + " TO JAVA OBJECT OF TYPE " + type);
            throw e;
        }
    }
}

