FROM ubuntu:22.04
WORKDIR /app

RUN apt-get update
RUN apt-get install -y openjdk-17-jdk
RUN apt-get install -y maven

CMD ["mvn", "clean", "compile", "package", "exec:java"]
