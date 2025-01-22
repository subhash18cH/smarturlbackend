package com.subhash.smrturlf.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import com.subhash.smrturlf.model.QrCode;
import com.subhash.smrturlf.model.UrlMapping;
import com.subhash.smrturlf.model.Userr;
import com.subhash.smrturlf.repository.QrCodeRepository;
import com.subhash.smrturlf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QrCodeService {

      @Autowired
      UserRepository userRepository;

      @Autowired
      QrCodeRepository qrCodeRepository;

    public QrCode generateAndSaveQRCode(UrlMapping urlMapping, String url, int width, int height, Userr user) throws WriterException, IOException {
        byte[] qrCodeImage = generateQRCodeImage(url, width, height);
        QrCode qrCode = new QrCode();
        qrCode.setImage(qrCodeImage);
        qrCode.setUrlMapping(urlMapping);
        qrCode.setShortUrl(url);
        qrCode.setUser(user);
        return qrCodeRepository.save(qrCode);
    }



    private byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }


    public List<QrCode> getQRCodesByUser(Userr user) {
        return qrCodeRepository.findByUserOrderByIdDesc(user);
    }

    @Transactional
    public void deleteQr(String url) {
        qrCodeRepository.deleteByShortUrl(url);
    }
}
