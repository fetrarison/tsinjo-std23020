package com.examen.demo.datastructure;

import com.examen.demo.PaymentStatus;
import com.examen.demo.Donation;
import com.examen.demo.Donor;
import com.examen.demo.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonationDAO {
    private Connection connection;

    public DonationDAO(Connection connection) {
        this.connection = connection;
    }

    public void insert(Donation donation) throws SQLException {

        Donor donor = donation.getDonor();
        String checkDonorSql = "SELECT email FROM donor WHERE email = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkDonorSql);
        checkStmt.setString(1, donor.getEmail());
        ResultSet rs = checkStmt.executeQuery();

        if (!rs.next()) {
            String insertDonorSql = "INSERT INTO donor (email, full_name) VALUES (?, ?)";
            PreparedStatement donorStmt = connection.prepareStatement(insertDonorSql);
            donorStmt.setString(1, donor.getEmail());
            donorStmt.setString(2, donor.getFullName());
            donorStmt.executeUpdate();
        }

        // Ensuite insérer le payment
        Payment payment = donation.getPayment();
        String insertPaymentSql = "INSERT INTO payment (id, date, amount, method, status) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement paymentStmt = connection.prepareStatement(insertPaymentSql);
        paymentStmt.setString(1, payment.getId());
        paymentStmt.setTimestamp(2, Timestamp.valueOf(payment.getDate()));
        paymentStmt.setBigDecimal(3, payment.getAmount());
        paymentStmt.setString(4, payment.getMethod());
        paymentStmt.setString(5, payment.getStatus().name());
        paymentStmt.executeUpdate();

        // Enfin insérer la donation
        String insertDonationSql = "INSERT INTO donation (id, donor_email, payment_id) VALUES (?, ?, ?)";
        PreparedStatement donationStmt = connection.prepareStatement(insertDonationSql);
        donationStmt.setLong(1, donation.getId());
        donationStmt.setString(2, donor.getEmail());
        donationStmt.setString(3, payment.getId());
        donationStmt.executeUpdate();
    }

    public List<Donation> findAllByOrderByPaymentDateDesc() throws SQLException {
        List<Donation> donations = new ArrayList<>();
        String sql = """
            SELECT d.id, dr.email, dr.full_name, p.id as payment_id, 
                   p.date, p.amount, p.method, p.status
            FROM donation d
            JOIN donor dr ON d.donor_email = dr.email
            JOIN payment p ON d.payment_id = p.id
            ORDER BY p.date DESC
            """;

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Donor donor = new Donor(
                    rs.getString("email"),
                    rs.getString("full_name")
            );

            Payment payment = new Payment(
                    rs.getString("payment_id"),
                    rs.getTimestamp("date").toLocalDateTime(),
                    rs.getBigDecimal("amount"),
                    rs.getString("method"),
                    PaymentStatus.valueOf(rs.getString("status"))
            );

            Donation donation = new Donation();
            donation.setId(rs.getLong("id"));
            donation.setDonor(donor);
            donation.setPayment(payment);

            donations.add(donation);
        }
        return donations;
    }
}