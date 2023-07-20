#
# Docker buildfile for the conforming agent
# See copyright notice in the top folder
# See authors file in the top folder
# See license file in the top folder
#
FROM alpine:3.18.2
ARG JAR
ARG LIB

ARG APP_USER=agent
ARG APP_UID=10100
ARG APP_GID=30000

RUN apk update && \
    apk add openjdk17-jre-headless=17.0.7_p7-r1 --no-cache && \
    rm -rf /var/cache/apk/*

RUN addgroup --gid "$APP_GID" --system "$APP_USER"

RUN adduser \
     --shell /sbin/nologin \
     --disabled-password \
     --gecos "" \
     --ingroup "$APP_USER" \
     --no-create-home \
     --uid "$APP_UID" \
     "$APP_USER"

USER "$APP_USER"
WORKDIR /app
VOLUME /tmp

COPY target/conforming-agent-*.jar /app/lib/

# TODO implement wget or curl-based health check
HEALTHCHECK NONE

EXPOSE 8080

ENTRYPOINT ["java","-cp","/app/lib/*", "org.eclipse.tractusx.agents.conforming.Bootstrap"]