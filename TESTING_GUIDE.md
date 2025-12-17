# 🚀 Guía de Prueba - Endpoint de Importación de Productos

## Paso 1: Inicia el Backend

```bash
cd "c:\Users\morag\OneDrive\Desktop\Proeycto final\DSY1104_BENJAMIN_MORA_Evaluacion3_PandaGamers-main\backend"
gradlew.bat bootRun
```

Espera hasta ver:
```
Started BackendApplication in X.XXX seconds
```

El backend estará en: `http://localhost:8080`

## Paso 2: Inicia el Frontend

En otra terminal:

```bash
cd "c:\Users\morag\OneDrive\Desktop\Proeycto final\DSY1104_BENJAMIN_MORA_Evaluacion3_PandaGamers-main\frontend"
npm start
```

El frontend se abrirá en: `http://localhost:3000`

## Paso 3: Prueba de Autenticación

Antes de poder importar, necesitas estar autenticado como ADMIN.

### Opción A: Usuario Admin Existente
Si ya tienes un usuario admin en la BD:
1. Ve a `http://localhost:3000/login`
2. Ingresa email y contraseña del admin
3. Haz clic en "Login"

### Opción B: Crear Usuario Admin (MongoDB)
Si no existe usuario admin, agrégalo directamente a MongoDB:

```bash
# Conéctate a MongoDB
mongosh "mongodb://localhost:27017/pandagamers"

# Agregar usuario admin
db.users.insertOne({
  nombre: "Admin",
  email: "admin@pandagamers.com",
  password: "$2a$10$N9qo8uLOickgx2ZMRZoMye1KO7Lg0VqmZXABjcvN5AjFvPvmwV.H2", // bcrypt: "password"
  rol: "ADMIN",
  carrito: [],
  createdAt: new Date(),
  direccion: "123 Gamer Street"
})

# Salir
exit
```

**Datos de login:**
- Email: `admin@pandagamers.com`
- Contraseña: `password`

## Paso 4: Accede a la Página de Importación

1. Desde el frontend, ve a: `http://localhost:3000/admin/import`
2. Deberías ver tres opciones:
   - Importar Ejemplos (5 productos predefinidos)
   - Importar desde Archivo JSON
   - Información sobre campos requeridos

## Paso 5: Prueba la Importación

### Opción 1: Importar Ejemplos
1. Haz clic en el botón **"Importar Ejemplos"**
2. Espera la respuesta (Loading...)
3. Deberías ver: ✅ "5 productos importados exitosamente"
4. Verifica en `/productos` que aparezcan los nuevos productos

### Opción 2: Importar desde JSON
1. Haz clic en **"Descargar Ejemplo JSON"** (descargará `productos_ejemplo.json`)
   - O usa el archivo: `productos_ejemplo.json` en la raíz del proyecto
2. Selecciona el archivo para subir
3. Espera la respuesta (Loading...)
4. Deberías ver: ✅ "20 productos importados desde archivo"
5. Verifica en `/productos` que aparezcan todos los productos

### Opción 3: cURL (Direct API)
```bash
# Primero, obtén el token login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@pandagamers.com", "password": "password"}'

# Copia el token del response

# Luego, importa productos
curl -X POST http://localhost:8080/products/import \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "products": [
      {
        "id": "test1",
        "name": "Test Producto",
        "description": "Producto de prueba",
        "price": 99.99,
        "category": "Accesorios",
        "image": "/images/test.avif",
        "stock": 10
      }
    ]
  }'
```

## Paso 6: Verifica los Productos Importados

### En la Base de Datos
```bash
mongosh "mongodb://localhost:27017/pandagamers"
db.products.find().pretty()
```

Deberías ver los productos con campos:
- `nombre` (convertido de `name`)
- `descripcion` (convertido de `description`)
- `precio` (convertido de `price`)
- `categoria` (convertido de `category`)
- `imagen` (convertido de `image`)
- `stock`
- `createdAt` (timestamp)

### En la UI
1. Ve a `http://localhost:3000/productos`
2. Deberías ver los productos listados
3. Haz clic en uno para ver sus detalles en `/productos/:id`
4. Prueba agregar a carrito

