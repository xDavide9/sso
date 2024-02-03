package com.xdavide9.sso.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JsonParserServiceTest {

    @InjectMocks
    private JsonParserService underTest;

    @Mock
    private ObjectMapper mapper;

    @Test
    void itShouldConvertJavaToJson() throws Exception {
        // given
        Object o = new Object();
        given(mapper.writeValueAsString(o)).willReturn("\"key\":\"value\"");
        // when
        String json = underTest.json(o);
        // then
        assertThat(json).isEqualTo("\"key\":\"value\"");
    }

    @Test
    void itShouldNotConvertJavaToJsonAndThrow() throws Exception {
        // given
        Object o = new Object();
        given(mapper.writeValueAsString(o)).willThrow(JsonProcessingException.class);
        // when & then
        assertThatThrownBy(() -> underTest.json(o))
                .isInstanceOf(JsonProcessingException.class);
    }

    @Test
    void itShouldCovertJsonToJava() throws Exception {
        // given
        Object o = new Object();
        String json = "json";
        Class<Object> type = Object.class;
        given(mapper.readValue(json, type)).willReturn(o);
        // when
        Object result = underTest.java(json, type);
        // then
        assertThat(result).isEqualTo(o);
    }

    @Test
    void itShouldNotConvertJsonToJavaAndThrow() throws Exception {
        // given
        String json = "json";
        Class<Object> type = Object.class;
        given(mapper.readValue(json, type)).willThrow(JsonProcessingException.class);
        // when & then
        assertThatThrownBy(() -> underTest.java(json, type))
                .isInstanceOf(JsonProcessingException.class);
    }
}