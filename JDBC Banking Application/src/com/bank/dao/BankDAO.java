package com.bank.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.util.DBConnection;

/**
 * BankDAO — Data Access Object for all database operations.
 *
 * <p>Handles direct SQL communication with the MySQL database.
 * All business logic stays in {@link com.bank.service.BankService};
 * this class only reads and writes data.
 *
 * <p>OOP principle demonstrated: Single Responsibility — this class
 * is solely responsible for database interaction, nothing else.
 *
 * <p>Resource management: every Connection and PreparedStatement
 * is opened inside try-with-resources so they are automatically
 * closed after use — no resource leaks.
 *
 * @author Himanshu Vinchurkar
 * @version 2.0
 */
public class BankDAO {

    /**
     * Creates a new bank account and persists it to the database.
     *
     * <p>Password is stored as a SHA-256 hash (hashed in
     * {@link com.bank.model.Account#setPassword(String)}).
     *
     * @param acc the {@link Account} object containing all account details
     * @throws Exception if a database error occurs or the ID already exists
     */
    public void createAccount(Account acc) throws Exception {
    	String query = 
    		    "INSERT INTO account(id, name, password, balance) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, acc.getId());
            ps.setString(2, acc.getName());
            ps.setString(3, acc.getPassword()); // already hashed via setter
            ps.setDouble(4, acc.getBalance());

            ps.executeUpdate();
            System.out.println("Account created successfully!");
        }
    }

    /**
     * Checks whether an account with the given ID exists in the database.
     *
     * @param id the account ID to look up
     * @return {@code true} if the account exists, {@code false} otherwise
     * @throws Exception if a database error occurs
     */
    public boolean accountExists(int id) throws Exception {
        String query = "SELECT id FROM account WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true if at least one row found
            }
        }
    }

    /**
     * Validates login credentials and returns the matching account.
     *
     * <p>The password is hashed before comparison so plain-text
     * passwords are never sent to the database.
     *
     * @param id       the account ID entered by the user
     * @param password the raw password entered by the user (will be hashed)
     * @return the matching {@link Account}, or {@code null} if credentials are wrong
     * @throws Exception if a database error occurs
     */
    public Account login(int id, String password) throws Exception {
        String query = "SELECT * FROM account WHERE id = ? AND password = ?";

        // Hash the password before querying
        String hashedPassword = Account.hashPassword(password);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, id);
            ps.setString(2, hashedPassword);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account acc = new Account();
                    acc.setId(rs.getInt(1));
                    acc.setName(rs.getString(2));
                    acc.setPassword(rs.getString(3));
                    acc.setBalance(rs.getDouble(4));
                    return acc;
                }
            }
        }
        return null;
    }

    /**
     * Updates the balance of an account identified by its ID.
     *
     * @param id      the account ID whose balance should be updated
     * @param balance the new balance value to set
     * @throws Exception if a database error occurs
     */
    public void updateBalance(int id, double balance) throws Exception {
        String query = "UPDATE account SET balance = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setDouble(1, balance);
            ps.setInt(2, id);

            ps.executeUpdate();
        }
    }

    /**
     * Retrieves the current balance of the given account.
     *
     * @param id the account ID to query
     * @return the current balance, or {@code 0.0} if the account is not found
     * @throws Exception if a database error occurs
     */
    public double getBalance(int id) throws Exception {
        String query = "SELECT balance FROM account WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    // ══════════════════════════════════════════════════════════════
    //  Transaction history — uses the Transaction model (Step 4)
    // ══════════════════════════════════════════════════════════════

    /**
     * Saves a completed transaction to the {@code transactions} table.
     *
     * <p>Called by {@link com.bank.service.BankService} after every
     * successful deposit, withdrawal, or transfer.
     *
     * <p>Required SQL to create the table:
     * <pre>
     * CREATE TABLE transactions (
     *     id        INT AUTO_INCREMENT PRIMARY KEY,
     *     from_id   INT,
     *     to_id     INT,
     *     amount    DOUBLE,
     *     type      VARCHAR(20),
     *     date      DATETIME
     * );
     * </pre>
     *
     * @param txn the {@link Transaction} object to persist
     * @throws Exception if a database error occurs
     */
    public void saveTransaction(Transaction txn) throws Exception {
        String query = "INSERT INTO transactions (from_id, to_id, amount, type, date) "
                     + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, txn.getFromId());
            ps.setInt(2, txn.getToId());
            ps.setDouble(3, txn.getAmount());
            ps.setString(4, txn.getType());
            ps.setObject(5, txn.getDate()); // LocalDateTime supported by JDBC 4.2+

            ps.executeUpdate();
        }
    }

    /**
     * Retrieves all transactions where the given account was
     * either the sender or the receiver, ordered by most recent first.
     *
     * @param accountId the account ID to fetch history for
     * @return a {@link List} of {@link Transaction} objects; empty if none found
     * @throws Exception if a database error occurs
     */
    public List<Transaction> getTransactionHistory(int accountId) throws Exception {
        String query = "SELECT from_id, to_id, amount, type, date "
                     + "FROM transactions "
                     + "WHERE from_id = ? OR to_id = ? "
                     + "ORDER BY date DESC";

        List<Transaction> history = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, accountId);
            ps.setInt(2, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction txn = new Transaction();
                    txn.setFromId(rs.getInt("from_id"));
                    txn.setToId(rs.getInt("to_id"));
                    txn.setAmount(rs.getDouble("amount"));
                    txn.setType(rs.getString("type"));
                    txn.setDate(rs.getObject("date", LocalDateTime.class));
                    history.add(txn);
                }
            }
        }
        return history;
    }
}