# LuxuryTime — E-Commerce Backend API

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-OAuth2_%2F_JWT-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![Keycloak](https://img.shields.io/badge/Keycloak-IAM-4D4D4D?style=for-the-badge&logo=keycloak&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate_JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white)

Backend RESTful enterprise per **LuxuryTime**, piattaforma e-commerce dedicata all'orologeria di lusso.  
Il sistema è progettato con particolare attenzione alla **sicurezza OAuth2/OIDC**, alla **gestione della concorrenza ad alto traffico** e all'**integrità transazionale** dell'inventario.

**Repository Frontend (Angular):** [LuxuryTimeFrontend](https://github.com/M4ik0l/LuxuryTimeFrontend)

---

## Architettura e Caratteristiche Principali

* **Autenticazione & Autorizzazione Stateless (Keycloak OAuth2 / JWT):**
    * Integrazione con **Keycloak** come Identity and Access Management (IAM).
    * Nessuna password salvata nel database applicativo: validazione crittografica dei token JWT tramite endpoint "jwk-set-uri" e conversione custom dei ruoli ("ROLE_USER", "ROLE_ADMIN") tramite "JwtAuthConverter".
    * Sincronizzazione automatica dell'anagrafica utente al login ("/auth/sync").

* **Gestione della Concorrenza e Prevenzione Race Conditions:**
    * **Pessimistic Write Locking ("@Lock(LockModeType.PESSIMISTIC_WRITE)")** e isolamento transazionale **SERIALIZABLE / READ_COMMITTED** durante le operazioni sul carrello e il checkout.
    * **Prenotazione real-time dello stock:** l'aggiunta di un orologio al carrello decrementa atomicamente la disponibilità in magazzino, impedendo l'overselling tra utenti concorrenti.

* **Task Schedulati (Cron Job di Pulizia Carrelli):**
    * Servizio background ("@Scheduled") che monitora la scadenza temporale dei carrelli (TTL di 10 minuti).
    * Allo scadere del timer senza checkout, i prodotti bloccati vengono rimossi dal carrello e **ripristinati automaticamente nello stock** disponibile.

* **Gestione Ordini, Indirizzi e Catalogo Admin:**
    * Operazioni CRUD protette da RBAC ("@PreAuthorize("hasRole('ADMIN')")") per l'inserimento, modifica ed eliminazione degli orologi nel catalogo.
    * Storico ordini immutabile e gestione rubrica indirizzi di spedizione per ciascun cliente.

---

## Stack Tecnologico

* **Framework:** Spring Boot 3.x (Spring Web, Spring Data JPA, Spring Security OAuth2 Resource Server)
* **Database:** MySQL 8.x (con generazione automatica dello schema tramite Hibernate DDL "update")
* **Identity Provider:** Keycloak (Realm: "luxurytime-backend")
* **Build Tool:** Maven
* **Utilities:** Lombok, Jackson

---

## Configurazione e Avvio Rapido

### 1. Prerequisiti
* **JDK 17+**
* **MySQL Server** attivo sulla porta "3306"
* **Keycloak** attivo sulla porta "8180"

### 2. Configurazione Database
Crea un database vuoto su MySQL (le tabelle verranno generate automaticamente da Hibernate al primo avvio):

    CREATE DATABASE luxurytime_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

*(Verifica o aggiorna username e password nel file "src/main/resources/application.yaml").*

### 3. Configurazione Keycloak
Assicurati che su Keycloak ("http://localhost:8180") sia configurato:
* **Realm:** "luxurytime-backend"
* **Client ID:** "luxurytime-backend" (con ruoli "USER" e "ADMIN")

### 4. Avvio dell'applicazione

    ./mvnw spring-boot:run

Il server sarà in ascolto su "http://localhost:8080".

---

## 👤 Autore
Sviluppato da **[M4ik0l](https://github.com/M4ik0l)** — Progetto di *Piattaforme Software per Applicazioni sul Web (PSW)*.
