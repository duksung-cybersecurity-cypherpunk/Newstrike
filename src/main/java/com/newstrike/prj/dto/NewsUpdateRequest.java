package com.newstrike.prj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class NewsUpdateRequest {

    private long id;
    private String title;
    private String content;
    
}
