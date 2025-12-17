# PandaGamers Frontend - Conectado con Spring Boot Backend

## Configuración de la Conexión al Backend

El frontend React está configurado para conectarse con el backend Spring Boot en **`http://localhost:8080`**.

### Archivo de Configuración: `src/api.js`

El archivo `src/api.js` contiene una instancia centralizada de axios con:

- **baseURL**: `http://localhost:8080` (o la variable de entorno `REACT_APP_API_URL`)
- **Interceptor de autenticación**: Adjunta automáticamente el JWT token al header `Authorization` (excepto en endpoints `/auth/`)
- **Interceptor de errores**: Maneja respuestas 401 (token expirado) redirigiendo a login

### Endpoints Disponibles

```javascript
// Auth
import { authAPI } from './api';
await authAPI.register(name, email, password);  // POST /auth/register
await authAPI.login(email, password);           // POST /auth/login

// Products
import { productsAPI } from './api';
await productsAPI.getAll();                     // GET /products
await productsAPI.getById(id);                  // GET /products/:id
await productsAPI.create(productData);          // POST /products/create (ADMIN)
await productsAPI.update(id, productData);      // PUT /products/update/:id (ADMIN)
await productsAPI.delete(id);                   // DELETE /products/delete/:id (ADMIN)

// Orders
import { ordersAPI } from './api';
await ordersAPI.create(userId, orderItems);    // POST /orders/create/:userId
await ordersAPI.getByUser(userId);              // GET /orders/user/:userId

// Transbank
import { transbankAPI } from './api';
await transbankAPI.createTransaction(orderId);  // POST /pay/create?orderId=ORDER_ID
await transbankAPI.confirmTransaction(token);   // GET /pay/confirm/:token
```

## Almacenamiento de Token

Tras login exitoso, el token JWT se almacena en `localStorage`:

```javascript
localStorage.setItem('token', token);        // Token JWT
localStorage.setItem('auth_user', JSON.stringify(user)); // Datos del usuario
```

El token se adjunta automáticamente a todas las peticiones protegidas por el interceptor de axios.

## Integración en Componentes

### Ejemplo: Login con Backend

```javascript
import { authAPI } from '../api';

const handleLogin = async (email, password) => {
  try {
    const response = await authAPI.login(email, password);
    const { token, user } = response.data;
    localStorage.setItem('token', token);
    localStorage.setItem('auth_user', JSON.stringify(user));
    // Redirigir o actualizar contexto
  } catch (error) {
    console.error('Login error:', error);
  }
};
```

### Ejemplo: Obtener Productos del Backend

```javascript
import { productsAPI } from '../api';

useEffect(() => {
  productsAPI.getAll()
    .then(response => setProducts(response.data))
    .catch(error => console.error('Error fetching products:', error));
}, []);
```

## Fallback a localStorage (dataStore)

Si el backend no responde, algunos componentes caen en fallback automático a `dataStore` (data local en localStorage) para garantizar continuidad.

## Desarrollo Local

1. Asegúrate que el backend corre en `http://localhost:8080`:
   ```bash
   cd backend
   gradlew.bat bootRun
   ```

2. En otra terminal, inicia el frontend:
   ```bash
   npm start
   ```

3. El frontend estará en `http://localhost:3000` y hará peticiones a `http://localhost:8080`.

## Variables de Entorno (Opcional)

Para cambiar la URL del backend en desarrollo o producción, define:

```bash
# .env (en la raíz del frontend)
REACT_APP_API_URL=http://tu-backend:8080
```

Entonces reinicia `npm start`.

## Colección Postman

Para probar los endpoints sin el frontend, usa la colección Postman en `backend/postman/panda_gamers.postman_collection.json`.
