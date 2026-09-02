package com.mhrs.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "T.C. Kimlik Numarası boş bırakılamaz")
    @Pattern(regexp = "^[1-9]{1}[0-9]{10}$", message = "Geçerli bir 11 haneli T.C. Kimlik Numarası giriniz")
    private String tckn;

    @NotBlank(message = "Ad Soyad alanı zorunludur")
    @Size(min = 3, max = 100, message = "Ad Soyad 3 ile 100 karakter arasında olmalıdır")
    private String fullName;

    @NotBlank(message = "E-posta alanı zorunludur")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    @NotBlank(message = "Şifre alanı zorunludur")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String password;

    @NotBlank(message = "Telefon numarası zorunludur")
    @Pattern(regexp = "^(05)[0-9]{9}$", message = "Geçerli bir telefon numarası giriniz (Örn: 05XXXXXXXXX)")
    private String phoneNumber;
}