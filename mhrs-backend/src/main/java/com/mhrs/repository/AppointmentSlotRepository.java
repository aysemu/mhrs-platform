package com.mhrs.repository;

import com.mhrs.entity.AppointmentSlot;
import com.mhrs.entity.enums.SlotStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, UUID> {

    // Vatandaşın randevu ararken gördüğü liste (Kilitsiz hızlı okuma)
    List<AppointmentSlot> findByDoctorIdAndStatusAndStartTimeBetweenOrderByStartTimeAsc(
            UUID doctorId,
            SlotStatus status,
            OffsetDateTime start,
            OffsetDateTime end);

    // Randevu alma anında kaydı DB seviyesinde kilitleyen metot (SELECT ... FOR
    // UPDATE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM AppointmentSlot s WHERE s.id = :slotId")
    Optional<AppointmentSlot> findByIdWithPessimisticLock(@Param("slotId") UUID slotId);

    // Çift slot oluşturulmasını engellemek için kontrol
    boolean existsByDoctorIdAndStartTime(UUID doctorId, OffsetDateTime startTime);
}