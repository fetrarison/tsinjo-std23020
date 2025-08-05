package com.examen.demo.datastructure;



import com.examen.demo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HelpDao {
    private final Connection connection;

    public HelpDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(Help help) throws SQLException {
        connection.setAutoCommit(false);
        try {
            insertBeneficiary(help.getBeneficiary());
            insertPayment(help.getPayment());
            insertHelp(help);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void insertBeneficiary(Beneficiary beneficiary) throws SQLException {
        String sql = "INSERT INTO beneficiary (email, full_name) VALUES (?, ?) "
                + "ON CONFLICT (email) DO NOTHING";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, beneficiary.getEmail());
            stmt.setString(2, beneficiary.getFullName());
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

    private void insertHelp(Help help) throws SQLException {
        String sql = "INSERT INTO help (id, beneficiary_email, payment_id, accident_description) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, help.getId());
            stmt.setString(2, help.getBeneficiary().getEmail());
            stmt.setString(3, help.getPayment().getId());
            stmt.setString(4, help.getAccidentDescription());
            stmt.executeUpdate();
        }
    }

    public List<Help> findAllByOrderByPaymentDateDesc() throws SQLException {
        String sql = """
            SELECT h.id, b.email, b.full_name, p.id as payment_id, 
                   p.date, p.amount, p.method, p.status, h.accident_description
            FROM help h
            JOIN beneficiary b ON h.beneficiary_email = b.email
            JOIN payment p ON h.payment_id = p.id
            ORDER BY p.date DESC
            """;

        List<Help> helps = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                helps.add(mapToHelp(rs));
            }
        }
        return helps;
    }

    private Help mapToHelp(ResultSet rs) throws SQLException {
        Beneficiary beneficiary = new Beneficiary(
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

        Help help = new Help();
        help.setId(rs.getLong("id"));
        help.setBeneficiary(beneficiary);
        help.setPayment(payment);
        help.setAccidentDescription(rs.getString("accident_description"));

        return help;
    }
}
