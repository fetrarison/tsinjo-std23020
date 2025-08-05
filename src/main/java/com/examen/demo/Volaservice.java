package com.examen.demo;


import com.examen.demo.PaymentStatus;
import com.examen.demo.DonationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VolaService {

    @Value("${vola.api.key}")
    private String volaApiKey;

    @Value("${vola.api.url}")
    private String volaApiUrl;

    private final RestTemplate restTemplate;

    public VolaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String createPayment(DonationDto donationDto) {
        String paymentId = "vola_" + UUID.randomUUID().toString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + volaApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format(
                "{\"amount\": %.2f, \"currency\": \"MGA\", \"method\": \"%s\", \"reference\": \"%s\"}",
                donationDto.getAmount(), donationDto.getPaymentMethod(), paymentId);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    volaApiUrl + "/payments",
                    request,
                    String.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                log.info("Payment created with Vola: {}", paymentId);
                return paymentId;
            }
        } catch (Exception e) {
            log.error("Error creating Vola payment", e);
        }

        return paymentId; // Retourne quand même un ID même en cas d'échec
    }

    @Async
    public void verifyPaymentStatus(String paymentId, String callbackUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + volaApiKey);

        try {
            while (true) {
                TimeUnit.SECONDS.sleep(5); // Vérification toutes les 5 secondes

                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        volaApiUrl + "/payments/" + paymentId,
                        HttpMethod.GET,
                        request,
                        String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    // Parse la réponse pour obtenir le statut (simplifié)
                    if (response.getBody().contains("\"status\":\"SUCCEEDED\"")) {
                        notifyCallback(callbackUrl, paymentId, PaymentStatus.SUCCEEDED);
                        break;
                    } else if (response.getBody().contains("\"status\":\"FAILED\"")) {
                        notifyCallback(callbackUrl, paymentId, PaymentStatus.FAILED);
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment verification interrupted", e);
        } catch (Exception e) {
            log.error("Error verifying payment status", e);
        }
    }

    private void notifyCallback(String callbackUrl, String paymentId, PaymentStatus status) {
        // Implémentation de la notification du callback
        // (utiliser RestTemplate pour appeler votre propre endpoint)
    }
}
