package com.xdavide9.sso.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class that uses Jackson's {@link ObjectMapper} to convert java objects to json and vice-versa.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class JsonParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * converts java objects to json. In case of failure an error message in capital letters is thrown.
     * @param o the object to be converted
     * @return json of the object passed
     * @since 0.0.1-SNAPSHOT
     */
    public static String json(Object o) {
        try {
            String json = mapper.writeValueAsString(o);
            System.out.println("JACKSON PARSING OBJECT " + o + " TO JSON " + json);
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
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
    public static<T> T java(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JACKSON CANNOT CONVERT JSON " + json + " TO JAVA OBJECT");
        }
    }
}

