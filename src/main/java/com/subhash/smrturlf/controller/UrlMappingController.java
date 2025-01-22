package com.subhash.smrturlf.controller;


import com.subhash.smrturlf.DTO.DailyClicksDTO;
import com.subhash.smrturlf.DTO.DeviceAnalyticsDTO;
import com.subhash.smrturlf.DTO.UserStatsDTO;
import com.subhash.smrturlf.model.UrlMapping;
import com.subhash.smrturlf.model.UrlMappingDTO;
import com.subhash.smrturlf.model.UserInfo;
import com.subhash.smrturlf.model.Userr;
import com.subhash.smrturlf.repository.UserInfoRepository;
import com.subhash.smrturlf.service.UrlMappingService;
import com.subhash.smrturlf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/url")
public class UrlMappingController {

    @Autowired
    private UrlMappingService urlService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoRepository userRepository;

    @PostMapping("/shortUrl")
    public String convertToShortUrl(@RequestParam String LongUrl,
                                    Principal principal) throws URISyntaxException, NoSuchAlgorithmException {
        Userr user=userService.findByUserName(principal.getName());
        return urlService.shortenUrl(user,LongUrl);
    }

    @GetMapping("/user")
    public ResponseEntity<List<UrlMappingDTO>> getUserURLs(Principal principal) {
        try {
            Userr user = userService.findByUserName(principal.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            List<UrlMapping> urls = urlService.getUrlsForUser(user);
            List<UrlMappingDTO> urlDTOs = urls.stream()
                    .map(url -> new UrlMappingDTO(
                            url.getId(),
                            url.getLongUrl(),
                            url.getShortUrl()
                    ))
                    .collect(Collectors.toList());

            if (urlDTOs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Collections.emptyList());
            }

            return ResponseEntity.ok(urlDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/total-stats")
    public ResponseEntity<UserStatsDTO> getUserStats(Principal principal) {
        Userr user = userService.findByUserName(principal.getName());
        List<UrlMapping> urlMappings = urlService.findByUser(user);
        int totalUrls = urlMappings.size();
        int totalClicks = urlMappings.stream()
                .flatMap(url -> url.getClickAnalytics().stream())
                .mapToInt(UserInfo::getClickCount)
                .sum();
        return ResponseEntity.ok(new UserStatsDTO(totalUrls, totalClicks));
    }

    @GetMapping("/device-stats")
    public ResponseEntity<DeviceAnalyticsDTO> getDeviceStats(Principal principal) {
        DeviceAnalyticsDTO stats = urlService.getDeviceAnalytics(principal.getName());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/daily-clicks")
    public ResponseEntity<List<DailyClicksDTO>> getDailyClicks(Principal principal) {
        List<DailyClicksDTO> dailyClicks = urlService.getDailyClicks(principal.getName());
        return ResponseEntity.ok(dailyClicks);
    }

    @GetMapping("/getAllUrls")
    public List<UrlMapping> getAllUrls(){
        return urlService.getAllUrls();
    }

    //delete an url and its associated info
    @DeleteMapping("/delete-url")
    public void deleteUrl(@RequestParam String shortUrl){
        urlService.removeUrl(shortUrl);
    }

    @GetMapping("/info")
    public List<UserInfo> info(@RequestParam String shortUrl){
        List<UserInfo> userInfo=userRepository.findByShortUrl(shortUrl);
        if(userInfo!=null){
           return userInfo;
        }
        return null;
    }
}
