package com.subhash.smrturlf.repository;

import com.subhash.smrturlf.model.UrlMapping;
import com.subhash.smrturlf.model.Userr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping,Integer> {
    UrlMapping findByLongUrl(String longUrl);

    List<UrlMapping> findAllByUserOrderByIdDesc(Userr user);

    UrlMapping findByShortUrl(String shortUrl);

//    List<UrlMapping> findByUserName(String username);

//    boolean existsByLongUrlAndUserName(String longUrl, String userName);

//    UrlMapping findByLongUrlAndUserName(String longUrl, String userName);

    UrlMapping findByLongUrlAndUser(String longUrl, Userr user);

    List<UrlMapping> findByUser(Userr user);

    List<UrlMapping> findAllByUser(Userr user);

//    UrlMapping findByShortUrlAndUserName(String shortUrl, String userName);
}
