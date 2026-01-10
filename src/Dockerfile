# Use Java 21 JDK
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy Main.java
COPY Main.java .

# Compile Java
RUN javac Main.java

# Run the program
CMD ["java", "Main"]
