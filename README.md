🎯 **Task Manager** isn’t just another to-do list. It’s a minimalist yet powerful console application for managing complex dependencies between tasks and subtasks. Perfect for technical projects where one task blocks the completion of others.

- 🔒 Prevents task completion until all subtasks are marked as done. 
- 📊 Generates reports on overdue tasks and weekly progress.
---
Demo 
---


---
## ⚙️ Installation & Run

### ✅ Requirements

* Java 17 or 21
* Maven (or use the provided wrapper)
* (Optional) PostgreSQL or H2 for persistence

---
### 🚀 Quick Start

```bash
# Clone the repository
git clone https://github.com/desser2002/TaskManager.git
cd task-manager

# Build the project
./mvnw clean install

# Run the application
java -jar target/task-manager.jar
```

---

### 🧪 Run Tests

```bash
./mvnw test
```

---

### ℹ️ Notes

The application uses a **console interface**. After launch, you will be guided through task creation and management via interactive prompts.

---

## 🧾 Available Commands

### ✅ Task Commands

* `task create` – create a new task
* `task all` – list all tasks
* `task update` – update task name or description
* `task update time` – update start date or deadline
* `task update status` – change task status (`NEW`, `IN_PROGRESS`, `DONE`)
* `task delete` – delete a task

---

### ✅ Subtask Commands

* `subtask add` – add a subtask to a task
* `subtask` – list subtasks of a task
* `subtask update` – update subtask name or status
* `subtask delete` – delete a subtask
* `subtask move` – move a subtask to another task
* `subtask task` – show parent task for a subtask

---

### 📊 Report Commands

* `report status` – show tasks by status
* `report delayed` – list overdue tasks
* `report weekdone` – show tasks completed in the current week

---
 
## 🧱 Project Structure & Architecture

The project follows the **Hexagonal Architecture** (Ports & Adapters) principle, organizing code around clear responsibilities and separation of concerns.

### 🔹 `domen.application` — Application Layer

Contains the main entry point and logic that orchestrates user interaction.

* `handler` — Command-line handlers grouped by domain (`task`, `subtask`, `report`).
* `util` — Utility classes to run the application (`ConsoleApplication`, `Main`).
* `ConsoleHandler` — Central console interface handler.

### 🔹 `domen.domain` — Domain Layer

The core business logic and interfaces (ports).

* `model` — Core interfaces and services:

  * `TaskRepository`, `SubtaskRepository` — interfaces (ports)
  * `TaskService`, `SubtaskService`, `TaskReportService` — domain logic
* `exception` — Custom domain exceptions (`SubtasksNotCompletedException`, etc.)

### 🔹 `domen.infrastructure.database` — Infrastructure Layer

Implements repositories using SQL (adapters for the database).

* `TaskRepositorySQL`, `SubtaskRepositorySQL` — concrete adapters using JDBC

---

### 📁 Summary View

```
src/
└── main/
    └── java/
        └── domen/
            ├── application/
            │   ├── handler/              # CLI handlers
            │   ├── util/                 # App bootstrap & utils
            │   └── Main.java             # App entry point
            │
            ├── domain/
            │   ├── model/                # Business logic & interfaces
            │   └── exception/            # Domain exceptions
            │
            └── infrastructure/
                └── database/             # SQL adapters for repositories
```

---

## 📚 Technical Documentation

For more detailed documentation, check the following resources:

* 📄 [Database Schema](docs/database-schema.png)
* 🧩 [Domain Class Diagram](docs/domain-model.md)

---
