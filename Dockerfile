FROM maven:3.9.6-amazoncorretto-21 as build
LABEL authors="christian"
VOLUME /root/.m2
WORKDIR /bookmarks
COPY . /bookmarks
RUN mvn package

FROM amazoncorretto:21.0.2-al2023-headless
COPY --from=build /bookmarks/target/bookmarks-latest.jar /
ENTRYPOINT ["java", "-jar", "/bookmarks-latest.jar"]
