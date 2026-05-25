# 🌾 Fallah Application - Agricultural Store Management System

Modern mobile application for managing agricultural tools stores using Kotlin and Jetpack Compose.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.6-green)
![Room](https://img.shields.io/badge/Room-2.6-orange)
![Hilt](https://img.shields.io/badge/Hilt-2.48-purple)
![API](https://img.shields.io/badge/API-24+-brightgreen)

---

# 📱 Overview

**Fallah Application** is a complete agricultural store management mobile app designed for farmers and agricultural businesses.

The app helps manage:

- Products & Inventory
- Sales & Invoices
- Customers & Debts
- Reports & Analytics
- PDF Invoice Sharing
- WhatsApp Integration

Supports both **Arabic** and **English** languages.

---

# ✨ Features

- 🛍️ Product Management
- 🛒 Shopping Cart System
- 💰 Sales Tracking
- 📊 Debt Management
- 👥 Customer Management
- 📈 Reports & Statistics
- 🌍 Arabic & English Support
- 🌙 Dark / Light Theme
- 🖨️ PDF Invoice Generation
- 📤 WhatsApp Sharing
- 💾 Backup & Restore
- 🔔 Smart Notifications

---

# 🛠️ Tech Stack

| Technology | Version |
|------------|----------|
| Kotlin | 2.0 |
| Jetpack Compose | 1.6 |
| Room Database | 2.6 |
| Dagger Hilt | 2.48 |
| Coroutines | 1.7 |
| Material 3 | 1.2 |

---

# 🧱 Architecture

```text
UI (Jetpack Compose)
        ↓
ViewModel
        ↓
Repository
        ↓
Room Database
```

---

# 📁 Project Structure

```text
FallahApplication/
│
├── app/
│   ├── data/
│   ├── di/
│   ├── ui/
│   ├── uit/
│   ├── viewmodel/
│   └── MainActivity.kt
│
├── README.md
├── LICENSE
└── build.gradle.kts
```

---

# 🚀 Installation

## Clone Project

```bash
git clone https://github.com/enori-ben/FallahApplication.git
cd FallahApplication
```

## Run Project

Open the project in Android Studio and press ▶ Run

Or use:

```bash
./gradlew assembleDebug
```

---

# 🗄️ Database Example

```sql
CREATE TABLE products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    price REAL NOT NULL,
    quantity INTEGER NOT NULL
);
```

---

# 📸 Screenshots

Coming Soon...

---

# 👨‍💻 Developer

**NourEddine BenAttous**

GitHub: https://github.com/enori-ben

Email: enoridz11@gmail.com

---

# 📄 License

MIT License © 2026 enori-ben

---

# ⭐ Support

If you like this project, give it a ⭐ on GitHub.

---

<div align="center">

Made with ❤️ for farmers and agricultural businesses

</div>
