# MHRS Platform (Merkezi Hekim Randevu Sistemi)

Bu proje, mikroservis/modern mimari prensiplerine uygun olarak geliştirilmiş MHRS (Merkezi Hekim Randevu Sistemi) platformudur.

## Proje Mimarisi ve Teknoloji Yığını

- **Backend:** Java 21 & Spring Boot 3.3+ (REST API, JPA/Hibernate, Redis Cache)
- **Frontend:** Next.js (TypeScript, App Router, Modern UI)
- **Veritabanı:** PostgreSQL 16
- **Önbellek (Cache):** Redis 7
- **Konteynerizasyon:** Docker & Docker Compose
- **Orkestrasyon:** Kubernetes (k8s)

---

## Dosya ve Klasör Yapısı

```text
mhrs-platform/
│
├── docker-compose.yml           # Veritabanı, Redis ve servisleri ayağa kaldıran ana dosya
├── init.sql                     # Veritabanı ilk kurulum SQL scripti
├── .gitignore                   # Git dışı bırakılacak dosyalar (node_modules, target vb.)
├── README.md                    # Proje çalıştırma dokümantasyonu
│
├── mhrs-backend/                # Spring Boot (Java 21) Projesi
│   ├── pom.xml                  # Maven bağımlılıkları
│   └── src/
│       ├── main/
│       │   ├── java/com/mhrs/
│       │   └── resources/       # application.yml
│       └── test/
│
├── mhrs-frontend/               # Next.js (TypeScript) Projesi
│   ├── package.json
│   ├── tsconfig.json
│   └── src/
│       ├── app/
│       └── components/
│
└── k8s/                         # Kubernetes Manifest Dosyaları
    ├── postgres.yaml
    ├── redis.yaml
    ├── backend.yaml
    └── frontend.yaml
```

---

## Hızlı Başlangıç (Docker Compose ile)

Tüm servisleri (PostgreSQL, Redis, Spring Boot Backend ve Next.js Frontend) tek komutla ayağa kaldırmak için:

```bash
docker-compose up --build -d
```

### Servis Bağlantı Adresleri:
- **Frontend UI:** http://localhost:3000
- **Backend REST API:** http://localhost:8080/api
- **PostgreSQL:** `localhost:5432` (Kullanıcı: `mhrs_user`, Şifre: `mhrs_password`, DB: `mhrs_db`)
- **Redis:** `localhost:6379`

Servisleri durdurmak için:
```bash
docker-compose down
```

---

## Yerel Geliştirme (Local Development)

### 1. Backend (Spring Boot - Java 21)
```bash
cd mhrs-backend
mvn clean install
mvn spring-boot:run
```

### 2. Frontend (Next.js - TypeScript)
```bash
cd mhrs-frontend
npm install
npm run dev
```

---

## Kubernetes (k8s) Kurulumu

Manifest dosyalarını Kubernetes kümenize dağıtmak için:

```bash
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/redis.yaml
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml
```
