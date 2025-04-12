# Architektura Heksagonalna w Javie - wstęp

## Wstęp

Architektura heksagonalna, znana również jako architektura "Ports and Adapters", jest wzorcem projektowym promującym oddzielenie logiki biznesowej od technologii zewnętrznych. **Niniejszy artykuł przedstawia uproszczoną wersję tej architektury**, aby ułatwić zrozumienie podstawowych koncepcji i zastosowań w języku Java. Omówimy historię jej powstania, kluczowe komponenty oraz przedstawimy praktyczne przykłady implementacji.

---

## 1. Historia powstania architektury heksagonalnej

### Geneza i motywacje

Architektura heksagonalna została zaproponowana przez Alistaira Cockburna w 2005 roku jako odpowiedź na ograniczenia tradycyjnych architektur warstwowych. Cockburn zauważył, że aplikacje często są silnie zależne od technologii zewnętrznych, co utrudnia ich testowanie, rozwój i utrzymanie. Celem było stworzenie wzorca, który umożliwi budowanie systemów bardziej odpornych na zmiany technologiczne.

### Twórca architektury: Alistair Cockburn

Alistair Cockburn jest jednym z pionierów metodologii Agile i autorem wielu publikacji na temat inżynierii oprogramowania. Wprowadzając architekturę heksagonalną, dążył do promowania zasad, które zwiększają elastyczność i trwałość aplikacji w zmieniającym się środowisku technologicznym.

### Pochodzenie nazwy "heksagonalna"

Nazwa "heksagonalna" pochodzi od sześciokąta używanego w diagramach do wizualizacji tej architektury. W tym modelu:

- **Sześciokąt** reprezentuje centralną część aplikacji – logikę biznesową.
- **Boki sześciokąta** symbolizują różne porty komunikacyjne, przez które aplikacja wchodzi w interakcje ze światem zewnętrznym.
- **Porty** to interfejsy, które umożliwiają komunikację między aplikacją a zewnętrznymi systemami, takimi jak bazy danych, interfejsy użytkownika czy usługi sieciowe.

Diagram heksagonalny obrazuje, że aplikacja może komunikować się w wielu kierunkach, a jej logika biznesowa pozostaje niezależna od zewnętrznych technologii. Sześciokąt symbolizuje wielokierunkowość interakcji, a liczba boków jest umowna i służy jedynie jako metafora.

---

## 2. Podstawy architektury heksagonalnej

### Czym jest architektura heksagonalna?

Architektura heksagonalna to wzorzec projektowy skupiający się na izolacji logiki biznesowej od technologii zewnętrznych. Główną ideą jest to, że centralna część aplikacji – domena – nie powinna być zależna od zewnętrznych interfejsów czy infrastruktury. Dzięki temu aplikacja staje się bardziej elastyczna i łatwiejsza w utrzymaniu.

### Kluczowe komponenty: model, logika biznesowa, porty i adaptery

- **Model**: Reprezentuje dane i struktury domenowe aplikacji. Są to klasy odzwierciedlające obiekty biznesowe i ich relacje.
- **Logika biznesowa (Service)**: Zawiera zasady i procesy biznesowe. Operuje na modelach, realizując funkcjonalności aplikacji.
- **Porty**: Interfejsy definiujące sposoby komunikacji z logiką biznesową.
- **Adaptery**: Implementacje portów, które dostosowują interfejsy do konkretnych technologii lub protokołów.

---

## 3. Porównanie z innymi architekturami

### Architektura warstwowa vs. heksagonalna

**Zalety i wady obu podejść w tabeli:**

