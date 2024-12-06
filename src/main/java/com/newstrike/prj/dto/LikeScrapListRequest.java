package com.newstrike.prj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeScrapListRequest<T> {

    private boolean isSuccess;
    private int code;
    private String message;
    private List<T> items;

    public LikeScrapListRequest(List<T> items) {
        this.isSuccess = true;
        this.code = 1000;
        this.message = "요청에 성공하였습니다.";
        this.items = items;
    }
}