## Paso 7: Prueba Duplicados

1. Importa los mismos 5 productos de ejemplo nuevamente
2. En lugar de duplicarse, se deben **actualizar**
3. Verifica en `/productos` que siga habiendo 5 (no 10)
4. La BD debe mostrar la misma cantidad de documentos

## 🧪 Casos de Prueba

### ✅ Caso Exitoso
- Array de productos válido
- Rol ADMIN
- Resultado: 200 OK, productos guardados

### ❌ Casos de Error

**Error 400 - Array vacío:**
```bash
curl -X POST http://localhost:8080/products/import \
  -H "Authorization: Bearer TOKEN" \
  -d '{"products": []}'
# Response: "No products to import"
```

**Error 401 - Sin token:**
```bash
curl -X POST http://localhost:8080/products/import \
  -d '{"products": [...]}'
# Response: 401 Unauthorized
```

**Error 403 - No es ADMIN:**
- Intenta con un usuario que no tenga rol ADMIN
- Response: "Access Denied"

## 📊 Validar Normalización

Después de importar, verifica que en la BD los campos están normalizados:

```bash
mongosh "mongodb://localhost:27017/pandagamers"

# Ver un producto específico
db.products.findOne({ nombre: "PlayStation 5" })

# Resultado esperado:
{
  _id: ObjectId("..."),
  nombre: "PlayStation 5",           // ← nombre (no name)
  descripcion: "...",                 // ← descripcion (no description)
  precio: 499.99,                     // ← precio (no price)
  categoria: "Consolas",              // ← categoria (no category)
  imagen: "/images/Consolas/...",    // ← imagen (no image)
  stock: 10,
  createdAt: ISODate("2025-12-03T...")
}
```

## 🔍 Debugging

### Si no ves productos en `/productos`
1. Verifica que el backend esté corriendo: `http://localhost:8080/products`
2. Verifica que el token sea válido (no expirado)
3. Abre la consola del navegador (F12) y busca errores
4. Verifica que en MongoDB existan los productos:
   ```bash
   mongosh "mongodb://localhost:27017/pandagamers"
   db.products.countDocuments()
   ```

### Si la importación falla
1. Verifica que estés logueado como ADMIN
2. Verifica que el archivo JSON sea válido
3. Busca errores en la consola del backend
4. Verifica que MongoDB esté corriendo

### Si hay error "dataStore not defined"
- Ya está arreglado, pero si persiste:
- Borra caché del navegador: Ctrl+Shift+Del
- Hard refresh: Ctrl+Shift+R
- Limpia `node_modules` y reinstala: `npm install`

## 📝 Notas Importantes

1. **Sin ADMIN, no puedes importar**: Asegúrate de tener rol ADMIN
2. **Sin backend corriendo, verás errores**: Backend debe estar en `http://localhost:8080`
3. **Duplicados por nombre**: Si importas 2 veces el mismo producto, se actualiza (no duplica)
4. **Campos mapéados automáticamente**: El frontend envia `name`, el backend lo guarda como `nombre`
5. **Imágenes locales**: Las rutas de imágenes deben estar en `frontend/public/images/`

## 🎯 Flujo Completo de Prueba

```
1. Inicia Backend (gradlew.bat bootRun)
   ↓
2. Inicia Frontend (npm start)
   ↓
3. Accede a /login
   ↓
4. Login como admin@pandagamers.com / password
   ↓
5. Ve a /admin/import
   ↓
6. Haz clic en "Importar Ejemplos"
   ↓
7. Espera confirmación ✅
   ↓
8. Ve a /productos
   ↓
9. Verifica que aparezcan 5 nuevos productos
   ↓
10. Haz clic en uno → /productos/:id
    ↓
11. Agrega a carrito → Carrito
    ↓
12. Verifica en MongoDB que los campos estén normalizados
    ↓
✅ TODO FUNCIONA
```

---

**Tiempo estimado de prueba**: 10-15 minutos
**Soporte**: Si hay problemas, revisa la sección de Debugging
