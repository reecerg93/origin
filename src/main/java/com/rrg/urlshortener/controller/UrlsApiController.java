package com.rrg.urlshortener.controller;

import com.rrg.urlshortener.exception.InvalidFieldException;
import com.rrg.urlshortener.exception.MissingFieldException;
import com.rrg.urlshortener.exception.ResourceNotFoundException;
import com.rrg.urlshortener.model.Url;
import com.rrg.urlshortener.openapi.api.UrlsApi;
import com.rrg.urlshortener.openapi.model.*;
import com.rrg.urlshortener.service.UrlService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.util.Date;

@RestController
public class UrlsApiController implements UrlsApi {
    private final UrlService urlService;

    public UrlsApiController(@Qualifier("urlService") UrlService urlService) {
        this.urlService = urlService;
    }

    @Override
    public ResponseEntity<ResponseDto> createShortUrl(@NotNull ShortUrlCreateDto shortUrlCreateDto) throws MissingFieldException, InvalidFieldException {
        var url = urlService.createShortUrl(shortUrlCreateDto.getFullUrl());
        var returnDto = convertToShortUrlDto(url);
        return new ResponseEntity<>(returnDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ResponseDto> getShortUrlIdsByFullUrl(@NotNull String fullUrl) throws InvalidFieldException, MissingFieldException {
        var urls = urlService.getUrlsByFullUrl(fullUrl);
        var returnDto = new ShortUrlSearchDto();
        returnDto.setShortUrlIds(urls.stream().map(this::convertToMetricsDto).toList());
        return new ResponseEntity<>(returnDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> getRedirection(@NotNull String shortUrlId) throws MissingFieldException, ResourceNotFoundException, InvalidFieldException {
        var headers = urlService.processRedirection(shortUrlId);
        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }

    @Override
    public ResponseEntity<ResponseDto> getMetricsByShortUrlId(@NotNull String shortUrlId) throws MissingFieldException, ResourceNotFoundException {
        var url = urlService.getUrlByShortUrlId(shortUrlId);
        var returnDto = convertToMetricsDto(url);
        return new ResponseEntity<>(returnDto, HttpStatus.OK);
    }

    private ShortUrlMetricsDto convertToMetricsDto(Url url) {
        var returnDto = new ShortUrlMetricsDto();
        returnDto.setVisits(url.getVisits());
        returnDto.setShortUrlId(url.getShortUrlId());
        return returnDto;
    }

    private ShortUrlDto convertToShortUrlDto(Url url) {
        var date = new Date();
        var returnDto = new ShortUrlDto();
        returnDto.setTimestamp(date.toInstant().atOffset(ZoneOffset.UTC));
        returnDto.setFullUrl(url.getFullUrl());
        returnDto.setShortUrlId(url.getShortUrlId());
        return returnDto;
    }
}
