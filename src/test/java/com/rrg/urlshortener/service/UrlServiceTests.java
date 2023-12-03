package com.rrg.urlshortener.service;

import com.rrg.urlshortener.TestUtil;
import com.rrg.urlshortener.exception.InvalidFieldException;
import com.rrg.urlshortener.exception.MissingFieldException;
import com.rrg.urlshortener.exception.ResourceNotFoundException;
import com.rrg.urlshortener.exception.ShortUrlIdGenerationException;
import com.rrg.urlshortener.model.Url;
import com.rrg.urlshortener.repository.UrlRepository;
import com.rrg.urlshortener.service.impl.UrlServiceImpl;
import com.rrg.urlshortener.util.UrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTests extends TestUtil {

    private static final int ATTEMPT_LIMIT = 5;

    @Mock
    private UrlRepository repo;
    @Mock
    private UrlUtil util;

    @InjectMocks
    private UrlServiceImpl service;

    private List<Url> testUrls;

    @BeforeEach
    public void setup() throws IllegalAccessException {
        testUrls = getTestUrls();
        FieldUtils.writeField(service, "attemptLimit", ATTEMPT_LIMIT, true);
    }

    @DisplayName("JUnit test for createShortUrl method")
    @Test
    void givenFullUrl_whenCreateShortUrl_thenReturnUrl() {
        var url = testUrls.get(0);

        when(util.generateId()).thenReturn("AbCdEfG");
        when(util.isValidUrl(TEST_FULL_URL)).thenReturn(true);
        when(repo.findByShortUrlId("AbCdEfG")).thenReturn(Optional.empty());
        when(service.saveUrl(TEST_FULL_URL, "AbCdEfG")).thenReturn(url);

        var createdUrl = service.createShortUrl(TEST_FULL_URL);
        assertNotNull(createdUrl);
    }

    @DisplayName("JUnit test for createShortUrl method which throws ShortUrlIdGenerationException")
    @Test
    void givenFullUrl_whenCreateShortUrl_thenThrowsShortUrlIdGenerationException() {
        var url = testUrls.get(0);
        var fullUrl = url.getFullUrl();

        when(util.generateId()).thenReturn("AbCdEfG");
        when(util.isValidUrl(TEST_FULL_URL)).thenReturn(true);
        when(repo.findByShortUrlId("AbCdEfG")).thenReturn(Optional.of(url));

        assertThrows(ShortUrlIdGenerationException.class, () -> service.createShortUrl(fullUrl));
    }

    @DisplayName("JUnit test for createShortUrl method which throws MissingFieldException")
    @Test
    void givenBlankUrl_whenCreateShortUrl_thenThrowsMissingFieldException() {
        var incorrectUrl = StringUtils.EMPTY;

        assertThrows(MissingFieldException.class, () -> service.createShortUrl(incorrectUrl));
    }

    @DisplayName("JUnit test for createShortUrl method which throws InvalidFieldException")
    @Test
    void givenIncorrectUrl_whenCreateShortUrl_thenThrowsInvalidFieldException() {
        var incorrectUrl = INCORRECT_TEST_FULL_URL;

        when(util.isValidUrl(incorrectUrl)).thenReturn(false);

        assertThrows(InvalidFieldException.class, () -> service.createShortUrl(incorrectUrl));
    }

    @DisplayName("JUnit test for processRedirection method")
    @Test
    void givenShortUrlId_whenProcessRedirection_thenReturnHttpHeaders() {
        var shortUrlId = "AbCdEfG";
        var url = getTestUrls().get(0);

        when(repo.findByShortUrlId(shortUrlId)).thenReturn(Optional.of(url));

        var httpHeaders = service.processRedirection(shortUrlId);

        assertNotNull(httpHeaders);
        assertNotNull(httpHeaders.getLocation());
        assertEquals(TEST_FULL_URL, httpHeaders.getLocation().toString());
    }

    @DisplayName("JUnit test for processRedirection method which throws InvalidFieldException")
    @Test
    void givenShortUrlId_whenProcessRedirection_thenThrowsInvalidFieldException() {
        var shortUrlId = "AbCdEfG";
        var url = new Url();
        url.setFullUrl(INCORRECT_TEST_FULL_URL);
        url.setShortUrlId(shortUrlId);

        when(repo.findByShortUrlId(shortUrlId)).thenReturn(Optional.of(url));

        assertThrows(InvalidFieldException.class, () -> service.processRedirection(shortUrlId));
    }

    @DisplayName("JUnit test for getUrlByShortUrlId method")
    @Test
    void givenShortUrlId_whenGetUrlByShortUrlId_thenReturnUrl() {
        var shortUrlId = "AbCdEfG";
        var url = getTestUrls().get(0);

        when(repo.findByShortUrlId(shortUrlId)).thenReturn(Optional.of(url));

        var retrievedUrl = service.getUrlByShortUrlId(shortUrlId);

        assertEquals(url, retrievedUrl);
    }

    @DisplayName("JUnit test for getUrlByShortUrlId method which throws MissingFieldException")
    @Test
    void givenMissingShortUrlId_whenGetUrlByShortUrlId_thenThrowsMissingFieldException() {
        var shortUrlId = StringUtils.EMPTY;

        assertThrows(MissingFieldException.class, () -> service.getUrlByShortUrlId(shortUrlId));
    }

    @DisplayName("JUnit test for getUrlByShortUrlId method which throws ResourceNotFoundException")
    @Test
    void givenShortUrlId_whenGetUrlByShortUrlId_thenThrowsResourceNotFoundException() {
        var shortUrlId = "AbCdEfG";

        when(repo.findByShortUrlId(shortUrlId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getUrlByShortUrlId(shortUrlId));
    }

    @DisplayName("JUnit test for getUrlsByFullUrl method")
    @Test
    void givenFullUrl_whenGetUrlsByFullUrl_thenReturnUrls() {
        when(repo.findAllByFullUrl(TEST_FULL_URL)).thenReturn(getTestUrls());
        when(util.sanitiseUrl(TEST_FULL_URL)).thenReturn(TEST_FULL_URL);
        when(util.isValidUrl(TEST_FULL_URL)).thenReturn(true);

        var urls = service.getUrlsByFullUrl(TEST_FULL_URL);

        assertEquals(getTestUrls(), urls);
    }

    @DisplayName("JUnit test for getUrlsByFullUrl method which throws MissingFieldException")
    @Test
    void givenBlankFullUrl_whenGetUrlsByFullUrl_thenThrowsMissingFieldException() {
        var fullUrl = StringUtils.EMPTY;

        assertThrows(MissingFieldException.class, () -> service.getUrlsByFullUrl(fullUrl));
    }

    @DisplayName("JUnit test for getUrlsByFullUrl method which throws MissingFieldException")
    @Test
    void givenIncorrectFullUrl_whenGetUrlsByFullUrl_thenThrowsInvalidFieldException() {
        var incorrectFullUrl = INCORRECT_TEST_FULL_URL;
        when(util.sanitiseUrl(incorrectFullUrl)).thenReturn(incorrectFullUrl);
        when(util.isValidUrl(incorrectFullUrl)).thenReturn(false);

        assertThrows(InvalidFieldException.class, () -> service.getUrlsByFullUrl(incorrectFullUrl));
    }

    @DisplayName("JUnit test for saveUrl method")
    @Test
    void givenFullUrlAndShortUrlId_whenSaveUrl_thenReturnUrl() {
        var fullUrl = TEST_FULL_URL;
        var shortUrlId = "AbCdEfG";
        var url = new Url();
        url.setShortUrlId(shortUrlId);
        url.setFullUrl(fullUrl);

        when(util.sanitiseUrl(fullUrl)).thenReturn(fullUrl);
        when(repo.save(url)).thenReturn(url);

        var savedUrl = service.saveUrl(fullUrl, shortUrlId);

        assertEquals(shortUrlId, savedUrl.getShortUrlId());
        assertEquals(fullUrl, savedUrl.getFullUrl());
        assertEquals(0, savedUrl.getVisits());
    }
}
