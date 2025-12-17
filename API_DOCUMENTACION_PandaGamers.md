# Documentación de API e Integración — PandaGamers

**Título:** Documentación de API para Gestión de Productos, Usuarios y Órdenes

**Versión del Documento:** 1.0

**Fecha de Creación:** 4 de diciembre de 2025

---

📝 **Descripción General**

Este documento describe los endpoints del backend de PandaGamers y las instrucciones de integración local con el frontend. Está pensado para docentes, desarrolladores y evaluadores.

🌐 **Información General**

- **URL del Backend (local):** `http://localhost:8080`
- **URL Swagger (local):** `http://localhost:8080/swagger-ui.html`

---

**📘 Documentación de Endpoints**

**🔐 Endpoints de Autenticación**

- **POST** `/api/auth/login`
  - **Descripción:** Autentica al usuario y retorna un token JWT.
  - **Ejemplo de entrada (JSON):**

```json
{ "email": "ejemplo@correo.com", "password": "123456" }
```
  - **Respuestas:**
    - `200 OK` — Autenticación correcta. Respuesta incluye `token`.
    - `401 Unauthorized` — Credenciales inválidas.
  - **Ejemplo de uso (cURL):**

```bash
curl -X POST "http://localhost:8080/api/auth/login" -H "Content-Type: application/json" -d "{ \"email\": \"ejemplo@correo.com\", \"password\": \"123456\" }"
```

- **POST** `/api/auth/register`
  - **Descripción:** Registra un nuevo usuario en el sistema.
  - **Ejemplo de entrada (JSON):**

```json
{
  "nombre": "Juan",
  "email": "juan@ejemplo.com",
  "password": "123456",
  "roles": ["USER"]
}
```
  - **Respuestas:**
    - `201 Created` — Usuario creado.
    - `400 Bad Request` — Datos inválidos.

---

**🛒 Endpoints de Productos**

- **GET** `/api/products`
  - **Descripción:** Lista todos los productos disponibles.
  - **Autenticación:** No.
  - **Respuesta:** `200 OK` con arreglo de productos.
  - **Ejemplo (cURL):**

```bash
curl http://localhost:8080/api/products
```

- **POST** `/api/products`
  - **Descripción:** Crea un producto (solo admin).
  - **Autenticación:** Sí (token JWT con rol Admin o código admin según implementación).
  - **Ejemplo de entrada (JSON):**

```json
{
  "title": "Mouse Gamer",
  "description": "Mouse con sensor óptico...",
  "price": 49990,
  "stock": 10,
  "category": "Mouses"
}
```
  - **Respuestas:**
    - `201 Created` — Producto creado.
    - `400 Bad Request` — Datos inválidos.
  - **Encabezado de autenticación:** `Authorization: Bearer <JWT_TOKEN>`

- **PUT** `/api/products/{id}`
  - **Descripción:** Actualiza un producto existente.
  - **Método:** `PUT`
  - **Autenticación:** Admin.
  - **Respuestas:** `200 OK`, `400 Bad Request`, `404 Not Found`.

---

**🎁 Endpoints de Ofertas**

- **GET** `/api/offers`
  - **Descripción:** Obtiene la lista de ofertas activas.
  - **Método:** `GET`
  - **Autenticación:** No.

---

**🧾 Endpoints de Órdenes**

- **POST** `/api/orders`
  - **Descripción:** Crea una nueva orden para el usuario autenticado.
  - **Autenticación:** Sí (JWT).
  - **Ejemplo de entrada (JSON):**

```json
{
  "items": [
    { "productId": "6423...", "quantity": 2 }
  ],
  "direccion": "Calle Falsa 123",
  "metodoPago": "transferencia"
}
```
  - **Respuestas:**
    - `201 Created` — Orden creada.
    - `400 Bad Request` — Datos inválidos.

