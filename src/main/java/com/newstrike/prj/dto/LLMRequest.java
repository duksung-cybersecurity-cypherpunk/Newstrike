package com.newstrike.prj.dto;

public class LLMRequest {
    private String title;
    private String content;
    private String source;
    private String link;
    private String fiveWOneH;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFiveWOneH() {
        return fiveWOneH;
    }

    public void setFiveWOneH(String fiveWOneH) {
        this.fiveWOneH = fiveWOneH;
    }
}
