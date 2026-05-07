# Use Java 21 JDK
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy Main.java
<<<<<<< Updated upstream
COPY src/main/Main.java .
=======
COPY src/main/java/Main.java .
>>>>>>> Stashed changes

# Compile Java
RUN javac Main.java

EXPOSE 8081

# Run the program
CMD ["java", "Main"]
