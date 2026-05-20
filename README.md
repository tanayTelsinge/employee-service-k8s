# employee-service-k8s

A production-style Employee Management REST API built with Spring Boot, deployed as a Kubernetes-native service on K3s with Prometheus metrics and Grafana dashboards.

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                    K3s Cluster                       │
│                                                     │
│  ┌──────────────────┐     ┌─────────────────────┐   │
│  │  employee-service │────▶│     postgres        │   │
│  │  (Spring Boot)    │     │  (PostgreSQL 15)    │   │
│  │  :8080            │     │  :5432              │   │
│  └────────┬─────────┘     └─────────────────────┘   │
│           │ /actuator/prometheus                     │
│           ▼                                         │
│  ┌──────────────────┐     ┌─────────────────────┐   │
│  │   Prometheus     │────▶│     Grafana          │   │
│  │   :9090          │     │     :3000            │   │
│  └──────────────────┘     └─────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.x |
| Language | Java 25 |
| Database | PostgreSQL 15 |
| Migrations | Flyway |
| ORM | Spring Data JPA + Hibernate |
| Mapping | MapStruct |
| Security | Spring Security |
| Metrics | Micrometer + Prometheus |
| Containerization | Docker |
| Orchestration | K3s (Lightweight Kubernetes) |
| Monitoring | Prometheus + Grafana |
| Build | Gradle |

---

## Prerequisites

- Docker Desktop
- WSL2 with Ubuntu
- K3s installed on WSL2
- kubectl
- Helm 3

---

## Project Structure

```
employee-service-k8s/
├── src/
│   └── main/
│       ├── java/com/tanay/employee/
│       │   ├── config/          # Security config
│       │   ├── controller/      # REST controllers
│       │   ├── dto/             # Request/Response DTOs
│       │   ├── exception/       # Global exception handling
│       │   ├── mapper/          # MapStruct mappers
│       │   ├── model/           # JPA entities
│       │   ├── repository/      # Spring Data repositories
│       │   └── service/         # Business logic
│       └── resources/
│           ├── db/migration/    # Flyway SQL migrations
│           └── application.yml
├── k8s/
│   ├── namespace.yaml
│   ├── secret.yaml
│   ├── configmap.yaml
│   ├── postgres.yaml
│   ├── deployment.yaml
│   ├── service.yaml
│   └── servicemonitor.yaml
├── Dockerfile
├── docker-compose.yml
└── build.gradle
```

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/employees` | List all employees (paginated) |
| GET | `/api/v1/employees/{id}` | Get employee by ID |
| POST | `/api/v1/employees` | Create employee |
| PUT | `/api/v1/employees/{id}` | Update employee |
| DELETE | `/api/v1/employees/{id}` | Soft delete employee |

### Sample Request

```bash
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Tanay",
    "lastName": "Telsinge",
    "email": "tanay@example.com",
    "department": "Engineering",
    "designation": "Senior SWE",
    "salary": 100000
  }'
```

### Sample Response

```json
{
  "id": 1,
  "firstName": "Tanay",
  "lastName": "Telsinge",
  "email": "tanay@example.com",
  "department": "Engineering",
  "designation": "Senior SWE",
  "salary": 100000,
  "createdAt": "2026-05-20T21:43:05",
  "updatedAt": "2026-05-20T21:43:05",
  "isActive": true
}
```

---

## Running Locally

### Option 1 — Docker Compose

```bash
# Build and start all services
docker-compose up --build

# Test
curl http://localhost:8080/api/v1/employees
```

### Option 2 — IntelliJ

```bash
# Start PostgreSQL
docker run --name employeedb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=employeedb \
  -p 5432:5432 \
  -d postgres:15

# Run EmployeeServiceApplication from IntelliJ
# Add VM option: -Duser.timezone=UTC
```

---

## Deploying to K3s

### 1. Install K3s (WSL2)

```bash
curl -sfL https://get.k3s.io | sh -
export KUBECONFIG=/etc/rancher/k3s/k3s.yaml
```

### 2. Build and load image

```bash
# Build image
docker build -t employee-service:latest .

# Load into K3s
docker save employee-service:latest -o /tmp/employee-service.tar
sudo k3s ctr images import /tmp/employee-service.tar
```

### 3. Apply manifests

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

### 4. Verify

```bash
kubectl get pods -n employee-app

# Test API
curl http://localhost:30080/api/v1/employees
```

---

## Prometheus + Grafana

### Install via Helm

```bash
# Add repos
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

# Install Prometheus
helm install prometheus prometheus-community/prometheus \
  --namespace monitoring \
  --create-namespace \
  --set nodeExporter.enabled=false

# Install Grafana
helm install grafana grafana/grafana \
  --namespace monitoring
```

### Access

```bash
# Prometheus
kubectl port-forward svc/prometheus-server 9090:80 -n monitoring
# Open http://localhost:9090

# Grafana
kubectl port-forward svc/grafana 3000:80 -n monitoring
# Open http://localhost:3000
# Username: admin
# Password: kubectl get secret grafana -n monitoring -o jsonpath="{.data.admin-password}" | base64 --decode
```

### Grafana Dashboard

Import dashboard ID **4701** (JVM Micrometer) and select Prometheus as data source.

Metrics available:
- HTTP request rate
- Request duration (avg, max)
- JVM heap usage
- GC activity
- Thread count

---

## Key Design Decisions

**Soft Delete** — employees are never hard deleted. `is_active = false` flags them as deleted. All queries filter by `is_active = true`.

**DTO Pattern** — `EmployeeRequest` and `EmployeeResponse` separate API contract from DB entity. Service layer works with entities — each consumer (REST, Kafka, scheduler) maps to their own DTO.

**Flyway** — all schema changes are versioned SQL migrations. Never use `ddl-auto: update` in production.

**MapStruct** — compile-time mapper generation. No reflection, no runtime overhead.

**Constructor Injection** — all dependencies injected via constructor (`@RequiredArgsConstructor`). No field injection.

---

## Metrics Endpoint

```bash
curl http://localhost:30080/actuator/prometheus
```

Returns metrics in Prometheus exposition format scraped every 15 seconds.