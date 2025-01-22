package com.subhash.smrturlf.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class QrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] image;

    private String shortUrl;

    @OneToOne
    @JoinColumn(name = "url_mapping_id")
    private UrlMapping urlMapping;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Userr user;
}
