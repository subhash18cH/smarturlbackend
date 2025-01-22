package com.subhash.smrturlf.controller;

import com.google.zxing.WriterException;

import com.subhash.smrturlf.model.QrCode;
import com.subhash.smrturlf.model.QrCodeDTO;
import com.subhash.smrturlf.model.UrlMapping;
import com.subhash.smrturlf.model.Userr;
import com.subhash.smrturlf.repository.UrlMappingRepository;
import com.subhash.smrturlf.service.QrCodeService;
import com.subhash.smrturlf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/qrcode")
public class QrCodeController {

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private UserService userService;

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @PostMapping("/generate")
    public ResponseEntity<String> generateQRCode(@RequestParam String url, Principal principal) {
        Userr user=userService.findByUserName(principal.getName());
        UrlMapping urlMapping=urlMappingRepository.findByShortUrl(url);
        try {
            QrCode qrCode = qrCodeService.generateAndSaveQRCode(urlMapping,url, 300, 300,user);
            return ResponseEntity.ok("QR Code generated and saved for URL: ");
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error generating QR code");
        }
    }

    @GetMapping("/retrieve")
    public ResponseEntity<List<QrCodeDTO>> retrieveQRCodes(Principal principal) {
        Userr user = userService.findByUserName(principal.getName());
        List<QrCode> qrCodes = qrCodeService.getQRCodesByUser(user);

        if (qrCodes != null && !qrCodes.isEmpty()) {
            List<QrCodeDTO> qrCodeDTOs = qrCodes.stream()
                    .map(qrCode -> new QrCodeDTO(
                            Base64.getEncoder().encodeToString(qrCode.getImage()),
                            qrCode.getShortUrl()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(qrCodeDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/delete-qr")
    public void deleteQr(@RequestParam String url){
        qrCodeService.deleteQr(url);
    }
}
