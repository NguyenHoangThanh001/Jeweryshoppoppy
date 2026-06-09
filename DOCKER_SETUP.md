# 🐳 JeweryShopPoppy — Docker Setup Guide

## Prerequisites

Make sure you have installed:
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (includes Docker Compose)
- Git

> **RAM requirement:** SQL Server needs at least **2 GB RAM** allocated to Docker. In Docker Desktop → Settings → Resources, set Memory to 2 GB or more.

---

## Project Structure (after adding Docker files)

```
Jeweryshoppoppy/
├── docker-compose.yml          ← Orchestrates all 3 services
├── .env.example                ← Copy to .env and fill in secrets
├── create-jewelry-db.sql       ← Auto-imported into SQL Server
│
├── Jewelry-Store/              ← Spring Boot backend
│   ├── Dockerfile
│   └── .dockerignore
│
└── Front end offical file/     ← JavaScript frontend
    ├── Dockerfile
    ├── nginx.conf
    └── .dockerignore
```

---

## Quick Start

### 1. Clone the repository
```bash
git clone https://github.com/NguyenHoangThanh001/Jeweryshoppoppy.git
cd Jeweryshoppoppy
```

### 2. Copy the Docker files into place

Place the provided files as shown in the structure above.

### 3. Create your `.env` file
```bash
cp .env.example .env
```

Then open `.env` and fill in your values:
```env
DB_NAME=JewelryDB
DB_PASSWORD=YourStrong@Password123   # Must be 8+ chars, mixed case + number + symbol
VNPAY_TMN_CODE=your_tmn_code
VNPAY_HASH_SECRET=your_hash_secret
```

### 4. Configure Spring Boot for Docker

Add a `src/main/resources/application-docker.properties` to your backend:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

### 5. Start everything
```bash
docker compose up --build
```

On first run this will:
- Pull SQL Server 2022 image (~1.5 GB)
- Build the Spring Boot JAR
- Build the frontend with Node
- Start all 3 containers in the right order

---

## Accessing the App

| Service   | URL                          |
|-----------|------------------------------|
| Frontend  | http://localhost              |
| Backend API | http://localhost:8080       |
| SQL Server | localhost:1433 (SA user)    |

---

## Useful Commands

```bash
# Start in background
docker compose up -d

# View live logs
docker compose logs -f

# View logs for one service only
docker compose logs -f backend

# Stop everything
docker compose down

# Stop and delete ALL data (wipes the database!)
docker compose down -v

# Rebuild after code changes
docker compose up --build backend
```

---

## Troubleshooting

**SQL Server won't start?**
- Increase Docker memory to at least 2 GB in Docker Desktop settings.

**Backend fails to connect to DB?**
- SQL Server takes ~30s to initialize. The backend will retry automatically.
- Check logs: `docker compose logs sqlserver`

**Frontend shows blank page?**
- If your project has no `npm run build` script (plain HTML/JS), update the frontend Dockerfile to skip the build step and copy files directly.
- Check: `docker compose logs frontend`

**Port already in use?**
- Change the host port in `docker-compose.yml`, e.g. `"8081:8080"` for the backend.

---

## Stopping & Cleanup

```bash
# Stop containers (keeps data)
docker compose down

# Full reset including database volume
docker compose down -v
docker volume rm jewelry_sqlserver_data
```
