# 🔧 TROUBLESHOOTING - Panda Gamers Integration

## Problemas Comunes y Soluciones

---

### ❌ Error: "Cannot find module 'axios'"

**Solución:**
```bash
cd frontend
npm install axios
```

---

### ❌ Error: "POST /auth/login 403 Forbidden"

**Causas posibles:**
1. Backend no está corriendo en puerto 8080
2. CORS no está configurado correctamente
3. Credenciales inválidas

**Solución:**
1. Verificar que backend esté corriendo: `./gradlew.bat bootRun`
2. Verificar que esté escuchando en puerto 8080
3. Verificar credenciales en MongoDB

---

### ❌ Error: "GET /products 401 Unauthorized"

**Causa:** Token JWT no es válido o no está siendo enviado

**Solución:**
1. Hacer login primero en `/login`
2. Verificar que token esté en localStorage:
   ```javascript
   console.log(localStorage.getItem('token'));
   ```
3. Si está vacío, hacer login de nuevo

---

### ❌ Error: "Cannot connect to MongoDB"

**Causa:** Conexión a MongoDB Atlas no está disponible

**Solución:**
1. Verificar URL en `application.properties`:
   ```
   spring.data.mongodb.uri=mongodb+srv://usuario:pass@cluster.mongodb.net/dbname
   ```
2. Verificar conexión a internet
3. Verificar IP whitelist en MongoDB Atlas (agregar 0.0.0.0/0)
4. Verificar credenciales

---

### ❌ Error: "npm start - React Development Server no abre"

**Solución:**
```bash
cd frontend
npm install
npm start
```

Si sigue sin abrir, abrir manualmente: http://localhost:3000

---

### ❌ Error: "BUILD FAILED - Cannot resolve symbol 'User'"

**Solución:** Backend no se compiló correctamente
```bash
cd backend
./gradlew.bat clean build
```

Si hay errores, revisar que todos los modelos estén en `src/main/java/com/example/backend/model/`

---

### ⚠️ Advertencia: "Deprecated: Spring Security"

**Es normal.** Solo una advertencia, no afecta funcionalidad.

---

## 🧪 Verificación Manual con Postman

### 1. Registrar usuario
```
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "name": "Test User",
  "email": "test@test.com",
  "password": "password123"
}
```

**Respuesta esperada:**
```json
{
  "id": "...",
  "name": "Test User",
  "email": "test@test.com",
  "token": "eyJhbG..."
}
```

---

### 2. Login
```
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "test@test.com",
  "password": "password123"
}
```

**Respuesta esperada:**
```json
{
  "id": "...",
  "name": "Test User",
  "email": "test@test.com",
  "token": "eyJhbG..."
}
```

Copiar el `token` para los siguientes requests.

---

### 3. Obtener productos
```
GET http://localhost:8080/products
Authorization: Bearer {token}
```

---

### 4. Obtener producto por ID
```
GET http://localhost:8080/products/product_id
Authorization: Bearer {token}
```

---

### 5. Crear producto (Admin)
```
POST http://localhost:8080/products/create
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "nombre": "Producto Test",
  "descripcion": "Descripción",
  "precio": 99.99,
  "categoria": "Electrónicos",
  "stock": 10,
  "imagen": "url_imagen"
}
```

---

### 6. Crear orden
```
POST http://localhost:8080/orders/create/user_id
Authorization: Bearer {token}
Content-Type: application/json

[
  {
    "productId": "product_id",
    "cantidad": 1
  }
]
```

---

### 7. Obtener órdenes del usuario
```
GET http://localhost:8080/orders/user/user_id
Authorization: Bearer {token}
```

---

## 🔍 Debug en el Frontend

### Ver tokens en localStorage
```javascript
// En la consola del navegador (F12)
console.log('Token:', localStorage.getItem('token'));
console.log('User:', localStorage.getItem('auth_user'));
```

### Ver requests en Network tab
1. Abrir F12 (Developer Tools)
2. Ir a tab "Network"
3. Hacer una acción (login, cargar productos, etc.)
4. Ver requests HTTP y respuestas

### Logging de API calls
```javascript
// En frontend/src/api.js está configurado:
// - Todos los requests tienen timeout de 10s
// - Token se adjunta automáticamente
// - Errores 401 limpian token y redirigen a /login
```

---

## 📊 Verificar Base de Datos

### MongoDB Atlas Console
1. Ir a https://cloud.mongodb.com/
2. Login con credenciales
3. Ir a "Clusters"
4. Click "Browse Collections"
5. Verificar datos en:
   - `panda_gamers` > `users`
   - `panda_gamers` > `products`
   - `panda_gamers` > `orders`

---

## 🚀 Performance Check

### Backend
- **Compilación:** `./gradlew.bat build` (debe completar en <30s)
- **Startup:** Esperar a "Started BackendApplication" (5-10s)
- **Requests:** <100ms típicamente

### Frontend
- **Build:** `npm run build` (debe completar en <2 min)
- **Start:** `npm start` (debe completar en <10s)
- **Page Load:** <2s típicamente

---

## 🔐 Security Check

✅ Verificar que:
- [ ] Backend esté en puerto 8080
- [ ] CORS esté configurado correctamente
- [ ] JWT Token no es hardcodeado
- [ ] Credenciales no están en el código
- [ ] MongoDB URI está en variables de entorno
- [ ] Contraseñas usan BCrypt

---

## 📱 Testing en Diferentes Navegadores

| Navegador | Status | Notas |
|-----------|--------|-------|
| Chrome | ✅ | Recomendado |
| Firefox | ✅ | Funciona bien |
| Safari | ✅ | Puede haber CORS issues |
| Edge | ✅ | Basado en Chromium |
| IE 11 | ❌ | No soportado (ES6) |

---

## 🆘 Si Nada Funciona

### 1. Limpiar y reinstalar

**Backend:**
```bash
cd backend
./gradlew.bat clean
./gradlew.bat build
```

**Frontend:**
```bash
cd frontend
rm -r node_modules package-lock.json
npm install
npm start
```

### 2. Verificar puertos

```bash
# En cmd.exe
netstat -ano | findstr :8080
netstat -ano | findstr :3000
```

Si están ocupados, cambiar puertos o matar procesos.

### 3. Revisar logs

**Backend:**
- Logs en consola durante `./gradlew.bat bootRun`
- Buscar errores tipo "SQLException", "MongoException", etc.

**Frontend:**
- Abrir DevTools (F12)
- Tab "Console" para ver errores JavaScript
- Tab "Network" para ver requests fallidos

### 4. Contactar support

Si persiste el problema:
1. Capturar error completo (screenshot)
2. Incluir pasos para reproducir
3. Incluir versión de Java, Node.js, npm
4. Incluir logs relevantes

---

## ✅ Checklist de Funcionamiento

- [ ] Backend compilado sin errores
- [ ] Backend corriendo en puerto 8080
- [ ] Frontend compilado sin errores
- [ ] Frontend corriendo en puerto 3000
- [ ] Puedo registrar usuario
- [ ] Puedo hacer login
- [ ] Token se guarda en localStorage
- [ ] Puedo ver productos
- [ ] Puedo agregar a carrito
- [ ] Puedo crear orden
- [ ] Puedo ver mis compras
- [ ] Admin panel funciona
- [ ] CRUD de productos funciona

---

**Si todos los items están marcados, ¡la integración está funcionando correctamente! ✅**
