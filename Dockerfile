# JDK 17을 기반으로 하는 이미지 사용
FROM openjdk:17-oracle

# 작업 디렉토리
WORKDIR /usr/src/app

# 변수 정의 (빌드 시 사용)
ARG JAR_PATH=./build/libs

# 빌드한 파일 옮기기
COPY ${JAR_PATH}/*.jar ${JAR_PATH}/app.jar

# 환경변수 설정 (실행중인 컨테이너에 액세스 가능)
ENV JAR_PATH ${JAR_PATH}

EXPOSE 8080

# ENV 이용해서 실행
CMD java -jar ${JAR_PATH}/app.jar
