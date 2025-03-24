### Wstęp do Spring Framework i Spring Boot

Spring Framework to jeden z najpopularniejszych frameworków w świecie Java, oferujący elastyczne narzędzia do budowy nowoczesnych aplikacji. Spring Boot to rozszerzenie Spring Framework, które automatyzuje konfigurację i upraszcza proces tworzenia aplikacji.

W artykule przeprowadzimy Cię przez porównanie projektów z i bez Spring Boot oraz wyjaśnimy kluczowe zagadnienia, takie jak: beany, konteksty, kontener IoC, klasa konfiguracyjna, profile i mechanizmy takie jak `@Value` oraz `@ConditionalOnProperty`.

----

## Projekt bez Spring Boot

Bez Spring Boot musimy jawnie konfigurować i zarządzać kontekstem aplikacji. Poniżej przedstawiamy przykładową aplikację.

### Przykładowa aplikacja Spring bez Spring Boot

#### Klasa serwisowa

```java
import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String sayHello() {
        return "Hello, World!";
    }
}
```

#### Główna klasa aplikacji

```java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.example");
        HelloService helloService = context.getBean(HelloService.class);
        System.out.println(helloService.sayHello());
        context.close();
    }
}
```

### Co tutaj musimy zrobić?

- Ręcznie inicjalizujemy kontekst aplikacji (`AnnotationConfigApplicationContext`).
- Musimy jawnie wskazać pakiety do przeskanowania w celu wykrycia komponentów (`"com.example"`).
- Zamykamy kontekst ręcznie za pomocą `context.close()`.

---

## Projekt ze Spring Boot

Spring Boot automatyzuje proces konfiguracji, obsługuje uruchamianie aplikacji i eliminuje wiele kroków manualnych.

#### Klasa serwisowa

```java
import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String sayHello() {
        return "Hello, World!";
    }
}
```

#### Główna klasa aplikacji

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);
        HelloService helloService = context.getBean(HelloService.class);
        System.out.println(helloService.sayHello());
    }
}
```

### Co zmienia Spring Boot?

1. **Automatyczne skanowanie pakietów:** Dzięki adnotacji `@SpringBootApplication`, Spring Boot automatycznie wykrywa wszystkie komponenty w tym samym pakiecie lub jego podpakietach.
2. **Automatyczne zarządzanie cyklem życia aplikacji:** Spring Boot zamyka kontekst aplikacji automatycznie.
3. **Brak potrzeby konfiguracji kontekstu:** Nie musimy ręcznie wskazywać klas lub pakietów.

---

## Dodatkowe cechy Spring Boot

Oprócz automatyzacji i uproszczenia konfiguracji, Spring Boot oferuje szereg dodatkowych funkcji:

1. **Wbudowany serwer aplikacji:** Możesz uruchomić aplikację jako zwykły proces JVM dzięki wbudowanym serwerom (Tomcat, Jetty).
2. **Startery:** Gotowe zestawy zależności (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`) pozwalają szybko dodać funkcjonalności.
3. **Profilowanie środowisk:** Obsługa różnych konfiguracji dla środowisk (np. dev, prod) przy użyciu profili.
4. **Prosty system konfiguracji:** Możliwość definiowania parametrów w `application.properties` lub `application.yml`.
5. **Obsługa monitorowania:** Integracja z narzędziami monitorującymi, takimi jak Actuator.

---

## Beany i ich tworzenie

### W jaki sposób można stworzyć bean?

**Bean** to obiekt przechowywany i zarządzany przez kontener IoC. W Spring Framework można tworzyć beany na kilka sposobów: za pomocą plików XML, adnotacji lub konfiguracji w Javie. Poniżej opisujemy każdy sposób z przykładami.

#### 1. **Bean w XML**
Beany można definiować w plikach XML, co było popularne w początkowych wersjach Spring Framework. Obecnie jest to rzadziej stosowany sposób, ponieważ konfiguracja w Javie i adnotacje są bardziej czytelne.

