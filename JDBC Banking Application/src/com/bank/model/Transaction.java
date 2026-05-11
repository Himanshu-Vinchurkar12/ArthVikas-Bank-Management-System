package com.bank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Transaction — Model class representing a single bank transaction.
 *
 * <p>Stores all metadata about a banking operation: who sent money,
 * who received it, how much, what type of operation it was,
 * and when it happened.
 *
 * <p>Used by {@link com.bank.service.BankService} to build transaction
 * records after every deposit, withdrawal, or transfer, and by
 * {@link com.bank.dao.BankDAO} to persist and retrieve those records
 * from the {@code transactions} table.
 *
 * <p>OOP principles demonstrated:
 * <ul>
 *   <li>Encapsulation — all fields are {@code private}, accessed
 *       only through getters and setters</li>
 *   <li>Abstraction — callers work with this model object rather
 *       than raw SQL result sets or loose variables</li>
 * </ul>
 *
 * <p>Valid transaction types:
 * <ul>
 *   <li>{@code "DEPOSIT"}  — money added to an account</li>
 *   <li>{@code "WITHDRAW"} — money removed from an account</li>
 *   <li>{@code "TRANSFER"} — money moved between two accounts</li>
 * </ul>
 *
 * @author Himanshu Vinchurkar
 * @version 2.0
 */
public class Transaction {

    /**
     * Account ID of the sender.
     * For DEPOSIT and WITHDRAW, this is the same as {@link #toId}.
     */
    private int fromId;

    /**
     * Account ID of the receiver.
     * For DEPOSIT and WITHDRAW, this is the same as {@link #fromId}.
     */
    private int toId;

    /** The amount involved in this transaction, in ₹ (Indian Rupees). */
    private double amount;

    /**
     * Type of transaction.
     * One of: {@code "DEPOSIT"}, {@code "WITHDRAW"}, {@code "TRANSFER"}.
     */
    private String type;

    /**
     * Date and time when this transaction was recorded.
     * Set to {@link LocalDateTime#now()} at the moment of the operation.
     */
    private LocalDateTime date;

    // ══════════════════════════════════════════════════════════════
    //  Constructors
    // ══════════════════════════════════════════════════════════════

    /**
     * Default no-argument constructor.
     * Required for constructing Transaction objects field-by-field
     * (used when reading rows from the database in {@link com.bank.dao.BankDAO}).
     */
    public Transaction() {
    }

    /**
     * Convenience constructor for creating a fully populated Transaction
     * in a single line — used in {@link com.bank.service.BankService}.
     *
     * @param fromId account ID of the sender
     * @param toId   account ID of the receiver
     * @param amount transaction amount in ₹ (must be &gt; 0)
     * @param type   one of {@code "DEPOSIT"}, {@code "WITHDRAW"}, {@code "TRANSFER"}
     * @param date   the timestamp of the transaction
     */
    public Transaction(int fromId, int toId, double amount,
                       String type, LocalDateTime date) {
        this.fromId = fromId;
        this.toId   = toId;
        this.amount = amount;
        this.type   = type;
        this.date   = date;
    }

    // ══════════════════════════════════════════════════════════════
    //  Getters and Setters
    // ══════════════════════════════════════════════════════════════

    /**
     * Returns the sender's account ID.
     *
     * @return from account ID
     */
    public int getFromId() {
        return fromId;
    }

    /**
     * Sets the sender's account ID.
     *
     * @param fromId the source account ID
     */
    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    /**
     * Returns the receiver's account ID.
     *
     * @return to account ID
     */
    public int getToId() {
        return toId;
    }

    /**
     * Sets the receiver's account ID.
     *
     * @param toId the destination account ID
     */
    public void setToId(int toId) {
        this.toId = toId;
    }

    /**
     * Returns the transaction amount in ₹.
     *
     * @return transaction amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the transaction amount.
     *
     * @param amount the amount in ₹ (should be &gt; 0)
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Returns the transaction type.
     *
     * @return one of {@code "DEPOSIT"}, {@code "WITHDRAW"}, {@code "TRANSFER"}
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the transaction type.
     *
     * @param type one of {@code "DEPOSIT"}, {@code "WITHDRAW"}, {@code "TRANSFER"}
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the date and time of the transaction.
     *
     * @return {@link LocalDateTime} timestamp of the transaction
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Sets the date and time of the transaction.
     *
     * @param date {@link LocalDateTime} when the transaction occurred
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    // ══════════════════════════════════════════════════════════════
    //  Object overrides
    // ══════════════════════════════════════════════════════════════

    /**
     * Returns a human-readable summary of this transaction.
     *
     * <p>Format:
     * <pre>
     *   [DEPOSIT]  ₹500.00  |  From: #101  →  To: #101  |  2025-06-01 14:32:00
     * </pre>
     *
     * <p>Useful for console output and debugging.
     *
     * @return formatted string representation of this transaction
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return String.format(
            "[%-8s]  ₹%,-10.2f  |  From: #%d  →  To: #%d  |  %s",
            type,
            amount,
            fromId,
            toId,
            date != null ? date.format(formatter) : "N/A"
        );
    }
}