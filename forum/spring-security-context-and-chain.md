# **SecurityFilterChain i pochodne**

Spring Security zapewnia ogromne możliwości konfiguracji `SecurityFilterChain`, umożliwiając precyzyjne dostosowanie mechanizmów autoryzacji, uwierzytelniania, zarządzania sesjami, polityki bezpieczeństwa i wyjątków.

#### **1.1. Autoryzacja zasobów**
Autoryzacja polega na sprawdzaniu, czy użytkownik ma dostęp do określonego zasobu. Spring Security pozwala na konfigurację dostępu do endpointów na kilka sposobów:

- `permitAll()` – dostęp dla wszystkich użytkowników, niezależnie od uwierzytelnienia.
- `authenticated()` – dostęp tylko dla zalogowanych użytkowników.
- `hasRole("ADMIN")` – dostęp tylko dla użytkowników z określoną rolą.
- `hasAuthority("READ_PRIVILEGES")` – dostęp na podstawie określonych uprawnień.
- `denyAll()` – całkowite blokowanie dostępu do zasobu.

Przykładowa konfiguracja autoryzacji:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/public/**").permitAll()      // Publiczne zasoby
    .requestMatchers("/user/**").hasRole("USER")    // Dostęp dla użytkownika
    .requestMatchers("/admin/**").hasRole("ADMIN")  // Dostęp dla administratora
    .anyRequest().authenticated()                   // Wszystkie inne wymagają zalogowania
)
```

#### **1.2. Uwierzytelnianie użytkowników**
Spring Security obsługuje różne mechanizmy uwierzytelniania, które można dowolnie konfigurować:
- **Formularz logowania (`formLogin()`)** – klasyczne uwierzytelnianie przez stronę logowania.
- **HTTP Basic (`httpBasic()`)** – uwierzytelnianie na poziomie nagłówka `Authorization: Basic`.
- **JWT (JSON Web Token)** – bezstanowy mechanizm uwierzytelniania oparty na tokenach.
- **OAuth2/OpenID Connect** – federacyjne logowanie przez zewnętrzne usługi (np. Google, Facebook).
- **LDAP** – logowanie oparte na katalogu użytkowników LDAP.

Przykładowa konfiguracja logowania:
```java
http
    .formLogin(form -> form
        .loginPage("/login")
        .defaultSuccessUrl("/home", true)
        .permitAll()
    )
    .httpBasic(Customizer.withDefaults());
```
W przypadku aplikacji REST API warto zastąpić `formLogin()` obsługą tokenów JWT.

#### **1.3. Zarządzanie sesją**
Spring Security pozwala określić sposób zarządzania sesjami użytkowników:
- `SessionCreationPolicy.STATELESS` – bezstanowa aplikacja (np. JWT).
- `SessionCreationPolicy.IF_REQUIRED` – sesja tworzona tylko wtedy, gdy jest potrzebna.
- `SessionCreationPolicy.ALWAYS` – sesja tworzona zawsze.
- `SessionCreationPolicy.NEVER` – sesja nigdy nie jest tworzona.

W przypadku JWT należy wyłączyć zarządzanie sesjami:
```java
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

#### **1.4. Obsługa wyjątków w autoryzacji**
Spring Security pozwala na własną obsługę błędów autoryzacji i uwierzytelniania:
- `accessDeniedHandler()` – obsługuje sytuację, gdy użytkownik nie ma uprawnień do zasobu.
- `authenticationEntryPoint()` – obsługuje błędy związane z brakiem uwierzytelnienia.

Przykładowa konfiguracja:
```java
.exceptionHandling(ex -> ex
    .authenticationEntryPoint((request, response, authException) -> {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Brak uwierzytelnienia");
    })
    .accessDeniedHandler((request, response, accessDeniedException) -> {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Brak dostępu");
    })
);
```

#### **1.5. CORS i CSRF**
Spring Security domyślnie stosuje zabezpieczenia przed CSRF (Cross-Site Request Forgery), które można wyłączyć w aplikacjach REST API:

```java
.csrf(AbstractHttpConfigurer::disable)
```

Obsługa CORS może być konfigurowana na poziomie Spring Security:
```java
.cors(cors -> cors.configurationSource(request -> {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("https://frontend.com"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    return config;
}))
```

---

### **2. Beany dostępne w Spring Security**
Spring Security pozwala na pełną konfigurację poprzez własne beany (`@Bean`). Oto kluczowe komponenty, które można dostosować.

#### **2.1. `UserDetailsService` – obsługa użytkowników**
`UserDetailsService` umożliwia dostarczanie użytkowników dla Spring Security. Może on pobierać dane np. z bazy danych.

Przykład własnej implementacji:
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
```

#### **2.2. `PasswordEncoder` – hashowanie haseł**
Spring Security nie przechowuje haseł w postaci jawnej – wymaga ich hashowania. Dostępne algorytmy:
- `BCryptPasswordEncoder` (zalecany),
- `PBKDF2`,
- `SCrypt`,
- `Argon2`.

Przykład:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

#### **2.3. `AuthenticationManager` – zarządzanie procesem logowania**
`AuthenticationManager` odpowiada za uwierzytelnianie użytkownika w aplikacji.

Przykład konfiguracji:
```java
@Bean
public AuthenticationManager authenticationManager(
        UserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder) throws Exception {
    
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder);

    return new ProviderManager(authProvider);
}
```

#### **2.4. `JwtUtil` – obsługa tokenów JWT**
Aby obsługiwać JWT, często stosuje się dedykowaną klasę do generowania i weryfikacji tokenów.

Przykładowa implementacja:
```java
@Component
public class JwtUtil {
    private final String SECRET_KEY = "supersecretkey12345678901234567890";

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
```

#### **2.5. `SecurityContextHolder` – przechowywanie informacji o użytkowniku**
`SecurityContextHolder` przechowuje dane o aktualnie zalogowanym użytkowniku w aplikacji.

Przykład pobrania użytkownika:
```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
String username = authentication.getName();
```

---
