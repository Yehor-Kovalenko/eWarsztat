# eWarsztat
---
## Manual for SonarQube scanning
1. Running SonarQube Server: `docker-compose up` (add `-d` flag for background run)
2. Open `http://localhost:9000` login with default username `admin` and password `admin` and change to your own password
3. Under section **Security** generate the API token and paste in the sonar-project.properties file
4. Run the SonarQube scanner on the project:
```bash
# Run the scanner which will exit after finish
docker run --rm -v "${pwd}:/usr/src" sonarsource/sonar-scanner-cli
```
5. After scan is complete open the `http://localhost:9000`, login, Go to Projects to see the result
6. To turn off the SonarQube server run `docker compose down`
---
## Manual for databases running
Run from the root project level (if it is not the first build you do not need the `--build` flag): 
```shell
docker-compose -f database/docker-compose.yml up --build
```
---
## OpenAPI docs
Go under endpoint of the apigateway service to the url `<api-gateway-base-url/swagger-ui.html`
---
## Manual for running mutation tests
```shell
mvn verify
```
---
ALTERNATIVE
1. Compile and build the module
2. Run
```shell
mvn org.pitest:pitest-maven:mutationCoverage
```
---
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