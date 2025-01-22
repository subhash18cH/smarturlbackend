package com.subhash.smrturlf.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String longUrl;

    private String shortUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Userr user;

    @OneToMany(mappedBy = "urlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserInfo> clickAnalytics;

    @OneToOne(mappedBy = "urlMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private QrCode qrCode;


}
