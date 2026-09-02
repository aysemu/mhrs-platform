package com.mhrs.exception;

//Redisson ile slot kilitlendiğinde diğer kullanıcıya dönülecek hata
public class SlotLockedException extends RuntimeException {
    public SlotLockedException(String message) {
        super(message);
    }
}