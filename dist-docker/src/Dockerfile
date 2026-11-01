# Base image Eclipse Temurin (Java Runtime Environment), pinned to the Ubuntu Noble
# (24.04 LTS) variant. Requirements: LTS support, arm32v7 and riscv64. The unsuffixed
# tag switches the Ubuntu base with every LTS release and loses riscv64 since
# Ubuntu Resolute (26.04, requires the not yet existing RVA23 hardware profile).
FROM eclipse-temurin:17-jre-noble

ENV TZ=Europe/Berlin
ENV CONFIG_JAVA_OPTS="-Duser.timezone=Europe/Berlin -Duser.language=de -Duser.country=DE"

VOLUME ["/opt/ccu-historian/config", "/database"]

COPY ccu-historian/build /opt/ccu-historian
COPY dist-docker/src/entrypoint.sh /usr/local/bin

WORKDIR /opt/ccu-historian
ENTRYPOINT ["entrypoint.sh"]

EXPOSE 80 2098 2099 8082 9092 5435

HEALTHCHECK --interval=1m --timeout=3s \
  CMD curl -f http://localhost/historian || exit 1
