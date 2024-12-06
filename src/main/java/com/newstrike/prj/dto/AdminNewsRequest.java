package com.newstrike.prj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
@AllArgsConstructor
public class AdminNewsRequest {
    private long id;
    private String title;
    private LocalDateTime createDate;
    private LocalDateTime publishDate;
    
}
