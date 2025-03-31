# eWarsztat
---
## Cel

Aplikacja do zarządzania warsztatem samochodowym. Aplikacja powinna pozwalać na możliwość rezerwacji wizyty w warsztacie, monitorowanie statusu naprawy oraz możliwość przeprowadzenia opłat za naprawę.
Aplikacja służy również do zarządzania personelem warsztatu samochodowego

## Wymagania
### Wymagania funkcjonalne
1. Wizyty i naprawa
    1. Użytkownik może zarezerwować wizytę w warsztacie na określony termin
    2. Użytkownik może wybrać rodzaj usług na które jest przeprowadzana rezerwacja
    3. Użytkownik otrzymuje potwierdzenia i  przypomnienia o wizytach
    4. Użytkownik może sprawdzić status naprawy
    5. Użytkownik może przeglądać historię wizyt
2. Płatności
    1. Aplikacja powinna obsługiwać różne metody płatności online jak i po naprawie stacjonarnie
    2. Możliwość generowania faktur i potwierdzeń płatności
3. Zarządzanie personelem warsztatu
    1. Możliwość zarządzania harmonogramem pracy pracowników, mianowicie wybór dostępności i monitorowanie statusu pracowników
4. Bezpieczeństwo
    1. Możliwość rejestracji oraz logowania użytkowników i uzyskanie dostępu do systemu według przyznaczonych ról
5. Komunikacja między klientem a warsztatem
### Wymagania niefunkcjonalne
1. Bezpieczeństwo
    1. Dane poufne są szyfrowane
2. Wydajność
    1. Aplikacja powinna obsługiwać średnią liczbę użytkowników jednocześnie
    2. Czas ładowania nie powinien przekraczać 5 sekund
3. Dostępność - aplikacja powinna być dostępna 99% czasu. Aplikacja powinna być dostępna na różnych urządzeniach
4. Skalowalność - aplikacja powinna być skalowalna w celu możliwego skalowania na większą liczbę warsztatów
---
## Stos technologiczny
1. Aplikacja oparta o Spring Boot framework
2. Architektura mikrousługowa 
   - (Spring Cloud Eureka do wykrywania usług)
   - Spring cloud config server dla synchronizacji konfiguracji między wszystkimi usługami
3. Autoryzacja przy użyciu (Keycloak lub Spring Auth Server)
4. Testowanie (AssertJ, Cucumber, testy integracyjne)
5. Wykorzystanie serwisu kolejkowania (Apache Kafka)
6. Statyczna analiza kodu (lokalny SonarQube)
7. Zaprojektowanie własnego API przy użyciu OpenAPI + zaprojektowanie aplikacji frontendowej która będzie konsumować udostępnione API
8. Wykorzystanie ostatnich nowości w JDK
9. Współbieżność
---