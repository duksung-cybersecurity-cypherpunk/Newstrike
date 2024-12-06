package com.newstrike.prj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeScrapResponseDto {
    private boolean liked;     // 좋아요 여부
    private boolean scrapped;  // 스크랩 여부
}
