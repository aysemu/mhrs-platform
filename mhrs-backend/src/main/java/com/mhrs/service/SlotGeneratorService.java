package com.mhrs.service;

import com.mhrs.dto.schedule.CreateScheduleRequest;
import com.mhrs.dto.schedule.GenerateSlotsRequest;
import com.mhrs.dto.schedule.SlotGenerationResult;
import com.mhrs.entity.AppointmentSlot;
import com.mhrs.entity.DoctorProfile;
import com.mhrs.entity.DoctorSchedule;
import com.mhrs.entity.enums.SlotStatus;
import com.mhrs.exception.BadRequestException;
import com.mhrs.exception.ResourceNotFoundException;
import com.mhrs.repository.AppointmentSlotRepository;
import com.mhrs.repository.DoctorProfileRepository;
import com.mhrs.repository.DoctorScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlotGeneratorService {

    private final DoctorScheduleRepository scheduleRepository;
    private final AppointmentSlotRepository slotRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(3);

    @Transactional
    public DoctorSchedule createSchedule(CreateScheduleRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BadRequestException("Mesai bitiş saati başlangıç saatinden sonra olmalıdır.");
        }

        DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Hekim profili bulunamadı: " + request.getDoctorId()));

        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .slotDurationMinutes(request.getSlotDurationMinutes() != null ? request.getSlotDurationMinutes() : 15)
                .isActive(true)
                .build();

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public SlotGenerationResult generateSlots(GenerateSlotsRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Bitiş tarihi başlangıç tarihinden önce olamaz.");
        }

        DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Hekim profili bulunamadı: " + request.getDoctorId()));

        List<DoctorSchedule> activeSchedules = scheduleRepository.findByDoctorIdAndIsActiveTrue(doctor.getId());
        if (activeSchedules.isEmpty()) {
            throw new BadRequestException("Hekime ait tanımlı aktif bir çalışma takvimi bulunamadı.");
        }

        List<AppointmentSlot> slotsToSave = new ArrayList<>();
        int skippedCount = 0;

        LocalDate currentDate = request.getStartDate();
        while (!currentDate.isAfter(request.getEndDate())) {
            int dayOfWeek = currentDate.getDayOfWeek().getValue();

            for (DoctorSchedule schedule : activeSchedules) {
                if (schedule.getDayOfWeek().equals(dayOfWeek)) {
                    LocalTime currentSlotTime = schedule.getStartTime();
                    int duration = schedule.getSlotDurationMinutes();

                    while (currentSlotTime.plusMinutes(duration).compareTo(schedule.getEndTime()) <= 0) {
                        LocalTime slotEndTime = currentSlotTime.plusMinutes(duration);

                        OffsetDateTime startOffset = currentDate.atTime(currentSlotTime).atOffset(ZONE_OFFSET);
                        OffsetDateTime endOffset = currentDate.atTime(slotEndTime).atOffset(ZONE_OFFSET);

                        boolean exists = slotRepository.existsByDoctorIdAndStartTime(doctor.getId(), startOffset);
                        if (!exists) {
                            AppointmentSlot slot = AppointmentSlot.builder()
                                    .doctor(doctor)
                                    .clinic(doctor.getClinic())
                                    .startTime(startOffset)
                                    .endTime(endOffset)
                                    .status(SlotStatus.OPEN)
                                    .build();
                            slotsToSave.add(slot);
                        } else {
                            skippedCount++;
                        }

                        currentSlotTime = slotEndTime;
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        if (!slotsToSave.isEmpty()) {
            slotRepository.saveAll(slotsToSave);
            log.info("Doktor [id={}] için toplam {} adet yeni randevu slotu üretildi.", doctor.getId(), slotsToSave.size());
        }

        return SlotGenerationResult.builder()
                .doctorId(doctor.getId())
                .generatedSlotsCount(slotsToSave.size())
                .skippedSlotsCount(skippedCount)
                .message(String.format("%d yeni randevu slotu başarıyla üretildi. %d adet mevcut slot atlandı.",
                        slotsToSave.size(), skippedCount))
                .build();
    }
}