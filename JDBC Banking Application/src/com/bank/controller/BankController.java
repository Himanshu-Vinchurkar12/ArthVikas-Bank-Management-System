package com.bank.controller;

import java.util.Scanner;

import com.bank.dao.BankDAO;
import com.bank.model.Account;
import com.bank.service.BankService;

/**
 * BankController — Entry point of the ArthVikas Bank application.
 *
 * <p>Handles all user interaction via console menu.
 * Delegates business logic to {@link BankService} and
 * account creation / login to {@link BankDAO}.
 *
 * <p>OOP principle demonstrated: Separation of concerns —
 * controller only handles I/O, never business logic directly.
 *
 * @author Himanshu Vinchurkar
 * @version 2.0
 */
public class BankController {

    /**
     * Application entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        BankDAO dao = new BankDAO();
        BankService service = new BankService();

        System.out.println("=============================");
        System.out.println("     Welcome to ArthVikas Bank     ");
        System.out.println("=============================");

        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            // ── Input validation: menu choice must be an integer ──
            if (!sc.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                sc.next();
                continue;
            }

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    Account acc = new Account();

                    System.out.print("Enter ID (positive integer): ");
                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid ID! Must be a number.");
                        sc.next();
                        break;
                    }
                    int newId = sc.nextInt();
                    if (newId <= 0) {
                        System.out.println("ID must be a positive number!");
                        break;
                    }
                    acc.setId(newId);

                    sc.nextLine(); // consume newline

                    System.out.print("Enter Name: ");
                    String name = sc.nextLine().trim();
                    if (name.isEmpty()) {
                        System.out.println("Name cannot be empty!");
                        break;
                    }
                    acc.setName(name);

                    System.out.print("Set Password (min 4 chars): ");
                    String pwd = sc.next();
                    if (pwd.length() < 4) {
                        System.out.println("Password too short! Minimum 4 characters.");
                        break;
                    }
                    acc.setPassword(pwd);

                    System.out.print("Initial Balance (min ₹0): ");
                    if (!sc.hasNextDouble()) {
                        System.out.println("Invalid amount!");
                        sc.next();
                        break;
                    }
                    double initBalance = sc.nextDouble();
                    if (initBalance < 0) {
                        System.out.println("Initial balance cannot be negative!");
                        break;
                    }
                    acc.setBalance(initBalance);

                    try {
                        dao.createAccount(acc);
                    } catch (Exception e) {
                        System.out.println("Error creating account: " + e.getMessage());
                    }
                    break;

                case 2:
                    System.out.print("Enter ID: ");
                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid ID!");
                        sc.next();
                        break;
                    }
                    int id = sc.nextInt();

                    System.out.print("Enter Password: ");
                    String pass = sc.next();

                    try {
                        Account user = dao.login(id, pass);

                        if (user != null) {
                            System.out.println("\nLogin Successful! Welcome, " + user.getName() + " 👋");
                            service.showBalance(id);

                            // ── Logged-in menu ──────────────────────────────
                            while (true) {
                                System.out.println("\n--- Account Menu ---");
                                System.out.println("1. Deposit");
                                System.out.println("2. Withdraw");
                                System.out.println("3. Transfer");
                                System.out.println("4. Show Balance");
                                System.out.println("5. Transaction History");
                                System.out.println("6. Logout");
                                System.out.print("Choice: ");

                                if (!sc.hasNextInt()) {
                                    System.out.println("Invalid input! Enter a number.");
                                    sc.next();
                                    continue;
                                }
                                int opt = sc.nextInt();

                                if (opt == 1) {
                                    System.out.print("Deposit Amount: ₹");
                                    if (!sc.hasNextDouble()) {
                                        System.out.println("Invalid amount!");
                                        sc.next();
                                        continue;
                                    }
                                    double depositAmt = sc.nextDouble();
                                    try {
                                        service.deposit(id, depositAmt);
                                        service.showBalance(id);
                                    } catch (Exception e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }

                                } else if (opt == 2) {
                                    System.out.print("Withdraw Amount: ₹");
                                    if (!sc.hasNextDouble()) {
                                        System.out.println("Invalid amount!");
                                        sc.next();
                                        continue;
                                    }
                                    double withdrawAmt = sc.nextDouble();
                                    try {
                                        service.withdraw(id, withdrawAmt);
                                        service.showBalance(id);
                                    } catch (Exception e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }

                                } else if (opt == 3) {
                                    System.out.print("Recipient Account ID: ");
                                    if (!sc.hasNextInt()) {
                                        System.out.println("Invalid ID!");
                                        sc.next();
                                        continue;
                                    }
                                    int toId = sc.nextInt();

                                    System.out.print("Transfer Amount: ₹");
                                    if (!sc.hasNextDouble()) {
                                        System.out.println("Invalid amount!");
                                        sc.next();
                                        continue;
                                    }
                                    double transferAmt = sc.nextDouble();
                                    try {
                                        service.transfer(id, toId, transferAmt);
                                        service.showBalance(id);
                                    } catch (Exception e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }

                                } else if (opt == 4) {
                                    try {
                                        service.showBalance(id);
                                    } catch (Exception e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }

                                } else if (opt == 5) {
                                    // ── NEW: Transaction History ──────────────
                                    try {
                                        service.showTransactionHistory(id);
                                    } catch (Exception e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }

                                } else if (opt == 6) {
                                    System.out.println("Logged out. Goodbye, " + user.getName() + "!");
                                    break;

                                } else {
                                    System.out.println("Invalid option! Choose 1–6.");
                                }
                            }

                        } else {
                            System.out.println("Login Failed! Invalid ID or password.");
                        }

                    } catch (Exception e) {
                        System.out.println("Login error: " + e.getMessage());
                    }
                    break;

                case 3:
                    System.out.println("\nThank you for using ArthVikas Bank. Goodbye!");
                    sc.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice! Please enter 1, 2, or 3.");
            }
        }
    }
}