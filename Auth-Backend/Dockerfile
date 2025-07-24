# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-21 as build

# Install JDK 24 manually
RUN apt-get update && \
    apt-get install -y wget && \
    wget https://download.oracle.com/java/24/latest/jdk-24_linux-x64_bin.tar.gz && \
    mkdir -p /opt/jdk-24 && \
    tar -xzf jdk-24_linux-x64_bin.tar.gz -C /opt/jdk-24 --strip-components=1 && \
    rm jdk-24_linux-x64_bin.tar.gz

ENV JAVA_HOME=/opt/jdk-24
ENV PATH=$JAVA_HOME/bin:$PATH

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

# ---- Runtime Stage ----
FROM eclipse-temurin:24-jdk

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
