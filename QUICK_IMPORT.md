# 🚀 IMPORTAR PRODUCTOS DEL DATASTORE AL BACKEND

## ¡Lo más fácil posible! 3 pasos:

### Paso 1: Inicia Backend y Frontend
```bash
# Terminal 1
cd backend
gradlew.bat bootRun

# Terminal 2
cd frontend
npm start
```

### Paso 2: Login como Admin
1. Ve a `http://localhost:3000/login`
2. Ingresa:
   - Email: `admin@pandagamers.com` (o tu admin existente)
   - Contraseña: `password`
3. Haz clic en Login

### Paso 3: Importa los 36 productos del dataStore
1. Ve a `http://localhost:3000/admin/import`
2. Busca el archivo: **`datastore_productos_import.json`**
3. Haz clic en "Selecciona el archivo"
4. Sube el archivo
5. ¡Listo! Deberías ver: ✅ "36 productos importados desde archivo"

### Paso 4: Verifica
1. Ve a `http://localhost:3000/productos`
2. Deberías ver los 36 productos listados por categoría:
   - ✅ 4 Accesorios
   - ✅ 4 Consolas
   - ✅ 4 Juegos de mesa
   - ✅ 4 Mouses
   - ✅ 4 PC Gamers
   - ✅ 4 Poleras
   - ✅ 4 Polerones
   - ✅ 4 Portamouse
   - ✅ 4 Sillas

---

## 📋 ¿Qué contiene el archivo?

El archivo **`datastore_productos_import.json`** contiene los **36 productos** que están en `dataStore.js`, normalizados al formato del backend:

- **Campo original** → **Campo Backend**
- `name` → `nombre`
- `description` → `descripcion`
- `price` → `precio`
- `category` → `categoria`
- `image` → `imagen`
- `stock` → `stock`

Todos los precios están en CLP (pesos chilenos) y las imágenes apuntan a rutas locales en `frontend/public/images/`

---

## ¿Dónde está el archivo?

**Ubicación**: `c:\Users\morag\OneDrive\Desktop\Proeycto final\DSY1104_BENJAMIN_MORA_Evaluacion3_PandaGamers-main\datastore_productos_import.json`

O simplemente bájalo desde la raíz del proyecto.

---

## ✅ Confirmación de Éxito

Después de importar, deberías ver:
- ✅ 36 productos en la BD MongoDB
- ✅ Página `/productos` muestra todos los 36
- ✅ Puedes hacer clic en cualquiera para ver detalles
- ✅ Puedes agregarlos al carrito
- ✅ Los precios se muestran correctamente

---

## 🔍 Validar en MongoDB

```bash
mongosh "mongodb://localhost:27017/pandagamers"
db.products.count()  # Debe mostrar 36
db.products.find().pretty()  # Ver todos los productos
```

---

¡Eso es! Tus 36 productos de dataStore.js ahora están en el backend MongoDB. 🎉
