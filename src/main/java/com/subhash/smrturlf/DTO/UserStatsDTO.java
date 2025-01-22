package com.subhash.smrturlf.DTO;

import lombok.Data;

@Data
public class UserStatsDTO {
    private int totalUrls;
    private int totalClicks;

    public UserStatsDTO(int totalUrls, int totalClicks) {
        this.totalUrls = totalUrls;
        this.totalClicks = totalClicks;
    }
}
