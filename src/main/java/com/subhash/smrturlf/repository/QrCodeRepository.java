package com.subhash.smrturlf.repository;

import com.subhash.smrturlf.model.QrCode;
import com.subhash.smrturlf.model.Userr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QrCodeRepository extends JpaRepository<QrCode,Long> {

    List<QrCode> findByUserOrderByIdDesc(Userr user);

    void deleteByShortUrl(String url);
}
