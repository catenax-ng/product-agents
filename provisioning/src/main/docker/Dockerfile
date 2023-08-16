#
# Docker buildfile for setting up the provisioning agent 
# with knowledge agents improvements and additional jdbc drivers 
# See copyright notice in the top folder
# See authors file in the top folder
# See license file in the top folder
#

##
# Build Container: Fixes diverse vulnerabilities in guava <32, tomcat, spring-boot 2.7<13, spring-framework <5.3.28 and spring-web (all 5 versions - need to exclude a deprecated package from the jar)
##

FROM openjdk:22-jdk-slim AS build


COPY target/provisioning-agent-*.jar /opt/ontop/lib/
COPY target/lib/guava-*.jar /opt/ontop/lib/
COPY target/lib/tomcat-embed-*.jar /opt/ontop/lib/
COPY target/lib/spring-*.jar /opt/ontop/lib/
COPY target/lib/guava-*.jar /opt/ontop/lib/
COPY target/lib/tomcat-embed-*.jar /opt/ontop/lib/
COPY target/lib/spring-*.jar /opt/ontop/lib/

WORKDIR /tmp

RUN  mv /opt/ontop/lib/spring-web-5.3.28.jar . && \
     jar xvf spring-web-5.3.28.jar && \
     rm -f spring-web-5.3.28.jar && \
     rm -rf org/springframework/remoting/httpinvoker && \
     jar cf /opt/ontop/lib/spring-web.jar *

FROM ontop/ontop:5.1.0

# Switch to root
USER root

# In case you need a debuggable image, please uncomment
#RUN rm -rf /opt/java/openjdk && apt-get -y upgrade && apt-get -y update && apt-get -y install openjdk-11-jdk

COPY resources/entrypoint.sh /opt/ontop/.

# run with docker --build-arg jdbcDrivers=path_to_my_driver to establish a different driver
ARG jdbcDrivers="https://repo1.maven.org/maven2/org/postgresql/postgresql/42.5.2/postgresql-42.5.2.jar https://repo1.maven.org/maven2/com/h2database/h2/2.2.220/h2-2.2.220.jar https://download.dremio.com/jdbc-driver/dremio-jdbc-driver-LATEST.jar https://repo1.maven.org/maven2/org/apache/calcite/avatica/avatica/1.22.0/avatica-1.22.0.jar"

RUN rm /opt/ontop/lib/guava-*.jar && \
    rm /opt/ontop/lib/tomcat-embed-*.jar && \
    rm /opt/ontop/lib/spring-*.jar && \
    mkdir -p /opt/ontop/jdbc && \
    for jdbcDriver in "$jdbcDrivers"; do wget --no-check-certificate -q -P /opt/ontop/jdbc ${jdbcDriver} ; done && \
    mkdir -p /opt/ontop/input && \
    mkdir -p /opt/ontop/database && \
    chmod 755 /opt/ontop/entrypoint.sh && \
    chown -R ontop:ontop /opt/ontop

COPY --from=build /opt/ontop/lib/*.jar /opt/ontop/lib/

# Switch back
USER ontop

# run with docker --build-arg sqlFiles=initial_sql to establish a different sql file
ARG sqlFiles=resources/dtc.sql
COPY ${sqlFiles} ./data/

# Additional Java debugging options
ARG JAVA_TOOL_OPTIONS ""

# ontop uses this as the default port, we need multiple ports if exposing multiple endpoints
EXPOSE 8080

# These are the environment variables pointing to the various settings
# To disable a setting simply leave it empty
# To enable different settings for multiple endpoints simply separate the options with a space
ENV ONTOP_PORT=8080
ENV ONTOP_ONTOLOGY_FILE=/opt/ontop/input/ontology.ttl
ENV ONTOP_MAPPING_FILE=/opt/ontop/input/mapping.obda
ENV ONTOP_PROPERTIES_FILE=/opt/ontop/input/settings.properties
ENV ONTOP_PORTAL_FILE=""
ENV ONTOP_CORS_ALLOWED_ORIGINS="*"
ENV ONTOP_DEV_MODE=true

# Original healthcheck only worked with one endpoint
HEALTHCHECK NONE

# Run Ontop Agents
ENTRYPOINT ["./entrypoint.sh"]

        