package com.newstrike.prj.repository;

import com.newstrike.prj.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<Users> findByEmail(String email);
    Optional<Users> findById(String id);  // 이 메서드에서 id를 JWT 토큰으로 추출한 후 사용
    boolean existsByIdAndLikedNews_Id(String userId, Long newsId);
}
