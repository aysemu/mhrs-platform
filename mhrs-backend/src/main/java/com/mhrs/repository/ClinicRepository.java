package com.mhrs.repository;

import com.mhrs.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, UUID> {
    List<Clinic> findByHospitalId(UUID hospitalId);
}