1. Użytkownicy
    - każdy uzyktownik musi byc zalogowany by kozystac z jakiejkolwiek funkcji
    - możliwosć rejestracji i logowania
    - użytkownik posiada unikatowy login i hasło
    - uzytkonik nie moze zmienic swoich danych (login , hasło)

2. Czat grupowy
    - kazdy uzytkownik moze stowrzyc publiczny pokój oraz przekazuje nie unikalną nazwe
    - użytkownik widzi wszytskkie pokoje i do nich należy
    - nie ma możliwosci edytwoania pokoju
    - użytkownik może stworzyć wiele pokoji
   
3. Czat prywatny
    - każdy ma czat prywatny z innymi użytkownikami
    - każdy użytkownik widzi pozostałych uzytkowników

4. System logowania
   - uzytkownik przekazuje login i haslo
   - jesli dane logowania sa nie prawidlowe to zwracamu 401
   - jesli dane logowania sa prawidlowe to zwracamy wygenrowany token(np. UUID)
   - TOKEN musimy zapisac w bazie dnaych wraz z informacji do kogo nalezy token
   - podczas wylogowania sie usuwamy rekord z tokenem z bazy danych
   - token powinnien miec date ważnosci np 7 dni
