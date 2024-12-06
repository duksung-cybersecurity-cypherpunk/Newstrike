package com.newstrike.prj.service;


import com.newstrike.prj.auth.JwtTokenUtil; 
import com.newstrike.prj.domain.News;
import com.newstrike.prj.domain.Scrap;
import com.newstrike.prj.domain.Users; 
import com.newstrike.prj.domain.Like;
import com.newstrike.prj.dto.NewsListRequest;
import com.newstrike.prj.dto.LikeScrapResponseDto;
import com.newstrike.prj.repository.NewsRepository;
import com.newstrike.prj.repository.ScrapRepository;
import com.newstrike.prj.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import static com.newstrike.prj.BaseResponseStatus.INVALID_NEWS_TITLE;
import static com.newstrike.prj.BaseResponseStatus.INVALID_NEWS_CONTENT;
import com.newstrike.prj.repository.LikeRepository;
import com.newstrike.prj.dto.NewsResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private ScrapRepository scrapRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    // LLM 데이터를 기반으로 뉴스 생성
    public News createNewsFromLLM(String title, String content, String source, String link, String fiveWOneH) {
        String thumbnailUrl = thumbnailService.getRandomThumbnailPath();
        News news = News.builder()
                .title(title)
                .content(content)
                .source(source)
                .link(link)
                .fiveWOneH(fiveWOneH)
                .createDate(LocalDateTime.now())
                .thumbnail(thumbnailUrl)
                .build();
        return newsRepository.save(news);
    }

    public News createNews(String title, String content) {
        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException(INVALID_NEWS_TITLE.getMessage());
        }
        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException(INVALID_NEWS_CONTENT.getMessage());
        }

        String thumbnailUrl = thumbnailService.getRandomThumbnailPath();
        News news = News.builder()
                .title(title)
                .content(content)
                .createDate(LocalDateTime.now())
                .thumbnail(thumbnailUrl)  // 수정된 부분
                .build();
        return newsRepository.save(news);
    }


    // 유저가 좋아요한 뉴스 목록을 반환
    public List<News> getLikedNewsByUser(String token) {
        String userId = jwtTokenUtil.getId(token.substring(7));  // Bearer 부분 제거 후 userId 추출
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        List<Like> likes = likeRepository.findByUser(user);
        return likes.stream().map(Like::getNews).collect(Collectors.toList());
    }

    // 유저가 스크랩한 뉴스 목록을 반환
    public List<News> getScrappedNewsByUser(String token) {
        String userId = jwtTokenUtil.getId(token.substring(7));  // JWT 토큰에서 userId 추출
        Users user = new Users();  // Users 객체를 생성하고 userId 설정
        user.setId(userId);
    
        // 사용자별 스크랩된 뉴스를 scrappedDate 기준으로 최신순으로 가져옴
        List<Scrap> scrappedNews = scrapRepository.findByUserOrderByScrappedDateDesc(user);
    
        // Scrap에서 News만 추출하여 반환
        return scrappedNews.stream()
            .map(Scrap::getNews)
            .filter(News::isPublished)
            .collect(Collectors.toList());
    }    

    // 좋아요 상태 토글 후 응답
    public LikeScrapResponseDto toggleLikeStatus(Long id, String token) {
        String userId = jwtTokenUtil.getId(token.substring(7));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));

        boolean isLiked;
        Optional<Like> existingLike = likeRepository.findByUserAndNews(user, news);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            news.decrementLikeCount();
            isLiked = false;
        } else {
            Like like = new Like(user, news);
            likeRepository.save(like);
            news.incrementLikeCount();
            isLiked = true;
        }

        newsRepository.save(news);

        return new LikeScrapResponseDto(isLiked, scrapRepository.existsByUserAndNews(user, news));
    }

    // 스크랩 상태 토글 후 응답
    public LikeScrapResponseDto toggleScrapStatus(Long id, String token) {
        String userId = jwtTokenUtil.getId(token.substring(7));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));

        boolean isScrapped;
        Optional<Scrap> existingScrap = scrapRepository.findByUserAndNews(user, news);
        if (existingScrap.isPresent()) {
            scrapRepository.delete(existingScrap.get());
            news.decrementScrapCount();
            isScrapped = false;
        } else {
            Scrap scrap = new Scrap(user, news);
            scrapRepository.save(scrap);
            news.incrementScrapCount();
            isScrapped = true;
        }

        newsRepository.save(news);

        return new LikeScrapResponseDto(likeRepository.existsByUserAndNews(user, news), isScrapped);
    }

    public void deleteNewsByIds(List<Long> ids) {
        List<News> newsList = newsRepository.findAllById(ids);
        if(newsList.isEmpty()) {
            throw new IllegalArgumentException("No news found");
        }

        newsRepository.deleteAll(newsList);
    }