| **Kryterium**                      | **Architektura Warstwowa**                                | **Architektura Heksagonalna**                                  |
|------------------------------------|-----------------------------------------------------------|----------------------------------------------------------------|
| **Zależności między komponentami** | Silne zależności między warstwami.                        | Niezależność logiki biznesowej od infrastruktury.              |
| **Testowalność**                   | Trudniejsza z powodu sprzężenia warstw.                   | Ułatwiona dzięki izolacji domeny.                              |
| **Elastyczność technologiczna**    | Ograniczona przez bezpośrednie zależności od technologii. | Wysoka dzięki portom i adapterom.                              |
| **Skalowalność i utrzymanie**      | Może być utrudnione przez złożoność zależności.           | Ułatwione przez modularność i jasny podział odpowiedzialności. |
| **Krzywa uczenia się**             | Mniejsza, prostsza do zrozumienia dla początkujących.     | Większa, wymaga zrozumienia koncepcji portów i adapterów.      |
| **Szybkość wdrożenia**             | Szybsze rozpoczęcie pracy w prostych projektach.          | Może wymagać więcej czasu na początkową konfigurację.          |

---

## 4. Struktura projektu w architekturze heksagonalnej

**Należy zauważyć, że struktura plików w architekturze heksagonalnej może się różnić w zależności od konkretnego projektu i potrzeb zespołu. Poniżej przedstawiamy uproszczony schemat struktury plików, który ma na celu zilustrowanie podstawowych koncepcji.**

### Foldery i ich zawartość

- **Domain**
  - **Model**: Klasy reprezentujące dane domenowe.
  - **Service**: Logika biznesowa aplikacji.
  - **Ports**: Interfejsy komunikacyjne (porty).
- **Infrastructure**
  - **Adapters**: Implementacje portów dostosowane do konkretnych technologii.
  - **Config**: Konfiguracje systemowe i zależności.

---

## 5. Praktyczne zastosowanie: Aplikacja "To-Do List"

### Opis funkcjonalności aplikacji

Aplikacja "To-Do List" umożliwia użytkownikom:

- Dodawanie nowych zadań.
- Oznaczanie zadań jako ukończone.
- Wyświetlanie listy zadań.

**Struktura plików aplikacji:**

```
to-do-list/
├── domain/
│   ├── model/
│   │   └── Task.java
│   ├── service/
│   │   └── TaskService.java
│   └── ports/
│       └── TaskRepositoryPort.java
├── infrastructure/
│   ├── adapters/
│   │   └── InMemoryTaskRepository.java
│   └── config/
└── Main.java
```

### Implementacja portu

**Port – interfejs repozytorium zadań**

```java
// domain/ports/TaskRepositoryPort.java
public interface TaskRepositoryPort {
    void save(Task task);
    Optional<Task> findById(String id);
    List<Task> findAll();
}
```

### Adapter

#### Adapter infrastruktury – InMemoryTaskRepository

```java
// infrastructure/adapters/InMemoryTaskRepository.java
public class InMemoryTaskRepository implements TaskRepositoryPort {
    private final Map<String, Task> tasks = new HashMap<>();

    @Override
    public void save(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }
}
```

### Logika biznesowa (Service)

```java
// domain/service/TaskService.java
public class TaskService {
    private final TaskRepositoryPort repository;

    public TaskService(TaskRepositoryPort repository) {
        this.repository = repository;
    }

    public void addTask(String title) {
        String id = UUID.randomUUID().toString();
        Task task = new Task(id, title, false);
        repository.save(task);
    }

    public void completeTask(String id) {
        Optional<Task> optionalTask = repository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setCompleted(true);
            repository.save(task);
        } else {
            throw new IllegalArgumentException("Task not found");
        }
    }

    public List<Task> getTasks() {
        return repository.findAll();
    }
}
```

### Model domenowy

```java
// domain/model/Task.java
public class Task {
    private final String id;
    private final String title;
    private boolean completed;

    public Task(String id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    // Gettery i settery
    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
```

### Uruchomienie aplikacji

