package com.mhrs.repository;

import com.mhrs.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, UUID> {
    List<DoctorProfile> findByClinicId(UUID clinicId);

    Optional<DoctorProfile> findByUserId(UUID userId);
}