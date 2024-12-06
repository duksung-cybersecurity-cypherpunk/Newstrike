package com.newstrike.prj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "isSuccess", "code", "message", "items", "currentPage", "totalPages", "totalItems" })
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private boolean isSuccess;

    private int code;
    private String message;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> items;

    // 이 필드들은 성공 응답 시에만 포함
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int currentPage;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int totalPages;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private long totalItems;

    // 페이지네이션을 포함한 응답 생성자
    public ApiResponse(List<T> items, int currentPage, int totalPages, long totalItems) {
        this.isSuccess = true;
        this.code = 1000;  // 성공 코드
        this.message = "요청에 성공하였습니다.";
        this.items = items;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }

    // 성공/실패 응답 생성자 (단일 객체나 에러 처리 시 사용)
    public ApiResponse(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.items = null;
        this.currentPage = 0;
        this.totalPages = 0;
        this.totalItems = 0;
    }

    // 성공/실패 응답 생성자 (단일 객체나 에러 처리 시 사용, 제네릭 타입 명시)
    public ApiResponse(boolean isSuccess, int code, String message, List<T> items) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.items = items;
        this.currentPage = 0;
        this.totalPages = 0;
        this.totalItems = 0;
    }
}
