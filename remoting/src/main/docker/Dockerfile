#
# Docker buildfile for setting up the remoting agent 
# See copyright notice in the top folder
# See authors file in the top folder
# See license file in the top folder
#

##
# Build Container: Fixes diverse vulnerabilities in guava <32, netty, jetty, spring-framework <5.3.28 and spring-web (all 5 versions - need to exclude a deprecated package from the jar)
##

FROM openjdk:22-jdk-slim AS build

COPY target/original-remoting-agent-*-SNAPSHOT.jar /opt/lib/
COPY target/lib/guava-*.jar /opt/lib/
COPY target/lib/netty-*.jar /opt/lib/
COPY target/lib/jetty-*.jar /opt/lib/
COPY target/lib/spring-*.jar /opt/lib/
COPY target/lib/httpmime-*.jar /opt/lib/

COPY resources/web/logging.properties /opt/conf/
COPY resources/web/server.xml /opt/conf/
COPY resources/web/web.xml /opt/conf/
COPY resources/web/tomcat-users.xml /opt/conf/

WORKDIR /tmp

RUN  mv /opt/lib/spring-web-5.3.28.jar . && \
     jar xvf spring-web-5.3.28.jar && \
     rm -f spring-web-5.3.28.jar && \
     rm -rf org/springframework/remoting/httpinvoker && \
     jar cf /opt/lib/spring-web.jar *

##
# Runtime Container: Currently based on Tomcat 9 (and the "old" Java Servlet API until RDF4J upgrades)
##

FROM tomcat:9-jre11-temurin

ENV JAVA_OPTS="-Xmx2g"
ENV CATALINA_OPTS="-Dorg.eclipse.rdf4j.appdata.basedir=/var/rdf4j"
# Additional Java debugging options
ARG JAVA_TOOL_OPTIONS ""

# TODO Should have a healthcheck
HEALTHCHECK NONE

ARG HTTP_PROXY=""
RUN if [ "${HTTP_PROXY}" != ""  ]; then \
        echo "Acquire::http::Proxy \"${HTTP_PROXY}\"" >> /etc/apt/apt.conf.d/proxy.conf; \
        echo "Acquire::https::Proxy \"${HTTP_PROXY}\"" >> /etc/apt/apt.conf.d/proxy.conf; \
    fi && \
    apt-get -y upgrade && \
    apt-get -y update && \
    apt-get -y --no-install-recommends install unzip=6.0-26ubuntu3 && \
    addgroup --system --gid 30000 tomcat && \
    adduser --disabled-password --gecos "" --ingroup tomcat --uid 10001 tomcat && \
    rm -rf /usr/local/tomcat/webapps/* && \
    if [ "${HTTP_PROXY}" != ""  ]; then rm -f /etc/apt/apt.conf.d/proxy.conf; fi && \
    wget -q -O /tmp/rdf4j.zip "https://rhlx01.hs-esslingen.de/pub/Mirrors/eclipse/rdf4j/eclipse-rdf4j-4.3.2-sdk.zip" && \
    unzip -d /tmp /tmp/rdf4j.zip eclipse-rdf4j-4.3.2/war/rdf4j-server.war  eclipse-rdf4j-4.3.2/war/rdf4j-workbench.war && \
    rm /tmp/rdf4j.zip

RUN	mkdir -p /var/rdf4j/server/conf && \
    mkdir -p /var/rdf4j/server/repositories && \
    unzip /tmp/eclipse-rdf4j-4.3.2/war/rdf4j-server.war -d /usr/local/tomcat/webapps/rdf4j-server && \
	unzip /tmp/eclipse-rdf4j-4.3.2/war/rdf4j-workbench.war -d /usr/local/tomcat/webapps/rdf4j-workbench && \
    rm -rf /tmp/eclipse-rdf4j-4.3.2 && \
    rm /usr/local/tomcat/webapps/rdf4j-server/WEB-INF/lib/guava-*.jar && \
    rm /usr/local/tomcat/webapps/rdf4j-server/WEB-INF/lib/netty-*.jar && \
    rm /usr/local/tomcat/webapps/rdf4j-server/WEB-INF/lib/jetty-*.jar && \
    rm /usr/local/tomcat/webapps/rdf4j-server/WEB-INF/lib/spring-*.jar && \
    rm /usr/local/tomcat/webapps/rdf4j-server/WEB-INF/lib/snappy-*.jar && \
    rm /usr/local/tomcat/webapps/rdf4j-workbench/WEB-INF/lib/guava-*.jar && \
    rm /usr/local/tomcat/webapps/rdf4j-workbench/WEB-INF/lib/netty-*.jar && \
    rm /usr/local/tomcat/webapps/rdf4j-workbench/WEB-INF/lib/jetty-*.jar && \
    rm /usr/local/tomcat/webapps/rdf4j-workbench/WEB-INF/lib/snappy-*.jar && \
    rm /usr/local/tomcat/conf/logging.properties && \
    rm /usr/local/tomcat/conf/server.xml && \
    rm /usr/local/tomcat/conf/tomcat-users.xml && \
    rm /usr/local/tomcat/conf/web.xml && \
    wget -q "https://repo1.maven.org/maven2/commons-logging/commons-logging/1.2/commons-logging-1.2.jar" -O /usr/local/tomcat/webapps/rdf4j-server/WEB-INF/lib/commons-logging-1.2.jar && \
    wget -q "https://repo1.maven.org/maven2/org/xerial/snappy/snappy-java/1.1.10.2/snappy-java-1.1.10.2.jar" -O /usr/local/tomcat/webapps/rdf4j-server/WEB-INF/lib/snappy-java-1.1.10.2.jar && \
    cp /usr/local/tomcat/webapps/rdf4j-server/WEB-INF/lib/snappy-java-1.1.10.2.jar /usr/local/tomcat/webapps/rdf4j-workbench/WEB-INF/lib/. && \
    chown -R tomcat:tomcat /var/rdf4j /usr/local/tomcat && \
	chmod 775 /usr/local/tomcat /usr/local/tomcat/bin /usr/local/tomcat/bin/catalina.sh /var/rdf4j/server 

COPY --from=build /opt/lib/*.jar /usr/local/tomcat/webapps/rdf4j-server/WEB-INF/lib/
COPY --from=build /opt/lib/*.jar /usr/local/tomcat/webapps/rdf4j-workbench/WEB-INF/lib/
COPY --from=build /opt/conf/*.xml /opt/conf/*.properties /usr/local/tomcat/conf/
COPY resources/web/rdf4j.xml /usr/local/tomcat/webapps/rdf4j-server/WEB-INF/web.xml
COPY resources/web/callback.xml /usr/local/tomcat/webapps/rdf4j-server/WEB-INF/callback.xml

USER tomcat

WORKDIR /usr/local/tomcat/

EXPOSE 8081

