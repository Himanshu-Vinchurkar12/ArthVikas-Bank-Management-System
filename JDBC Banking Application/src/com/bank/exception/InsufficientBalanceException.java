package com.bank.exception;

/**
 * InsufficientBalanceException — Custom checked exception thrown when
 * a withdrawal or transfer is attempted with inadequate account funds.
 *
 * <p>Extends {@link Exception} (checked exception) so the compiler
 * forces every caller to explicitly handle or declare it — preventing
 * balance-related errors from being silently swallowed.
 *
 * <p>OOP principles demonstrated:
 * <ul>
 *   <li>Inheritance — extends {@link Exception}, gaining all standard
 *       exception behaviour while adding domain-specific context</li>
 *   <li>Encapsulation — the available balance and requested amount
 *       are stored as private fields with getters, so callers can
 *       access them programmatically rather than parsing a string</li>
 *   <li>Abstraction — hides the complexity of financial error context
 *       behind a clean, domain-meaningful exception type</li>
 * </ul>
 *
 * <p>Usage example:
 * <pre>
 *   if (balance &lt; amount) {
 *       throw new InsufficientBalanceException(balance, amount);
 *   }
 * </pre>
 *
 * @author Himanshu Vinchurkar
 * @version 2.0
 * @see com.bank.service.BankService
 */
public class InsufficientBalanceException extends Exception {

    /**
     * Serial version UID for safe serialization of this exception.
     * Best practice for all {@link java.io.Serializable} classes.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Short error code identifying this exception type.
     * Useful for logging, debugging, or future API error responses.
     */
    private static final String ERROR_CODE = "INSUFFICIENT_BALANCE";

    /**
     * The balance available in the account at the time of the error.
     * Stored so callers can access it without parsing the message string.
     */
    private final double availableBalance;

    /**
     * The amount that was requested but could not be fulfilled.
     * Stored so callers can access it without parsing the message string.
     */
    private final double requestedAmount;

    // ══════════════════════════════════════════════════════════════
    //  Constructors
    // ══════════════════════════════════════════════════════════════

    /**
     * Constructs an {@code InsufficientBalanceException} with only
     * a custom message — used when exact balance figures are unavailable
     * or unnecessary to expose.
     *
     * <p>Kept for backward compatibility with existing callers.
     *
     * @param message a human-readable description of the error
     */
    public InsufficientBalanceException(String message) {
        super(message);
        this.availableBalance = 0.0;
        this.requestedAmount  = 0.0;
    }

    /**
     * Constructs an {@code InsufficientBalanceException} with precise
     * financial context — the preferred constructor.
     *
     * <p>Auto-generates a detailed message in the format:
     * <pre>
     *   [INSUFFICIENT_BALANCE] Available: ₹1500.00 | Requested: ₹3000.00
     *   | Shortfall: ₹1500.00
     * </pre>
     *
     * @param availableBalance the actual balance in the account (in ₹)
     * @param requestedAmount  the amount that was requested (in ₹)
     */
    public InsufficientBalanceException(double availableBalance,
                                        double requestedAmount) {
        super(String.format(
            "[%s] Available: ₹%.2f | Requested: ₹%.2f | Shortfall: ₹%.2f",
            ERROR_CODE,
            availableBalance,
            requestedAmount,
            (requestedAmount - availableBalance)
        ));
        this.availableBalance = availableBalance;
        this.requestedAmount  = requestedAmount;
    }

    /**
     * Constructs an {@code InsufficientBalanceException} with a custom
     * message and precise financial context — for cases where the
     * auto-generated message needs an additional description prefix.
     *
     * @param message          a custom description prefix (e.g. "Transfer failed!")
     * @param availableBalance the actual balance in the account (in ₹)
     * @param requestedAmount  the amount that was requested (in ₹)
     */
    public InsufficientBalanceException(String message,
                                        double availableBalance,
                                        double requestedAmount) {
        super(String.format(
            "%s [%s] Available: ₹%.2f | Requested: ₹%.2f | Shortfall: ₹%.2f",
            message,
            ERROR_CODE,
            availableBalance,
            requestedAmount,
            (requestedAmount - availableBalance)
        ));
        this.availableBalance = availableBalance;
        this.requestedAmount  = requestedAmount;
    }

    // ══════════════════════════════════════════════════════════════
    //  Getters
    // ══════════════════════════════════════════════════════════════

    /**
     * Returns the balance that was available in the account
     * at the time this exception was thrown.
     *
     * @return available balance in ₹
     */
    public double getAvailableBalance() {
        return availableBalance;
    }

    /**
     * Returns the amount that was requested but could not be fulfilled.
     *
     * @return requested amount in ₹
     */
    public double getRequestedAmount() {
        return requestedAmount;
    }

    /**
     * Returns the shortfall — how much extra balance was needed
     * to complete the operation.
     *
     * <p>Computed as: {@code requestedAmount - availableBalance}.
     *
     * @return shortfall amount in ₹ (always &gt;= 0 when this exception is thrown)
     */
    public double getShortfall() {
        return requestedAmount - availableBalance;
    }

    /**
     * Returns the fixed error code for this exception type.
     *
     * <p>Useful for programmatic error handling, logging systems,
     * or future REST API error response bodies.
     *
     * @return {@code "INSUFFICIENT_BALANCE"}
     */
    public String getErrorCode() {
        return ERROR_CODE;
    }

    /**
     * Returns a developer-friendly string for logging and debugging.
     *
     * <p>Format:
     * <pre>
     *   InsufficientBalanceException{code='INSUFFICIENT_BALANCE',
     *   available=₹1500.00, requested=₹3000.00, shortfall=₹1500.00}
     * </pre>
     *
     * @return detailed string representation of this exception
     */
    @Override
    public String toString() {
        return String.format(
            "InsufficientBalanceException{code='%s', available=₹%.2f,"
            + " requested=₹%.2f, shortfall=₹%.2f}",
            ERROR_CODE,
            availableBalance,
            requestedAmount,
            getShortfall()
        );
    }
}