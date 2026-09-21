package com.mhrs.dto.schedule;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleRequest {

    @NotNull(message = "Doktor ID boş olamaz.")
    private UUID doctorId;

    @NotNull(message = "Haftanın günü boş olamaz.")
    @Min(value = 1, message = "Gün değeri 1 (Pazartesi) ile 7 (Pazar) arasında olmalıdır.")
    @Max(value = 7, message = "Gün değeri 1 (Pazartesi) ile 7 (Pazar) arasında olmalıdır.")
    private Integer dayOfWeek;

    @NotNull(message = "Mesai başlangıç saati boş olamaz.")
    private LocalTime startTime;

    @NotNull(message = "Mesai bitiş saati boş olamaz.")
    private LocalTime endTime;

    @Builder.Default
    private Integer slotDurationMinutes = 15;
}