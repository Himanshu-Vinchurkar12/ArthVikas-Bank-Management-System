package com.bank.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Account — Model class representing a bank account.
 *
 * <p>Holds all account-level data: ID, name, hashed password,
 * and current balance. This is a pure data model — no business
 * logic or database calls are made here.
 *
 * <p>OOP principles demonstrated:
 * <ul>
 *   <li>Encapsulation — all fields are {@code private}; access
 *       is only through validated getters and setters</li>
 *   <li>Data hiding — password is hashed via SHA-256 inside
 *       {@link #setPassword(String)} before being stored,
 *       so the raw password never persists anywhere</li>
 * </ul>
 *
 * <p>Password security: SHA-256 is applied using Java's built-in
 * {@link java.security.MessageDigest} — no external library needed.
 *
 * @author Himanshu Vinchurkar
 * @version 2.0
 */
public class Account {

    /** Unique numeric identifier for this account. */
    private int id;

    /** Full name of the account holder. */
    private String name;

    /**
     * SHA-256 hashed password.
     * The raw password is never stored — only its hash.
     */
    private String password;

    /** Current balance in Indian Rupees (₹). */
    private double balance;

    // ══════════════════════════════════════════════════════════════
    //  Getters and Setters
    // ══════════════════════════════════════════════════════════════

    /**
     * Returns the unique account ID.
     *
     * @return account ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the account ID.
     *
     * @param id a positive integer identifying this account
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the account holder's name.
     *
     * @return name of the account holder
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the account holder's name.
     *
     * @param name full name of the account holder (non-empty)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the stored password value.
     *
     * <p>Note: this returns the SHA-256 hash, not the original
     * plain-text password — the raw password is never stored.
     *
     * @return the SHA-256 hashed password string
     */
    public String getPassword() {
        return password;
    }

    /**
     * Hashes the given password using SHA-256 and stores the result.
     *
     * <p>This means the raw password is never held in memory beyond
     * this method call. The hash is what gets saved to the database.
     *
     * <p>Example:
     * <pre>
     *   acc.setPassword("mySecret");
     *   // acc.getPassword() → "oWrsPwJtD3..." (SHA-256 hex)
     * </pre>
     *
     * @param password the plain-text password entered by the user
     */
    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    /**
     * Returns the current account balance.
     *
     * @return balance in ₹ (Indian Rupees)
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Sets the account balance.
     *
     * @param balance the balance to assign (should be &gt;= 0)
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    // ══════════════════════════════════════════════════════════════
    //  Static utility — Step 7: Password hashing
    // ══════════════════════════════════════════════════════════════

    /**
     * Hashes a plain-text password using the SHA-256 algorithm
     * and returns the result as a lowercase hex string.
     *
     * <p>This is a {@code static} utility method so it can be called
     * from {@link com.bank.dao.BankDAO#login(int, String)} without
     * creating an {@code Account} instance — keeping login comparison
     * consistent with how passwords are stored.
     *
     * <p>Uses only Java's built-in {@link java.security.MessageDigest}
     * — no external dependency required.
     *
     * @param password the plain-text password to hash
     * @return a 64-character lowercase hexadecimal SHA-256 hash string
     * @throws RuntimeException if SHA-256 algorithm is unavailable
     *                          (this should never happen on a standard JVM)
     */
    public static String hashPassword(String password) {
        try {
            // Get SHA-256 MessageDigest instance from Java Security API
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Compute the hash — returns a byte array
            byte[] hashBytes = md.digest(password.getBytes());

            // Convert each byte to a 2-char hex string and join them
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                // 0xff mask ensures unsigned interpretation of the byte
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0'); // pad single-char hex (e.g. "a" → "0a")
                }
                hexString.append(hex);
            }

            return hexString.toString(); // 64-character hex string

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed by the Java spec — this block
            // will never execute on a standard JVM
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Returns a string representation of this account
     * (password hash is intentionally excluded for security).
     *
     * @return formatted account summary
     */
    @Override
    public String toString() {
        return "Account{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", balance=₹" + balance
            + '}';
        // password deliberately omitted
    }
}