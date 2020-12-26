package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "otp")
public class OTP extends Auditable{

    private String code;
    private String sentToNumber;

    public static OTP make(String phoneNumber) {
        return OTP.builder()
                .code("0000")
                .sentToNumber(phoneNumber)
                .build();
    }

    public boolean validateEnteredOTP(OTP otp, Integer expiryMinutes) {
        if (!code.equals(otp.getCode())) {
            return false;
        }
        return true;
    }
}
