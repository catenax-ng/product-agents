#
# Docker buildfile for setting up the remoting agent 
# See copyright notice in the top folder
# See authors file in the top folder
# See license file in the top folder
#

# see https://github.com/eclipse/rdf4j/blob/main/docker/Dockerfile
FROM eclipse/rdf4j-workbench:latest
MAINTAINER Catena-X Team Knowledge Agents (https://catenax-ng.github.io/product-knowledge/)

# Additional Java debugging options
ARG JAVA_TOOL_OPTIONS ""

# TODO Should have a healthcheck
HEALTHCHECK NONE

# unpack the war
RUN /bin/unzip /usr/local/tomcat/webapps/rdf4j-server.war -d /usr/local/tomcat/webapps/rdf4j-server 
RUN rm /usr/local/tomcat/webapps/rdf4j-server.war

COPY target/agents.remoting-0.5.2-SNAPSHOT.jar /usr/local/tomcat/webapps/rdf4j-server/lib/.
