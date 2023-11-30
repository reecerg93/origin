package com.rrg.urlshortener.service;

import com.rrg.urlshortener.model.Url;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface UrlService {

    Url createShortUrl(String fullUrl);

    Url getUrlByShortUrlId(String shortUrlId);

    List<Url> getUrlsByFullUrl(String fullUrl);

    Url saveUrl(String fullUrl, String shortUrlId);

    HttpHeaders processRedirection(String shortUrlId);

    void incrementVisits(Url url);
}
