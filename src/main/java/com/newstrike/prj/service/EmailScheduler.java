package com.newstrike.prj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.newstrike.prj.domain.News;
import com.newstrike.prj.domain.Subscriber;

import java.util.List;

@Service
public class EmailScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private TemplateEngine templateEngine;  // Thymeleaf 템플릿 엔진

    @Scheduled(cron = "0 0/1 * * * ?")  // 매일 아침 9시 실행
    public void sendDailyEmails() {
        System.out.println("스케줄러 실행 중...");
        
        List<Subscriber> subscribers = emailService.getAllSubscribers();
        List<News> newsBatch = newsService.getLatestNewsBatch(3);

        for (Subscriber subscriber : subscribers) {
            String subject = "오늘의 뉴스";
            String body = buildHtmlContent(newsBatch, subscriber.getNickname());  // 개인화된 내용
            emailService.sendHtmlEmailWithNickname(subscriber.getEmail(), subject, body);
        }
    }

    private String buildHtmlContent(List<News> newsBatch, String nickname) {
        Context context = new Context();
        context.setVariable("nickname", nickname);
    
        // 로그 추가
        System.out.println("Generating email for: " + nickname);
        
        if (newsBatch.size() >= 3) {
            // 로그로 각 변수 출력하기
            System.out.println("News 1 Title: " + newsBatch.get(0).getTitle());
            System.out.println("News 1 Thumbnail URL: https://catfish-solid-specially.ngrok-free.app" + newsBatch.get(0).getThumbnail());
            
            context.setVariable("newsTitle1", newsBatch.get(0).getTitle());
            context.setVariable("thumbnail1", "https://catfish-solid-specially.ngrok-free.app" + newsBatch.get(0).getThumbnail() );
            context.setVariable("newsContent1", newsBatch.get(0).getContent());
            context.setVariable("newsLink1", newsBatch.get(0).getLink());
    
            System.out.println("News 2 Title: " + newsBatch.get(1).getTitle());
            System.out.println("News 2 Thumbnail URL: https://catfish-solid-specially.ngrok-free.app" + newsBatch.get(1).getThumbnail());
    
            context.setVariable("newsTitle2", newsBatch.get(1).getTitle());
            context.setVariable("thumbnail2", "https://catfish-solid-specially.ngrok-free.app/" + newsBatch.get(1).getThumbnail());
            context.setVariable("newsContent2", newsBatch.get(1).getContent());
            context.setVariable("newsLink2", newsBatch.get(1).getLink());
    
            System.out.println("News 3 Title: " + newsBatch.get(2).getTitle());
            System.out.println("News 3 Thumbnail URL: https://catfish-solid-specially.ngrok-free.app" + newsBatch.get(2).getThumbnail());
    
            context.setVariable("newsTitle3", newsBatch.get(2).getTitle());
            context.setVariable("thumbnail3", "https://catfish-solid-specially.ngrok-free.app" + newsBatch.get(2).getThumbnail());
            context.setVariable("newsContent3", newsBatch.get(2).getContent());
            context.setVariable("newsLink3", newsBatch.get(2).getLink());
        }
    
        return templateEngine.process("newsletterTemplate", context);
    }
     
}