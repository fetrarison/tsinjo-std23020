package com.examen.demo;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class DonationDto {
    private String email;
    private String fullName;
    private BigDecimal amount;
    private String paymentMethod;
}