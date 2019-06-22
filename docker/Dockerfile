FROM openjdk:8-jre

ADD ccu-historian-bin.zip /tmp/

RUN apt-get update
RUN apt-get install unzip

RUN mkdir /opt/ccu-historian
RUN mkdir /opt/ccu-historian/data
RUN mkdir /opt/ccu-historian/backup

RUN unzip /tmp/ccu-historian-bin.zip -d /opt/ccu-historian

EXPOSE 80 2098 2099 8082 9092 5435

WORKDIR /opt/ccu-historian

CMD ["java", "-jar", "ccu-historian.jar"]