- **GET** `/api/orders/user/{id}`
  - **Descripción:** Lista las órdenes del usuario.
  - **Método:** `GET`
  - **Autenticación:** Sí (JWT) — el usuario autenticado solo puede consultar sus órdenes o admin puede listar por usuario.
  - **Respuestas:**
    - `200 OK`
    - `401 Unauthorized`
    - `404 Not Found`

---

**🧪 Notas sobre Autenticación**

- Todos los endpoints que requieren autenticación esperan el header:

```
Authorization: Bearer <JWT_TOKEN>
```

- Obtén `<JWT_TOKEN>` haciendo `POST /api/auth/login`.

---

**🖥️ Integración con el Frontend (Local)**

A continuación tienes una guía práctica para ejecutar y conectar el frontend React con el backend Spring Boot localmente.

**Requisitos previos**
- JDK 21 instalado y `JAVA_HOME` configurado (para el backend).
- Node.js (14+) y `npm` o `yarn` (para el frontend).
- MongoDB o el datastore que use el proyecto (si aplica). Revisa `application.properties` en `backend/src/main/resources`.

**1) Ejecutar Backend (Windows, `cmd.exe`)**

- Abrir `cmd` en la carpeta del proyecto y ejecutar:

```bat
cd backend
gradlew.bat clean bootRun
```

- Swagger estará disponible en `http://localhost:8080/swagger-ui.html` si la dependencia de Springdoc está activa.

**2) Ejecutar Frontend (Windows, `cmd.exe`)**

- Abrir `cmd` y ejecutar desde la carpeta `frontend`:

```bat
cd frontend
npm install
npm start
```

- Por defecto React suele usar `http://localhost:3000`.

**3) Configurar la URL base del API en el Frontend**

- En este proyecto de ejemplo, el archivo con la URL base suele encontrarse en `frontend/src/api.js` o `frontend/src/config.js`.
- Busca una línea similar a:

```js
const API_BASE = "http://localhost:8080";
```

- Asegúrate de que el frontend apunte a `http://localhost:8080`.

**4) CORS**

- Si el navegador bloquea peticiones por CORS, habilita CORS en el backend (por ejemplo, en un `@Configuration` de Spring o controladores) o define reglas en `application.properties`/`SecurityConfig`.
- Ejemplo rápido (controlador):

```java
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/products")
public class ProductController { ... }
```

**5) Probar la integración**

- Inicia primero el backend (`bootRun`), luego el frontend (`npm start`).
- En el frontend, intenta listar productos; abre DevTools → Network para ver las llamadas a `http://localhost:8080/api/products`.
- Para endpoints autenticados, haz login en el frontend y revisa que el header `Authorization: Bearer <token>` se envíe en las peticiones.

**6) Ejemplos de peticiones útiles**

- Obtener productos (cURL):

```bash
curl http://localhost:8080/api/products
```

- Crear orden (con JWT):

```bash
curl -X POST "http://localhost:8080/api/orders" -H "Content-Type: application/json" -H "Authorization: Bearer <JWT_TOKEN>" -d "{ \"items\": [{ \"productId\": \"ID\", \"quantity\": 1 }], \"direccion\": \"Mi Direccion\" }"
```

**7) Postman / colección**

- El proyecto incluye una carpeta `backend/postman` con `panda_gamers.postman_collection.json`. Impórtala en Postman para probar rápidamente los endpoints.

---

**⚙️ Buenas prácticas y recomendaciones**

- Usa siempre el wrapper de Gradle (`gradlew.bat`) para asegurar la versión de Gradle correcta.
- No comitees carpetas de salida del IDE (`bin/`, `out/`). `backend/.gitignore` ya incluye `bin/`.
- Para debugging: ejecuta tests con `gradlew.bat test` y verifica `build/reports/tests/test/index.html`.
- Para documentación automática: si Swagger UI no aparece en `http://localhost:8080/swagger-ui.html`, revisa la dependencia `org.springdoc` en `backend/build.gradle` y que no haya conflicto en `application.properties`.

