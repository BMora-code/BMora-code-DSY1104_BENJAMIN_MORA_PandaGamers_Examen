# 📸 PASO A PASO VISUAL - Importar 36 Productos

## PASO 1: Inicia el Backend

```bash
cd "c:\Users\morag\OneDrive\Desktop\Proeycto final\DSY1104_BENJAMIN_MORA_Evaluacion3_PandaGamers-main\backend"
gradlew.bat bootRun
```

**Espera a ver:**
```
Started BackendApplication in X.XXX seconds
```

El backend está en: `http://localhost:8080`

---

## PASO 2: Inicia el Frontend

En otra terminal:

```bash
cd "c:\Users\morag\OneDrive\Desktop\Proeycto final\DSY1104_BENJAMIN_MORA_Evaluacion3_PandaGamers-main\frontend"
npm start
```

**Se abrirá automáticamente:** `http://localhost:3000`

---

## PASO 3: Ve a Login

**URL:** `http://localhost:3000/login`

```
┌─────────────────────────────────┐
│     PANDA GAMERS LOGIN          │
├─────────────────────────────────┤
│ Email:      admin@pandagamers.com
│ Contraseña: password             │
│                                 │
│     [LOGIN]                     │
└─────────────────────────────────┘
```

**Ingresa:**
- Email: `admin@pandagamers.com`
- Contraseña: `password`

Haz clic en **LOGIN**

---

## PASO 4: Espera a que inicie sesión

Deberías ser redirigido a `/` (Home) después del login.

---

## PASO 5: Ve a la Página de Importación

**URL:** `http://localhost:3000/admin/import`

O navega desde el menú si existe.

---

## PASO 6: Ve la página de importación

```
┌─────────────────────────────────────────────────────────────┐
│          IMPORTAR PRODUCTOS AL BACKEND                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ ┌────────────────────────────────────────────────────────┐ │
│ │ Opción 1: Importar 5 Productos de Ejemplo              │ │
│ │ Importa un conjunto pequeño de productos de demostración
│ │                                                        │ │
│ │         [Importar Ejemplos]                            │ │
│ └────────────────────────────────────────────────────────┘ │
│                                                             │
│ ┌────────────────────────────────────────────────────────┐ │
│ │ Opción 2: Importar 36 Productos de PandaGamers        │ │
│ │ Importa TODOS los 36 productos del dataStore (tienda) │ │
│ │                                                        │ │
│ │      [Importar 36 Productos]  ⭐ ← ESTE               │ │
│ └────────────────────────────────────────────────────────┘ │
│                                                             │
│ ┌────────────────────────────────────────────────────────┐ │
│ │ Opción 3: Importar desde Archivo JSON                  │ │
│ │ Sube un archivo JSON con tus productos personalizados  │ │
│ │                                                        │ │
│ │    [Selecciona un archivo]                            │ │
│ └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## PASO 7: Haz clic en el botón VERDE

**Busca el botón verde que dice:**
```
"Importar 36 Productos de PandaGamers"
```

Haz clic en él.

---

## PASO 8: Espera el Loading

```
┌──────────────────────────────────────┐
│    Importando...                     │
│    ⏳ Por favor espera               │
└──────────────────────────────────────┘
```

Esto toma entre **1-5 segundos**.

---

## PASO 9: ¡Éxito! 

```
┌──────────────────────────────────────────────────────────┐
│ ✅ 36 productos importados exitosamente                  │
└──────────────────────────────────────────────────────────┘
```

**Si ves este mensaje, ¡todo funcionó!** 🎉

---

## PASO 10: Verifica en la tienda

**URL:** `http://localhost:3000/productos`

Deberías ver:

