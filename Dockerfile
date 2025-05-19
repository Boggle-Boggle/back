FROM arm64v8/openjdk:17-jdk-slim
#작업 디렉토리 설정
WORKDIR /app

#앱 JAR 복사
COPY build/libs/*.jar app.jar

#Datadog Agent 다운로드
ADD https://dtdg.co/latest-java-tracer dd-java-agent.jar

#8080포트 오픈
EXPOSE 8080

#앱 실행시 Datadog APM 연결
ENTRYPOINT ["java", "-javaagent:/app/dd-java-agent.jar", "-Ddd.service=bbegok", "-Ddd.env=prod", "-Ddd.version=1.0", "-Ddd.agent.host=host.docker.internal", "-Ddd.trace.agent.port=8126", "-jar", "app.jar"]