package com.newstrike.prj.service;
// import java.util.Arrays;
// import java.util.List;
import java.util.Random;
import java.io.File;

import org.springframework.stereotype.Service;

@Service
public class ThumbnailService {

    private static final String THUMBNAIL_DIRECTORY = "/home/daun/workspace/newsletter_BE/prj/src/main/resources/static/thumbnails";
    private static final String THUMBNAIL_URL_PREFIX = "/thumbnails/";
    private final Random random = new Random();

    public String getRandomThumbnailPath() {
        File directory = new File(THUMBNAIL_DIRECTORY);
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        if (files == null || files.length == 0) {
            throw new IllegalStateException("Thumbnail directory is empty or not accessible.");
        }

        int index = random.nextInt(files.length);
        String fileName = files[index].getName();

        // 썸네일 파일의 URL 반환
        return THUMBNAIL_URL_PREFIX + fileName;
    }
}