//     // 마이페이지에서 스크랩한 뉴스 목록 가져오기
//     public List<News> getScrapNewsByJwtToken(String jwtToken) {
//     String userId = jwtTokenUtil.getId(jwtToken);
//     return scrapRepository.findByUsersId(userId)
//             .stream()
//             .map(Scrap::getNews)
//             .collect(Collectors.toList());
// }


    // 뉴스 리스트를 페이지네이션하여 반환
    public Page<NewsListRequest> getNewsByPage(int page, int size) {
    logger.info("Fetching page: {}, with size: {}", page, size);

    Page<NewsListRequest> newsPage = newsRepository.findByIsPublishedTrue(
            PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "publishDate")
                    .and(Sort.by(Sort.Direction.DESC, "id"))))
            .map(news -> new NewsListRequest(
                    news.getId(),
                    news.getTitle(),
                    news.getPublishDate(),
                    news.getLikeCount(),
                    news.getScrapCount(),
                    news.getThumbnail()
            ));

    logger.info("Total elements: {}, Total pages: {}", newsPage.getTotalElements(), newsPage.getTotalPages());

    return newsPage;
}
    
    // NewsService에 인자가 하나인 메서드 추가
    public News getNewsById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));
    }

    // 로그인 상관없이 뉴스를 조회
    // 특정 ID로 뉴스를 조회하고, 유저의 좋아요/스크랩 상태도 함께 반환
    // 로그인 상관없이 뉴스를 조회하고, 로그인한 경우 좋아요/스크랩 상태를 반환
