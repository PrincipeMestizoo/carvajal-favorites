# favorites — Microservicio de Lista de Deseos

Microservicio del ecosistema de e-commerce de **Carvajal** encargado de gestionar la
**lista de deseos (wishlist)** de cada usuario: agregar, listar, actualizar y eliminar
productos, detectar cuándo un producto ya no tiene stock, y mantener un **histórico
inmutable** de todas las acciones realizadas sobre cada ítem.

Forma parte de una arquitectura de microservicios que comparte una única base de
datos PostgreSQL (alojada en Supabase) con el resto de servicios del e-commerce
(catálogo, usuarios, etc.).

---

## Tabla de contenido

- [Stack técnico](#stack-técnico)
- [Arquitectura y modelo de datos](#arquitectura-y-modelo-de-datos)
- [Autenticación](#autenticación)
- [Variables de entorno](#variables-de-entorno)
- [Ejecución local](#ejecución-local)
- [Ejecución con Docker](#ejecución-con-docker)
- [Documentación de la API](#documentación-de-la-api)
- [Endpoints](#endpoints)
- [Manejo de errores](#manejo-de-errores)
- [Despliegue en Render](#despliegue-en-render)
- [Pruebas](#pruebas)

---

## Stack técnico

| Componente      | Tecnología                              |
|------------------|------------------------------------------|
| Lenguaje         | Java 25                                   |
| Framework        | Spring Boot 4.1.1                          |
| Persistencia     | Spring Data JPA (Hibernate) + PostgreSQL   |
| Seguridad        | Spring Security + JWT (jjwt 0.12.6)        |
| Documentación API| springdoc-openapi (Swagger UI)             |
| Build            | Maven                                      |
| Contenedores     | Docker (multi-stage build)                 |
| Base de datos    | PostgreSQL gestionado en Supabase          |

## Arquitectura y modelo de datos

El servicio opera sobre una base de datos **compartida** entre microservicios, pero
solo tiene propiedad de escritura sobre sus propias tablas:

| Tabla               | Entidad             | Propiedad de este servicio |
|---------------------|---------------------|------------------------------|
| `favorite`           | `Favorite`           | Lectura y escritura           |
| `history_favorite`   | `HistoryFavorite`    | Lectura y escritura           |
| `product`            | `Product`            | **Solo lectura** (pertenece al microservicio de Catálogo) |

La tabla `product` se consulta directamente en la misma base de datos (sin llamada
HTTP a otro servicio) únicamente para validar existencia, precio y stock del
producto al momento de agregar o listar favoritos.

### `Favorite`
Representa un ítem dentro de la lista de deseos de un usuario.

| Campo           | Tipo             | Notas                                             |
|------------------|------------------|-----------------------------------------------------|
| `idItemFavorite` | Long (PK, auto)  |                                                      |
| `idUser`         | Long             | Se obtiene del `subject` del JWT, nunca del cliente |
| `idProduct`      | Long             | FK lógica a `product`                               |
| `quantity`       | Integer          | ≥ 1                                                 |
| `state`          | `FavoriteState`  | `DISPONIBLE` / `SIN_STOCK`, se recalcula en cada consulta contra el stock real |
| `dateSave`       | LocalDateTime    | Se asigna automáticamente si no viene informada     |

### `HistoryFavorite`
Histórico inmutable de acciones sobre un ítem de favoritos.

| Campo           | Tipo           | Notas                                   |
|------------------|----------------|-------------------------------------------|
| `idHistory`      | Long (PK, auto)|                                            |
| `idItemFavorite` | Long           | Ítem sobre el que ocurrió la acción       |
| `action`         | `ActionType`   | `AGREGADO` / `ACTUALIZADO` / `ELIMINADO`  |
| `dateAction`     | LocalDateTime  | Se asigna automáticamente                 |

### `Product` (solo lectura)

| Campo           | Tipo   |
|------------------|--------|
| `idProduct`      | Long   |
| `nameProduct`    | String |
| `price`          | Long   |
| `stock`          | Integer|

## Autenticación

Este microservicio **no emite** tokens JWT: valida los tokens emitidos por el
servicio central de autenticación del e-commerce.

- Header esperado: `Authorization: Bearer <token>`
- El `idUser` se extrae del claim `subject` del token — nunca se confía en un id
  enviado por el cliente en el body o en la URL.
- El rol se extrae del claim `role` y se mapea como `ROLE_<rol>` (por defecto
  `ROLE_CLIENTE` si el claim no viene informado).
- Endpoints públicos (no requieren token): Swagger UI, OpenAPI docs y
  `/actuator/health`. Todo lo demás requiere autenticación.

## Variables de entorno

| Variable      | Descripción                                          | Ejemplo                        |
|----------------|-------------------------------------------------------|----------------------------------|
| `DB_HOST`      | Host de PostgreSQL (Supabase)                          | ``                     |
| `DB_PORT`      | Puerto de PostgreSQL                                    | ``                            |
| `DB_NAME`      | Nombre de la base de datos                              | ``                        |
| `DB_USERNAME`  | Usuario de la base de datos                             | ``                        |
| `DB_PASSWORD`  | Contraseña de la base de datos                          | —                                  |
| `JWT_SECRET`   | Secreto usado para **validar** la firma del JWT (HMAC)  | cadena larga y aleatoria          |
| `PORT`         | Puerto en el que escucha la app (opcional, default 8080)|                             |

> La conexión a la base de datos se fuerza por SSL (`sslmode=require`), requerido
> por Supabase para conexiones externas.

Crea un archivo `.env` (no versionado) para desarrollo local, o expórtalas en tu shell.

## Ejecución local

Requisitos: JDK 25 y Maven (o usar el wrapper `./mvnw`).

```bash
export DB_HOST=db.xxxxx.supabase.co
export DB_PORT=5432
export DB_NAME=postgres
export DB_USERNAME=postgres
export DB_PASSWORD=tu_password
export JWT_SECRET=un_secreto_largo_y_random

./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

## Ejecución con Docker

```bash
# Build de la imagen
docker build -t carvajal-favorites:latest .

# Run
docker run -d --name favorites \
  -p 8080:8080 \
  -e DB_HOST=db.xxxxx.supabase.co \
  -e DB_PORT=5432 \
  -e DB_NAME=postgres \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=tu_password \
  -e JWT_SECRET=un_secreto_largo_y_random \
  carvajal-favorites:latest

# Verificar salud
curl http://localhost:8080/actuator/health

# Logs
docker logs -f favorites

# Detener y limpiar
docker stop favorites && docker rm favorites
```

El `Dockerfile` usa build multi-stage (`maven:3.9-eclipse-temurin-25` para compilar y
`eclipse-temurin:25-jre` para ejecutar), corre como usuario no-root y expone el
puerto `8080`.

## Documentación de la API

Con la aplicación corriendo:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Endpoints

Base path: `/api/v1/favorites`. Todos requieren JWT salvo que se indique lo contrario.

### Lista de deseos

| Método | Ruta                                  | Descripción                                          |
|--------|----------------------------------------|--------------------------------------------------------|
| GET    | `/api/v1/favorites`                    | Lista los favoritos del usuario autenticado             |
| POST   | `/api/v1/favorites`                    | Agrega un producto a la lista de deseos                 |
| PUT    | `/api/v1/favorites/{idItemFavorite}`   | Actualiza cantidad/producto de un ítem                   |
| DELETE | `/api/v1/favorites/{idItemFavorite}`   | Elimina un ítem de la lista de deseos                    |

**Body (`POST` / `PUT`) — `FavoriteRequestDTO`**
```json
{
  "idProduct": 101,
  "quantity": 2
}
```
- `idProduct`: obligatorio.
- `quantity`: opcional, mínimo `1` si se envía.

**Respuesta — `FavoriteResponseDTO`**
```json
{
  "idItemFavorite": 1,
  "idUser": 15,
  "idProduct": 101,
  "nameProduct": "Audífonos Bluetooth",
  "price": 89000,
  "stockAvailable": 0,
  "quantity": 2,
  "state": "SIN_STOCK",
  "dateSave": "2026-09-08T10:15:30",
  "outOfStock": true
}
```
El estado (`state`) y la bandera `outOfStock` se recalculan en cada consulta
comparando contra el stock real del producto en la tabla `product`.

### Histórico de un ítem

| Método | Ruta                                                   | Descripción                                  |
|--------|----------------------------------------------------------|--------------------------------------------------|
| GET    | `/api/v1/favorites/{idItemFavorite}/history`             | Histórico de acciones sobre ese ítem             |

**Respuesta — `HistoryFavoriteResponseDTO[]`**
```json
[
  {
    "idHistory": 10,
    "idItemFavorite": 1,
    "action": "AGREGADO",
    "dateAction": "2026-09-08T10:15:30"
  },
  {
    "idHistory": 11,
    "idItemFavorite": 1,
    "action": "ACTUALIZADO",
    "dateAction": "2026-09-08T11:02:12"
  }
]
```

### Monitoreo

| Método | Ruta                 | Descripción                          | Requiere JWT |
|--------|------------------------|-----------------------------------------|:---:|
| GET    | `/actuator/health`    | Estado de salud del servicio (health check de Render) | No |

## Manejo de errores

Todas las excepciones de negocio devuelven un cuerpo consistente
(`ErrorResponse`):

```json
{
  "timestamp": "2026-09-08T10:20:00",
  "status": 404,
  "error": "Not Found",
  "message": "El producto no existe",
  "path": "/api/v1/favorites"
}
```

| Excepción                        | Status HTTP |
|-----------------------------------|:-----------:|
| `ResourceNotFoundException`        | 404 Not Found |
| `BusinessException`                | 409 Conflict |
| `MethodArgumentNotValidException`  | 400 Bad Request (incluye `errors` por campo) |
| Cualquier otra excepción no controlada | 500 Internal Server Error |

## Despliegue en Render

El repositorio incluye:
- `Dockerfile` — build multi-stage listo para producción.
- `.dockerignore` — excluye `target/`, `.git/`, `.idea/`, etc. del contexto de build.
- `render.yaml` — Blueprint para desplegar directamente desde GitHub.

Pasos:
1. En Render: **New → Blueprint** y conecta este repositorio.
2. Render detecta `render.yaml` y construye la imagen con el `Dockerfile`.
3. Completa las variables marcadas como secretas (`DB_HOST`, `DB_NAME`,
   `DB_USERNAME`, `DB_PASSWORD`); `JWT_SECRET` se autogenera y `DB_PORT` viene
   fijo en `5432`.
4. El health check está configurado en `/actuator/health`.

> Como `spring.jpa.hibernate.ddl-auto=validate`, el esquema (tablas `favorite`,
> `history_favorite`, `product`) debe existir previamente en la base de datos de
> Supabase antes del despliegue.

## Pruebas

```bash
./mvnw test
```

Incluye `spring-boot-starter-test`, `spring-security-test` y `h2` para pruebas de
integración en memoria sin depender de la base de datos de Supabase.