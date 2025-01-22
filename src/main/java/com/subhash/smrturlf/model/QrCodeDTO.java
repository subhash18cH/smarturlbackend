package com.subhash.smrturlf.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QrCodeDTO {
    private String image;
    private String shortUrl;
}
