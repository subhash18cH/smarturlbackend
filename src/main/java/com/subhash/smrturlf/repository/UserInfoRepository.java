package com.subhash.smrturlf.repository;



import com.subhash.smrturlf.model.UserInfo;
import com.subhash.smrturlf.model.Userr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo,Integer> {

    List<UserInfo>findByShortUrl(String shortUrl);

    UserInfo findByShortUrlAndIpAddress(String shortUrl, String ipAddress);

    List<UserInfo> findByUrlMapping_User(Userr user);

//    Userr findByUserName(String userName);
}
