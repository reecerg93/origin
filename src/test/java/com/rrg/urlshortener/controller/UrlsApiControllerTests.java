package com.rrg.urlshortener.controller;

import com.rrg.urlshortener.TestUtil;
import com.rrg.urlshortener.exception.InvalidFieldException;
import com.rrg.urlshortener.exception.MissingFieldException;
import com.rrg.urlshortener.exception.ResourceNotFoundException;
import com.rrg.urlshortener.model.Url;
import com.rrg.urlshortener.openapi.model.ShortUrlCreateDto;
import com.rrg.urlshortener.openapi.model.ShortUrlDto;
import com.rrg.urlshortener.openapi.model.ShortUrlMetricsDto;
import com.rrg.urlshortener.service.impl.UrlServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlsApiController.class)
class UrlsApiControllerTests extends TestUtil {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlServiceImpl urlService;

    List<Url> testUrls;

    @BeforeEach
    public void setup() {
        this.testUrls = getTestUrls();
    }

    @DisplayName("JUnit test for createShortUrl POST method which returns code 201")
    @Test
    void givenFullUrl_whenCreateShortUrl_thenReturnShortUrlDto() throws Exception {
        var dto = new ShortUrlCreateDto();
        dto.setFullUrl(TEST_FULL_URL);
        var url = testUrls.get(0);

        when(urlService.createShortUrl(dto.getFullUrl())).thenReturn(url);

        var result = this.mockMvc.perform(post("/urls")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(dto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        var returnDto = mapFromJson(result.getResponse().getContentAsString(), ShortUrlDto.class);
        assertEquals(dto.getFullUrl(), returnDto.getFullUrl());
        assertEquals(url.getShortUrlId(), returnDto.getShortUrlId());
        assertNotNull(returnDto.getTimestamp());
    }

    @DisplayName("JUnit test for createShortUrl POST method which returns code 400 for MissingFieldException")
    @Test
    void givenEmptyFullUrl_whenCreateShortUrl_thenReturnError400() throws Exception {
        var dto = new ShortUrlCreateDto();
        dto.setFullUrl(StringUtils.EMPTY);

        when(urlService.createShortUrl(dto.getFullUrl())).thenThrow(MissingFieldException.class);

        this.mockMvc.perform(post("/urls")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("JUnit test for createShortUrl POST method which returns code 400 for InvalidFieldException")
    @Test
    void givenIncorrectFullUrl_whenCreateShortUrl_thenReturnError400() throws Exception {
        var dto = new ShortUrlCreateDto();
        dto.setFullUrl(INCORRECT_TEST_FULL_URL);

        when(urlService.createShortUrl(dto.getFullUrl())).thenThrow(InvalidFieldException.class);

        this.mockMvc.perform(post("/urls")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("JUnit test for getShortUrlIdsByFullUrl GET method which returns code 200")
    @Test
    void givenFullUrl_whenGetShortUrlIdsByFullUrl_thenReturnShortUrlMetricsDtoList() throws Exception {
        var urls = testUrls;

        when(urlService.getUrlsByFullUrl(TEST_FULL_URL)).thenReturn(urls);

        this.mockMvc.perform(get("/urls")
                        .queryParam("fullUrl", TEST_FULL_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(urls.get(0).getShortUrlId())))
                .andExpect(content().string(containsString(String.valueOf(urls.get(0).getVisits()))))
                .andExpect(content().string(containsString(urls.get(1).getShortUrlId())))
                .andExpect(content().string(containsString(String.valueOf(urls.get(1).getVisits()))));
    }

    @DisplayName("JUnit test for getShortUrlIdsByFullUrl GET method which returns code 400 for MissingFieldException")
    @Test
    void givenEmptyFullUrl_whenGetShortUrlIdsByFullUrl_thenReturnError400() throws Exception {
        when(urlService.getUrlsByFullUrl(StringUtils.EMPTY)).thenThrow(MissingFieldException.class);

        this.mockMvc.perform(get("/urls")
                        .queryParam("fullUrl", StringUtils.EMPTY)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("JUnit test for getShortUrlIdsByFullUrl GET method which returns code 400 for InvalidFieldException")
    @Test
    void givenIncorrectFullUrl_whenGetShortUrlIdsByFullUrl_thenReturnError400() throws Exception {
        when(urlService.getUrlsByFullUrl(INCORRECT_TEST_FULL_URL)).thenThrow(InvalidFieldException.class);

        this.mockMvc.perform(get("/urls")
                        .queryParam("fullUrl", INCORRECT_TEST_FULL_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("JUnit test for getRedirection GET method which returns code 303")
    @Test
    void givenShortUrlId_whenGetRedirection_thenReturnHttpHeaders() throws Exception {
        var shortUrlId = "aB1cD2e";
        var headers = new HttpHeaders();
        headers.setLocation(new URI(TEST_FULL_URL));

        when(urlService.processRedirection(shortUrlId)).thenReturn(headers);

        this.mockMvc.perform(get("/urls/" + shortUrlId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", TEST_FULL_URL));
    }

    @DisplayName("JUnit test for getRedirection GET method which returns code 400 FOR MissingFieldException")
    @Test
    void givenMissingShortUrlId_whenGetRedirection_thenReturnError400() throws Exception {
        when(urlService.processRedirection(StringUtils.SPACE)).thenThrow(MissingFieldException.class);

        this.mockMvc.perform(get("/urls/" + StringUtils.SPACE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("JUnit test for getRedirection GET method which returns code 400 FOR InvalidFieldException due to persisted full URL")
    @Test
    void givenShortUrlId_whenGetRedirection_thenReturnError400() throws Exception {
        var shortUrlId = "aBcDeFg";

        when(urlService.processRedirection(shortUrlId)).thenThrow(InvalidFieldException.class);

        this.mockMvc.perform(get("/urls/" + shortUrlId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("JUnit test for getRedirection GET method which returns code 404 FOR ResourceNotFoundException")
    @Test
    void givenUnknownShortUrlId_whenGetRedirection_thenReturnError404() throws Exception {
        var unknownId = "lMnOpQr";

        when(urlService.processRedirection(unknownId)).thenThrow(ResourceNotFoundException.class);

        this.mockMvc.perform(get("/urls/" + unknownId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("JUnit test for get getMetricsByShortUrlId GET method")
    @Test
    void givenShortUrlId_whenGetMetricsByShortUrlId_thenReturnShortUrlMetricsDto() throws Exception {
        var url = testUrls.get(0);

        when(urlService.getUrlByShortUrlId(url.getShortUrlId())).thenReturn(url);

        var result = this.mockMvc.perform(get("/urls/" + url.getShortUrlId() + "/metrics")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var returnDto = mapFromJson(result.getResponse().getContentAsString(), ShortUrlMetricsDto.class);
        assertEquals(url.getShortUrlId(), returnDto.getShortUrlId());
        assertEquals(url.getVisits(), returnDto.getVisits());
    }

    @DisplayName("JUnit test for get getMetricsByShortUrlId GET method which returns code 400 for MissingFieldException")
    @Test
    void givenEmptyShortUrlId_whenGetMetricsByShortUrlId_thenReturnError400() throws Exception {
        when(urlService.getUrlByShortUrlId(StringUtils.SPACE)).thenThrow(MissingFieldException.class);

        this.mockMvc.perform(get("/urls/" + StringUtils.SPACE + "/metrics")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("JUnit test for get getMetricsByShortUrlId GET method which returns code 404 for ResourceNotFoundException")
    @Test
    void givenUnknownShortUrlId_whenGetMetricsByShortUrlId_thenReturnError404() throws Exception {
        var unknownId = "lMnOpQr";

        when(urlService.getUrlByShortUrlId(unknownId)).thenThrow(ResourceNotFoundException.class);

        this.mockMvc.perform(get("/urls/" + unknownId + "/metrics")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
