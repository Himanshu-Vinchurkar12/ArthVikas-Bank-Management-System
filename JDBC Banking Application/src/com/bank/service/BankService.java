package com.bank.service;

import java.time.LocalDateTime;
import java.util.List;

import com.bank.dao.BankDAO;
import com.bank.exception.InsufficientBalanceException;
import com.bank.model.Transaction;

/**
 * BankService — Core business logic layer of the TKA Bank application.
 *
 * <p>Implements {@link BankOperations} to demonstrate the OOP principle
 * of abstraction and polymorphism — the controller depends on the
 * interface contract, not this concrete implementation directly.
 *
 * <p>All validation (negative amounts, zero transfers, self-transfer)
 * is enforced here before any database call is made via {@link BankDAO}.
 *
 * <p>OOP principles demonstrated:
 * <ul>
 *   <li>Abstraction — implements {@code BankOperations} interface</li>
 *   <li>Single Responsibility — handles only business rules, no I/O</li>
 *   <li>Encapsulation — internal dao instance is private</li>
 * </ul>
 *
 * @author Himanshu Vinchurkar
 * @version 2.0
 */
public class BankService implements BankOperations {

    /** DAO instance used for all database operations. */
    private BankDAO dao = new BankDAO();

    // ══════════════════════════════════════════════════════════════
    //  Core banking operations
    // ══════════════════════════════════════════════════════════════

    /**
     * Deposits a positive amount into the specified account.
     *
     * <p>Validates that amount is greater than zero before
     * updating the database. Logs the transaction after success.
     *
     * @param id     the account ID to credit
     * @param amount the amount to deposit (must be &gt; 0)
     * @throws IllegalArgumentException if amount is zero or negative
     * @throws Exception                if a database error occurs
     */
    @Override
    public void deposit(int id, double amount) throws Exception {

        // ── Step 2: Input validation ──────────────────────────────
        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Deposit amount must be greater than zero. You entered: ₹" + amount);
        }

        double balance = dao.getBalance(id);
        balance += amount;
        dao.updateBalance(id, balance);
        System.out.println("✓ Deposited ₹" + amount + " successfully!");

