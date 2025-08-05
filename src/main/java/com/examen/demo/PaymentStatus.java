package com.examen.demo;


import lombok.Getter;

@Getter
public enum PaymentStatus {
    VERIFYING("En cours de vérification"),
    SUCCEEDED("Paiement réussi"),
    FAILED("Paiement échoué");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public static PaymentStatus fromString(String status) {
        for (PaymentStatus ps : PaymentStatus.values()) {
            if (ps.name().equalsIgnoreCase(status)) {
                return ps;
            }
        }
        throw new IllegalArgumentException("Statut de paiement inconnu: " + status);
    }

    public boolean isTerminalState() {
        return this == SUCCEEDED || this == FAILED;
    }
}