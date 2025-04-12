# JDBC w Javie

## 1. Wprowadzenie

JDBC (Java Database Connectivity) to API Java, które umożliwia komunikację z różnymi bazami danych poprzez standardowy
zestaw interfejsów i klas. Pozwala na wykonywanie operacji takich jak zapytania SQL, aktualizacje i zarządzanie
połączeniami.

## 2. Sterowniki JDBC i URL Bazy Danych

### Pobieranie Sterownika JDBC

Aby korzystać z JDBC, należy pobrać odpowiedni sterownik dla używanej bazy danych. Sterowniki te są zazwyczaj dostępne
jako pliki JAR. Na przykład, aby uzyskać sterownik dla PostgreSQL, można użyć menedżera zależności, takiego jak Maven:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.2.20</version>
</dependency>
```

### Struktura URL Bazy Danych

Każda baza danych ma swoją własną strukturę URL. Przykład dla PostgreSQL:

```
jdbc:postgresql://localhost:5432/nazwa_bazy_danych?user=nazwa_uzytkownika&password=haslo
```

Gdzie `localhost` to adres serwera, `5432` to port, `nazwa_bazy_danych` to nazwa bazy danych, a `user` i `password` to
dane do logowania.

## 3. Kluczowe Klasy JDBC i Interfejsy

### 3.1 `DriverManager`

`DriverManager` to klasa odpowiedzialna za zarządzanie sterownikami JDBC. Pozwala na uzyskanie połączenia z bazą danych
poprzez załadowanie odpowiedniego sterownika.

**Najczęściej wykorzystywane metody:**

- `getConnection(String url, String user, String password)`: nawiązuje połączenie z bazą danych.

### 3.2 `Connection`

Obiekt `Connection` reprezentuje połączenie z bazą danych. Oferuje metody do zarządzania połączeniem oraz transakcjami.

**Najczęściej wykorzystywane metody:**

- `createStatement()`: tworzy obiekt `Statement`.
- `prepareStatement(String sql)`: tworzy obiekt `PreparedStatement`.
- `close()`: zamyka połączenie.

### 3.3 `Statement` vs `PreparedStatement`

`Statement` jest używany do wykonywania zapytań SQL bez parametrów, natomiast `PreparedStatement` jest rozszerzeniem
`Statement` pozwalającym na tworzenie zapytań z parametrami, co zapewnia lepszą wydajność i ochronę przed atakami typu
SQL Injection.
> **Ciekawostka**: SQL Injection to technika ataku, która polega na wstrzyknięciu szkodliwego kodu SQL do zapytania w
> celu uzyskania nieautoryzowanego dostępu do danych.

---
Parametry w `PreparedStatement` pozwalają na dynamiczne wstawianie wartości do zapytań SQL. Można ustawiać różne typy
danych za pomocą metod takich jak `setString(int parameterIndex, String value)` lub
`setInt(int parameterIndex, int value)`. Istnieje wiele metod ustawiania różnych typów danych, np. `setLong`,
`setDouble`, `setObject` itd.

```java
String SQL = "INSERT INTO users(username, age) VALUES(?, ?)";
PreparedStatement preparedStatement = conn.prepareStatement(SQL);
preparedStatement.setString(1, "jankow");
preparedStatement.setInt(2, 35);
```

---
**Najczęściej wykorzystywane metody w `Statement`:**

- `execute(String sql)`: wykonuje dowolne zapytanie SQL i zwraca `boolean`.
- `executeQuery(String sql)`: wykonuje zapytanie SELECT i zwraca `ResultSet` zawierający wyniki.
- `executeUpdate(String sql)`: wykonuje zapytanie INSERT, UPDATE lub DELETE i zwraca `int` reprezentujący liczbę
  zmienionych wierszy.

Chociaż `execute()` może być użyte do dowolnego zapytania, `executeQuery()` i `executeUpdate()` są bardziej specyficzne
i zwracają konkretne typy wyników, co zwiększa czytelność kodu i redukuje ryzyko błędów.

### 3.4 `ResultSet`

`ResultSet` jest interfejsem, który przechowuje wyniki zapytania SQL i pozwala na iterowanie po wynikach oraz
odczytywanie danych.

**Najczęściej wykorzystywane metody:**

- `next()`: przechodzi do następnego wiersza wyników. Zwraca `true` jeśli istnieje kolejny wiersz, w przeciwnym razie
  `false`.
- `getString(String columnLabel)`: zwraca wartość kolumny jako `String`. Istnieje wiele metod, takich jak `getInt`,
  `getLong`, `getDouble` itd., aby uzyskać różne typy danych z kolumny.
- `close()`: zamyka `ResultSet` i zwalnia wszystkie zasoby z nim związane, takie jak otwarte połączenia z bazą danych.

## 4. Tworzenie Połączenia z Bazą Danych

Przykład kodu do nawiązania połączenia z bazą danych PostgreSQL:

```java
public static Connection getConnection() {
    Connection connection = null;
    try {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydatabase", "user", "password");
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return connection;
}
```

## 5. Wykonywanie Zapytania i Aktualizacja Danych

### 5.1 `execute()`

- Może być używane zarówno do zapytań zwracających dane (SELECT), jak i zmieniających dane (INSERT, UPDATE, DELETE).
- Zwraca `boolean`: `true` jeśli pierwszym wynikiem jest `ResultSet`, `false` jeśli jest to aktualizacja danych.
- Używane, gdy zapytanie może zwrócić różne wyniki (zarówno dane, jak i liczbę zmienionych rekordów).

```java
boolean isResultSet = statement.execute(selectSQL);
if (isResultSet) {
    ResultSet rs = statement.getResultSet();
} else {
    int updateCount = statement.getUpdateCount();
}
```

### 5.2 `executeUpdate()`

- Używane do zapytań, które zmieniają dane (INSERT, UPDATE, DELETE).
- Zwraca `int`: liczbę zmienionych wierszy.
- Przeznaczone do operacji, które nie zwracają wyników w postaci zestawu danych.

```java
String insertSQL = "INSERT INTO users (username, password) VALUES ('user', 'password')";
Statement statement = connection.createStatement();
int rowsInserted = statement.executeUpdate(insertSQL);
```

### 5.3 `executeQuery()`

- Używane wyłącznie do zapytań SELECT.
- Zwraca `ResultSet` zawierający wyniki zapytania.
- Przeznaczone do operacji, które zwracają dane.

```java
String selectSQL = "SELECT * FROM users";
ResultSet resultSet = statement.executeQuery(selectSQL);
```

## 6. Pobieranie Klucza Głównego po wstawieniu danych

Aby uzyskać wygenerowany klucz główny po wstawieniu danych, należy użyć `PreparedStatement` z opcją
`RETURN_GENERATED_KEYS`, a następnie pobrać klucz korzystając z `ResultSet`:

```java
String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
try (PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
    preparedStatement.setString(1, username);
    preparedStatement.setString(2, password);
    preparedStatement.executeUpdate();

    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
            int id = generatedKeys.getInt(1);
        }
    }
}
```

## 7. Praca z danymi w bazie na przykładzie klasy `User`

### Definicja klasy `User`

```java
public class User {
    private int id;
    private String username;
    private String password;