**Przykład:**
Plik `applicationContext.xml`:
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean id="helloService" class="com.example.HelloService"/>
</beans>
```

**Zalety:**
- Elastyczność konfiguracji (np. możliwość użycia różnych plików XML).  
  **Wady:**
- Dużo kodu konfiguracyjnego.

---

#### 2. **Bean z adnotacjami**
Najbardziej popularny sposób w nowoczesnych aplikacjach Spring. Beany są tworzone za pomocą adnotacji, takich jak `@Component`, `@Service`, `@Repository` czy `@Controller`.

**Przykład:**
Klasa `HelloService`:
```java
import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String sayHello() {
        return "Hello from annotated bean!";
    }
}
```

**Zalety:**
- Prosta i czytelna konfiguracja.
- Automatyczne wykrywanie beanów.

**Wady:**
- Wymaga adnotacji w każdej klasie oraz nacechowania klasą adnotacjami frameworku (co przeczy niektórym wzorcom architektury, by klasa pozostawała "czysta", bez nacechowania konkretnym frameworkiem).

---

#### 3. **Bean z Java Config**
Zamiast XML możemy definiować beany w klasach konfiguracyjnych przy użyciu metody oznaczonej `@Bean`.

**Przykład:**
Klasa konfiguracyjna:
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public HelloService helloService() {
        return new HelloService();
    }
}
```

**Zalety:**
- Brak potrzeby używania XML.
- Centralizacja konfiguracji w jednym miejscu.

**Wady:**
- Dodatkowy kod konfiguracyjny w Javie.

---

### Czy aplikacja może mieć wiele kontekstów?

Aplikacja może mieć wiele kontekstów. Na przykład:

- Kontekst globalny (root context) dla całej aplikacji.
- Kontekst specyficzny dla warstwy webowej (WebApplicationContext).

