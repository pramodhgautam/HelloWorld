pipeline {
    agent any

    environment {
        IMAGE_NAME = "hello-world-java"
        IMAGE_TAG = "latest"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'git@github.com:pramodhgautam/HelloWorld.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                  docker build -t $IMAGE_NAME:$IMAGE_TAG .
                '''
            }
        }

        stage('Deploy Container') {
            steps {
                sh '''
                  docker rm -f hello-world || true
                  docker run -d \
                    --name hello-world \
                    -p 8081:8081 \
                    --restart unless-stopped \
                    $IMAGE_NAME:$IMAGE_TAG
                '''
            }
        }
    }
}
