package com.bank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection — Utility class that manages MySQL database connectivity
 * for the TKA Bank application.
 *
 * <p>Provides a single static factory method {@link #getConnection()}
 * that creates and returns a fresh JDBC {@link Connection} on every call.
 *
 * <p>OOP principles demonstrated:
 * <ul>
 *   <li>Encapsulation — all database credentials and the JDBC URL are
 *       stored as {@code private static final} constants, hidden from
 *       the rest of the application</li>
 *   <li>Single Responsibility — this class does exactly one thing:
 *       supply database connections. Nothing else.</li>
 *   <li>Abstraction — callers receive a {@link Connection} object and
 *       never need to know the driver, host, port, or credentials</li>
 * </ul>
 *
 * <p><b>MySQL setup instructions:</b>
 * <ol>
 *   <li>Create the database:
 *       {@code CREATE DATABASE college_db;}</li>
 *   <li>Create the account table:
 *   <pre>
 *   CREATE TABLE account (
 *       id       INT PRIMARY KEY,
 *       name     VARCHAR(100) NOT NULL,
 *       password VARCHAR(64)  NOT NULL,
 *       balance  DOUBLE       NOT NULL DEFAULT 0.0
 *   );
 *   </pre></li>
 *   <li>Create the transactions table:
 *   <pre>
 *   CREATE TABLE transactions (
 *       id      INT AUTO_INCREMENT PRIMARY KEY,
 *       from_id INT,
 *       to_id   INT,
 *       amount  DOUBLE,
 *       type    VARCHAR(20),
 *       date    DATETIME
 *   );
 *   </pre></li>
 *   <li>Update {@link #DB_USER} and {@link #DB_PASSWORD} below
 *       if your MySQL credentials differ from the defaults.</li>
 * </ol>
 *
 * <p><b>Note on connection strategy:</b> This implementation opens
 * a new {@link Connection} per call. Each {@link com.bank.dao.BankDAO}
 * method closes its connection via try-with-resources, so there are
 * no open connections held longer than needed.
 *
 * @author Himanshu Vinchurkar
 * @version 2.0
 * @see com.bank.dao.BankDAO
 */
public class DBConnection {

    // ══════════════════════════════════════════════════════════════
    //  Database configuration constants
    //  ↓ Update these if your MySQL setup is different ↓
    // ══════════════════════════════════════════════════════════════

    /** MySQL JDBC driver prefix — standard for all MySQL connections. */
    private static final String JDBC_PREFIX = "jdbc:mysql://";

    /** Hostname where MySQL is running. {@code localhost} for local dev. */
    private static final String DB_HOST = "localhost";

    /** Port MySQL is listening on. Default MySQL port is {@code 3306}. */
    private static final int DB_PORT = 3306;

    /** Name of the database schema to connect to. */
    private static final String DB_NAME = "arthvikas_bank";

    /** MySQL username. Update this if your username differs from root. */
    private static final String DB_USER = "root";

    /**
     * MySQL password for {@link #DB_USER}.
     *
     * <p><b>Important:</b> In a real production application, never
     * hard-code passwords here — use environment variables or a
     * config file excluded from version control (e.g. via .gitignore).
     * This approach is acceptable for a local development project.
     */
    private static final String DB_PASSWORD = "Himanshu@28";

    /**
     * Fully qualified class name of the MySQL JDBC driver.
     * Required explicitly for some environments and older JDBC setups.
     * {@code com.mysql.cj.jdbc.Driver} is the correct driver for
     * MySQL Connector/J 8.x and above.
     */
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    /**
     * Full JDBC connection URL assembled from the constants above.
     *
     * <p>The {@code useSSL=false} parameter suppresses SSL warnings
     * on local MySQL installations.
     * The {@code allowPublicKeyRetrieval=true} parameter is required
     * for MySQL 8+ with the default authentication plugin.
     * The {@code serverTimezone=Asia/Kolkata} ensures
     * {@link java.time.LocalDateTime} values are stored and retrieved
     * in IST — matching the Indian Rupee context of this app.
     */
    private static final String DB_URL =
        JDBC_PREFIX + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
        + "?useSSL=false"
        + "&allowPublicKeyRetrieval=true"
        + "&serverTimezone=Asia/Kolkata";

    // ══════════════════════════════════════════════════════════════
    //  Private constructor — prevents instantiation
    // ══════════════════════════════════════════════════════════════

    /**
     * Private constructor — this is a pure utility class.
     *
     * <p>It should never be instantiated; all access is through
     * the static method {@link #getConnection()}.
     * Declaring this constructor {@code private} prevents accidental
     * instantiation (e.g. {@code new DBConnection()}).
     */
    private DBConnection() {
        // Utility class — no instances allowed
    }

    // ══════════════════════════════════════════════════════════════
    //  Factory method
    // ══════════════════════════════════════════════════════════════

    /**
     * Creates and returns a new JDBC {@link Connection} to the
     * TKA Bank MySQL database.
     *
     * <p>This method is called by every {@link com.bank.dao.BankDAO}
     * method inside a {@code try-with-resources} block, ensuring the
     * connection is automatically closed after each database operation.
     *
     * <p>Example usage in DAO:
     * <pre>
     *   try (Connection con = DBConnection.getConnection();
     *        PreparedStatement ps = con.prepareStatement(query)) {
     *       // use ps here — con and ps both auto-closed on exit
     *   }
     * </pre>
     *
     * @return a live {@link Connection} to the MySQL database
     * @throws SQLException if the connection cannot be established —
     *                      e.g. MySQL is not running, wrong credentials,
     *                      or the database does not exist
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Explicitly register the MySQL JDBC driver — required for
            // some environments and consistent with your project's setup
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "MySQL JDBC Driver not found! "
                + "Add mysql-connector-java to your classpath.", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}