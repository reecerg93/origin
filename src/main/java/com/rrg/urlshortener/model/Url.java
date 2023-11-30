package com.rrg.urlshortener.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash("Url")
public class Url {

    @Id
    private Integer id;
    @Indexed
    private String fullUrl;
    @Indexed
    private String shortUrlId;
    private long visits;
}
