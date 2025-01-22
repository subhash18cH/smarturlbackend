package com.subhash.smrturlf.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UrlMappingDTO {
    private Integer id;
    private String longUrl;
    private String shortUrl;
}
