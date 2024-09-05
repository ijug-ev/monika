FROM gcr.io/distroless/java21-debian12:nonroot
COPY monika-1.0-SNAPSHOT.jar /home
RUN [ "java", "-XX:ArchiveClassesAtExit=monika.jsa", "-jar", "/home/monika-1.0-SNAPSHOT.jar", "--stop" ]
RUN [ "java", "-XX:ArchiveClassesAtExit=healthcheck.jsa", "-jar", "/home/monika-1.0-SNAPSHOT.jar", "--check-health", "--ignore-errors" ]
EXPOSE 8080
ENTRYPOINT [ "java", "-XX:SharedArchiveFile=monika.jsa", "-jar", "/home/monika-1.0-SNAPSHOT.jar" ]
HEALTHCHECK CMD [ "java", "-XX:SharedArchiveFile=healthcheck.jsa", "-jar", "/home/monika-1.0-SNAPSHOT.jar", "--check-health" ]
