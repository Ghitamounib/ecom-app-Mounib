# Start Docker Containers
Write-Host "Starting Kafka Docker Containers..."
docker-compose up -d

# Wait for Kafka
Start-Sleep -Seconds 15

# Start Spring Cloud Stream App
Write-Host "Starting Kafka Ecom App..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "mvn spring-boot:run"

Write-Host "App started. Check README.md for test commands."
