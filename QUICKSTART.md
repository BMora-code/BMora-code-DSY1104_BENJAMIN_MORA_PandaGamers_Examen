# 🚀 QUICK START - Panda Gamers

## 5 Minutos para Empezar

### Terminal 1 - Backend
```bash
cd backend
./gradlew.bat bootRun
```

**Esperado:**
```
Starting BackendApplication v0.0.1-SNAPSHOT...
...
Started BackendApplication in X.XXX seconds (JVM running for X.XXX)
```

---

### Terminal 2 - Frontend
```bash
cd frontend
npm start
```

**Esperado:**
```
Compiled successfully!

Local:            http://localhost:3000
On Your Network:  http://192.168.X.X:3000
```

*(Se abrirá http://localhost:3000 automáticamente)*

---

## 🧪 Test Rápido

### 1. Registrar
```
1. URL: http://localhost:3000/login
2. Click "Registrarse"
3. Email: test@test.com
4. Password: password123
5. Click Registrarse
→ Redirige a login
```

### 2. Login
```
1. Email: test@test.com
2. Password: password123
3. Click Iniciar Sesión
→ Redirige a home
→ Token guardado en localStorage
```

### 3. Productos
```
1. Ir a http://localhost:3000/
   └─→ Ver productos destacados (desde backend)
2. Click en "Productos"
   └─→ Ver lista completa (desde backend)
3. Click en un producto
   └─→ Ver detalles (desde /products/{id})
```

### 4. Compra
```
1. Click "Agregar al Carrito"
2. Click en carrito (arriba)
3. Click "Ir a Checkout"
4. Llenar datos de envío
5. Click "Pagar Ahora"
→ Orden creada en backend
```

### 5. Mis Compras
```
1. Click en "Mis Compras" (menu)
2. Ver órdenes personales
→ Cargadas desde /orders/user/{userId}
```

---

## 🔧 Verificar Funcionamiento

### Backend OK ✅
- ✓ Terminal muestra "Started BackendApplication"
- ✓ No hay errores en consola
- ✓ Puerto 8080 activo

### Frontend OK ✅
- ✓ Terminal muestra "Compiled successfully"
- ✓ Página carga sin errores
- ✓ F12 → Console sin errores

### API Conectada ✅
- ✓ F12 → Network → ver requests a localhost:8080
- ✓ Requests tienen "Authorization: Bearer {token}"
- ✓ Respuestas tienen datos (no 500 errors)

---

## 🔗 URLs Principales

| URL | Descripción |
|-----|-------------|
| http://localhost:3000/ | Home |
| http://localhost:3000/login | Registrar / Login |
| http://localhost:3000/productos | Catálogo completo |
| http://localhost:3000/mis-compras | Órdenes del usuario |
| http://localhost:3000/admin | Panel de admin |
| http://localhost:3000/carrito | Carrito |

---

## 📊 Datos de Prueba

### Cuenta Admin (si existe)
```
Email: admin@admin.com
Password: admin123
```

### Cuenta Creada
```
Email: test@test.com
Password: password123
Role: user
```

---

## 🔍 Debug

### Ver Token en Consola
```javascript
// F12 → Console
console.log(localStorage.getItem('token'));
```

### Ver Requests
```
F12 → Network Tab → hacer acción → ver request
```

### Ver User Actual
```javascript
// F12 → Console
console.log(JSON.parse(localStorage.getItem('auth_user')));
```

---

## ⚠️ Problemas Comunes

### "Cannot GET /products"
❌ Backend no está corriendo
✅ Solución: Ejecutar `./gradlew.bat bootRun`

### "Cannot connect to MongoDB"
❌ Sin internet o MongoDB Atlas caído
✅ Solución: Verificar conexión, esperar

### "401 Unauthorized"
❌ Token no es válido
✅ Solución: Hacer login de nuevo

### "npm: command not found"
❌ Node.js no está instalado
✅ Solución: Descargar de nodejs.org

---

## 📱 Preview

```
┌─────────────────────────────┐
│      PANDA GAMERS 🎮        │
├─────────────────────────────┤
│ Home | Productos | Ofertas  │
├─────────────────────────────┤
│                             │
│  [Producto 1]  [Producto 2] │
│  [Producto 3]  [Producto 4] │
│                             │
│  Login ↑  Carrito 🛒        │
│                             │
└─────────────────────────────┘
```

---

## ✅ Checklist

- [ ] Backend corriendo (puerto 8080)
- [ ] Frontend corriendo (puerto 3000)
- [ ] Página carga sin errores
- [ ] Puedo registrar usuario
- [ ] Puedo hacer login
- [ ] Veo productos en home
- [ ] Puedo ver detalle de producto
- [ ] Puedo agregar a carrito
- [ ] Puedo hacer checkout
- [ ] Puedo ver mis compras

**Si todos están ✓ → ¡Todo funciona! 🎉**

---

## 🎓 Aprender Más

- `README_INTEGRATION.md` - Documentación completa
- `TROUBLESHOOTING.md` - Solución de problemas
- `CHANGELOG.md` - Cambios detallados

---

**¡Listo para demostrar! 🚀**
