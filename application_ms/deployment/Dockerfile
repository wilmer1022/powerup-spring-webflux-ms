FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY build/libs/*.jar Application.jar
ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
RUN adduser -D -s /bin/sh appuser
USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar Application.jar" ]