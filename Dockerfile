FROM krmp-d2hub-idock.9rum.cc/goorm/gradle:8.2.1-jdk17

WORKDIR /home/gradle/project

COPY . .

WORKDIR /home/gradle/project/tily

RUN echo "systemProp.http.proxyHost=krmp-proxy.9rum.cc\nsystemProp.http.proxyPort=3128\nsystemProp.https.proxyHost=krmp-proxy.9rum.cc\nsystemProp.https.proxyPort=3128" > /root/.gradle/gradle.properties

RUN gradle init

RUN gradle wrapper

RUN ./gradlew clean build -x test

#COPY --from=build /home/gradle/project/build/libs/TILy-0.0.1-SNAPSHOT.jar .

#ENV DATABASE_URL=jdbc:mariadb://mariadb/krampoline

#CMD ["java", "-jar", "-Dspring.profiles.active=prod", "TILy-0.0.1-SNAPSHOT.jar"]