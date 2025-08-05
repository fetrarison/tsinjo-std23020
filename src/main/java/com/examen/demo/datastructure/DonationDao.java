package com.examen.demo.datastructure;


import com.examen.demo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonationDao {
    private final Connection connection;

    public DonationDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(Donation donation) throws SQLException {
        connection.setAutoCommit(false);
        try {
            insertDonor(donation.getDonor());
            insertPayment(donation.getPayment());
            insertDonation(donation);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void insertDonor(Donor donor) throws SQLException {
        String sql = "INSERT INTO donor (email, full_name) VALUES (?, ?) "
                + "ON CONFLICT (email) DO NOTHING";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, donor.getEmail());
            stmt.setString(2, donor.getFullName());
            stmt.executeUpdate();
        }
    }

    private void insertPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO payment (id, date, amount, method, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, payment.getId());
            stmt.setTimestamp(2, Timestamp.valueOf(payment.getDate()));
            stmt.setBigDecimal(3, payment.getAmount());
            stmt.setString(4, payment.getMethod());
            stmt.setString(5, payment.getStatus().name());
            stmt.executeUpdate();
        }
    }

    private void insertDonation(Donation donation) throws SQLException {
        String sql = "INSERT INTO donation (id, donor_email, payment_id) "
                + "VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, donation.getId());
            stmt.setString(2, donation.getDonor().getEmail());
            stmt.setString(3, donation.getPayment().getId());
            stmt.executeUpdate();
        }
    }

    public List<Donation> findAllByOrderByPaymentDateDesc() throws SQLException {
        String sql = """
            SELECT d.id, dr.email, dr.full_name, p.id as payment_id, 
                   p.date, p.amount, p.method, p.status
            FROM donation d
            JOIN donor dr ON d.donor_email = dr.email
            JOIN payment p ON d.payment_id = p.id
            ORDER BY p.date DESC
            """;

        List<Donation> donations = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                donations.add(mapToDonation(rs));
            }
        }
        return donations;
    }

    public void updatePaymentStatus(String paymentId, PaymentStatus status) throws SQLException {
        String sql = "UPDATE payment SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, paymentId);
            stmt.executeUpdate();
        }
    }

    private Donation mapToDonation(ResultSet rs) throws SQLException {
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

        return donation;
    }
}