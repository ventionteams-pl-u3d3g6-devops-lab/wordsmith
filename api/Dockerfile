#use a debian image
FROM maven:3-eclipse-temurin-11-alpine as build

#set the working directory
WORKDIR /app

#copy the source code to the container
COPY . .

#build the program with Maven
RUN mvn clean verify

#stage 2
FROM maven:3-eclipse-temurin-11-alpine

#set the working directory
WORKDIR /app

#copy build jar from prev. stage
COPY --from=build /app/target/ .

#expose the port the app runs on
EXPOSE 8080

#command to run the application
CMD ["java", "-Xmx8m", "-Xms8m", "-jar", "words.jar"]