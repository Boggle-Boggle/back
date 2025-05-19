FROM arm64v8/openjdk:17-jdk-slim
#작업 디렉토리 설정
WORKDIR /app

#앱 JAR 복사
COPY build/libs/*.jar app.jar

#8080포트 오픈
EXPOSE 8080

#DD JAR 복사
COPY dd-java-agent.jar /app/dd-java-agent.jar

# 앱 실행 (Datadog APM 연동 포함)
ENTRYPOINT ["java", \
  "-javaagent:/app/dd-java-agent.jar", \
  "-Ddd.service=bbegok", \
  "-Ddd.env=prod", \
  "-Ddd.version=1.0", \
  "-Ddd.agent.host=datadog", \
  "-Ddd.trace.agent.port=8126", \
  "-jar", "app.jar"]