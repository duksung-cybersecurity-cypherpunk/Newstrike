package com.newstrike.prj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import com.newstrike.prj.domain.News;

@Data
@Getter
@AllArgsConstructor
public class NewsResponse {
    private News news;
    private boolean isLiked;
    private boolean isScrapped;

    // getter, setter
}