#### Przykład inicjalizacji kontekstu z XML i adnotacji

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        // Kontekst XML
        ApplicationContext xmlContext = new ClassPathXmlApplicationContext("applicationContext.xml");

        // Kontekst z Java Config
        ApplicationContext javaConfigContext = new AnnotationConfigApplicationContext(AppConfig.class);

        // Przykład współdzielenia beanów
        HelloService helloService = xmlContext.getBean(HelloService.class);
        System.out.println(helloService.sayHello());
    }
}
```

## Kontekst i kontener IoC

**Kontener IoC** to centralny element Spring Framework, który zarządza tworzeniem i cyklem życia beanów. W aplikacji możemy korzystać z różnych rodzajów kontekstów.
**Kontekst** w Spring to implementacja kontenera IoC, która dodatkowo zapewnia dostęp do mechanizmów obsługi zdarzeń, międzynarodowości (i18n), mechanizmów zarządzania zasobami (np. pliki properties) oraz integracji z aplikacjami webowymi.

Kontekst odpowiada za inicjalizację kontenera IoC oraz rejestrowanie i zarządzanie beanami. W praktyce jest to warstwa pozwalająca aplikacji uzyskać dostęp do zarządzanych obiektów.

### Rodzaje kontekstów

1. **ApplicationContext:** Główny kontekst aplikacji, obsługujący wszystkie funkcjonalności Springa.
2. **ClassPathXmlApplicationContext:** Kontekst oparty na plikach XML.
3. **AnnotationConfigApplicationContext:** Kontekst obsługujący konfiguracje oparte na adnotacjach i klasach konfiguracyjnych.
4. **WebApplicationContext:** Specjalizacja ApplicationContext dla aplikacji webowych.


### Jak działa kontener IoC w Spring?

1. **Rejestracja beanów**: Beany są rejestrowane w kontenerze IoC podczas inicjalizacji kontekstu aplikacji. Mogą być definiowane za pomocą:
    - XML,
    - adnotacji (np. `@Component`, `@Service`, `@Repository`),
    - klas konfiguracyjnych (Java Config).

2. **Inicjalizacja beanów**: Kontener IoC tworzy instancje beanów i wstrzykuje zależności, na przykład przez konstruktor, setter lub pole.

3. **Zarządzanie cyklem życia**: Kontener IoC obsługuje proces inicjalizacji i niszczenia beanów (np. przez adnotacje `@PostConstruct`, `@PreDestroy` lub interfejsy `InitializingBean`, `DisposableBean`).

4. **Dostęp do beanów**: Beany przechowywane w kontenerze można pobierać za pomocą metod takich jak `getBean()` z kontekstu aplikacji.

---

### Jak są przechowywane beany?

Beany są przechowywane w kontenerze IoC jako **singletony** (domyślny zakres). Oznacza to, że istnieje tylko jedna instancja beana w kontekście aplikacji, o ile nie określisz innego zakresu (`prototype`, `request`, `session` itp.).

**Przykład zakresu singleton i prototype:**
```java
@Component
@Scope("prototype")
public class PrototypeBean {
    // Każde pobranie z kontenera tworzy nową instancję
}
```

### Rodzaje beanów w Spring (zakresy):

1. **Singleton** (domyślny): Jedna instancja beana na cały kontekst aplikacji.
2. **Prototype**: Tworzy nową instancję beana przy każdym żądaniu.
3. **Request**: Jedna instancja na każde żądanie HTTP (dla aplikacji webowych).
4. **Session**: Jedna instancja na sesję HTTP użytkownika (dla aplikacji webowych).
5. **Application**: Jedna instancja na cykl życia aplikacji (globalny kontekst).
6. **WebSocket**: Jedna instancja na sesję WebSocket (dla aplikacji z WebSocket).

Zakresy `Request`, `Session`, `Application` i `WebSocket` są specyficzne dla niektórych aplikacji webowych.

---

### Kluczowe funkcje kontenera IoC względem beanów:
1. **Przechowywanie**: Kontener IoC przechowuje beany jako obiekty gotowe do użycia.
2. **Zarządzanie zależnościami**: Automatyczne wstrzykiwanie zależności (Dependency Injection).
3. **Zarządzanie cyklem życia**: Tworzenie, inicjalizacja, niszczenie beanów.
4. **Umożliwienie dostępu**: Metody, takie jak `getBean()`, pozwalają na uzyskanie beana.

---

## **Zarządzanie nazwami beanów i rozwiązywanie konfliktów**

### **Jak są nadawane nazwy beanów w Springu?**

1. **Domyślne nadawanie nazw beanów**  
   Jeśli Spring wykryje komponent (bean) bez podanej nazwy, automatycznie przypisze mu nazwę na podstawie **nazwy klasy**:
    - Bean otrzyma nazwę **klasy z małą literą na początku**.
    - Przykład:
      ```java
      @Component
      public class MyService { }
      ```
      Domyślna nazwa tego beana to **`myService`**.

2. **W przypadku metod z adnotacją `@Bean` w klasach konfiguracyjnych**:
    - Nazwa beana to domyślnie **nazwa metody**, która zwraca dany bean.
    - Przykład:
      ```java
      @Configuration
      public class AppConfig {
          @Bean
          public MyService myServiceBean() {
              return new MyService();
          }
      }
      ```
      Domyślna nazwa tego beana to **`myServiceBean`**.

3. **Nazwy oparte na aliasach**  
   Można przypisywać aliasy do beanów za pomocą adnotacji **`@Qualifier`** lub **`@AliasFor`**.

---

### **Jak samemu nadawać nazwy beanów?**

1. **Adnotacja `@Component` lub jej warianty**  
   Możesz określić nazwę beana bezpośrednio w adnotacji:
   ```java
   @Component("customBeanName")
   public class MyService { }
   ```

2. **Adnotacja `@Bean`**  
   W przypadku metod konfiguracyjnych można nadać nazwę beana, podając ją jako argument do `@Bean`:
   ```java
   @Configuration
   public class AppConfig {
       @Bean(name = "customServiceBean")
       public MyService myService() {
           return new MyService();
       }
   }
   ```

3. **Adnotacja `@Qualifier`**  
   Używa się jej w celu wskazania konkretnego beana, szczególnie gdy istnieje więcej niż jeden bean tego samego typu:
   ```java
   @Component("customBeanName")
   public class MyService { }
   ```

---

### **Co to jest konflikt beanów?**

Konflikt beanów pojawia się, gdy:
1. W aplikacji istnieje **więcej niż jeden bean tego samego typu** lub tej samej nazwy.
2. Spring nie wie, który bean wstrzyknąć podczas dependency injection (**DI**) (np. przez konstruktor lub pole).

---

### **Przykład konfliktu**

#### **Problem: wiele beanów tego samego typu**
```java
@Component
public class ServiceA { }

@Component
public class ServiceB extends ServiceA { }

@Component
public class Consumer {
    private final ServiceA service;

    @Autowired
    public Consumer(ServiceA service) {
        this.service = service;
    }
}
```
- Tutaj Spring zgłosi wyjątek podczas uruchamiania:  
  `NoUniqueBeanDefinitionException`, ponieważ istnieją dwa beany typu `ServiceA` (jeden jako `ServiceA`, drugi jako `ServiceB`).

---

### **Jak naprawić konflikt beanów?**

1. **Użycie `@Qualifier`**  
   Możesz wskazać dokładny bean, który chcesz wstrzyknąć:
   ```java
   @Component
   public class Consumer {
       private final ServiceA service;

       @Autowired
       public Consumer(@Qualifier("serviceA") ServiceA service) {
           this.service = service;
       }
   }
   ```

2. **Użycie `@Primary`**  
   Możesz oznaczyć jeden z beanów jako domyślny, aby uniknąć konfliktu:
   ```java
   @Component
   @Primary
   public class ServiceA { }

   @Component
   public class ServiceB extends ServiceA { }
   ```

3. **Nadawanie unikalnych nazw beanom**  
   Upewnij się, że każda nazwa beana jest unikalna, np. przez jawne ustawienie nazwy w adnotacji `@Bean` lub `@Component`.

---

## **Wstrzykiwanie wartości z plików konfiguracyjnych**
Dzięki adnotacji **@Value** możemy łatwo wstrzykiwać wartości z plików konfiguracyjnych (**application.properties** lub **application.yaml**) do naszych beanów.

### Przykład:

**application.properties**
```properties
app.name=My Awesome Application
```

**Klasa konfiguracyjna**
```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${app.name}")
    private String appName;

    public String getAppName() {
        return appName;
    }
}
```

Jeśli używasz **application.yaml**, zapis wygląda tak:
```yaml
app:
  name: My Awesome Application
