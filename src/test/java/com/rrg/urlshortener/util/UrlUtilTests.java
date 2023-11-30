package com.rrg.urlshortener.util;

import com.rrg.urlshortener.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UrlUtilTests extends AbstractTest {

    private static final Integer ID_LENGTH = 7;

    @Autowired
    private UrlUtil util;

    @DisplayName("JUnit test for generateId method")
    @Test
    void generateIdTest() {
        var id = util.generateId();
        assertNotNull(id);
        assertEquals(ID_LENGTH, id.length());
    }

    @DisplayName("JUnit test for isValidUrl method which returns true")
    @Test
    void givenUrl_whenIsValidUrl_thenReturnTrue() {
        var validUrl = util.isValidUrl(TEST_FULL_URL);

        assertTrue(validUrl);
    }

    @DisplayName("JUnit test for isValidUrl method which returns false")
    @Test
    void givenUrl_whenIsValidUrl_thenReturnFalse() {
        var validUrl = util.isValidUrl(INCORRECT_TEST_FULL_URL);

        assertFalse(validUrl);
    }

    @DisplayName("JUnit test for sanitiseUrl method")
    @Test
    void givenUrl_whenSanitiseUrl_thenReturnSanitisedUrl() {
        var originalUrl = "https://original.com/";
        var targetUrl = "https://original.com";

        var processedUrl = util.sanitiseUrl(originalUrl);

        assertEquals(targetUrl, processedUrl);
    }
}
