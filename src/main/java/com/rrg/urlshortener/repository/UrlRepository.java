package com.rrg.urlshortener.repository;

import com.rrg.urlshortener.model.Url;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends CrudRepository<Url, Integer> {

    Optional<Url> findByShortUrlId(String shortUrlId);

    List<Url> findAllByFullUrl(String fullUrl);
}
