# JDK 17을 기반으로 하는 이미지 사용
FROM openjdk:17-alpine

WORKDIR /usr/src/app

ARG JAR_PATH=./build/libs

# 빌드한 파일 옮기기
COPY ${JAR_PATH}/modutimer-0.0.1-SNAPSHOT.jar ${JAR_PATH}/modutimer-0.0.1-SNAPSHOT.jar

# 실행하기
CMD ["java","-jar","./build/libs/modutimer-0.0.1-SNAPSHOT.jar"]

