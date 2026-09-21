package com.mhrs.dto.schedule;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateSlotsRequest {

    @NotNull(message = "Doktor ID boş olamaz.")
    private UUID doctorId;

    @NotNull(message = "Başlangıç tarihi boş olamaz.")
    @FutureOrPresent(message = "Başlangıç tarihi bugünden önce olamaz.")
    private LocalDate startDate;

    @NotNull(message = "Bitiş tarihi boş olamaz.")
    private LocalDate endDate;
}