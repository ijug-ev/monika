FROM gcr.io/distroless/java17-debian12
COPY helloworld-1.0-SNAPSHOT.jar /home
RUN [ "java", "-XX:ArchiveClassesAtExit=helloworld.jsa", "-jar", "/home/helloworld-1.0-SNAPSHOT.jar", "--stop" ]
RUN [ "java", "-XX:ArchiveClassesAtExit=healthcheck.jsa", "-jar", "/home/helloworld-1.0-SNAPSHOT.jar", "--check-health", "--ignore-errors" ]
EXPOSE 8080
ENTRYPOINT [ "java", "-XX:SharedArchiveFile=helloworld.jsa", "-jar", "/home/helloworld-1.0-SNAPSHOT.jar" ]
HEALTHCHECK CMD [ "java", "-XX:SharedArchiveFile=healthcheck.jsa", "-jar", "/home/helloworld-1.0-SNAPSHOT.jar", "--check-health" ]