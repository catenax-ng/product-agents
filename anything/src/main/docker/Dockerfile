#
# Docker buildfile for setting up a lightweigth backend sparql source
# See copyright notice in the top folder
# See authors file in the top folder
# See license file in the top folder
#

FROM openjdk:11-jdk as run

COPY resources/xyz.sh /opt/xyz/bin/

ARG DOWNLOAD_URL=https://github.com/SPARQL-Anything/sparql.anything/releases/download/v0.8.1/sparql-anything-server-0.8.1.jar
ARG HTTP_PROXY=""
RUN if [ "${HTTP_PROXY}" != ""  ]; then \
        echo "Acquire::http::Proxy \"${HTTP_PROXY}\"" >> /etc/apt/apt.conf.d/proxy.conf; \
        echo "Acquire::https::Proxy \"${HTTP_PROXY}\"" >> /etc/apt/apt.conf.d/proxy.conf; \
    fi \
    && if [ "${HTTP_PROXY}" != ""  ]; then rm -f /etc/apt/apt.conf.d/proxy.conf; fi \
    && mkdir -p /opt/xyz/lib \
    && groupadd --system -g 30000 xyz \
    && useradd --base-dir /opt/xyz --system -u 10001 --gid xyz xyz \
    && wget -q "${DOWNLOAD_URL}" -O /opt/xyz/lib/sparql-anything-server.jar \
    && chown -R xyz:xyz /opt/xyz \
    && chmod +x /opt/xyz/bin/xyz.sh

EXPOSE 3000

USER xyz

WORKDIR /opt/xyz

# TODO Should have a healthcheck
HEALTHCHECK NONE

ENTRYPOINT ["bin/xyz.sh"]