```
┌─────────────────────────────────────────────────────────┐
│        PRODUCTOS PANDA GAMERS                           │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  🏷️ Accesorios (4)                                     │
│  ├─ Auriculares HyperX        $79.990                  │
│  ├─ Control Inalámbrico        $59.990                  │
│  ├─ Mousepad RGB               $29.990                  │
│  └─ Teclado Razer             $149.990                  │
│                                                         │
│  🎮 Consolas (4)                                        │
│  ├─ Nintendo Switch           $349.990                  │
│  ├─ PlayStation 4 Pro         $399.990                  │
│  ├─ PlayStation 5             $599.990                  │
│  └─ Xbox Series X             $549.990                  │
│                                                         │
│  🎲 Juegos de mesa (4)                                  │
│  ├─ Carcassonne               $49.990                   │
│  ├─ Catan                     $59.990                   │
│  ├─ Monopoly                  $39.990                   │
│  └─ Risk                      $54.990                   │
│                                                         │
│  🖱️ Mouses (4)                                          │
│  ├─ HyperX Pulsefire          $69.990                   │
│  ├─ Logitech G502             $89.990                   │
│  ├─ Razer DeathAdder          $79.990                   │
│  └─ SteelSeries Rival 3       $64.990                   │
│                                                         │
│  💻 PC Gamers (4)                                       │
│  ├─ PC Alienware            $1.999.990                  │
│  ├─ PC ASUS ROG Strix       $1.799.990                  │
│  ├─ PC HP Omen              $1.499.990                  │
│  └─ PC MSI Gaming           $1.699.990                  │
│                                                         │
│  👕 Poleras (4)                                         │
│  ├─ Polera Gamer 1            $29.990                   │
│  ├─ Polera Gamer 2            $34.990                   │
│  ├─ Polera God of War         $39.990                   │
│  └─ Polera Papa Gamer         $32.990                   │
│                                                         │
│  🧥 Polerones (4)                                       │
│  ├─ Polerón Gamer 1           $59.990                   │
│  ├─ Polerón Gamer 2           $64.990                   │
│  ├─ Polerón Smash Vintage     $69.990                   │
│  └─ Polerón Smash Ultimate    $74.990                   │
│                                                         │
│  🖱️📱 Portamouse (4)                                     │
│  ├─ HyperX Fury S             $39.990                   │
│  ├─ Logitech G640             $49.990                   │
│  ├─ Razer Goliathus           $44.990                   │
│  └─ SteelSeries QcK           $52.990                   │
│                                                         │
│  🪑 Sillas (4)                                          │
│  ├─ Silla Cougar             $299.990                   │
│  ├─ Silla DXRacer            $399.990                   │
│  ├─ Silla GT Omega           $349.990                   │
│  └─ Silla SecretLab          $499.990                   │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**Total: 36 productos visibles** ✅

---

## PASO 11: Prueba con un producto

Haz clic en cualquiera, por ejemplo "PlayStation 5":

```
┌──────────────────────────────────────────────────┐
│        PLAYSTATION 5                             │
├──────────────────────────────────────────────────┤
│ [Imagen de PS5]                                  │
│                                                  │
│ Precio: $599.990 CLP                             │
│ Stock disponible: 3 unidades                     │
│ Categoría: Consolas                              │
│                                                  │
│ Descripción:                                     │
│ "La consola más potente con ray tracing         │
│  y SSD ultra rápido."                            │
│                                                  │
│ Cantidad: [1] +/- [Agregar al Carrito]          │
│                                                  │
│ ⭐⭐⭐⭐⭐ (4.5 stars)                            │
└──────────────────────────────────────────────────┘
```

---

## PASO 12: Agrega al carrito

Haz clic en **[Agregar al Carrito]**

Ve al carrito: `http://localhost:3000/carrito`

```
┌──────────────────────────────────────────────────┐
│        MI CARRITO                                │
├──────────────────────────────────────────────────┤
│                                                  │
│ PlayStation 5              x1   $599.990         │
│                                                  │
│ Subtotal:        $599.990                        │
│ Envío:           $0 (gratis)                     │
│ ──────────────────────────────                   │
│ TOTAL:          $599.990                         │
│                                                  │
│ [Continuar Comprando] [Ir a Checkout]           │
│                                                  │
└──────────────────────────────────────────────────┘
```

---

## PASO 13: Verifica en MongoDB (Opcional)

```bash
mongosh "mongodb://localhost:27017/pandagamers"

# Ver total de productos
db.products.count()
# Resultado: 36

# Ver todos
db.products.find().pretty()

# Ver solo consolas
db.products.find({ categoria: "Consolas" })

# Ver un producto específico
db.products.findOne({ nombre: "PlayStation 5" })
```

**Resultado esperado:**
```json
{
  "_id": ObjectId("..."),
  "nombre": "PlayStation 5",
  "descripcion": "La consola más potente con ray tracing y SSD ultra rápido.",
  "precio": 599990,
  "categoria": "Consolas",
  "imagen": "/images/Consolas/PlayStation 5.webp",
  "stock": 3,
  "createdAt": ISODate("2025-12-03T...")
}
```

---

## ✅ ¡COMPLETADO!

```
┌────────────────────────────────────┐
│     TODO FUNCIONANDO ✅            │
├────────────────────────────────────┤
│                                    │
│ ✅ Backend corriendo               │
│ ✅ Frontend corriendo              │
│ ✅ 36 productos importados         │
│ ✅ Visible en /productos           │
│ ✅ Se pueden agregar al carrito    │
│ ✅ Guardados en MongoDB            │
│                                    │
│ 🎉 ¡ÉXITO! 🎉                     │
│                                    │
└────────────────────────────────────┘
```

---

## 🔧 Si algo no funciona:

### Error: "No encuentra el admin"
```bash
mongosh "mongodb://localhost:27017/pandagamers"
db.users.insertOne({
  nombre: "Admin",
  email: "admin@pandagamers.com",
  password: "password",
  rol: "ADMIN"
})
```

### Error: "No ve los productos"
1. Recarga la página: `Ctrl+F5` (hard refresh)
2. Limpia caché: `Ctrl+Shift+Del`
3. Verifica que `/products` devuelve datos: `http://localhost:8080/products`

### Error: "Port 8080 already in use"
```bash
# Cambia el puerto en backend/application.properties
server.port=8081
```

---

**Tiempo total:** ~10 minutos
**Resultado:** 36 productos de PandaGamers en tu tienda MongoDB 🚀
