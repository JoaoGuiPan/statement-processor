FROM gradle:8.7.0-jdk21-alpine AS build
COPY --chown=gradle:gradle . /home/app
WORKDIR /home/app
RUN gradle build

FROM amazoncorretto:21
RUN mkdir -p /app
WORKDIR /app
COPY --from=build /home/app/build/libs/*.jar ./app.jar
EXPOSE $PORT
CMD [ "java", "-jar", "./app.jar" ]