-- UUID üretimi için eklenti
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Kullanıcılar Tablosu
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tckn_hash VARCHAR(64) NOT NULL UNIQUE,
    masked_tckn VARCHAR(11) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'ROLE_CITIZEN',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Hastaneler Tablosu
CREATE TABLE IF NOT EXISTS hospitals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(150) NOT NULL,
    city_code INT NOT NULL,
    district_code INT NOT NULL,
    address TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Poliklinikler / Branşlar
CREATE TABLE IF NOT EXISTS clinics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    hospital_id UUID NOT NULL REFERENCES hospitals(id) ON DELETE CASCADE,
    branch_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_hospital_branch UNIQUE (hospital_id, branch_name)
);

-- 4. Hekim Profilleri
CREATE TABLE IF NOT EXISTS doctor_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    clinic_id UUID NOT NULL REFERENCES clinics(id) ON DELETE RESTRICT,
    title VARCHAR(50) NOT NULL,
    room_number VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. Hekim Çalışma Cetveli
CREATE TABLE IF NOT EXISTS doctor_schedules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doctor_id UUID NOT NULL REFERENCES doctor_profiles(id) ON DELETE CASCADE,
    day_of_week INT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INT NOT NULL DEFAULT 15,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT chk_schedule_time CHECK (start_time < end_time)
);

-- 6. Randevu Dilimleri (Slots) - Race Condition Önleyici
CREATE TABLE IF NOT EXISTS appointment_slots (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doctor_id UUID NOT NULL REFERENCES doctor_profiles(id) ON DELETE CASCADE,
    clinic_id UUID NOT NULL REFERENCES clinics(id) ON DELETE RESTRICT,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    locked_until TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_doctor_slot UNIQUE (doctor_id, start_time),
    CONSTRAINT chk_slot_time CHECK (start_time < end_time)
);

-- 7. Randevular
CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    slot_id UUID NOT NULL UNIQUE REFERENCES appointment_slots(id) ON DELETE RESTRICT,
    patient_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    cancellation_reason TEXT,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- İndeksler
CREATE INDEX IF NOT EXISTS idx_hospitals_city_district ON hospitals(city_code, district_code);
CREATE INDEX IF NOT EXISTS idx_slots_search ON appointment_slots(clinic_id, doctor_id, start_time, status);
CREATE INDEX IF NOT EXISTS idx_appointments_patient ON appointments(patient_id, status);

-- Örnek Başlangıç Verileri
INSERT INTO hospitals (id, name, city_code, district_code, address) VALUES 
('a0000000-0000-0000-0000-000000000001', 'Ankara Bilkent Şehir Hastanesi', 6, 1, 'Üniversiteler Mah. 1604. Cad. No: 9 Çankaya/Ankara'),
('a0000000-0000-0000-0000-000000000002', 'İstanbul Başakşehir Çam ve Sakura Şehir Hastanesi', 34, 5, 'Başakşehir Mah. G-434 Cad. No: 2 Başakşehir/İstanbul')
ON CONFLICT (id) DO NOTHING;

INSERT INTO clinics (id, hospital_id, branch_name) VALUES 
('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'Dahiliye (İç Hastalıkları)'),
('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'Göz Hastalıkları'),
('b0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000001', 'Kardiyoloji')
ON CONFLICT (id) DO NOTHING;