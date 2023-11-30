package com.rrg.urlshortener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rrg.urlshortener.model.Url;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AbstractTest {

    protected static final String TEST_FULL_URL = "https://example.com";
    protected static final String INCORRECT_TEST_FULL_URL = "www.incorrect.com";

    protected List<Url> getTestUrls() {
        var firstUrl = new Url();
        firstUrl.setShortUrlId("AbCdEfG");
        firstUrl.setFullUrl(TEST_FULL_URL);
        firstUrl.setVisits(5);
        var secondUrl = new Url();
        secondUrl.setShortUrlId("aB1cD2e");
        secondUrl.setFullUrl(TEST_FULL_URL);
        secondUrl.setVisits(3);
        return Arrays.asList(firstUrl, secondUrl);
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(json, clazz);
    }
}
