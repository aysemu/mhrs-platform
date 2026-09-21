package com.mhrs.controller;

import com.mhrs.dto.schedule.CreateScheduleRequest;
import com.mhrs.dto.schedule.GenerateSlotsRequest;
import com.mhrs.dto.schedule.SlotGenerationResult;
import com.mhrs.entity.DoctorSchedule;
import com.mhrs.service.SlotGeneratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final SlotGeneratorService slotGeneratorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'HOSPITAL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DoctorSchedule> createSchedule(@Valid @RequestBody CreateScheduleRequest request) {
        DoctorSchedule created = slotGeneratorService.createSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/generate-slots")
    @PreAuthorize("hasAnyRole('DOCTOR', 'HOSPITAL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<SlotGenerationResult> generateSlots(@Valid @RequestBody GenerateSlotsRequest request) {
        SlotGenerationResult result = slotGeneratorService.generateSlots(request);
        return ResponseEntity.ok(result);
    }
}