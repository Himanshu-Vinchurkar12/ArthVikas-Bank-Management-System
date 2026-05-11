# 🏦 ArthVikas Bank Management System  

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Language](https://img.shields.io/badge/language-Java-orange)
![Database](https://img.shields.io/badge/database-MySQL-blue)
![Architecture](https://img.shields.io/badge/architecture-Layered-success)

ArthVikas Bank Management System is a secure and modular **console-based banking application** developed using **Core Java, JDBC, and MySQL**.  

The project simulates real-world banking operations such as:
- Account Creation  
- Secure Login Authentication  
- Deposit & Withdrawal  
- Fund Transfer  
- Transaction History Management  

Built using **Object-Oriented Programming principles** and a **Layered Architecture**, the system ensures clean code structure, modularity, and secure transaction handling.

---

# 🚀 Features  

- 🔐 **Secure Authentication** – Login system with SHA-256 password hashing  
- 👤 **Account Management** – Create and manage user bank accounts  
- 💰 **Banking Operations** – Deposit, withdrawal, and balance inquiry  
- 🔄 **Fund Transfer** – Secure money transfer between users  
- 📜 **Transaction History** – Track all banking transactions  
- 🗄 **JDBC + MySQL Integration** – Persistent database storage  
- ⚠️ **Custom Exception Handling** – Handles invalid transactions safely  
- 🏗 **Layered Architecture** – Controller, Service, DAO, and Model separation  
- ✅ **Input Validation** – Secure and reliable transaction processing  

---

# 🧰 Tech Stack  

- **Java** – Core programming language  
- **JDBC** – Database connectivity  
- **MySQL** – Relational database  
- **Collections Framework** – Data management  
- **OOP Concepts** – Encapsulation, Inheritance, Polymorphism, Abstraction  
- **SHA-256** – Password hashing security  
- **Git & GitHub** – Version control  

---

# 🏗 System Architecture  

```text
+-------------------+
|   User Console    |
+-------------------+
          |
          v
+-------------------+
|    Controller     |
+-------------------+
          |
          v
+-------------------+
|     Services      |
+-------------------+
          |
          v
+-------------------+
|        DAO        |
+-------------------+
          |
          v
+-------------------+
|      MySQL DB     |
+-------------------+
```

## Project Structure

```text
ArthVikas-Bank-System/
│
├── src/
│   ├── controller/
│   ├── service/
│   ├── dao/
│   ├── model/
│   ├── exception/
│   ├── util/
│   └── Main.java
│
├── database/
│   └── schema.sql
│
├── screenshots/
│
└── README.md
```

## Banking Operations Flow
```text
Start
  |
Register / Login
  |
Authentication
  |
Dashboard
  ├── Deposit
  ├── Withdraw
  ├── Transfer
  ├── Transaction History
  └── Balance Inquiry
  |
Logout
```

---

## 🗄 Database Design  

### 👤 Users Table  

| Field | Type |
|-------|------|
| id | INT |
| full_name | VARCHAR |
| email | VARCHAR |
| password_hash | VARCHAR |
| balance | DOUBLE |

---

### 💳 Transactions Table  

| Field | Type |
|-------|------|
| transaction_id | INT |
| sender_id | INT |
| receiver_id | INT |
| amount | DOUBLE |
| transaction_type | VARCHAR |
| timestamp | DATETIME |

---

## 🔒 Security Features  

- 🔐 **SHA-256 Password Encryption** – Secure storage of user passwords  
- ⚠️ **Custom Exception Handling** – Handles insufficient balance and invalid operations  
- 🔄 **Synchronized Fund Transfers** – Ensures safe and reliable transactions  
- 🛡️ **Input Validation** – Prevents invalid or malicious transaction inputs  

---
---

## 📚 Learning Outcomes  

Through this project, I gained hands-on experience in:  

- ☕ **Core Java Development**  
- 🔌 **JDBC Connectivity & Database Integration**  
- 🗄️ **MySQL Database Management**  
- 🔐 **Secure Authentication Systems**  
- ⚠️ **Exception Handling & Validation**  
- 🏗️ **Scalable Software Architecture**  

---

## 👨‍💻 Author  

# Himanshu Vinchurkar  

### ☕ Java Developer | 📱 Mobile App Developer | ⚙️ Backend Development Enthusiast  

🔗 GitHub: [Himanshu-Vinchurkar12](https://github.com/Himanshu-Vinchurkar12)

---

