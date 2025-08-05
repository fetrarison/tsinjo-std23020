package com.examen.demo;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Help {
    private Long id;
    private Beneficiary beneficiary;
    private Payment payment;
    private String accidentDescription;
}