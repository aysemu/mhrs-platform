package com.mhrs.service;

import com.mhrs.dto.appointment.AppointmentResponse;
import com.mhrs.dto.appointment.ConfirmBookingRequest;
import com.mhrs.dto.appointment.HoldSlotRequest;
import com.mhrs.entity.Appointment;
import com.mhrs.entity.AppointmentSlot;
import com.mhrs.entity.User;
import com.mhrs.entity.enums.AppointmentStatus;
import com.mhrs.entity.enums.SlotStatus;
import com.mhrs.exception.ConflictException;
import com.mhrs.exception.ResourceNotFoundException;
import com.mhrs.exception.SlotLockedException;
import com.mhrs.repository.AppointmentRepository;
import com.mhrs.repository.AppointmentSlotRepository;
import com.mhrs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final AppointmentSlotRepository slotRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;

    private static final String LOCK_PREFIX = "slot:lock:";
    private static final long HOLD_DURATION_SECONDS = 120; // 2 dakika geçici tutma süresi

    /**
     * 1. KATMAN: Slotu Redisson Dağıtık Kilidi ile 2 dakikalığına geçici rezervasyona alır.
     */
    public void holdSlot(HoldSlotRequest request, UUID patientId) {
        UUID slotId = request.getSlotId();
        String lockKey = LOCK_PREFIX + slotId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 0 sn bekle (hemen dene), 120 sn kilit tut (leaseTime)
            boolean acquired = lock.tryLock(0, HOLD_DURATION_SECONDS, TimeUnit.SECONDS);

            if (!acquired) {
                throw new SlotLockedException("Seçilen randevu saati başka bir kullanıcı tarafından rezerve ediliyor. Lütfen birazdan tekrar deneyin.");
            }

            // Slot durumunu DB'den kontrol et
            AppointmentSlot slot = slotRepository.findById(slotId)
                    .orElseThrow(() -> new ResourceNotFoundException("Randevu slotu bulunamadı: " + slotId));

            if (slot.getStatus() != SlotStatus.OPEN) {
                lock.unlock();
                throw new ConflictException("Bu randevu saati artık uygun değil.");
            }

            log.info("Slot [id={}] hasta [patientId={}] için 120 sn rezerve edildi.", slotId, patientId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Kilitleme işlemi kesintiye uğradı.", e);
        }
    }

    /**
     * 2. KATMAN: Pessimistic DB Kilidi (SELECT FOR UPDATE) ve UNIQUE kısıtı ile randevuyu kesinleştirir.
     */
    @Transactional
    public AppointmentResponse confirmBooking(ConfirmBookingRequest request, UUID patientId) {
        UUID slotId = request.getSlotId();
        String lockKey = LOCK_PREFIX + slotId;
        RLock distributedLock = redissonClient.getLock(lockKey);

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Hasta kaydı bulunamadı: " + patientId));

        // Satır düzeyinde Pessimistic Write Lock
        AppointmentSlot slot = slotRepository.findByIdWithPessimisticLock(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Randevu slotu bulunamadı: " + slotId));

        if (slot.getStatus() == SlotStatus.BOOKED) {
            throw new ConflictException("Bu randevu saati daha önce başka bir hasta adına onaylanmıştır.");
        }

        try {
            slot.setStatus(SlotStatus.BOOKED);
            slotRepository.save(slot);

            // Entity alan adına uygun olarak 'patient' kullanıldı
            Appointment appointment = Appointment.builder()
                    .slot(slot)
                    .patient(patient)
                    .status(AppointmentStatus.ACTIVE)
                    .notes(request.getNotes())
                    .build();

            Appointment savedAppointment = appointmentRepository.save(appointment);

            log.info("Randevu başarıyla oluşturuldu [appointmentId={}, slotId={}, patientId={}]",
                    savedAppointment.getId(), slot.getId(), patient.getId());

            return mapToResponse(savedAppointment, slot, patient);

        } catch (DataIntegrityViolationException e) {
            log.error("Veritabanı çakışması yakalandı: {}", e.getMessage());
            throw new ConflictException("Hekimin bu zaman diliminde zaten başka bir randevusu mevcuttur.");
        } finally {
            if (distributedLock.isHeldByCurrentThread()) {
                distributedLock.unlock();
                log.info("Slot dağıtık kilidi serbest bırakıldı: {}", lockKey);
            }
        }
    }

    private AppointmentResponse mapToResponse(Appointment appointment, AppointmentSlot slot, User patient) {
        var doctor = slot.getDoctor();
        var clinic = doctor.getClinic();
        var hospital = clinic.getHospital();

        return AppointmentResponse.builder()
                .appointmentId(appointment.getId())
                .slotId(slot.getId())
                .citizenId(patient.getId())
                .citizenFullName(patient.getFullName())
                .doctorId(doctor.getId())
                .doctorFullName(doctor.getTitle() + " " + doctor.getUser().getFullName())
                .hospitalName(hospital.getName())
                .clinicName(clinic.getBranchName())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}