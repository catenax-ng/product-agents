FROM openjdk:17-jdk-alpine

COPY target/conforming-agent-*.jar /app/lib/

RUN addgroup -S -g 30000 agent \
    && adduser -h /app -S -u 10000 -G agent agent \
    && chown -R agent:agent /app

WORKDIR /app
VOLUME /tmp
USER agent

# TODO Reestablish the original health check
HEALTHCHECK NONE

EXPOSE 8080
ENTRYPOINT ["java","-cp","/app/lib/*", "io.catenax.knowledge.agents.conforming.Bootstrap"]