    // Gettery i settery
}
```

### 7.1 Wstawianie Obiektu `User` do Bazy Danych

```java
public void saveUser(User user, Connection connection) {
    String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
      }
}
    
```

### 7.2 Odczytywanie Obiektu `User` z Bazy Danych

```java
public User getUserById(int id, Connection connection) {
    String sql = "SELECT id, username, password FROM users WHERE id = ?";
    User user = null;
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setInt(1, id);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return user;
}
```

### 7.3 Odczytywanie kolekcji obiektów `User` z Bazy Danych

```java
public List<User> getAllUsers(Connection connection) {
    List<User> users = new ArrayList<>();
    String sql = "SELECT id, username, password FROM users";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
         ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
            User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            users.add(user);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return users;
}
```

> Wskazówka: zwróć uwagę na różnicę w odczytywaniu pojedynczego wiersza od wielu wierszy

## 8. Obsługa Błędów i Wyjątków

Obsługa błędów w JDBC jest kluczowa dla tworzenia niezawodnych aplikacji. Najczęściej używanym podejściem jest
stosowanie konstrukcji `try-with-resources`, która automatycznie zamyka zasoby po ich użyciu.

Podstawowe wyjątki JDBC to:

- `SQLException`: podstawowy wyjątek informujący o błędach SQL lub problemach z komunikacją z bazą danych.
- `SQLTimeoutException`: wyjątek sygnalizujący przekroczenie limitu czasu połączenia.
- `SQLSyntaxErrorException`: wyjątek związany z błędami składni SQL.

Przykład obsługi błędów z użyciem `try-with-resources`:

```java
try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydatabase", "user", "password")) {
        // Tworzenie połączenia i operacje
        } catch (SQLException e){
        e.printStackTrace();
        }
}
```

# Projekt JDBC w Javie z Kompozycją Encji

## Opis
Celem tego projektu jest stworzenie aplikacji Java, która będzie wykorzystywać JDBC do zarządzania dwoma encjami: `User` i `Profile`. Encja `User` powinna zawierać kompozycję klasy `Profile`, co oznacza, że użytkownik posiada przypisany profil. Aplikacja będzie zawierała warstwę repozytorium do wykonywania operacji CRUD na bazie danych oraz warstwę Main, gdzie będą uruchamiane operacje.

## Wymagania
1. **Encje:**
  - Stwórz dwie klasy:
    - `User`: zawiera pola takie jak `id`, `username`, `password` oraz `Profile`.
    - `Profile`: zawiera pola `id`, `email` oraz `phone`.

2. **Baza Danych:**
  - Utwórz dwie tabele:
    - Tabela `users` zawiera pola `id`, `username`, `password` oraz `profile_id` (klucz obcy do tabeli `profiles`).
    - Tabela `profiles` zawiera pola `id`, `email`, `phone`.

3. **Repozytorium:**
  - Zaimplementuj warstwę repozytorium z operacjami CRUD dla encji `User` i `Profile`.
  - Operacje, które musisz zaimplementować:
    - Pobieranie użytkownika po ID, wraz z jego profilem.
    - Pobieranie wszystkich użytkowników.
    - Zapis użytkownika wraz z jego profilem do bazy danych.

4. **Warstwa Main:**
  - Zaimplementuj logikę w klasie `Main`, która będzie korzystać z repozytorium:
    - Stwórz użytkownika z przypisanym profilem i zapisz go do bazy danych.
    - Pobierz i wyświetl użytkownika z bazy danych.

5. **Testy Jednostkowe:**
  - Stwórz testy jednostkowe dla metod repozytorium, aby upewnić się, że operacje CRUD działają poprawnie.
  - Stwórz testy jednostkowe w oparciu o bazę danych in-memory (H2) oraz testy jednostkowe z symulacją połączenia z JDBC za pomocą Mockito
  - Testy powinny obejmować:
    - Pobieranie użytkownika po ID.
    - Pobieranie wszystkich użytkowników.
    - Zapis użytkownika i sprawdzenie, czy dane są poprawnie zapisane.

## Kroki Do Wykonania

1. Skonfiguruj bazę danych PostgreSQL z tabelami `users` i `profiles`.
2. Dodaj zależności JDBC do projektu (np. PostgreSQL JDBC driver).
3. Zaimplementuj klasy `User` i `Profile` oraz warstwę repozytorium.
4. W warstwie Main przetestuj zapisywanie i pobieranie użytkowników z profilami.
5. Zaimplementuj testy jednostkowe z użyciem JUnit, Mockito oraz implementacji H2, aby przetestować metody repozytorium.