---

**📎 Archivos y rutas clave**

- Backend:
  - `backend/build.gradle`
  - `backend/src/main/java/...` (controladores y servicios)
  - `backend/src/main/resources/application.properties`
  - `backend/postman/panda_gamers.postman_collection.json`
- Frontend:
  - `frontend/src/api.js` o `frontend/src/App.js` (dónde se configura el `API_BASE`)
  - `frontend/package.json`

---

  ## 📋 Tabla completa de endpoints (extraída del código fuente)

  La siguiente tabla contiene TODOS los endpoints reales que están definidos en los controladores bajo `backend/src/main/java/com/example/backend/controller`.
  Usé directamente los mappings y las anotaciones encontradas en el código — no se añadieron ni inventaron rutas.

  | Método | Ruta exacta (base: http://localhost:8080) | Controlador (archivo) | Descripción (1 línea) | Body requerido | Respuestas (códigos más relevantes) | Requiere autenticación | Requiere rol ADMIN |
  |---|---|---|---|---|---:|---:|---:|
  | POST | /auth/register | AuthController (`AuthController.java`) | Registrar nuevo usuario (envía `adminCode` para crear ADMIN) | RegisterRequest (nombre/name, email, password, adminCode opt.) | 200: Usuario registrado; 400: Email/password requeridos; 409: Email ya registrado; 500: Error servidor | No | No |
  | POST | /auth/login | AuthController (`AuthController.java`) | Login y generación de JWT | LoginRequest (email, password) | 200: Login exitoso; 400: Email/password requeridos; 401: Credenciales inválidas; 404: Usuario no encontrado; 500: Error servidor | No | No |
  | GET | /products | ProductController (`ProductController.java`) | Listar todos los productos (inserta productos iniciales si la colección está vacía) | — | 200: Éxito; 500: Error servidor | No | No |
  | GET | /products/{id} | ProductController (`ProductController.java`) | Obtener detalles de un producto por ID | — | 200: Éxito; 404: No encontrado; 500: Error servidor | No | No |
  | POST | /products/create | ProductController (`ProductController.java`) | Crear nuevo producto | Product (objeto Product) | 200: Producto creado; 400: Datos inválidos; 403: Acceso denegado; 500: Error servidor | Sí | Sí |
  | PUT | /products/update/{id} | ProductController (`ProductController.java`) | Actualizar producto existente | Product (objeto Product) | 200: Actualizado; 400: Datos inválidos; 403: Acceso denegado; 404: No encontrado; 500: Error servidor | Sí | Sí |
  | DELETE | /products/delete/{id} | ProductController (`ProductController.java`) | Eliminar producto | — | 200: Eliminado; 403: Acceso denegado; 500: Error servidor | Sí | Sí |
  | PUT | /products/admin/stock/{id} | ProductController (`ProductController.java`) | Actualizar stock de producto (payload simple) | StockUpdateRequest { stock:int } | 200: Stock actualizado; 400: Datos inválidos; 403: Acceso denegado; 404: No encontrado; 500: Error servidor | Sí | Sí (protección a nivel HTTP y método) |
  | POST | /products/import | ProductController (`ProductController.java`) | Importar lote de productos (create/update) | ImportProductRequest (lista de productos) | 200: Productos importados; 400: No hay productos para importar; 403: Acceso denegado; 500: Error servidor | Sí | Sí |
  | POST | /offers/create | OfferController (`OfferController.java`) | Crear nueva oferta / promoción | Offer (objeto Offer) | 200: Oferta creada; 400: Datos inválidos; 403: Acceso denegado; 500: Error servidor | Sí | Sí |
  | PUT | /offers/update/{id} | OfferController (`OfferController.java`) | Actualizar oferta existente | Offer (objeto Offer) | 200: Oferta actualizada; 400: Datos inválidos; 403: Acceso denegado; 404: No encontrado; 500: Error servidor | Sí | Sí |
  | DELETE | /offers/delete/{id} | OfferController (`OfferController.java`) | Eliminar oferta | — | 200: Eliminada; 403: Acceso denegado; 500: Error servidor | Sí | Sí |
  | GET | /offers/admin/all | OfferController (`OfferController.java`) | Listar todas las ofertas (view admin) | — | 200: Éxito; 403: Acceso denegado; 500: Error servidor | Sí | Sí |
  | GET | /offers/all/public | OfferController (`OfferController.java`) | Listar ofertas públicas (visible al público) | — | 200: Éxito; 500: Error servidor | No | No |
  | POST | /orders/create/{userId} | OrderController (`OrderController.java`) | Crear nueva orden para usuario (valida stock, aplica DUOC) | CreateOrderRequest (items, subtotal, total, shipping, ...) | 200: Orden creada; 400: Datos inválidos o stock insuficiente; 500: Error servidor | Sí (según SecurityConfig `/orders/**` authenticated) | No (solo admin endpoints bajo `/orders/admin/**`) |
  | GET | /orders/user/{userId} | OrderController (`OrderController.java`) | Obtener órdenes de un usuario | — | 200: Éxito; 500: Error servidor | Sí | No |
  | GET | /orders/{id} | OrderController (`OrderController.java`) | Obtener orden por ID | — | 200: Éxito; 404: No encontrada; 500: Error servidor | Sí | No |
  | GET | /orders/admin/all | OrderController (`OrderController.java`) | Listar todas las órdenes (admin) | — | 200: Éxito; 403: Acceso denegado; 500: Error servidor | Sí | Sí |
  | DELETE | /orders/admin/{id} | OrderController (`OrderController.java`) | Eliminar orden (admin) | — | 200: Eliminada; 403: Acceso denegado; 500: Error servidor | Sí | Sí |
  | PUT | /orders/{id}/complete | OrderController (`OrderController.java`) | Marcar orden como completada | — | 200: Orden completada; 404: No encontrada; 500: Error servidor | Sí | No |
  | PUT | /orders/{id}/status | OrderController (`OrderController.java`) | Actualizar estado de orden (envía JSON con `status`) | Map JSON { "status": "NuevoEstado" } | 200: Estado actualizado; 400: Datos inválidos; 404: No encontrada; 500: Error servidor | Sí | No |
  | GET | /users/profile | UserController (`UserController.java`) | Obtener perfil del usuario autenticado actual | — | 200: Éxito; 401: No autenticado; 404: Usuario no encontrado; 500: Error servidor | Sí | No |
  | GET | /users/admin/all | UserController (`UserController.java`) | Listar todos los usuarios (admin) | — | 200: Éxito; 403: Acceso denegado; 500: Error servidor | Sí | Sí |
  | DELETE | /users/admin/{id} | UserController (`UserController.java`) | Eliminar usuario (admin) | — | 200: Eliminado; 403: Acceso denegado; 500: Error servidor | Sí | Sí |
  | POST | /users/admin/create | UserController (`UserController.java`) | Crear usuario desde Admin | User (objeto User) | 200: Usuario creado; 400: Email y password requeridos; 403: Acceso denegado; 500: Error servidor | Sí | Sí |
  | POST | /pay/create | TransbankController (`TransbankController.java`) | Crear transacción de pago (query params: orderId, userId?, amount?, returnUrl?, finalUrl?) | NO body (usa query params) | 200: Transacción creada | No (permitido) | No |
  | GET | /pay/confirm/{token} | TransbankController (`TransbankController.java`) | Confirmar resultado de transacción por token | — | 200: Confirmada; 404: Token no encontrado | No (permitido) | No |

  > Nota: en `SecurityConfig` hay reglas adicionales (ej. `/products/**` se permite a nivel HTTP, pero los métodos con `@PreAuthorize("hasRole('ADMIN')")` requieren ADMIN). Para órdenes, `/orders/**` exige autenticación y `/orders/admin/**` exige ADMIN.

  ### 🟦 Tabla lista para Excel

  Si prefieres el formato estilo hoja de cálculo (columnas listas para copiar/pegar en Excel), aquí tienes la versión equivalente con las columnas solicitadas:

  | Metodo HTTP | Ruta del Endpoint | Descripción | Datos de entrada | Respuestas | API PÚBLICA/PRIVADA | Requiere Autenticación | Roles permitidos | Observaciones |
  |---|---|---|---|---|---|---:|---|---|
  | POST | /auth/register | Registrar nuevo usuario y generar JWT (opcional adminCode) | { nombre/name, email, password, adminCode? } | 200: Usuario registrado; 400: Email/password requeridos; 409: Email ya registrado; 500: Error servidor | PÚBLICA | No | N/A | `adminCode` opcional para crear ADMIN |
  | POST | /auth/login | Autenticar usuario y obtener token JWT | { email, password } | 200: Login exitoso; 400: Email/password requeridos; 401: Credenciales inválidas; 404: Usuario no encontrado; 500: Error servidor | PÚBLICA | No | N/A | Retorna `token` en body |
  | GET | /products | Listar productos (inserta productos iniciales si vacío) | N/A | 200: Éxito; 500: Error servidor | PÚBLICA | No | N/A | Devuelve ProductResponse[] |
  | GET | /products/{id} | Obtener producto por ID | N/A | 200: Éxito; 404: No encontrado; 500: Error servidor | PÚBLICA | No | N/A | Devuelve ProductResponse |
  | POST | /products/create | Crear producto | Product (JSON) | 200: Producto creado; 400: Datos inválidos; 403: Acceso denegado; 500: Error servidor | PRIVADA | Sí | ADMIN | `@PreAuthorize('hasRole("ADMIN")')` |
  | PUT | /products/update/{id} | Actualizar producto | Product (JSON) | 200: Actualizado; 400: Datos inválidos; 403: Acceso denegado; 404: No encontrado; 500: Error servidor | PRIVADA | Sí | ADMIN | |
  | DELETE | /products/delete/{id} | Eliminar producto | N/A | 200: Eliminado; 403: Acceso denegado; 500: Error servidor | PRIVADA | Sí | ADMIN | |
  | PUT | /products/admin/stock/{id} | Actualizar stock (payload: { stock:int }) | { stock:int } | 200: Stock actualizado; 400: Datos inválidos; 403: Acceso denegado; 404: No encontrado; 500: Error servidor | PRIVADA | Sí | ADMIN | Protegido a nivel HTTP y por PreAuthorize |
  | POST | /products/import | Importar lote de productos | ImportProductRequest (lista de productos) | 200: Productos importados; 400: No hay productos para importar; 403: Acceso denegado; 500: Error servidor | PRIVADA | Sí | ADMIN | Actualiza por nombre si ya existe |
  | POST | /offers/create | Crear oferta / promoción | Offer (JSON) | 200: Oferta creada; 400: Datos inválidos; 403: Acceso denegado; 500: Error servidor | PRIVADA | Sí | ADMIN | |
  | PUT | /offers/update/{id} | Actualizar oferta | Offer (JSON) | 200: Oferta actualizada; 400: Datos inválidos; 403: Acceso denegado; 404: No encontrado; 500: Error servidor | PRIVADA | Sí | ADMIN | |
  | DELETE | /offers/delete/{id} | Eliminar oferta | N/A | 200: Eliminada; 403: Acceso denegado; 500: Error servidor | PRIVADA | Sí | ADMIN | |
  | GET | /offers/admin/all | Listar ofertas (admin) | N/A | 200: Éxito; 403: Acceso denegado; 500: Error servidor | PRIVADA | Sí | ADMIN | |
  | GET | /offers/all/public | Listar ofertas públicas | N/A | 200: Éxito; 500: Error servidor | PÚBLICA | No | N/A | Endpoint público visible al frontend |
  | POST | /orders/create/{userId} | Crear nueva orden (valida stock, aplica DUOC) | CreateOrderRequest (items, subtotal, shippingCost, iva, total, deliveryOption, shippingInfo) | 200: Orden creada; 400: Datos inválidos o stock insuficiente; 500: Error servidor | PRIVADA | Sí | USER/ADMIN | Requiere autenticación; admin endpoints en `/orders/admin/**` |
  | GET | /orders/user/{userId} | Obtener órdenes del usuario | N/A | 200: Éxito; 500: Error servidor | PRIVADA | Sí | USER/ADMIN | Devuelve OrderResponse[] |
  | GET | /orders/{id} | Obtener orden por ID | N/A | 200: Éxito; 404: No encontrada; 500: Error servidor | PRIVADA | Sí | USER/ADMIN | |
  | GET | /orders/admin/all | Listar todas las órdenes (admin) | N/A | 200: Éxito; 403: Acceso denegado; 500: Error servidor | PRIVADA | Sí | ADMIN | Protegido por SecurityConfig y PreAuthorize |
  | DELETE | /orders/admin/{id} | Eliminar orden (admin) | N/A | 200: Eliminada; 403: Acceso denegado; 500: Error servidor | PRIVADA | Sí | ADMIN | |
  | PUT | /orders/{id}/complete | Marcar orden completada | N/A | 200: Orden completada; 404: No encontrada; 500: Error servidor | PRIVADA | Sí | USER/ADMIN | Cambia status a "Completada" |
  | PUT | /orders/{id}/status | Actualizar estado de orden | { status: "NuevoEstado" } | 200: Estado actualizado; 400: Datos inválidos; 404: No encontrada; 500: Error servidor | PRIVADA | Sí | USER/ADMIN | Acepta JSON con `status` |
  | GET | /users/profile | Obtener perfil del usuario autenticado | N/A | 200: Éxito; 401: No autenticado; 404: Usuario no encontrado; 500: Error servidor | PRIVADA | Sí | USER/ADMIN | Retorna datos del usuario sin password |
  | GET | /users/admin/all | Listar usuarios (admin) | N/A | 200: Éxito; 403: Acceso denegado; 500: Error servidor | PRIVADA | Sí | ADMIN | |
  | DELETE | /users/admin/{id} | Eliminar usuario (admin) | N/A | 200: Eliminado; 403: Acceso denegado; 500: Error servidor | PRIVADA | Sí | ADMIN | |
  | POST | /users/admin/create | Crear usuario por admin | User (JSON) | 200: Usuario creado; 400: Email y password requeridos; 403: Acceso denegado; 500: Error servidor | PRIVADA | Sí | ADMIN | Si no se especifica rol, se crea USER por defecto |
  | POST | /pay/create | Crear transacción de pago (query params) | Query params: orderId, userId?, amount?, returnUrl?, finalUrl? | 200: Transacción creada | PÚBLICA | No | N/A | Devuelve PaymentResponse con token y url (sandbox) |
  | GET | /pay/confirm/{token} | Confirmar transacción por token | N/A | 200: Confirmada; 404: Token no encontrado | PÚBLICA | No | N/A | Devuelve ConfirmPaymentResponse |



**¿Necesitas que genere una versión imprimible (PDF) o la agregue al repo como `API_DOCUMENTACION_PandaGamers.md`?**

He creado este archivo en la raíz del proyecto: `API_DOCUMENTACION_PandaGamers.md`.

Si quieres, puedo:
- actualizar ejemplos de request/responses con los esquemas reales del backend,
- añadir una sección de errores comunes y códigos de respuesta por endpoint,
- crear pasos específicos para habilitar CORS en la configuración de seguridad del backend,
- generar la colección de Postman ya con variables de entorno (`{{baseUrl}}`, `{{token}}`).

Dime cuál de esas acciones deseas que haga a continuación.
