FROM openjdk:22
COPY target/productShipping-0.0.1-SNAPSHOT.jar productShipping-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/productShipping-0.0.1-SNAPSHOT.jar" ]