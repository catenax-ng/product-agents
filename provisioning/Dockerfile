#
# Docker buildfile for setting up the provisioning agent using an internal h2 database
# See copyright notice in the top folder
# See authors file in the top folder
# See license file in the top folder
#

FROM ontop/ontop-endpoint:4.2.1

#start script needs special root treatmnt
COPY resources/start.sh /opt/ontop/.

# Security issue A1SLDT-828
RUN groupadd ontop && \
    useradd -g ontop -ms /bin/sh ontop && \
    chown -R ontop:ontop /opt/ontop && \
    mkdir /data && \
    chown -R ontop:ontop /data && \
    mkdir /input && \
    chown -R ontop:ontop /input && \
    mkdir /database && \
    chown -R ontop:ontop /database && \
    chmod 755 /opt/ontop/start.sh

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

# These are the environment variables pointing to the mapping input      
ENV ONTOP_ONTOLOGY_FILE=/input/ontology.ttl
ENV ONTOP_MAPPING_FILE=/input/mapping.obda
ENV ONTOP_PROPERTIES_FILE=/input/settings.properties
ENV ONTOP_PORTAL_FILE=/input/portal.toml
ENV ONTOP_CORS_ALLOWED_ORIGINS="*"
ENV ONTOP_DEV_MODE=true
ENV ONTOP_PORT=8080

# Run Ontop Agent with CORS disabled (we expect an ingress layer anyway)
# TODO check security settings
# TODO Telemetry, instrumentation and debugging
ENTRYPOINT ["./start.sh"]

        