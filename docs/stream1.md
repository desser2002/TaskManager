# Własna implementacja Stream w Javie - część 1

## 1. Wstęp do Stream w Javie

Stream w Javie pozwala na przetwarzanie danych w sposób funkcyjny, nie modyfikując źródłowej kolekcji. Obsługuje operacje takie jak filtrowanie (`filter`), przekształcanie (`map`), redukcja (`reduce`), a także wykonywanie operacji na elementach (`forEach`). W tym artykule zaimplementujemy własną uproszczoną wersję `Stream`, aby lepiej zrozumieć, jak działają te mechanizmy.

## 2. Struktura klasy `MyStream`

Zaczynamy od struktury klasy `MyStream`. Dodajemy metodę `toList()`, która pozwoli łatwo przekształcić strumień na listę do testowania.

```java
public class MyStream<T> {

    private final List<T> elements;

    public MyStream(List<T> elements) {
        this.elements = elements;
    }

    public List<T> toList() {
        return new ArrayList<>(elements);
    }

    // Tutaj będą implementowane kolejne metody
}
```

## 3. Zadania do zaimplementowania

Poniżej znajdują się zadania, które pomogą Ci stopniowo implementować metody w klasie `MyStream`. Każde zadanie zawiera sygnaturę metody oraz opis, jak powinna działać.

### Zadanie 1: Implementacja metody `filter`

**Opis:**
Metoda `filter` przetwarza strumień, pozostawiając tylko elementy, które spełniają warunek określony przez `Predicate<T>`.

**Sygnatura metody:**
```java
public MyStream<T> filter(Predicate<T> predicate)
```

**Interfejs `Predicate`:**
```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}
```

**Przykład użycia:**
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
MyStream<Integer> stream = new MyStream<>(numbers);
List<Integer> evenNumbers = stream.filter(n -> n % 2 == 0).toList();
System.out.println(evenNumbers); // Wyjście: [2, 4]
```

### Zadanie 2: Implementacja metody `map`

**Opis:**
Metoda `map` przekształca elementy strumienia za pomocą funkcji określonej przez `Function<T, R>`.

**Sygnatura metody:**
```java
public <R> MyStream<R> map(Function<T, R> mapper)
```

**Interfejs `Function`:**
```java
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}
```

**Przykład użycia:**
```java
List<String> names = Arrays.asList("Anna", "Bartek", "Cecylia");
MyStream<String> stream = new MyStream<>(names);
List<String> upperNames = stream.map(String::toUpperCase).toList();
System.out.println(upperNames); // Wyjście: [ANNA, BARTEK, CECYLIA]
```

### Zadanie 3: Implementacja metody `forEach`

**Opis:**
Metoda `forEach` wykonuje podaną akcję dla każdego elementu strumienia.

**Sygnatura metody:**
```java
public void forEach(Consumer<T> action)
```

**Interfejs `Consumer`:**
```java
@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}
```

**Przykład użycia:**
```java
List<String> names = Arrays.asList("Anna", "Bartek", "Cecylia");
MyStream<String> stream = new MyStream<>(names);
stream.forEach(System.out::println);
// Wyjście:
// Anna
// Bartek
// Cecylia
```

### Zadanie 4: Złożona operacja: `filter`, `map`, `forEach`

Po zaimplementowaniu metod `filter`, `map`, i `forEach`, możesz wykonać to zadanie:

**Opis:**
Przekształć listę nazw, które mają długość większą niż 4 znaki, zmieniając je na wielkie litery, a następnie wyświetl je.

**Dane wejściowe:**
- Lista: `["Anna", "Bartek", "Cecylia", "Dan"]`

**Dane wyjściowe:**
- `["BARTEK", "CECYLIA"]`

**Podpowiedź:**
Użyj `filter` do filtrowania, `map` do przekształcenia i `forEach` do wyświetlenia wyników.

### Zadanie 5: Implementacja metody `count`

**Opis:**
Metoda `count` zwraca liczbę elementów w strumieniu.

**Sygnatura metody:**
```java
public long count()
```

**Przykład użycia:**
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
MyStream<Integer> stream = new MyStream<>(numbers);
long totalElements = stream.count();
System.out.println(totalElements); // Wyjście: 5
```

### Zadanie 6: Implementacja metody `distinct`

**Opis:**
Metoda `distinct` zwraca nowy strumień z unikalnymi elementami.

**Sygnatura metody:**
```java
public MyStream<T> distinct()
```

**Przykład użycia:**
```java
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4);
MyStream<Integer> stream = new MyStream<>(numbers);
List<Integer> uniqueNumbers = stream.distinct().toList();
System.out.println(uniqueNumbers); // Wyjście: [1, 2, 3, 4]
```

### Zadanie 7: Implementacja metody `limit`

**Opis:**
Metoda `limit` ogranicza strumień do podanej liczby elementów.

**Sygnatura metody:**
```java
public MyStream<T> limit(int maxSize)
```

**Przykład użycia:**
```java
List<String> names = Arrays.asList("Anna", "Bartek", "Cecylia", "Daniel");
MyStream<String> stream = new MyStream<>(names);
List<String> limitedNames = stream.limit(2).toList();
System.out.println(limitedNames); // Wyjście: [Anna, Bartek]
```

