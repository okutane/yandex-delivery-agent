FROM maven:3.5-jdk-8-onbuild
CMD ["java","-jar","/usr/src/app/target/agent-1.0.0-SNAPSHOT.jar"]
