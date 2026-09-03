package com.mhrs.dto.appointment;

import com.mhrs.entity.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private UUID appointmentId;
    private UUID slotId;
    private UUID citizenId;
    private String citizenFullName;
    private UUID doctorId;
    private String doctorFullName;
    private String hospitalName;
    private String clinicName;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private AppointmentStatus status;
    private String notes;
    private OffsetDateTime createdAt;
}