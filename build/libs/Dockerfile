# 1. JDK 이미지를 기반으로 설정
FROM openjdk:17-jdk-alpine

# 2. 애플리케이션 JAR 파일을 컨테이너로 복사
COPY ./prj-0.0.1-SNAPSHOT.jar /app/prj-0.0.1-SNAPSHOT.jar

# 3. 애플리케이션 실행
WORKDIR /app
ENTRYPOINT ["java", "-jar", "prj-0.0.1-SNAPSHOT.jar"]
