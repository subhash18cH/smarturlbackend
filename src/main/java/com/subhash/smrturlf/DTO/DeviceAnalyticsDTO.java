package com.subhash.smrturlf.DTO;

import lombok.Data;

import java.util.Map;

@Data
public class DeviceAnalyticsDTO {
    private long totalClicks;
    private Map<String, Long> deviceTypeDistribution;

    public DeviceAnalyticsDTO(long totalClicks, Map<String, Long> deviceTypeDistribution) {
        this.totalClicks = totalClicks;
        this.deviceTypeDistribution = deviceTypeDistribution;
    }
}
