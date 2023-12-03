package com.rrg.urlshortener.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

@Component
public class UrlUtil {

    private final Integer idLength;
    private final String permittedChars;
    private final Random random = new Random();

    public UrlUtil(@Value("${id.length}") Integer idLength, @Value("${permitted.chars}") String permittedChars) {
        this.idLength = idLength;
        this.permittedChars = permittedChars;
    }

    public String generateId() {
        var idBuilder = new StringBuilder();
        for (int i = 0; i < idLength; i++) {
            var index = random.nextInt(permittedChars.length());
            idBuilder.append(permittedChars.charAt(index));
        }
        return idBuilder.toString();
    }

    public boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public String sanitiseUrl(String url) {
        if (!StringUtils.isBlank(url) && url.charAt(url.length() - 1) == '/') {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
