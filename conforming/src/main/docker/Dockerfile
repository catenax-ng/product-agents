FROM openjdk:17-jdk-alpine

WORKDIR /app
VOLUME /tmp
COPY target/conforming-agent-*.jar /app/lib/

RUN groupadd --system -g 30000 agent \
    && useradd --base-dir /app --system -u 10000 --gid agent agent \
    && chown -R agent:agent /app

USER agent

# TODO Reestablish the original health check
HEALTHCHECK NONE

EXPOSE 8080
ENTRYPOINT ["java","-cp","/app/lib/*", "io.catenax.knowledge.agents.conforming.Bootstrap"]