        // ── Step 4: Log the transaction ───────────────────────────
        logTransaction(id, id, amount, "DEPOSIT");
    }

    /**
     * Withdraws a positive amount from the specified account
     * if sufficient balance exists.
     *
     * <p>Validates that amount is greater than zero and that the
     * account holds enough funds before proceeding.
     * Logs the transaction after success.
     *
     * @param id     the account ID to debit
     * @param amount the amount to withdraw (must be &gt; 0)
     * @throws IllegalArgumentException    if amount is zero or negative
     * @throws InsufficientBalanceException if the account balance is too low
     * @throws Exception                    if a database error occurs
     */
    @Override
    public void withdraw(int id, double amount) throws Exception {

        // ── Step 2: Input validation ──────────────────────────────
        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Withdrawal amount must be greater than zero. You entered: ₹" + amount);
        }

        double balance = dao.getBalance(id);

        if (balance < amount) {
            throw new InsufficientBalanceException(
                "Insufficient balance! Available: ₹" + balance + " | Requested: ₹" + amount);
        }

        balance -= amount;
        dao.updateBalance(id, balance);
        System.out.println("✓ Withdrew ₹" + amount + " successfully!");

        // ── Step 4: Log the transaction ───────────────────────────
        logTransaction(id, id, amount, "WITHDRAW");
    }

    /**
     * Transfers a positive amount from one account to another atomically.
     *
     * <p>The method is {@code synchronized} to prevent race conditions
     * when multiple transfers happen concurrently (thread safety).
     *
     * <p>Validates: positive amount, sender != receiver, both accounts
     * exist, and sender has sufficient balance — in that order.
     * Logs the transaction after success.
     *
     * @param fromId the sender's account ID
     * @param toId   the receiver's account ID
     * @param amount the amount to transfer (must be &gt; 0)
     * @throws IllegalArgumentException    if amount &lt;= 0 or sender == receiver
     * @throws Exception                   if either account does not exist
     * @throws InsufficientBalanceException if sender's balance is too low
     */
    @Override
    public synchronized void transfer(int fromId, int toId, double amount) throws Exception {

        // ── Step 2: Input validation ──────────────────────────────
        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Transfer amount must be greater than zero. You entered: ₹" + amount);
        }

        if (fromId == toId) {
            throw new IllegalArgumentException(
                "Cannot transfer to the same account!");
        }

        // Verify both accounts exist before touching any balance
        if (!dao.accountExists(toId)) {
            throw new Exception("Receiver account ID " + toId + " does not exist!");
        }
        if (!dao.accountExists(fromId)) {
            throw new Exception("Sender account ID " + fromId + " does not exist!");
        }

        double senderBalance = dao.getBalance(fromId);

        if (senderBalance < amount) {
            throw new InsufficientBalanceException(
                "Transfer failed! Available: ₹" + senderBalance + " | Requested: ₹" + amount);
        }

        // Debit sender
        dao.updateBalance(fromId, senderBalance - amount);

        // Credit receiver
        double receiverBalance = dao.getBalance(toId);
        dao.updateBalance(toId, receiverBalance + amount);

        System.out.println("✓ Transferred ₹" + amount
            + " to Account ID " + toId + " successfully!");

        // ── Step 4: Log the transaction ───────────────────────────
        logTransaction(fromId, toId, amount, "TRANSFER");
    }

    // ══════════════════════════════════════════════════════════════
    //  Display helpers
    // ══════════════════════════════════════════════════════════════

    /**
     * Prints the current balance of the specified account to the console.
     *
     * @param id the account ID to query
     * @throws Exception if a database error occurs
     */
    @Override
    public void showBalance(int id) throws Exception {
        double balance = dao.getBalance(id);
        System.out.println("─────────────────────────────");
        System.out.printf("  Current Balance: ₹%.2f%n", balance);
        System.out.println("─────────────────────────────");
    }

    /**
     * Prints the full transaction history for the given account.
     *
     * <p>Shows each transaction's type, amount, counterpart ID,
     * and timestamp in a formatted table. Displays a message if
     * no transactions have been made yet.
     *
     * @param accountId the account ID whose history is to be displayed
     * @throws Exception if a database error occurs
     */
    @Override
    public void showTransactionHistory(int accountId) throws Exception {
        List<Transaction> history = dao.getTransactionHistory(accountId);

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("          Transaction History — Account " + accountId);
        System.out.println("══════════════════════════════════════════════════");

        if (history.isEmpty()) {
            System.out.println("  No transactions found for this account.");
        } else {
            System.out.printf("  %-12s %-10s %-12s %s%n",
                "Type", "Amount", "Account", "Date & Time");
            System.out.println("  ──────────────────────────────────────────────");

            for (Transaction txn : history) {
                // For TRANSFER: show the other party's ID
                // For DEPOSIT/WITHDRAW: from_id == to_id, so show own ID
                int otherParty = (txn.getType().equals("TRANSFER"))
                    ? (txn.getFromId() == accountId ? txn.getToId() : txn.getFromId())
                    : accountId;

                String direction = (txn.getType().equals("TRANSFER")
                    && txn.getFromId() == accountId) ? "→ To" : "← From";

                String party = txn.getType().equals("TRANSFER")
                    ? direction + " #" + otherParty
                    : "Own account";

                System.out.printf("  %-12s ₹%-9.2f %-12s %s%n",
                    txn.getType(),
                    txn.getAmount(),
                    party,
                    txn.getDate());
            }
        }
        System.out.println("══════════════════════════════════════════════════\n");
    }

    // ══════════════════════════════════════════════════════════════
    //  Private helper
    // ══════════════════════════════════════════════════════════════

    /**
     * Builds a {@link Transaction} object and saves it via DAO.
     *
     * <p>Private helper — called internally after every successful
     * deposit, withdrawal, or transfer. Keeps transaction logging
     * logic in one place (DRY principle).
     *
     * @param fromId the source account ID
     * @param toId   the destination account ID
     * @param amount the transaction amount
     * @param type   one of: {@code "DEPOSIT"}, {@code "WITHDRAW"}, {@code "TRANSFER"}
     * @throws Exception if a database error occurs
     */
    private void logTransaction(int fromId, int toId,
                                double amount, String type) throws Exception {
        Transaction txn = new Transaction();
        txn.setFromId(fromId);
        txn.setToId(toId);
        txn.setAmount(amount);
        txn.setType(type);
        txn.setDate(LocalDateTime.now());
        dao.saveTransaction(txn);
    }
}