```java
// Main.java
public class Main {
    public static void main(String[] args) {
        // Konfiguracja
        TaskRepositoryPort repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);

        // Dodawanie zadań
        service.addTask("Nauczyć się architektury heksagonalnej");
        service.addTask("Napisać prostą aplikację w Javie");

        // Wyświetlanie zadań
        System.out.println("Lista zadań:");
        service.getTasks().forEach(task -> 
            System.out.println(task.getId() + ": " + task.getTitle() + " [Ukończone: " + task.isCompleted() + "]")
        );

        // Oznaczanie zadania jako ukończone
        String taskId = service.getTasks().get(0).getId();
        service.completeTask(taskId);

        // Wyświetlanie zaktualizowanej listy zadań
        System.out.println("\nPo ukończeniu zadania:");
        service.getTasks().forEach(task -> 
            System.out.println(task.getId() + ": " + task.getTitle() + " [Ukończone: " + task.isCompleted() + "]")
        );
    }
}
```

### Testy jednostkowe dla serwisu

```java
// domain/service/TaskServiceTest.java
public class TaskServiceTest {
    private TaskRepositoryPort repository;
    private TaskService service;

    @BeforeEach
    public void setUp() {
        repository = Mockito.mock(TaskRepositoryPort.class);
        service = new TaskService(repository);
    }

    @Test
    public void testAddTask() {
        // Given
        String title = "Testowe zadanie";

        // When
        service.addTask(title);

        // Then
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        Mockito.verify(repository).save(captor.capture());
        Task savedTask = captor.getValue();
        assertEquals(title, savedTask.getTitle());
        assertFalse(savedTask.isCompleted());
    }

    @Test
    public void testCompleteTask() {
        // Given
        String taskId = "1";
        Task task = new Task(taskId, "Zadanie do ukończenia", false);
        Mockito.when(repository.findById(taskId)).thenReturn(Optional.of(task));

        // When
        service.completeTask(taskId);

        // Then
        assertTrue(task.isCompleted());
        Mockito.verify(repository).save(task);
    }

    @Test
    public void testGetTasks() {
        // Given
        List<Task> tasks = Arrays.asList(
            new Task("1", "Zadanie 1", false),
            new Task("2", "Zadanie 2", true)
        );
        Mockito.when(repository.findAll()).thenReturn(tasks);

        // When
        List<Task> result = service.getTasks();

        // Then
        assertEquals(2, result.size());
        assertEquals("Zadanie 1", result.get(0).getTitle());
        assertEquals("Zadanie 2", result.get(1).getTitle());
    }
}
```

---

## 6. Inne przykłady zastosowań

### Aplikacja do tłumaczenia tekstu

#### Opis działania

Aplikacja tłumaczy podany tekst z jednego języka na inny, korzystając z zewnętrznych usług tłumaczeniowych.

**Struktura plików aplikacji:**

```
translation-app/
├── domain/
│   ├── service/
│   │   └── TranslationService.java
│   └── ports/
│       └── TranslationApiPort.java
├── infrastructure/
│   └── adapters/
│       ├── GoogleTranslateAdapter.java
│       └── DeepLAdapter.java
└── Main.java
```

#### Port

```java
// domain/ports/TranslationApiPort.java
public interface TranslationApiPort {
    String translateText(String text, String sourceLanguage, String targetLanguage);
}
```

#### Adaptery

**Adapter do Google Translate API**

```java
// infrastructure/adapters/GoogleTranslateAdapter.java
public class GoogleTranslateAdapter implements TranslationApiPort {
    @Override
    public String translateText(String text, String sourceLanguage, String targetLanguage) {
        // Implementacja integracji z Google Translate API
        return "Przetłumaczony tekst przez Google";
    }
}
```

**Adapter do DeepL API**

```java
// infrastructure/adapters/DeepLAdapter.java
public class DeepLAdapter implements TranslationApiPort {
    @Override
    public String translateText(String text, String sourceLanguage, String targetLanguage) {
        // Implementacja integracji z DeepL API
        return "Przetłumaczony tekst przez DeepL";
    }
}
```

#### Serwis tłumaczeń

```java
// domain/service/TranslationService.java
public class TranslationService {
    private final TranslationApiPort apiPort;

    public TranslationService(TranslationApiPort apiPort) {
        this.apiPort = apiPort;
    }

    public String translate(String text, String sourceLanguage, String targetLanguage) {
        return apiPort.translateText(text, sourceLanguage, targetLanguage);
    }
}
```

