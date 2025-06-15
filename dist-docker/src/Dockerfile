# The latest usable version of the base image Eclipse Temurin (Java Runtime Environment) is 17.
# The following conditions must be met: Support for arm32v7 and LTS (Long-Term Support).
FROM eclipse-temurin:17-jre

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