Po zaimplementowaniu metody `limit`, możesz wykonać poniższe zadanie.

### Zadanie 8: Złożona operacja: `distinct`, `limit`, `count`

**Opis:**
Policz unikalne elementy listy, ograniczając je do pierwszych 3 wyników.

**Dane wejściowe:**
- Lista: `[1, 2, 2, 3, 3, 3, 4]`

**Dane wyjściowe:**
- Liczba elementów: `3`


### Zadanie 9: Implementacja metody `skip`

**Opis:**
Metoda `skip` pomija podaną liczbę elementów w strumieniu.

**Sygnatura metody:**
```java
public MyStream<T> skip(int n)
```

**Przykład użycia:**
```java
List<String> names = Arrays.asList("Anna", "Bartek", "Cecylia", "Daniel");
MyStream<String> stream = new MyStream<>(names);
List<String> skippedNames = stream.skip(2).toList();
System.out.println(skippedNames); // Wyjście: [Cecylia, Daniel]
```

### Zadanie 10: Implementacja metody `sorted`

**Opis:**
Metoda `sorted` sortuje elementy strumienia na podstawie podanego komparatora.

**Sygnatura metody:**
```java
public MyStream<T> sorted(Comparator<T> comparator)
```

**Interfejs `Comparator`:**
```java
@FunctionalInterface
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```

**Przykład użycia:**
```java
List<String> names = Arrays.asList("Daniel", "Anna", "Bartek", "Cecylia");
MyStream<String> stream = new MyStream<>(names);
List<String> sortedNames = stream.sorted(String::compareTo).toList();
System.out.println(sortedNames); // Wyjście: [Anna, Bartek, Cecylia, Daniel]
```

### Zadanie 11: Złożona operacja: `skip`, `sorted`, `forEach`

Po zaimplementowaniu metod `skip`, `sorted`, i `forEach`, wykonaj to zadanie:

**Opis:**
Posortuj listę nazw, pomijając pierwsze 2 elementy, a następnie wyświetl pozostałe.

**Dane wejściowe:**
- Lista: `["Daniel", "Anna", "Bartek", "Cecylia"]`

**Dane wyjściowe:**
- `["Cecylia", "Daniel"]`

### Zadanie 12: Implementacja metody `findFirst`

**Opis:**
Metoda `findFirst` zwraca pierwszy element strumienia.

**Sygnatura metody:**
```java
public Optional<T> findFirst()
```

**Przykład użycia:**
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
MyStream<Integer> stream = new MyStream<>(numbers);
Optional<Integer> firstElement = stream.findFirst();
firstElement.ifPresent(System.out::println); // Wyjście: 1
```

## 4. Zadania złożone

Po zaimplementowaniu wszystkich metod, spróbuj rozwiązać te bardziej złożone zadania.

### Zadanie 13: Filtracja i przekształcenie elementów

**Opis:**
Przefiltruj listę liczb, pozostawiając tylko te większe niż 10, przekształć je, dodając do nich 5, a następnie znajdź pierwszy taki element.

**Dane wejściowe:**
- Lista: `[5, 8, 12, 15, 22]`

**Dane wyjściowe:**
- Pierwszy element: `17`

**Podpowiedź:**
Możesz użyć `filter`, `map`, oraz `findFirst`.

### Zadanie 14: Liczba unikalnych, ograniczonych liczb

**Opis:**
Policz unikalne liczby większe niż 5, ograniczając wynik do pierwszych 3 elementów.

**Dane wejściowe:**
- Lista: `[1, 6, 7, 8, 6, 9, 10]`

**Dane wyjściowe:**
- Liczba elementów: `3`

**Podpowiedź:**
Użyj metod `distinct`, `filter`, `limit` oraz `count`.

### Zadanie 15: Znalezienie wartości po sortowaniu

**Opis:**
Posortuj listę nazw, pomijając pierwsze 3 elementy, a następnie znajdź pierwszy taki element.

**Dane wejściowe:**
- Lista: `["Daniel", "Anna", "Bartek", "Cecylia", "Ela"]`

**Dane wyjściowe:**
- Pierwszy element: `"Daniel"`

**Podpowiedź:**
Możesz użyć `sorted`, `skip` i `findFirst`.

### Zadanie 16: Zliczanie przefiltrowanych wartości

**Opis:**
Policz liczbę elementów, które są większe niż 50 po przekształceniu ich o wartość +20.

**Dane wejściowe:**
- Lista: `[10, 30, 40, 60, 80]`

**Dane wyjściowe:**
- Liczba elementów: `3`

**Podpowiedź:**
Użyj `map`, `filter`, oraz `count`.

### Zadanie 17: Operacje na liście nazw

**Opis:**
Przekształć listę nazw, które zaczynają się na literę "A", do formy wielkich liter, a następnie wyświetl ich liczbę.

**Dane wejściowe:**
- Lista: `["Anna", "Agnieszka", "Bartek", "Cecylia", "Adam"]`

**Dane wyjściowe:**
- Liczba elementów: `3`

**Podpowiedź:**
Użyj `filter`, `map` oraz `count`.
