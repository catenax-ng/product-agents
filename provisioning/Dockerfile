#
# Docker buildfile for setting up the provisioning agent using an internal h2 database
# See copyright notice in the top folder
# See authors file in the top folder
# See license file in the top folder
#

FROM ontop/ontop-endpoint
WORKDIR /opt/ontop

# run with docker --build-arg=path_to_my_driver to establish a different driver
ARG jdbcDriver=repo/h2-2.1.214.jar 
COPY ${jdbcDriver} jdbc/

# Additional Java debugging options
ARG JAVA_TOOL_OPTIONS ""

# ontop uses this as the default port
EXPOSE 8080

# These are the environment variables pointing 
# to the mapping input
ENV ONTOLOGY=/input/ontology.ttl
ENV MAPPING=/input/mapping.obda
ENV SETTINGS=/input/settings.properties

# Run Ontop Agent with CORS disabled (we expect an ingress layer anyway)
# TODO check security settings
# TODO what about multiple "artifacts" = endpoints?
# TODO Telemetry, instrumentation and debugging

ENTRYPOINT java -cp ./lib/*:./jdbc/* -Dlogback.configurationFile=file:./log/logback.xml \
        it.unibz.inf.ontop.cli.Ontop endpoint \
        --ontology=${ONTOLOGY} \
        --mapping=${MAPPING} \
        --properties=${SETTINGS} \
        --dev \ 
        --cors-allowed-origins=* 