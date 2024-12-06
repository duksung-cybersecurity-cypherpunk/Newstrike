package com.newstrike.prj.repository;

import com.newstrike.prj.domain.Subscriber;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    boolean existsByEmail(String email);
    Optional<Subscriber> findByEmail(String email);
    List<Subscriber> findAll();
}
