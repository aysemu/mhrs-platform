package com.mhrs.exception;

//Çift Randevu veya mükerrer kayıtlar için
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}