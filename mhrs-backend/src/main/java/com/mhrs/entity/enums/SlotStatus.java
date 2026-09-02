package com.mhrs.entity.enums;

public enum SlotStatus {
    OPEN,      // Henüz kimse seçmedi, randevuya açık
    LOCKED,    // Kullanıcı seçti, Redisson ile 2 dk geçici kilitlendi
    BOOKED,    // Randevu kesinleşti, DB'de satıldı
    CANCELLED  // İptal edildi
}