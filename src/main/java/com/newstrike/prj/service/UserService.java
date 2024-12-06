package com.newstrike.prj.service;

import com.newstrike.prj.dto.JoinRequest;
import com.newstrike.prj.dto.LoginRequest;
import com.newstrike.prj.dto.UserRequest;
import com.newstrike.prj.dto.UserResponse;
import com.newstrike.prj.auth.JwtTokenUtil;
import com.newstrike.prj.domain.Users;
import com.newstrike.prj.domain.Subscriber;
import com.newstrike.prj.repository.SubscriberRepository;
import com.newstrike.prj.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SubscriberRepository subscriberRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Email 중복 체크
     * 회원가입 기능 구현 시 사용
     * 중복되면 true return
     */
    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Nickname 중복 체크
     * 회원가입 기능 구현 시 사용
     * 중복되면 true return
     */
    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * 회원가입 기능
     */
    @Transactional
    public void join(JoinRequest req) {
        Users newUser = req.toEntity(encoder.encode(req.getPasswd()));
        
        // 구독자 정보 확인
        if (subscriberRepository.existsByEmail(newUser.getEmail())) {
            newUser.setSubscribe(true);
        }

        userRepository.save(newUser);
    }

    /**
     * 로그인 기능
     */
    public Users login(LoginRequest req) {
        // 이메일로 사용자 조회
        Users user = userRepository.findByEmail(req.getEmail()).orElse(null);

        // 이메일이 존재하지 않는 경우
        if (user == null) {
            throw new UserNotFoundException("존재하지 않는 유저입니다.");
        }

        // 비밀번호가 일치하지 않는 경우
        if (!encoder.matches(req.getPasswd(), user.getPasswd())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 시 grade_count와 grade 업데이트 및 매달 1일 초기화
        updateLoginStats(user);

        return user;
    }

    public class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
    
    public class InvalidPasswordException extends RuntimeException {
        public InvalidPasswordException(String message) {
            super(message);
        }
    }    
    

    /**
     * email로 사용자 정보 조회
     */
    public Users getLoginUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public Users getUserById(String id) {
        return userRepository.findById(id)
                             .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    @Transactional
    public boolean changePasswd(String email, String newPassword) {
        Optional<Users> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return false;
        }

        Users user = optionalUser.get();
        user.setPasswd(encoder.encode(newPassword));
        userRepository.save(user);
        
        return true;
    }

    /**
     * 사용자 로그인 시 grade_count를 증가시키고 grade를 업데이트하는 메서드
     * 매달 1일에 grade와 grade_count를 초기화
     */
    private void updateLoginStats(Users user) {
        LocalDate today = LocalDate.now();

        // 매달 1일에 grade와 grade_count 초기화
        if (today.getDayOfMonth() == 1) {
            user.setGrade("뉴비");
            user.setGrade_count(0);
        } else if (user.getLastLoginDate() == null || !user.getLastLoginDate().isEqual(today)) {
            user.setGrade_count(user.getGrade_count() + 1);
            user.setLastLoginDate(today);
        }

        // grade_count에 따른 grade 업데이트
        updateGrade(user);

        userRepository.save(user); // 변경사항 저장
    }

    private void updateGrade(Users user) {
        int gradeCount = user.getGrade_count();
        int remainingLogins = calculateRemainingLoginsForNextGrade(gradeCount, user.getGrade());
    
        // Grade 업데이트 로직
        if (gradeCount >= 21) {
            user.setGrade("전문가");
            user.setNext_grade("최고 등급");
        } else if (gradeCount >= 14) {
            user.setGrade("매니아");
            user.setNext_grade("전문가");
        } else if (gradeCount >= 7) {
            user.setGrade("애청자");
            user.setNext_grade("매니아");
        } else {
            user.setGrade("뉴비");
            user.setNext_grade("애청자");
        }
    
        user.setRemainingLogins(remainingLogins); // 남은 로그인 횟수 설정
    }

    private int calculateRemainingLoginsForNextGrade(int gradeCount, String currentGrade) {
        switch (currentGrade) {
            case "뉴비":
                return Math.max(1, 7 - gradeCount); 
            case "애청자":
                return Math.max(1, 14 - gradeCount);
            case "매니아":
                return Math.max(1, 21 - gradeCount);
            case "전문가":
                return 0; // 메이저는 최상위 등급이므로 더 이상 업그레이드가 없음
            default:
                return 0;
        }
    }
    
    // 사용자 정보 수정 메서드
    public UserResponse updateUser(String token, UserRequest userRequest, boolean isPasswordChange) {
        String userId = jwtTokenUtil.getId(token);
    
        Users user = userRepository.findById(userId)
                                   .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    
        // 닉네임 업데이트
        if (userRequest.getNickname() != null && !userRequest.getNickname().isEmpty()) {
            user.setNickname(userRequest.getNickname());
        }
    
        // 비밀번호 업데이트 (컨트롤러에서 검증을 완료한 경우)
        if (isPasswordChange) {
            user.setPasswd(encoder.encode(userRequest.getNewPasswd()));
        }
    
        userRepository.save(user);
    
        return new UserResponse(
            user.getEmail(),
            user.getNickname(),
            user.getGrade_count(),
            user.getGrade(),
            user.getNext_grade(),
            user.getRemainingLogins(),
            user.getRole()
        );
    }    

    @Transactional
    public boolean unsubscribe(String email) {
        // Subscriber 엔티티에서 해당 이메일로 구독자 정보 삭제
        Optional<Subscriber> optionalSubscriber = subscriberRepository.findByEmail(email);
        if (optionalSubscriber.isPresent()) {
            subscriberRepository.delete(optionalSubscriber.get());
        } else {
            return false; // 구독자가 존재하지 않을 경우 false 반환
        }

        // Users 엔티티에서 해당 이메일의 사용자를 찾아 subscribe를 false로 설정
        Optional<Users> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            user.setSubscribe(false); 
            userRepository.save(user); // 변경사항 저장 후 로그 확인
            System.out.println("사용자의 구독 상태가 업데이트되었습니다: " + user.getEmail());
            return true;
        }        
        return false; // 사용자가 존재하지 않을 경우 false 반환
    }

    public boolean deleteUserById(String userId) {
        Optional<Users> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
            return true;
        }
        return false; // 사용자가 존재하지 않을 경우 false 반환
    }
}
