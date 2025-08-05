package com.examen.demo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Donation {
    private Long id;
    private Donor donor;
    private Payment payment;
}
