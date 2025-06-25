---

🎯 **Task Manager** isn’t just another to-do list. It’s a minimalist yet powerful console application for managing complex dependencies between tasks and subtasks. Perfect for technical projects where one task blocks the completion of others.

- 🔒 Prevents task completion until all subtasks are marked as done. 
- 📊 Generates reports on overdue tasks and weekly progress.
---
Demo 
---


---

## ⚙️ 3. Installation & Run

### ✅ Requirements

* Java 17 or 21
* Maven (or use the provided wrapper)
* (Optional) PostgreSQL or H2 for persistence

---

### 🚀 Quick Start


# Clone the repository
git clone https://github.com/desser2002/TaskManager.git
cd task-manager

# Build the project
./mvnw clean install

# Run the application
java -jar target/task-manager.jar

---

### 🧪 Run Tests

./mvnw test

---

### ℹ️ Notes

The application uses a **console interface**. After launch, you will be guided through task creation and management via interactive prompts.

---


