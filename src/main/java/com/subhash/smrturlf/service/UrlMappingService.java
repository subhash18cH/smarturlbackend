package com.subhash.smrturlf.service;


import com.subhash.smrturlf.DTO.DailyClicksDTO;
import com.subhash.smrturlf.DTO.DeviceAnalyticsDTO;
import com.subhash.smrturlf.model.UrlMapping;
import com.subhash.smrturlf.model.UserInfo;
import com.subhash.smrturlf.model.Userr;
import com.subhash.smrturlf.repository.UrlMappingRepository;
import com.subhash.smrturlf.repository.UserInfoRepository;
import com.subhash.smrturlf.utility.GeoData;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UrlMappingService {

    @Autowired
    private UrlMappingRepository repository;
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    public String shortenUrl(Userr user, String longUrl) throws URISyntaxException {

        validateUrl(longUrl);
        UrlMapping existingUrl1=repository.findByLongUrlAndUser(longUrl,user);
        if(existingUrl1 != null){
            System.out.println("repo mein nahi mila");
            return existingUrl1.getShortUrl();
        }
        String shortUrl = generateShortUrl();
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setLongUrl(longUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setUser(user);
        try {
            repository.save(urlMapping);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save URL mapping", e);
        }
        System.out.println("scratch se bana ke bheja");
        return shortUrl;
    }

    private String generateShortUrl() {
        long id = System.currentTimeMillis() + (long) (Math.random() * 1000);
        String base62String = generateBase62(id, 5);
        return base62String.length() > 5 ? base62String.substring(0, 5) : base62String;
    }

    private void validateUrl(String longUrl) throws URISyntaxException {
        new URI(longUrl);
    }

    public String getLongUrl(String shortUrl) {
        String userAgent = request.getHeader("User-Agent");

        //for development use
//        String ipAddress = request.getRemoteAddr();

        // for production use
        String ipAddress=getClientIp(request);

        String deviceType = userAgent.contains("Mobile") ? "Mobile" : "Desktop";
        UrlMapping urlMapping = repository.findByShortUrl(shortUrl);

        if (urlMapping != null) {
            updateUserInfo(urlMapping,shortUrl, ipAddress, deviceType);
            return urlMapping.getLongUrl();
        }
        throw new RuntimeException("URL not found");
    }

    //for production use to get ip address
    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            ip = ip.split(",")[0].trim();
            if (InetAddressValidator.getInstance().isValid(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    //update user info according to no. of clicks hit by one user from same ip address
    private void updateUserInfo(UrlMapping urlMapping,String shortUrl, String ipAddress, String deviceType) {
        UserInfo userInfo = userRepository.findByShortUrlAndIpAddress(shortUrl, ipAddress);
        GeoDataService geoService = new GeoDataService();
        GeoData geoData = geoService.getGeoData(ipAddress);
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setClickCount(1);
            userInfo.setUrlMapping(urlMapping);
            userInfo.setShortUrl(shortUrl);
            userInfo.setIpAddress(ipAddress);
            userInfo.setDeviceType(deviceType);
            userInfo.setAccessTime(LocalDateTime.now());
        } else {
            userInfo.setClickCount(userInfo.getClickCount() + 1);
            userInfo.setAccessTime(LocalDateTime.now());
        }
        if (geoData != null) {
            userInfo.setCity(geoData.getCity());
            userInfo.setRegion(geoData.getRegion());
            userInfo.setCountry(geoData.getCountry());
        }
        userRepository.save(userInfo);
    }

    private String generateBase62(long number, int length) {
        final String base62Chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder shortUrl = new StringBuilder();

        while (number > 0) {
            shortUrl.append(base62Chars.charAt((int) (number % 62)));
            number /= 62;
        }
        while (shortUrl.length() < length) {
            shortUrl.append(base62Chars.charAt((int) (Math.random() * 62)));
        }
        return shortUrl.reverse().toString();
    }

    private boolean isValidShortUrl(String shortUrl) {
        if (shortUrl == null || shortUrl.isEmpty()) {
            return false;
        }
        if (shortUrl.length() < 5 || shortUrl.length() > 10) {
            return false;
        }
        return true;
    }

    public void removeUrl(String shortUrl) {

        if (!isValidShortUrl(shortUrl)) {
            System.out.println("Invalid short URL format.");
            return;
        }
        UrlMapping urlMapping=repository.findByShortUrl(shortUrl);
        if(urlMapping != null){
            repository.delete(urlMapping);
        }else {
            System.out.println("Short URL not found in database.");
        }

    }

    public List<UrlMapping> getAllUrls() {
        return repository.findAll();
    }

    public List<UrlMapping> getUrlsForUser(Userr user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return repository.findAllByUserOrderByIdDesc(user);
    }


    public List<UrlMapping> findByUser(Userr user) {
        return repository.findByUser(user);
    }

    public DeviceAnalyticsDTO getDeviceAnalytics(String username) {
        Userr user = userService.findByUserName(username);
        List<UserInfo> userInfos = userRepository.findByUrlMapping_User(user);

        // Calculate total clicks
        long totalClicks = userInfos.stream()
                .mapToInt(UserInfo::getClickCount)
                .sum();

        // Group by device type and count
        Map<String, Long> deviceDistribution = userInfos.stream()
                .collect(Collectors.groupingBy(
                        UserInfo::getDeviceType,
                        Collectors.summingLong(UserInfo::getClickCount)
                ));

        return new DeviceAnalyticsDTO(totalClicks, deviceDistribution);
    }

    public List<DailyClicksDTO> getDailyClicks(String username) {
        Userr user = userService.findByUserName(username);

        // Get all UserInfo records for this user's URLs
        List<UserInfo> userInfos = userRepository.findByUrlMapping_User(user);

        // Group by date and sum clicks
        Map<LocalDate, Long> dailyClicks = userInfos.stream()
                .collect(Collectors.groupingBy(
                        info -> info.getAccessTime().toLocalDate(),
                        Collectors.summingLong(UserInfo::getClickCount)
                ));

        // Convert to list of DTOs and sort by date
        return dailyClicks.entrySet().stream()
                .map(entry -> new DailyClicksDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(DailyClicksDTO::getDate))
                .collect(Collectors.toList());
    }
}
