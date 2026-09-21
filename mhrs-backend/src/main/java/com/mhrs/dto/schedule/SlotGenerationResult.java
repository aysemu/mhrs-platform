package com.mhrs.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotGenerationResult {
    private UUID doctorId;
    private int generatedSlotsCount;
    private int skippedSlotsCount;
    private String message;
}