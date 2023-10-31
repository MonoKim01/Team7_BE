FROM gradle-8.2.1-jdk11 as build

WORKDIR /home/gradle/project

COPY . .

RUN echo "systemProp.http.proxyHost=krmp-proxy.9rum.cc\nsystemProp.http.proxyPort=3128\nsystemProp.https.proxyHost=krmp-proxy.9rum.cc\nsystemProp.https.proxyPort=3128" > /root/.gradle/gradle.properties

RUN gradle clean build

FROM eclipse-temurin:11

COPY --from=build /home/gradle/project/build/libs/TILy-0.0.1-SNAPSHOT.jar .

ENV DATABASE_URL=jdbc:mariadb://localhost:3306/TILy

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "TILy-0.0.1-SNAPSHOT.jar"]