#!/bin/bash

# WireMock Standalone Setup Script
# Downloads and runs WireMock standalone with your Finacle API mocks

set -e

echo "🚀 WireMock Standalone Setup"
echo "===================================="

PROJECT_DIR="/home/pramodgautam/IdeaProjects/HelloWorld"
WIREMOCK_VERSION="3.3.1"
WIREMOCK_JAR="wiremock-standalone-${WIREMOCK_VERSION}.jar"
WIREMOCK_URL="https://repo1.maven.org/maven2/org/wiremock/wiremock-standalone/${WIREMOCK_VERSION}/${WIREMOCK_JAR}"

cd "$PROJECT_DIR"

# Create __files and mappings directories
mkdir -p __files
mkdir -p mappings

echo "📥 Checking for WireMock standalone JAR..."

if [ ! -f "$WIREMOCK_JAR" ]; then
    echo "⬇️  Downloading WireMock ${WIREMOCK_VERSION}..."
    curl -o "$WIREMOCK_JAR" "$WIREMOCK_URL"
    if [ $? -eq 0 ]; then
        echo "✅ Downloaded successfully"
    else
        echo "❌ Failed to download WireMock"
        exit 1
    fi
else
    echo "✅ WireMock JAR already exists"
fi

echo ""
echo "✅ Setup complete!"
echo ""
echo "To start WireMock server, run:"
echo "  java -jar $WIREMOCK_JAR --port 8080"
echo ""
echo "Or with mappings from file:"
echo "  java -jar $WIREMOCK_JAR --port 8080 --mappings-reset-on-startup"
echo ""
echo "Access the API at: http://localhost:8080"
echo "Admin console: http://localhost:8080/__admin"

