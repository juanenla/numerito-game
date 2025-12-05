# Numerito Game API

API REST del juego "Numerito" - Adivina el número de 4 cifras distintas.

## Descripción del Juego

- El juego genera un número secreto de 4 cifras distintas (0-9)
- La primera cifra siempre es 1-9 (no puede ser 0)
- El jugador hace intentos con números de 4 cifras
- Cada intento recibe feedback:
  - **B (Bien)**: Cifra correcta en posición correcta
  - **R (Regular)**: Cifra correcta en posición incorrecta
  - **M (Mal)**: Cifra que no está en el número secreto
- El juego termina cuando B = 4 (número adivinado)

## Requisitos

- Java 17 o superior
- Maven 3.6+

## Estructura del Proyecto

```
numerito-game/
├── pom.xml
├── .gitignore
├── README.md
└── src/
    ├── main/
    │   ├── java/com/mijuego/numerito/
    │   │   ├── NumeoritoGameApplication.java    # Aplicación Spring Boot
    │   │   ├── GameSession.java                 # Lógica del juego
    │   │   ├── GuessResult.java                 # Resultado de evaluación
    │   │   ├── SecretNumberGenerator.java       # Generador de números
    │   │   ├── exception/
    │   │   │   └── InvalidGuessException.java
    │   │   └── api/
    │   │       ├── controller/
    │   │       │   └── GameController.java      # Endpoints REST
    │   │       ├── service/
    │   │       │   ├── GameService.java         # Servicio de sesiones
    │   │       │   └── GameNotFoundException.java
    │   │       ├── dto/
    │   │       │   ├── GameCreatedResponse.java
    │   │       │   ├── GuessRequest.java
    │   │       │   ├── GuessResponse.java
    │   │       │   ├── GameStateResponse.java
    │   │       │   └── ErrorResponse.java
    │   │       ├── exception/
    │   │       │   └── GlobalExceptionHandler.java  # Manejo de errores
    │   │       └── config/
    │   │           └── CorsConfig.java          # Configuración CORS
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/mijuego/numerito/
            ├── GameSessionTest.java
            ├── SecretNumberGeneratorTest.java
            └── api/
                └── GameControllerIntegrationTest.java
```

## Iniciar la Aplicación

```bash
# Opción 1: Usando Maven
mvn spring-boot:run

# Opción 2: Compilar y ejecutar JAR
mvn clean package
java -jar target/numerito-game-1.0.0-SNAPSHOT.jar
```

La API estará disponible en: `http://localhost:8080`

## API REST - Endpoints

### 1. Crear Nueva Partida

```bash
POST /api/game
Content-Type: application/json
```

**Respuesta exitosa (201 Created):**
```json
{
  "gameId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "message": "Partida creada exitosamente"
}
```

**Ejemplo con curl:**
```bash
curl -X POST http://localhost:8080/api/game
```

### 2. Realizar un Intento

```bash
POST /api/game/{gameId}/guess
Content-Type: application/json

{
  "guess": "1234"
}
```

**Respuesta exitosa (200 OK):**
```json
{
  "bien": 1,
  "regular": 2,
  "mal": 1,
  "win": false,
  "attemptNumber": 3,
  "finished": false
}
```

**Respuesta de error (400 Bad Request):**
```json
{
  "error": "INVALID_GUESS",
  "message": "Todas las cifras deben ser distintas, dígito repetido: 2",
  "timestamp": "2024-12-05T10:30:45.123"
}
```

**Respuesta de error (404 Not Found):**
```json
{
  "error": "GAME_NOT_FOUND",
  "message": "Partida con ID abc-123 no encontrada",
  "timestamp": "2024-12-05T10:30:45.123"
}
```

**Ejemplo con curl:**
```bash
curl -X POST http://localhost:8080/api/game/abc-123/guess \
  -H "Content-Type: application/json" \
  -d '{"guess": "1234"}'
```

### 3. Consultar Estado de Partida

```bash
GET /api/game/{gameId}
```

**Respuesta exitosa (200 OK):**
```json
{
  "gameId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "attempts": 5,
  "finished": false
}
```

**Ejemplo con curl:**
```bash
curl http://localhost:8080/api/game/abc-123
```

## Reglas de Validación

Los intentos deben cumplir:
- Exactamente 4 dígitos
- Primer dígito entre 1-9 (no puede ser 0)
- Solo caracteres numéricos
- Todas las cifras distintas entre sí

Ejemplos de intentos inválidos:
- `"0123"` - Empieza con 0
- `"1122"` - Dígitos repetidos
- `"123"` - Menos de 4 dígitos
- `"12345"` - Más de 4 dígitos
- `"12a4"` - Caracteres no numéricos

## CORS

La API está configurada para aceptar peticiones desde:
- `http://localhost:5173` (Vite dev server)
- `http://localhost:3000` (Create React App, etc.)
- `http://127.0.0.1:5173`

Para producción, edita `src/main/java/com/mijuego/numerito/api/config/CorsConfig.java`

## Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar solo tests de integración de la API
mvn test -Dtest=GameControllerIntegrationTest
```

**Tests implementados:**
- 35 tests de lógica de dominio
- 8 tests de integración de API
- Total: 43 tests

## Ejemplo de Flujo Completo

```bash
# 1. Crear partida
GAME_ID=$(curl -s -X POST http://localhost:8080/api/game | jq -r '.gameId')

# 2. Hacer intentos
curl -X POST http://localhost:8080/api/game/$GAME_ID/guess \
  -H "Content-Type: application/json" \
  -d '{"guess": "1234"}'

curl -X POST http://localhost:8080/api/game/$GAME_ID/guess \
  -H "Content-Type: application/json" \
  -d '{"guess": "5678"}'

# 3. Consultar estado
curl http://localhost:8080/api/game/$GAME_ID
```

## Uso de la Lógica de Dominio (Sin API)

Si solo quieres usar la lógica sin el servidor REST:

```java
import com.mijuego.numerito.GameSession;
import com.mijuego.numerito.GuessResult;
import com.mijuego.numerito.exception.InvalidGuessException;

GameSession game = new GameSession();

try {
    GuessResult result = game.guess("1234");
    System.out.println("Bien: " + result.bienCount());
    System.out.println("Regular: " + result.regularCount());
    System.out.println("Mal: " + result.malCount());
} catch (InvalidGuessException e) {
    System.err.println("Intento inválido: " + e.getMessage());
}
```

## Configuración

Edita `src/main/resources/application.properties`:

```properties
# Puerto del servidor
server.port=8080

# Logging
logging.level.com.mijuego.numerito=INFO
```

## Próximos Pasos

- ✅ ~~API REST con Spring Boot~~
- 🔄 Frontend web con Vite + React
- 🔄 Base de datos con Supabase
- 🔄 Sistema de puntuación y ranking
- 🔄 Despliegue en la nube

## Repositorio

https://github.com/juanenla/numerito-game

## Licencia

Proyecto educacional para el curso de IA APP.
