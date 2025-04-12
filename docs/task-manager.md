## Wymagania Projektu: **Task Manager**

---

### 1. **Opis projektu**

Aplikacja do zarządzania zadaniami i podzadaniami. Każde zadanie może mieć wiele podzadań, które muszą być ukończone,
zanim zadanie nadrzędne może zostać oznaczone jako ukończone.

---

### 2. **Funkcjonalności (MVP)**

#### Zadania:

- Dodanie nowego zadania z nazwą i opcjonalnym opisem
- Przypisanie daty rozpoczęcia i deadline
- Lista wszystkich zadań (aktywnych, zakończonych, z opóźnieniem)
- Zmiana statusu zadania (`NEW`, `IN_PROGRESS`, `DONE`)
- Zarządzanie zadaniem (dodawanie, usuwanie, edytowanie)

#### Podzadania:

- Zarządzanie podzadaniem w istniejącym zadaniu (dodawanie, usuwanie, edytowanie, przesuwanie do innego zadania)
- Blokowanie możliwości zakończenia zadania, dopóki podzadania nie są `DONE`

#### Raporty:

- Lista zadań opóźnionych
- Lista zadań według statusu
- Liczba zadań wykonanych w danym tygodniu

---

### 3. **Architektura (Hexagonal / Ports & Adapters)**

#### Warstwy:

- **Application**: logika przypadków użycia (use cases), inicjowanie działania aplikacji (wywołuje logikę domenową)
- **Domain**: logika biznesowa, interfejsy portów, modele domenowe
- **Infrastructure**: Klasy konfiguracyjne, implementacje portów (adaptery), połączenia z bazą danych, encje bazodanowe

---

### 4. **Technologie**

- **Java 17/21**
- **Maven**, opcjonalnie Gradle
- **JDBC** – PostgreSQL lub H2
- **JUnit 5** – testy jednostkowe
- **Mockito** – mockowanie portów
- **Log4j2** – logowanie
- **Checkstyle + PMD** – analiza statyczna
- **GitHub Actions (CI)** – automatyczne testy, styl, pmd

---

### 5. **Wymagania niefunkcjonalne**

- Pokrycie testami jednostkowymi > 80%
- Architektura oparta na porty i adaptery
- CI pipeline z testami, PMD i Checkstyle
- Dobre logowanie (np. tworzenie taska, błąd przy statusie)

---

### 6. **Obsługa wyjątków**

- Obsługa błędów zgodna z architekturą heksagonalną — wyjątki domenowe nie zależą od infrastruktury
- Własne typy wyjątków domenowych (np. `InvalidTaskStatusException`, `SubtasksNotCompletedException`,
  `TaskNotValidException`)
- W warstwie Application odpowiednie mapowanie wyjątków domenowych na błędy techniczne (komunikaty konsolowe)
- Logowanie błędów w adapterach (np. błąd bazy, brak połączenia)

### 7. **Testy**

- Testy jednostkowe logiki domenowej
- Mockowanie repozytoriów do testów logiki
- Testy walidujące spójność statusów (np. nie można oznaczyć jako DONE, jeśli subtaski nie są DONE)
- Weryfikacja przypadków brzegowych i wyjątków

---
