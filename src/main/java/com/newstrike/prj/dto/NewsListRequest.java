package com.newstrike.prj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
@AllArgsConstructor
public class NewsListRequest {
    private Long id;
    private String title;
    private LocalDateTime publishDate;
    private Integer likeCount;
    private Integer scrapCount;
    private String thumbnail;
}

