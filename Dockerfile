FROM maven:3.9.6-amazoncorretto-21 as build
LABEL authors="christian"
COPY . /bookmarks
WORKDIR /bookmarks
RUN mvn package
VOLUME /root/.m2

FROM amazoncorretto:21.0.2-al2023-headless
COPY --from=build /bookmarks/target/bookmarks-latest.jar /
ENTRYPOINT ["java", "-jar", "/bookmarks-latest.jar"]
