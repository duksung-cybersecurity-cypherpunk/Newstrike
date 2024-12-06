package com.newstrike.prj.service;

import com.newstrike.prj.domain.Subscriber;
import com.newstrike.prj.repository.SubscriberRepository;
import org.springframework.stereotype.Service;

@Service
public class SubscribeService {

    private final SubscriberRepository subscriberRepository;

    public SubscribeService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    public Subscriber subscribe(String email, String nickname) {
        // 이미 구독된 사용자라면 예외를 던짐
        if (subscriberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 구독되지 않은 경우 새로운 구독자 추가
        Subscriber subscriber = new Subscriber();
        subscriber.setEmail(email);
        subscriber.setNickname(nickname);

        return subscriberRepository.save(subscriber);
    }
}
