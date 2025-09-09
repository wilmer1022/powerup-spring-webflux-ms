FROM gradle:jdk17-corretto AS build
WORKDIR /opt/app
COPY . /opt/app
RUN gradle clean
RUN gradle bootJar

FROM eclipse-temurin:17-jre AS runtime

ARG USERNAME=user
ARG USER_UID=999
ARG USER_GID=$USER_UID


WORKDIR /opt/app

COPY --from=build /opt/app/applications/app-service/build/libs/*.jar /opt/app/app.jar
COPY applications/app-service/aes_512_key.bin /opt/app/aes_512_key.bin

RUN groupadd --gid "$USER_GID" "$USERNAME" \
    && useradd --uid "$USER_UID" --gid "$USER_GID" -m "$USERNAME" \
    && chown -R "$USER_UID:$USER_GID" "/opt/app"

USER user


ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
EXPOSE 8080
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]