# Numerito Game

Lógica de dominio del juego "Numerito" - Adivina el número de 4 cifras distintas.

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
    │   └── java/
    │       └── com/mijuego/numerito/
    │           ├── GameSession.java           # Lógica principal del juego
    │           ├── GuessResult.java           # Resultado de evaluación
    │           ├── SecretNumberGenerator.java # Generador de números
    │           └── exception/
    │               └── InvalidGuessException.java
    └── test/
        └── java/
            └── com/mijuego/numerito/
                ├── GameSessionTest.java
                └── SecretNumberGeneratorTest.java
```

## Compilar y Ejecutar Tests

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar todos los tests
mvn test

# Generar JAR
mvn package
```

## Uso Básico

```java
import com.mijuego.numerito.GameSession;
import com.mijuego.numerito.GuessResult;
import com.mijuego.numerito.exception.InvalidGuessException;

// Crear una nueva partida
GameSession game = new GameSession();

try {
    // Hacer un intento
    GuessResult result = game.guess("1234");

    System.out.println("Bien: " + result.bienCount());
    System.out.println("Regular: " + result.regularCount());
    System.out.println("Mal: " + result.malCount());
    System.out.println("Ganaste: " + result.isWin());
    System.out.println("Intento #: " + result.attemptNumber());

} catch (InvalidGuessException e) {
    System.err.println("Intento inválido: " + e.getMessage());
}

// Verificar estado de la partida
System.out.println("Intentos realizados: " + game.getAttempts());
System.out.println("Partida terminada: " + game.isFinished());
```

## Reglas de Validación

Los intentos deben cumplir:
- Exactamente 4 dígitos
- Primer dígito entre 1-9 (no puede ser 0)
- Solo caracteres numéricos
- Todas las cifras distintas entre sí

Si un intento no cumple estas reglas, se lanza `InvalidGuessException`.

## Conectar con GitHub

```bash
# Desde el directorio del proyecto
cd numerito-game

# El repo ya está inicializado, solo conecta con GitHub:
git remote add origin https://github.com/TU_USUARIO/numerito-game.git

# Subir el código
git push -u origin main
```

## Próximos Pasos

Este proyecto contiene solo la lógica de dominio. Próximas extensiones:
- API REST con Spring Boot
- Frontend web
- Base de datos para guardar partidas
- Sistema de puntuación y ranking

## Licencia

Proyecto educacional para el curso de IA APP.
