package com.examen.demo.controller;

import com.examen.demo.datastructure.DonationDAO;
import com.examen.demo.datastructure.HelpDAO;
import com.examen.demo.*;
import com.examen.demo.DonationDto;
import com.examen.demo.VolaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationDAO donationDao;
    private final HelpDAO helpDao;
    private final VolaService volaService;

    @GetMapping
    public String getAllDonations(Model model) {
        try {
            model.addAttribute("donations", donationDao.findAllByOrderByPaymentDateDesc());
            model.addAttribute("helps", helpDao.findAllByOrderByPaymentDateDesc());
            model.addAttribute("newDonation", new DonationDto());
        } catch (SQLException e) {
            log.error("Error fetching donations", e);
            model.addAttribute("error", "Unable to fetch donations");
        }
        return "index";
    }

    @PostMapping
    public String createDonation(@ModelAttribute DonationDto donationDto, Model model) {
        try {
            // 1. Créer le paiement dans Vola
            String paymentId = volaService.createPayment(donationDto);

            // 2. Créer les entités
            Donor donor = Donor.builder()
                    .email(donationDto.getEmail())
                    .fullName(donationDto.getFullName())
                    .build();

            Payment payment = Payment.builder()
                    .id(paymentId)
                    .date(LocalDateTime.now())
                    .amount(donationDto.getAmount())
                    .method(donationDto.getPaymentMethod())
                    .status(PaymentStatus.VERIFYING)
                    .build();

            Donation donation = new Donation();
            donation.setDonor(donor);
            donation.setPayment(payment);

            // 3. Sauvegarder
            donationDao.insert(donation);

            // 4. Démarrer la vérification asynchrone
            volaService.verifyPaymentStatus(paymentId, "/donations/payment-callback");

            return "redirect:/donations";

        } catch (Exception e) {
            log.error("Error creating donation", e);
            model.addAttribute("error", "Failed to process donation");
            return getAllDonations(model);
        }
    }

    @PostMapping("/payment-callback")
    @ResponseBody
    public String paymentCallback(@RequestBody VolaPaymentStatusUpdate update) {
        try {
            // Mettre à jour le statut du paiement en base
            donationDao.updatePaymentStatus(update.getPaymentId(), update.getStatus());
            return "Callback processed";
        } catch (SQLException e) {
            log.error("Error processing payment callback", e);
            return "Error processing callback";
        }
    }
}