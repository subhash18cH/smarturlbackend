package com.subhash.smrturlf.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyClicksDTO {
    private LocalDate date;
    private long clickCount;

    public DailyClicksDTO(LocalDate date, long clickCount) {
        this.date = date;
        this.clickCount = clickCount;
    }
}