#### Testy jednostkowe dla serwisu tłumaczeń

```java
// domain/service/TranslationServiceTest.java
public class TranslationServiceTest {
    private TranslationApiPort apiPort;
    private TranslationService translationService;

    @BeforeEach
    public void setUp() {
        apiPort = Mockito.mock(TranslationApiPort.class);
        translationService = new TranslationService(apiPort);
    }

    @Test
    public void testTranslate() {
        // Given
        String text = "Hello";
        String sourceLanguage = "EN";
        String targetLanguage = "PL";
        Mockito.when(apiPort.translateText(text, sourceLanguage, targetLanguage)).thenReturn("Cześć");

        // When
        String result = translationService.translate(text, sourceLanguage, targetLanguage);

        // Then
        assertEquals("Cześć", result);
        Mockito.verify(apiPort).translateText(text, sourceLanguage, targetLanguage);
    }
}
```

---

### Wysyłanie informacji do innej mikrousługi

#### Opis scenariusza

Aplikacja musi wysłać powiadomienie do innej mikrousługi po wykonaniu pewnej akcji, np. po zarejestrowaniu nowego użytkownika.

**Struktura plików aplikacji:**

```
notification-app/
├── domain/
│   ├── service/
│   │   └── NotificationService.java
│   └── ports/
│       └── MessageSenderPort.java
├── infrastructure/
│   └── adapters/
│       ├── KafkaMessageSender.java
│       └── HttpMessageSender.java
└── Main.java
```

#### Port

```java
// domain/ports/MessageSenderPort.java
public interface MessageSenderPort {
    void sendMessage(String message);
}
```

#### Adaptery

**Adapter Kafka**

```java
// infrastructure/adapters/KafkaMessageSender.java
public class KafkaMessageSender implements MessageSenderPort {
    @Override
    public void sendMessage(String message) {
        // Implementacja wysyłania wiadomości przez Kafka
        System.out.println("Wiadomość wysłana przez Kafka: " + message);
    }
}
```

**Adapter HTTP Request**

```java
// infrastructure/adapters/HttpMessageSender.java
public class HttpMessageSender implements MessageSenderPort {
    @Override
    public void sendMessage(String message) {
        // Implementacja wysyłania żądania HTTP
        System.out.println("Wiadomość wysłana przez HTTP: " + message);
    }
}
```

#### Serwis powiadomień

```java
// domain/service/NotificationService.java
public class NotificationService {
    private final MessageSenderPort senderPort;

    public NotificationService(MessageSenderPort senderPort) {
        this.senderPort = senderPort;
    }

    public void notify(String message) {
        senderPort.sendMessage(message);
    }
}
```

#### Testy jednostkowe dla serwisu powiadomień

```java
// domain/service/NotificationServiceTest.java
public class NotificationServiceTest {
    private MessageSenderPort senderPort;
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        senderPort = Mockito.mock(MessageSenderPort.class);
        notificationService = new NotificationService(senderPort);
    }

    @Test
    public void testNotify() {
        // Given
        String message = "Nowy użytkownik został zarejestrowany.";

        // When
        notificationService.notify(message);

        // Then
        Mockito.verify(senderPort).sendMessage(message);
    }
}
```

---

## 7. Podsumowanie

Architektura heksagonalna to potężne narzędzie w arsenale projektanta oprogramowania. Poprzez oddzielenie logiki biznesowej od technologii zewnętrznych, umożliwia tworzenie aplikacji bardziej elastycznych, łatwych w utrzymaniu i testowaniu. **Należy jednak pamiętać, że przedstawione przykłady i struktury plików są uproszczone i mogą się różnić w zależności od konkretnych wymagań projektu.** Jej zalety są szczególnie widoczne w dużych i złożonych systemach, gdzie adaptacja do zmian jest kluczowa.