# Use Java 21 JDK
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy Main.java
COPY src/Main.java .

# Compile Java
RUN javac Main.java

EXPOSE 8081

# Run the program
CMD ["java", "Main"]
