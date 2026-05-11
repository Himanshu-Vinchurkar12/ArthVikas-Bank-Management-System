package com.bank.service;

/**
 * BankOperations — Interface defining the contract for all
 * core banking operations in the TKA Bank application.
 *
 * <p>OOP principles demonstrated:
 * <ul>
 *   <li>Abstraction — callers depend on this interface,
 *       not the concrete {@link BankService} implementation</li>
 *   <li>Polymorphism — any class implementing this interface
 *       can be swapped in without changing the controller</li>
 * </ul>
 *
 * <p>This design allows easy extension — e.g. a
 * {@code PremiumBankService} with higher transfer limits
 * could implement this same interface.
 *
 * @author Himanshu Vinchurkar
 * @version 2.0
 */
public interface BankOperations {

    /**
     * Deposits the specified amount into the given account.
     *
     * @param id     the account ID to credit
     * @param amount the amount to deposit (must be &gt; 0)
     * @throws Exception if validation fails or a database error occurs
     */
    void deposit(int id, double amount) throws Exception;

    /**
     * Withdraws the specified amount from the given account.
     *
     * @param id     the account ID to debit
     * @param amount the amount to withdraw (must be &gt; 0)
     * @throws Exception if validation fails, balance is insufficient,
     *                   or a database error occurs
     */
    void withdraw(int id, double amount) throws Exception;

    /**
     * Transfers the specified amount from one account to another.
     *
     * @param fromId the sender's account ID
     * @param toId   the receiver's account ID
     * @param amount the amount to transfer (must be &gt; 0)
     * @throws Exception if validation fails, balance is insufficient,
     *                   either account doesn't exist, or a database error occurs
     */
    void transfer(int fromId, int toId, double amount) throws Exception;

    /**
     * Displays the current balance of the given account.
     *
     * @param id the account ID to query
     * @throws Exception if a database error occurs
     */
    void showBalance(int id) throws Exception;

    /**
     * Displays the full transaction history for the given account.
     *
     * @param accountId the account ID whose history to display
     * @throws Exception if a database error occurs
     */
    void showTransactionHistory(int accountId) throws Exception;
}