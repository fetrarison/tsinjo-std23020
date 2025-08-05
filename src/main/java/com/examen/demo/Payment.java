package com.examen.demo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String id;
    private LocalDateTime date;
    private BigDecimal amount;
    private String method;
    private PaymentStatus status;
}

