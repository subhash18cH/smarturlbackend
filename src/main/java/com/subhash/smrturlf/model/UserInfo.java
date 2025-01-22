package com.subhash.smrturlf.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String ipAddress;
    private String deviceType;
    private LocalDateTime accessTime;
    private int clickCount=0;
    private String city;
    private String region;
    private String country;
    private String shortUrl;

    @ManyToOne
    @JoinColumn(name = "url_mapping_id")
    private UrlMapping urlMapping;
}
