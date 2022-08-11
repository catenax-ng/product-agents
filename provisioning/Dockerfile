#
# Docker buildfile for setting up the provisioning agent using an internal h2 database
# See copyright notice in the top folder
# See authors file in the top folder
# See license file in the top folder
#

FROM ontop/ontop-endpoint:4.2.1
MAINTAINER Catena-X Team Knowledge Agents (https://catenax-ng.github.io/product-knowledge/)

# Security issue A1SLDT-828
RUN groupadd ontop && \
    useradd -g ontop -ms /bin/sh ontop && \
    chown -R ontop:ontop /opt/ontop && \
    mkdir /data && \
    chown -R ontop:ontop /data && \
    mkdir /input && \
    chown -R ontop:ontop /input && \
    mkdir /database && \
    chown -R ontop:ontop /database

WORKDIR /opt/ontop
USER ontop

# run with docker --build-arg=path_to_my_driver to establish a different driver
ARG jdbcDriver=repo/h2-2.1.214.jar 
COPY ${jdbcDriver} jdbc/

ARG sqlFiles=resources/dtc.sql
COPY ${sqlFiles} /data/

# Additional Java debugging options
ARG JAVA_TOOL_OPTIONS ""

# ontop uses this as the default port
EXPOSE 8080

# These are the environment variables pointing 
# to the mapping input      
ENV ONTOP_ONTOLOGY_FILE=/input/ontology.ttl
ENV ONTOP_MAPPING_FILE=/input/mapping.obda
ENV ONTOP_PROPERTIES_FILE=/input/settings.properties
ENV ONTOP_PORTAL_FILE=/input/portal.toml
ENV ONTOP_CORS_ALLOWED_ORIGINS=*
ENV ONTOP_DEV_MODE=true

# TODO Should have a healthcheck
HEALTHCHECK NONE

# Run Ontop Agent with CORS disabled (we expect an ingress layer anyway)
# TODO check security settings
# TODO what about multiple "artifacts" = endpoints?
# TODO Telemetry, instrumentation and debugging

ENTRYPOINT ["java", "-cp", "./lib/*:./jdbc/*","-Dlogback.configurationFile=file:./log/logback.xml", "it.unibz.inf.ontop.cli.Ontop","endpoint","--ontology=${ONTOP_ONTOLOGY_FILE}","--mapping=${ONTOP_MAPPING_FILE}","--properties=${ONTOP_PROPERTIES_FILE}","--portal","${ONTOP_PORTAL_FILE}","--dev","--cors-allowed-origins=${ONTOP_CORS_ALLOWED_ORIGINS}"]