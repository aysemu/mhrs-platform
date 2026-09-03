package com.mhrs.controller;

import com.mhrs.dto.appointment.AppointmentResponse;
import com.mhrs.dto.appointment.ConfirmBookingRequest;
import com.mhrs.dto.appointment.HoldSlotRequest;
import com.mhrs.security.UserPrincipal;
import com.mhrs.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final BookingService bookingService;

    @PostMapping("/hold")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<Void> holdSlot(
            @Valid @RequestBody HoldSlotRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        bookingService.holdSlot(request, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/confirm")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<AppointmentResponse> confirmBooking(
            @Valid @RequestBody ConfirmBookingRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        AppointmentResponse response = bookingService.confirmBooking(request, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
