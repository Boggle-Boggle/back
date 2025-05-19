FROM arm64v8/openjdk:17-jdk-slim
#작업 디렉토리 설정
WORKDIR /app

#앱 JAR 복사
COPY build/libs/*.jar app.jar

RUN apt-get update || (cat /etc/apt/sources.list && exit 1)
RUN apt-get install -y curl
RUN curl -L -o dd-java-agent.jar https://dtdg.co/latest-java-tracer
RUN apt-get clean && rm -rf /var/lib/apt/lists/*

#8080포트 오픈
EXPOSE 8080

#앱 실행시 Datadog APM 연결
ENTRYPOINT ["java", "-javaagent:/app/dd-java-agent.jar", "-Ddd.service=bbegok", "-Ddd.env=prod", "-Ddd.version=1.0", "-Ddd.agent.host=datadog", "-Ddd.trace.agent.port=8126", "-jar", "app.jar"]