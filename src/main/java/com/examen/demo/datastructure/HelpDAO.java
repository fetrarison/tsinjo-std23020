package com.examen.demo.datastructure;

import com.examen.demo.Help;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HelpDAO {
    private Connection connection;

    public HelpDAO(Connection connection) {
        this.connection = connection;
    }

    public void insert(Help help) throws SQLException {
        String sql = "INSERT INTO help (email, name, date, amount, accident_reason) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, help.getEmail());
        stmt.setString(2, help.getName());
        stmt.setTimestamp(3, Timestamp.valueOf(help.getDate()));
        stmt.setDouble(4, help.getAmount());
        stmt.setString(5, help.getAccidentReason());
        stmt.executeUpdate();
    }

    public List<Help> findAll() throws SQLException {
        List<Help> list = new ArrayList<>();
        String sql = "SELECT * FROM help";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            list.add(new Help(
                    rs.getString("email"),
                    rs.getString("name"),
                    rs.getTimestamp("date").toLocalDateTime(),
                    rs.getDouble("amount"),
                    rs.getString("accident_reason")
            ));
        }
        return list;
    }
}
