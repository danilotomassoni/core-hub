#!/usr/bin/env bash

set -Eeuo pipefail

ROOT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$ROOT_DIR/.logs"

mkdir -p "$LOG_DIR"

if ! command -v java >/dev/null 2>&1; then
  echo "Erro: Java não está instalado ou não está no PATH."
  exit 1
fi

if ! command -v docker >/dev/null 2>&1; then
  echo "Erro: Docker não está instalado ou não está no PATH."
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "Erro: o comando 'docker compose' não está disponível."
  exit 1
fi

wait_for_port() {
  local host="$1"
  local port="$2"
  local attempts=60

  echo "Aguardando $host:$port..."
  until (echo >/dev/tcp/"$host"/"$port") >/dev/null 2>&1; do
    attempts=$((attempts - 1))
    if (( attempts == 0 )); then
      echo "Erro: $host:$port não respondeu a tempo."
      exit 1
    fi
    sleep 1
  done
}

pids=()

start_service() {
  local service="$1"

  echo "Iniciando $service..."
  (
    cd "$ROOT_DIR/$service"
    ./mvnw spring-boot:run
  ) >"$LOG_DIR/$service.log" 2>&1 &
  pids+=("$!")
}

cleanup() {
  echo
  echo "Encerrando serviços Spring..."
  for pid in "${pids[@]}"; do
    kill "$pid" 2>/dev/null || true
  done
  wait 2>/dev/null || true
}

trap cleanup INT TERM EXIT

echo "Subindo infraestrutura local com Docker Compose..."
docker compose -f "$ROOT_DIR/docker-compose.yaml" up -d

wait_for_port localhost 5432
wait_for_port localhost 5433
wait_for_port localhost 5434
wait_for_port localhost 5672

start_service discovery
wait_for_port localhost 8761

start_service user-service
start_service auth-service
start_service product-service
start_service gateway

echo
echo "Todos os serviços foram iniciados."
echo "Gateway: http://localhost:8000"
echo "Eureka:  http://localhost:8761"
echo "RabbitMQ UI: http://localhost:15672 (guest/guest)"
echo "PGAdmin: http://localhost:5050"
echo "Logs: $LOG_DIR"
echo "Pressione Ctrl+C para encerrar os serviços Spring."

wait