```

---

## **Profile środowiskowe**
Profile w Springu pozwalają na dostosowanie konfiguracji do różnych środowisk, takich jak **dev**, **test**, czy **prod**. Spring automatycznie ładuje odpowiedni plik konfiguracyjny (np. **application-dev.properties**) na podstawie aktywowanego profilu.

### Przykład z plikami konfiguracyjnymi:

**application-dev.properties**
```properties
spring.datasource.url=jdbc:h2:mem:dev
```

**application-prod.properties**
```properties
spring.datasource.url=jdbc:mysql://prod-db
```

**Aktywacja profilu:**
- Można to zrobić w **application.properties**:
  ```properties
  spring.profiles.active=dev
  ```
- Lub jako argument JVM:
  ```
  -Dspring.profiles.active=prod
  ```

### Definiowanie profili w Javie:

**Konfiguracja środowiskowa**
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataSourceConfig {

    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        return new H2DataSource();
    }

    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        return new MysqlDataSource();
    }
}
```

---

## **@ConditionalOnProperty**
Adnotacja **@ConditionalOnProperty** pozwala aktywować komponenty lub konfiguracje na podstawie obecności lub wartości określonych właściwości w pliku konfiguracyjnym.

### Przykład:

**application.properties**
```properties
feature.enabled=true
```

**Bean warunkowy**
```java
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "feature.enabled", havingValue = "true", matchIfMissing = false)
public class FeatureComponent {
    public FeatureComponent() {
        System.out.println("Feature is enabled!");
    }
}
```

- **`name`**: Określa właściwość do sprawdzenia.
- **`havingValue`**: Oczekiwana wartość właściwości.
- **`matchIfMissing`**: Jeśli `true`, bean zostanie utworzony, nawet gdy właściwość nie istnieje.

---

## **Adnotacja @ConfigurationProperties**
Adnotacja **@ConfigurationProperties** pozwala mapować całe grupy właściwości do klas konfiguracyjnych. Jest to szczególnie przydatne, gdy zarządzamy złożoną konfiguracją.

### Przykład:

**application.yaml**
```yaml
app:
  name: My Application
  version: 1.0.0
  features:
    enableLogin: true
    maxUsers: 100
```

**Klasa konfiguracyjna**
```java
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String name;
    private String version;
    private Features features;

    // Gettery i Settery

    public static class Features {
        private boolean enableLogin;
        private int maxUsers;

        // Gettery i Settery
    }
}
```

- **`@ConfigurationProperties`** z `prefix` mapuje klucz `app` do klasy `AppProperties`.
- Wymaga dodania adnotacji **@EnableConfigurationProperties(AppProperties.class)** w klasie głównej Spring Boot, jeśli nie tworzysz beana.

---

## **Plik bootstrap.yml**
Spring Boot obsługuje także plik **bootstrap.yml**, który ładowany jest wcześniej niż **application.properties**/**application.yaml**. Jest on używany do konfiguracji początkowej, np. w przypadku serwera **Config Server**.

**Przykład bootstrap.yml**
```yaml
spring:
  application:
    name: my-service
  cloud:
    config:
      uri: http://config-server:8888
```

---

## **Hierarchia właściwości**
Spring Boot ładuje właściwości w określonej kolejności (od najwyższego priorytetu do najniższego):
1. Właściwości przekazane jako argumenty JVM (`-D`).
2. Zmienne środowiskowe (np. `SPRING_DATASOURCE_URL`).
3. Właściwości z pliku **application.properties**/**application.yaml** wskazane przez argument JVM.
4. Plik **application.properties**/**application.yaml** w folderze zasobów.
5. Plik **application-{profile}.properties**/**application-{profile}.yaml** w folderze zasobów, nadsłania właściwości pliku **application.properties**/**application.yaml**.
6. Wartości domyślne z kodu.

---