public NewsResponse getNewsById(Long id, String token) {
    // 뉴스 조회
    News news = newsRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));

    // 기본적으로 좋아요/스크랩 상태는 false로 설정
    boolean isLiked = false;
    boolean isScrapped = false;

    // token이 null이 아니고, 유효한 경우에만 좋아요/스크랩 상태 확인
    if (token != null && !token.isEmpty() && token.startsWith("Bearer ")) {
        String userId = jwtTokenUtil.getId(token.substring(7));

        if (userId != null) {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // 좋아요 상태 확인
            isLiked = likeRepository.existsByUserAndNews(user, news);
            // 스크랩 상태 확인
            isScrapped = scrapRepository.existsByUserAndNews(user, news);
        }
    }

    // NewsResponse 객체로 반환 (뉴스 내용 + 좋아요/스크랩 상태)
    return new NewsResponse(news, isLiked, isScrapped);
}


    // 로그인 상관없이 뉴스를 조회하고, 로그인한 경우 좋아요/스크랩 상태를 반환
    public NewsResponse getNewsWithUserStatus(Long id, String token) {
        // 뉴스 조회
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));

        // 기본적으로 좋아요/스크랩 상태는 false로 설정
        boolean isLiked = false;
        boolean isScrapped = false;

        // token이 null이 아니고, 유효한 경우에만 좋아요/스크랩 상태 확인
        if (token != null && !token.isEmpty() && token.startsWith("Bearer ")) {
            try {
                String userId = jwtTokenUtil.getId(token.substring(7));

                if (userId != null) {
                    Users user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                    // 좋아요 상태 확인
                    isLiked = likeRepository.existsByUserAndNews(user, news);
                    // 스크랩 상태 확인
                    isScrapped = scrapRepository.existsByUserAndNews(user, news);
                }
            } catch (Exception e) {
                // 토큰이 유효하지 않으면 좋아요/스크랩 상태는 false로 유지
            }
        }

        // NewsResponse 객체로 반환 (뉴스 내용 + 좋아요/스크랩 상태)
        return new NewsResponse(news, isLiked, isScrapped);
    }



    public void publishNewsByIds(List<Long> ids) {
        List<News> newsList = newsRepository.findAllById(ids);

        if (newsList.isEmpty()) {
            throw new IllegalArgumentException("No news found with the provided ids");
        }

        for (News news : newsList) {
            if (!news.isPublished()) {
                news.setPublished(true);
                news.setPublishDate(LocalDateTime.now());
            }
        }

        newsRepository.saveAll(newsList);
    }

    public void unpublishNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No news found with the provided id"));

        if (news.isPublished()) {
            news.setPublished(false);
            news.setPublishDate(null);
            newsRepository.save(news);
        }
    }

    public News getNextPublishedNews(Long currentId, String sortType) {
        List<News> sortedNewsList = getSortedNewsList(sortType);
    
        // 현재 뉴스의 인덱스 찾기
        int currentIndex = findCurrentNewsIndex(sortedNewsList, currentId);
    
        if (currentIndex >= 0 && currentIndex < sortedNewsList.size() - 1) {
            return sortedNewsList.get(currentIndex + 1);  // 다음 뉴스
        } else {
            throw new IllegalArgumentException("No next news available");
        }
    } 

    public News getPreviousPublishedNews(Long currentId, String sortType) {
        List<News> sortedNewsList = getSortedNewsList(sortType);
    
        // 현재 뉴스의 인덱스 찾기
        int currentIndex = findCurrentNewsIndex(sortedNewsList, currentId);
    
        if (currentIndex > 0) {
            return sortedNewsList.get(currentIndex - 1);  // 이전 뉴스
        } else {
            throw new IllegalArgumentException("No previous news available");
        }
    }
    
    private int findCurrentNewsIndex(List<News> newsList, Long currentNewsId) {
        for (int i = 0; i < newsList.size(); i++) {
            if (newsList.get(i).getId().equals(currentNewsId)) {
                return i;
            }
        }
        return -1;  // 뉴스가 리스트에 없을 경우
    }

    public List<News> getSortedNewsList(String sortType) {
        Page<News> newsPage;
        
        // 인기순 또는 최신순에 따라 다른 정렬 기준을 적용
        if (sortType.equals("popular")) {
            // 인기순으로 정렬된 페이지 가져오기
            Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("publishDate")));
            newsPage = newsRepository.findAll(pageable);
        } else {
            // 최신순으로 정렬된 페이지 가져오기
            Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.desc("publishDate")));
            newsPage = newsRepository.findAll(pageable);
        }
    
        // Page<News>에서 List<News>로 변환
        return newsPage.getContent();
    }

    public Page<NewsListRequest> getNewsByPageAndSearchTerm(int page, int size, String searchTerm) {
        // PublishDate를 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "publishDate"));
        Page<News> newsPage;
    
        if (searchTerm != null && !searchTerm.isEmpty()) {
            // isPublished가 true인 항목만 검색하고, publishDate로 최신순 정렬
            newsPage = newsRepository.findByTitleContainingAndIsPublishedTrueOrContentContainingAndIsPublishedTrue(searchTerm, searchTerm, pageable);
        } else {
            // 전체 뉴스 중에서 isPublished가 true인 항목만 가져오고, publishDate로 최신순 정렬
            newsPage = newsRepository.findByIsPublishedTrue(pageable);
        }
    
        // 변환 작업
        List<NewsListRequest> newsListRequests = newsPage.getContent().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    
        return new PageImpl<>(newsListRequests, pageable, newsPage.getTotalElements());
    }
    
    public Page<NewsListRequest> getNewsByPageAndSearchTermAndLikes(int page, int size, String searchTerm, boolean isByLikes) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<News> newsPage;
        
        // 현재 날짜와 일주일 전 날짜 구하기
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        // 일주일 내에 발행된 뉴스 중 좋아요 순으로 정렬된 뉴스 가져오기
        List<News> recentNews = newsRepository
                .findByPublishDateAfterAndIsPublishedTrue(oneWeekAgo, Sort.by(
                        Sort.Order.desc("likeCount"),
                        Sort.Order.desc("publishDate")
                ));
        
        // 일주일 이전에 발행된 모든 뉴스 가져오기
        List<News> olderNews;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            olderNews = newsRepository
                    .findByTitleContainingAndIsPublishedTrueOrContentContainingAndIsPublishedTrueAndPublishDateBefore(
                            searchTerm, searchTerm, oneWeekAgo, Sort.by(Sort.Order.desc("publishDate"))
                    );
        } else {
            olderNews = newsRepository
                    .findByPublishDateBeforeAndIsPublishedTrue(oneWeekAgo, Sort.by(Sort.Order.desc("publishDate")));
        }
        
        // 두 뉴스 리스트를 결합
        List<News> allNews = new ArrayList<>(recentNews);
        allNews.addAll(olderNews);

        // 페이지에 맞게 결과 슬라이싱
        int start = Math.min((int) pageable.getOffset(), allNews.size());
        int end = Math.min((start + pageable.getPageSize()), allNews.size());
        List<News> paginatedNews = allNews.subList(start, end);

        // 변환 작업
        List<NewsListRequest> newsListRequests = paginatedNews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(newsListRequests, pageable, allNews.size());
    }


    private NewsListRequest convertToDto(News news) {
        return new NewsListRequest(
            news.getId(),
            news.getTitle(),
            news.getPublishDate(), // LocalDate
            news.getLikeCount(),    // Integer
            news.getScrapCount(),   // Integer
            news.getThumbnail()      // String
        );
    }

    public List<News> getLatestNewsBatch(int limit) {
        return newsRepository.findTop3ByIsPublishedTrueOrderByPublishDateDesc();
    }
}