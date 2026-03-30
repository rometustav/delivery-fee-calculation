# Delivery Fee Calculator

A Spring Boot application that calculates delivery fees based on regional base fee and extra fees for weather conditions.

## Tech Stack

- Java 25
- Spring Boot 4.0.5
- H2 database
- Swagger / OpenAPI 3

## Getting Started

### Prerequisites
- Java 25
- Maven

### Run the application
```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

### Run tests
```bash
./mvnw test
```

## API Documentation

Swagger UI is available at:
```
http://localhost:8080/swagger-ui/index.html
```

## Endpoints

### Delivery Fee
```
GET /api/v1/delivery-fee?city=TARTU&vehicleType=BIKE
```

**Parameters:**
- `city` - `TALLINN`, `TARTU` or `PARNU` (required)
- `vehicleType` - `CAR`, `SCOOTER` or `BIKE` (required)
- `datetime` - ISO-8601 datetime for historical calculations (optional)

**Example response:**
```json
{
  "totalFee": 4.0,
  "currency": "EUR"
}
```

### Fee Rules (CRUD)
```
GET    /api/v1/rules/base-fees
POST   /api/v1/rules/base-fees
PUT    /api/v1/rules/base-fees/{id}
DELETE /api/v1/rules/base-fees/{id}

GET    /api/v1/rules/extra-fees
POST   /api/v1/rules/extra-fees
PUT    /api/v1/rules/extra-fees/{id}
DELETE /api/v1/rules/extra-fees/{id}
```

### Admin
```
POST /api/v1/admin/import-weather
GET  /api/v1/admin/weather-observations
```

## Weather Data

Weather data is automatically imported from [ilmateenistus.ee](https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php) every hour at HH:15:00. The cron schedule is configurable in `application.properties`:
```
weather.cron=0 15 * * * *
```

To trigger an import manually:
```
POST /api/v1/admin/import-weather
```

## H2 Console

The H2 database console is available at:
```
http://localhost:8080/h2-console
```
JDBC URL: `jdbc:h2:mem:delivery`

## Fee Calculation

Total fee = RBF + ATEF + WSEF + WPEF

- **RBF** - Regional base fee (city + vehicle type)
- **ATEF** - Air temperature extra fee (scooter/bike only)
- **WSEF** - Wind speed extra fee (bike only)
- **WPEF** - Weather phenomenon extra fee (scooter/bike only)

Fee rules are stored in the database and can be managed through the REST interface.