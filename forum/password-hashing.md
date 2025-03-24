# **Hashowanie haseł – Jak działa, czy można je odwrócić i jak poprawnie zabezpieczać dane?**

Bezpieczeństwo haseł jest jednym z najważniejszych aspektów każdej aplikacji. W tym artykule omówimy **czym jest hashowanie**, **jak poprawnie przechowywać hasła**, **czy da się odwrócić hash** oraz **jakie znaczenie ma sekret**.

---

## **1. Czym jest hashowanie haseł?**
**Hashowanie** to proces przekształcania dowolnych danych wejściowych (np. hasła) na **ciąg znaków o stałej długości** przy użyciu funkcji matematycznej zwanej **funkcją skrótu**.

Główne cechy hashowania:
- **Nieodwracalność** – nie można odwrócić procesu hashowania i odzyskać oryginalnego hasła.
- **Deterministyczność** – ten sam ciąg wejściowy zawsze daje ten sam wynik.
- **Odporność na kolizje** – różne wejścia powinny dawać różne wyniki.
- **Nieprzewidywalność** – nawet minimalna zmiana w wejściu powoduje całkowicie inny wynik.

Przykład:
```
Hash("password123") -> "ef92b778bafe771e89245b89ecbc18d0"
```

**Algorytmy hashujące:**
- **SHA-256, SHA-512** – popularne, ale NIEZALECANE do haseł (nie są odporne na brute-force).
- **BCrypt** – zalecany w Spring Security (stosuje losowe solenie i spowalnia obliczenia).
- **PBKDF2** – używany w mechanizmach bezpieczeństwa jak `OAuth`.
- **Argon2** – nowoczesny, bardziej odporny na ataki brute-force niż BCrypt.

---

## **2. Czy można odhashować hasło?**
**Nie** – funkcje hashujące są **jednokierunkowe**, co oznacza, że **nie można odwrócić** procesu i odzyskać oryginalnego hasła.

Jedynym sposobem na „złamanie” hasha jest atak brute-force lub atak słownikowy, gdzie próbuje się dużej liczby haseł i porównuje wyniki. **Dlatego ważne jest stosowanie soli i odpowiedniego algorytmu hashującego.**

---

## **3. Jak poprawnie hashować hasło w Spring Security?**
Spring Security dostarcza kilka implementacji **PasswordEncoder**, ale **najlepszym wyborem jest BCryptPasswordEncoder**.

Przykład użycia `BCryptPasswordEncoder`:
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHashingExample {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "superSecret123";
        String hashedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Hashed Password: " + hashedPassword);

        // Sprawdzenie poprawności hasła
        boolean matches = passwordEncoder.matches("superSecret123", hashedPassword);
        System.out.println("Czy hasło poprawne? " + matches);
    }
}
```
**Co się dzieje w tym kodzie?**
1. Tworzymy obiekt `BCryptPasswordEncoder`.
2. Hashujemy hasło użytkownika.
3. Porównujemy oryginalne hasło z jego zahashowaną wersją.

- **BCrypt używa mechanizmu `salt`, czyli losowej wartości dodawanej do hasła przed jego zahashowaniem, dzięki czemu każde hashowane hasło wygląda inaczej.**

---

## **4. Czym powinien być sekret?**
**Sekret** to unikalna wartość, która może być używana w różnych mechanizmach bezpieczeństwa:
- **W algorytmach hashujących** – np. w HMAC (Hash-based Message Authentication Code).
- **W JWT (JSON Web Token)** – używany do podpisywania tokenów.
- **W szyfrowaniu** – jako klucz do zabezpieczania danych.

- **Sekret powinien być długi, losowy i przechowywany w bezpiecznym miejscu**, np. w zmiennych środowiskowych lub systemie do zarządzania sekretami (np. HashiCorp Vault).

Przykład przechowywania sekretu w `.env`:
```
JWT_SECRET=3d8a91fbe2370c8aaf6d6b9872ff9936
```
i odczytu w kodzie Java:
```java
String secret = System.getenv("JWT_SECRET");
```

---

## **5. Błędy, których należy unikać**
- **Przechowywanie haseł w postaci jawnej** – nigdy nie zapisuj haseł w bazie danych w niezaszyfrowanej formie.\
- **Używanie algorytmów MD5, SHA-1** – są one przestarzałe i podatne na ataki.\
- **Brak soli w hashowaniu** – każda hashowana wartość powinna być inna dla każdego użytkownika.\
- **Używanie tego samego sekretu w wielu miejscach** – sekret powinien być unikalny dla każdego mechanizmu bezpieczeństwa.

---

## **6. Podsumowanie**
- **Hashowanie jest nieodwracalne** – nie można odzyskać oryginalnego hasła z jego hasha.\
- **BCrypt jest zalecanym algorytmem** – zapewnia ochronę przed atakami brute-force.\
- **Sól i sekret są kluczowe** – chronią przed atakami słownikowymi.\
- **Sekrety powinny być przechowywane w bezpieczny sposób** – w zmiennych środowiskowych lub systemach zarządzania tajnymi danymi.
