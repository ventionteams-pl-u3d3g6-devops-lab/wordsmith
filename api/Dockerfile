#use a debian image
FROM debian:latest

#install Java and Maven
RUN apt-get update && apt-get install -y default-jre maven

#set the working directory
WORKDIR /app

#copy the source code to the container
COPY . /app

#build the program with Maven
RUN mvn verify

#expose the port the app runs on
EXPOSE 8080

#command to run the application
CMD ["java", "-Xmx8m", "-Xms8m", "-jar", "target/words.jar"]