#!/bin/bash

# WireMock Server Startup Script
# This script builds and runs the WireMock server

set -e

echo "💫 WireMock Finacle API Mock Server"
echo "===================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

PROJECT_DIR="/home/pramodgautam/IdeaProjects/HelloWorld"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java is not installed${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Java version:${NC}"
java -version

cd "$PROJECT_DIR"

echo -e "\n${YELLOW}📦 Building project...${NC}"
mvn clean package -DskipTests -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo -e "\n${YELLOW}🚀 Starting WireMock Server...${NC}"
echo -e "${GREEN}Server will run on http://localhost:8080${NC}"
echo -e "${GREEN}Admin API: http://localhost:8080/__admin${NC}"
echo ""

java -cp target/wiremock-server.jar com.example.FinacleWireMockServer

