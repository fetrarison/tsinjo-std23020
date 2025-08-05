package com.examen.demo.endpoint.rest.controller.health;



import com.examen.demo.datastructure.DonationDao;
import com.examen.demo.datastructure.HelpDao;
import com.examen.demo.*;
import com.examen.demo.DonationDto;
import com.examen.demo.VolaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/donations")
public class DonationController {

    private final DonationDao donationDao;
    private final HelpDao helpDao;
    private final VolaService volaService;

    public DonationController(DonationDao donationDao, HelpDao helpDao, VolaService volaService) {
        this.donationDao = donationDao;
        this.helpDao = helpDao;
        this.volaService = volaService;
    }

    // Endpoint 1: Afficher la page avec formulaire et historique
    @GetMapping
    public String showPage(Model model) {
        try {
            model.addAttribute("donations", donationDao.findAllByOrderByPaymentDateDesc());
            model.addAttribute("helps", helpDao.findAllByOrderByPaymentDateDesc());
            model.addAttribute("donationForm", new DonationDto());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }

    // Endpoint 2: Traiter un nouveau don
    @PostMapping
    public String createDonation(@ModelAttribute DonationDto donationDto) {
        try {
            // 1. Créer le paiement
            String paymentId = volaService.createPayment(donationDto);

            // 2. Préparer les entités
            Donor donor = new Donor();
            donor.setEmail(donationDto.getEmail());
            donor.setFullName(donationDto.getFullName());

            Payment payment = new Payment();
            payment.setId(paymentId);
            payment.setDate(LocalDateTime.now());
            payment.setAmount(donationDto.getAmount());
            payment.setMethod(donationDto.getPaymentMethod());
            payment.setStatus(PaymentStatus.VERIFYING);

            Donation donation = new Donation();
            donation.setDonor(donor);
            donation.setPayment(payment);

            // 3. Sauvegarder
            donationDao.insert(donation);

            // 4. Démarrer vérification asynchrone (simplifiée)
            new Thread(() -> {
                try {
                    Thread.sleep(5000); // Simulation vérification
                    donationDao.updatePaymentStatus(paymentId, PaymentStatus.SUCCEEDED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/donations";
    }
}