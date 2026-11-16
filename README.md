# Digital Library Management System

Spring Boot 3.5 + Spring Security + Thymeleaf + Spring Data JPA + **MySQL**.
Two roles: **ADMIN** (librarian) and **USER** (member).

## 1. Prerequisites
- JDK 21 or newer (JDK 25 is fine)
- MySQL Server running locally (the one your MySQL Workbench connects to)
- IntelliJ IDEA (open the folder as a Maven project) or Maven on the command line

## 2. Configure the database password
Open `src/main/resources/application.properties` and set:

```
spring.datasource.password=YOUR_MYSQL_PASSWORD
```
(or set the `DB_PASSWORD` environment variable). The username defaults to `root`.
You do **not** need to create the database or tables: the app creates the
`digital_library` database and all tables on first start.

## 3. Run
- IntelliJ: open `DigitalLibraryApplication` and press Run, or
- Terminal: `mvn spring-boot:run`

Then open <http://localhost:8080>.

## 4. Log in
| Role  | Email               | Password   |
|-------|---------------------|------------|
| Admin | admin@library.com   | Admin@123  |
| Member| register on the site|            |

The admin account and 12 sample books are created on first start
(change or switch off in `application.properties`, `library.*` keys).
Change the admin password before putting this anywhere real.

## 5. Rules built in
- Loan period: 14 days. Max 5 books at a time per member.
- Fine: 5 per day late (`library.fine-per-day`), created automatically on return.
- A member with an **unpaid fine** or an **overdue book** cannot borrow until it is cleared.
- **Reservations** are allowed only when every copy is issued. Free copies go to the
  longest-waiting reservations first.
- A book that has loan history cannot be deleted (set quantity to 0 instead).

## 6. Project layout
```
src/main/java/ng/martG/library
  config/       SecurityConfig, LibraryUserDetailsService, DataSeeder, LibraryProperties
  model/        entities: AppUser, Book, Loan, Reservation, ContactMessage (+ enums)
  repository/   Spring Data JPA interfaces
  service/      business rules: LoanService, ReservationService, BookService, FineCalculator, ...
  controller/   web layer (member pages + /admin pages)
  dto/          form objects with validation
src/main/resources
  templates/    Thymeleaf pages   static/css/style.css
database/schema.sql   reference schema (optional)
```

## 7. Run the tests
`mvn test` (unit tests for the fine calculation; no database needed).
# digitalLibrary
