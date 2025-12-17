# 📱 GUÍA COMPLETA DEL FRONTEND - PANDA GAMERS

## 🏗️ Arquitectura General

La arquitectura de Panda Gamers sigue el patrón de **React con Contextos para estado global**. El flujo es:

1. **App.js** es el punto de entrada que envuelve todo con providers (AuthProvider y CartProvider)
2. **Contextos** (AuthContext, CartContext) manejan el estado global sin necesidad de Redux
3. **Componentes** consumen datos de contextos mediante `useContext()`
4. **api.js** centraliza todas las llamadas HTTP con axios, incluyendo interceptores para agregar JWT
5. **Backend** (Spring Boot en puerto 8080) valida y persiste datos en MongoDB

**Ventaja:** Cuando un usuario cambia en AuthContext, TODOS los componentes que lo usan se actualizan automáticamente sin pasar props manualmente.

```
┌──────────────────────────────────────────────────────────────┐
│                        App.js                                 │
│  (Router + BrowserRouter + Rutas principales)                │
└──────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
   ┌─────────────┐      ┌────────────┐       ┌──────────────┐
   │ AuthProvider│      │CartProvider│       │    Header    │
   │ (Contextos) │      │(Contextos) │       │  (Componente)│
   └─────────────┘      └────────────┘       └──────────────┘
        │                     │                     │
        ▼                     ▼                     ▼
   ┌─────────────┐      ┌────────────┐       ┌──────────────┐
   │  User Data  │      │Cart Items  │       │ Navigation   │
   │  + Token    │      │ + Totals   │       │ + Menu       │
   └─────────────┘      └────────────┘       └──────────────┘
        │
        └────────────────► api.js (Llamadas HTTP)
                               │
                               ▼
                        http://localhost:8080
                         (Spring Boot Backend)
```

---

## 📂 Estructura de Carpetas

```
frontend/src/
├── pages/                    # Componentes de PÁGINAS COMPLETAS
│   ├── Home.js              # Página principal
│   ├── Productos.js         # Catálogo de productos
│   ├── ProductDetail.js     # Detalles de 1 producto
│   ├── Carrito.js           # Carrito de compras
│   ├── Checkout.js          # Checkout / pago
│   ├── CheckoutSuccess.js   # Éxito de pago
│   ├── CheckoutError.js     # Error de pago
│   ├── Login.js             # Login / Registro
│   ├── AdminPanel.js        # Panel de administración
│   ├── AdminImport.js       # Importar productos
│   ├── MisCompras.js        # Órdenes del usuario
│   ├── Ofertas.js           # Ofertas especiales
│   ├── Blog.js              # Blog
│   ├── Contacto.js          # Contacto
│   └── Conocenos.js         # Quiénes somos
│
├── components/              # Componentes REUTILIZABLES
│   ├── Header.js            # Encabezado / Navegación
│   ├── Footer.js            # Pie de página
│   ├── ProductoCard.js      # Tarjeta de producto
│   ├── CarritoItem.js       # Item en el carrito
│   └── ...otros
│
├── context/                 # Contextos de estado global
│   ├── AuthContext.js       # Gestiona autenticación
│   └── CartContext.js       # Gestiona carrito
│
├── data/                    # Datos locales
│   └── dataStore.js         # Base de datos en memoria (fallback)
│
├── styles/                  # Estilos CSS
│   ├── global.css
│   └── ...estilos específicos
│
├── api.js                   # Cliente HTTP (axios)
├── App.js                   # Componente principal
├── index.js                 # Punto de entrada
└── ...otros archivos
```

---

## 🔑 CONCEPTOS CLAVE

**¿Por qué necesitamos "contextos"?** 
Sin contextos, si quieres pasar datos del padre al hijo, hijo al nieto, nieto al bisnieto... tienes que pasar "props" en cada nivel. ¡Es un caos! **Contextos permiten que cualquier componente acceda a datos sin tener que pasarlos por cada nivel.**

Piensa en un contexto como una "tienda central" - cualquiera puede ir ahí y obtener lo que necesita.

### 1️⃣ CONTEXTOS (Estado Global)

#### **AuthContext** - Gestión de Usuario Logueado

**¿Qué es?** Un contexto que guarda información del usuario logueado: nombre, email, rol, token JWT, etc.

**¿Dónde se usa?** En cualquier componente que necesite saber "¿Quién es el usuario actual?" o "¿Está logueado?"

**Propiedades completas:**

```javascript
// Lo que AuthContext proporciona a toda la app:
{
  // DATOS DEL USUARIO
  user: {
    id: "123abc",                           // ID único en MongoDB
    email: "benjamin@example.com",          // Email del usuario
    name: "Benjamin",                       // Nombre completo
    role: "admin",                          // "admin" o "user"
    token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // JWT token
    hasDuocDiscount: true,                  // ¿Es estudiante DUOC? (@duocuc)
    createdAt: "2025-01-01T10:00:00Z",      // Cuándo se creó la cuenta
  },
  
  // FUNCIONES PARA ACTUALIZAR
  login: (email, password) => Promise,      // Iniciar sesión
  logout: () => void,                       // Cerrar sesión
  register: (email, password, adminCode?) => Promise,  // Registro nuevo
  
  // ESTADO DE CARGA
  loading: false,                           // ¿Se está autenticando?
  error: null,                              // Último error ("Invalid credentials", etc.)
}
```

**¿Cuándo se actualiza cada propiedad?**

| Propiedad | Cuándo | Cómo |
|-----------|--------|------|
| `user` | Login/Logout/Registro | `AuthContext.login(userData)` |
| `token` | Login/Logout | Se guarda en localStorage |
| `loading` | Durante petición | Seteado a true/false automáticamente |
| `error` | Login falla | Se guarda el mensaje de error |
| `hasDuocDiscount` | Registro (si @duocuc) | Detectado automáticamente |

---

#### **Ejemplo: Usar AuthContext en un Componente**

```javascript
function Header() {
  const { user, logout, loading } = useContext(AuthContext);
  const navigate = useNavigate();
  
  const handleLogout = async () => {
    logout();  // Limpiar usuario y localStorage
    navigate('/');  // Ir a home
  };
  
  if (loading) return <header><p>Cargando...</p></header>;
  
  return (
    <header>
      <nav>
        <a href="/">Inicio</a>
        <a href="/productos">Productos</a>
        <a href="/carrito">Carrito</a>
        
        {user ? (
          <>
            {user.role === 'admin' && (
              <a href="/admin">Admin Panel</a>
            )}
            <span>Hola, {user.name}!</span>
            <button onClick={handleLogout}>Logout</button>
          </>
        ) : (
          <a href="/login">Login</a>
        )}
      </nav>
    </header>
  );
}

// Si user es null → muestra "Login"
// Si user existe y es admin → muestra "Admin Panel"
// Si user existe pero es user normal → NO muestra Admin Panel
```

---

#### **Flujo Completo: Login**

```
Usuario en página /login
    ↓
Completa email + contraseña
    ↓
Click "Iniciar Sesión"
    ↓
handleLogin() llama AuthContext.login(email, password)
    ↓
AuthContext:
  1. setLoading(true)
  2. POST http://localhost:8080/auth/login
  3. Backend valida credenciales en MongoDB
    ├─ ✅ Correctas: devuelve { token, user }
    └─ ❌ Incorrectas: devuelve 401 "Invalid credentials"
    ↓
Si ✅ Éxito:
  1. localStorage.setItem('token', token)
  2. localStorage.setItem('auth_user', JSON.stringify(user))
  3. setUser(userData)
  4. setLoading(false)
  5. setError(null)
  6. Componentes que usan AuthContext se re-renderizañ
  7. navigate('/') - ir a home
    ↓
Si ❌ Error:
  1. setError("Usuario o contraseña incorrectos")
  2. setLoading(false)
  3. Mantener en página /login
  4. Mostrar mensaje de error
```

---

#### **Flujo: Recuperar Usuario al Recargar**

```
Usuario refresca la página (F5)
    ↓
App.js carga
    ↓
AuthProvider ejecuta useEffect:
    ↓
    1. Leer localStorage('auth_user')
    ✓ ¿Hay datos guardados?
        - SÍ: Parsear JSON y setUser(user)
        - NO: setUser(null)
    ↓
    2. Si hay user, también buscar en backend:
       GET /auth/profile (con token en header)
    ✓ ¿Token es válido?
        - SÍ: setUser(userDelBackend)
        - NO: Borrar token, setUser(null)
    ↓
Ahora el usuario sigue logueado sin entrar a /login
(El token persiste entre sesiones)
```

---

#### **Flujo: Logout**

```
Usuario hace click en "Cerrar Sesión"
    ↓
handleLogout() llama AuthContext.logout()
    ↓
AuthContext:
  1. localStorage.removeItem('token')
  2. localStorage.removeItem('auth_user')
  3. setUser(null)
  4. setError(null)
    ↓
Header.js ve que user es null
    ↓
Re-renderiza: muestra botón "Login" en lugar de "Hola, usuario"
    ↓
navigate('/') - ir a home
```

---

#### **CartContext** - Gestión del Carrito

**¿Qué es?** Contexto que guarda qué productos tiene el usuario en su carrito de compras.

**Propiedades completas:**

```javascript
// Lo que CartContext proporciona:
{
  // DATOS DEL CARRITO
  cart: [
    {
      id: "prod-1",              // ID del producto
      name: "PS5",               // Nombre
      price: 500,                // Precio unitario
      image: "/images/ps5.jpg",  // URL de imagen
      quantity: 2,               // Cantidad en carrito
      category: "Consolas",
      stock: 5                   // Stock disponible
    },
    {
      id: "prod-2",
      name: "Xbox Series X",
      price: 400,
      image: "/images/xbox.jpg",
      quantity: 1,
      category: "Consolas",
      stock: 3
    }
  ],
  
  // FUNCIONES PARA MODIFICAR
  addToCart: (product, quantity) => void,    // Agregar producto
  removeFromCart: (productId) => void,       // Eliminar producto
  updateQuantity: (productId, newQty) => void,  // Cambiar cantidad
  clearCart: () => void,                     // Vaciar todo el carrito
  
  // INFORMACIÓN CALCULADA
  totalItems: 3,                             // Suma de cantidades
  getTotal: () => 900,                       // Suma precios × cantidad
}
```

**Almacenamiento:**

```javascript
// En localStorage, cada usuario tiene su carrito:
// Si user.id = "123abc"
localStorage.getItem('cart_123abc')
// Contiene: JSON del array de items

// Si usuario NO está logueado:
localStorage.getItem('cart_guest')
```

---

#### **Ejemplo: Usar CartContext**

```javascript
function Carrito() {
  const { cart, removeFromCart, updateQuantity, clearCart } = useContext(CartContext);
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  
  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  
  if (cart.length === 0) {
    return <div><p>Carrito vacío</p></div>;
  }
  
  return (
    <div>
      <h1>Mi Carrito</h1>
      
      <table>
        <thead>
          <tr>
            <th>Producto</th>
            <th>Precio</th>
            <th>Cantidad</th>
            <th>Subtotal</th>
            <th>Acción</th>
          </tr>
        </thead>
        <tbody>
          {cart.map(item => (
            <tr key={item.id}>
              <td>{item.name}</td>
              <td>${item.price}</td>
              <td>
                <button onClick={() => updateQuantity(item.id, item.quantity - 1)}>-</button>
                {item.quantity}
                <button onClick={() => updateQuantity(item.id, item.quantity + 1)}>+</button>
              </td>
              <td>${item.price * item.quantity}</td>
              <td>
                <button onClick={() => removeFromCart(item.id)}>Eliminar</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      
      <div>
        <h3>Total: ${total}</h3>
        <button onClick={clearCart}>Vaciar Carrito</button>
        
        {user ? (
          <button onClick={() => navigate('/checkout')}>
            Proceder al Checkout
          </button>
        ) : (
          <button onClick={() => navigate('/login')}>
            Login para Checkout
          </button>
        )}
      </div>
    </div>
  );
}
```

---

#### **Flujo: Agregar al Carrito**

```
Usuario en página de producto
    ↓
Click "Agregar al Carrito"
    ↓
addToCart(producto, cantidad) se ejecuta
    ↓
CartContext verifica:
  1. ¿El producto ya está en el carrito?
     - SÍ: aumentar quantity
     - NO: agregar como nuevo item
    ↓
  2. Guardar en localStorage:
     localStorage.setItem('cart_' + userId, JSON.stringify(cart))
    ↓
  3. setCart(newCart) actualiza estado
    ↓
Componentes que usan CartContext se re-renderizañ:
  - Header.js ve badge +1 en carrito
  - Carrito.js ve item nuevo en la lista
    ↓
Mostrar notificación: "Agregado al carrito!"
```

---

#### **Sincronización Entre Tabs (Ventanas del Navegador)**

```javascript
// CartContext incluye un listener especial:
useEffect(() => {
  const handleStorageChange = (e) => {
    // Detecta cambios en localStorage desde otro tab
    if (e.key === 'cart_' + user?.id) {
      setCart(JSON.parse(e.newValue));  // Actualizar carrito
    }
  };
  
  window.addEventListener('storage', handleStorageChange);
  
  return () => {
    window.removeEventListener('storage', handleStorageChange);
  };
}, [user]);

// Resultado:
// Si tienes dos tabs abiertos:
// Tab 1: Agregar producto → carrito se actualiza
// Tab 2: Automáticamente ve el nuevo producto sin recargar
```

---

### 2️⃣ API CLIENT (api.js) - Cliente HTTP Centralizado

**¿Qué es?** Un archivo que centraliza TODAS las llamadas HTTP. En lugar de que cada componente haga sus propias llamadas, van a través de `api.js`. Esto permite:
- Agregar JWT token automáticamente a cada petición
- Normalizar respuestas (convertir nombres de campos)
- Manejar errores en un solo lugar
- Cambiar el backend URL sin tocar componentes
- Reutilizar lógica

**Configuración base:**

```javascript
import axios from 'axios';

// Crear instancia de axios configurada
const api = axios.create({
  baseURL: 'http://localhost:8080',           // URL base del backend
  timeout: 10000,                              // Esperar máximo 10s por respuesta
  headers: { 
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// INTERCEPTOR DE REQUEST (antes de enviar)
api.interceptors.request.use(config => {
  // Leer token del localStorage
  const token = localStorage.getItem('token');
  
  // Si hay token, agregarlo al header
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  console.log(`[API] ${config.method.toUpperCase()} ${config.url}`);
  return config;
});

// INTERCEPTOR DE RESPONSE (después de recibir respuesta)
api.interceptors.response.use(
  response => {
    // Éxito (2xx)
    return response;
  },
  error => {
    // Error
    if (error.response?.status === 401) {
      // Token expirado
      console.log('Token expirado, redirigiendo a login');
      localStorage.removeItem('token');
      localStorage.removeItem('auth_user');
      window.location.href = '/login';
    }
    
    if (error.response?.status === 403) {
      // Acceso denegado (permisos insuficientes)
      console.error('Acceso denegado (403)');
    }
    
    return Promise.reject(error);
  }
);
```

---

#### **ENDPOINTS DISPONIBLES - Detallade**

##### **Autenticación (authAPI)**

```javascript
// 1. LOGIN - Iniciar sesión
authAPI.login = (email, password) => {
  // POST http://localhost:8080/auth/login
  // Body: { email: "user@example.com", password: "123456" }
  
  // Respuesta exitosa (200):
  return {
    token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    user: {
      id: "123abc",
      email: "user@example.com",
      name: "Benjamin",
      role: "user"
    }
  };
  
  // Errores posibles:
  // 400: Email/contraseña faltando
  // 401: Credenciales inválidas
  // 500: Error en backend
};

// 2. REGISTER - Crear usuario nuevo
authAPI.register = (email, password, confirmPassword, adminCode = null) => {
  // POST http://localhost:8080/auth/register
  // Body: {
  //   email: "user@example.com",
  //   password: "123456",
  //   confirmPassword: "123456",
  //   adminCode: "123456789"  // Opcional, si quieres ser admin
  // }
  
  // Respuesta exitosa (201):
  return {
    token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    user: {
      id: "456def",
      email: "user@example.com",
      name: "Benjamin",
      role: adminCode ? "admin" : "user"  // Si adminCode es válido, eres admin
    }
  };
  
  // Errores posibles:
  // 400: Email ya existe
  // 400: Contraseñas no coinciden
  // 400: AdminCode inválido
  // 422: Validación falló
};

// 3. PROFILE - Obtener perfil actual
authAPI.getProfile = () => {
  // GET http://localhost:8080/auth/profile
  // Header: Authorization: Bearer {token}
  
  // Respuesta (200):
  return {
    id: "123abc",
    email: "user@example.com",
    name: "Benjamin",
    role: "user",
    hasDuocDiscount: true
  };
  
  // Errores:
  // 401: Token inválido
  // 404: Usuario no encontrado
};
```

---

##### **Productos (productsAPI)**

```javascript
// 1. GET ALL - Obtener todos los productos
productsAPI.getAll = () => {
  // GET http://localhost:8080/products
  
  // Respuesta (200):
  return [
    {
      id: "prod-1",
      nombre: "PS5",          // ← Campo backend (español)
      descripcion: "Consola gaming",
      precio: 500,
      categoria: "Consolas",
      imagen: "/images/ps5.jpg",
      stock: 10,
      createdAt: "2025-01-01T10:00:00Z"
    },
    // ... más productos
  ];
  
  // Normalización automática:
  // El frontend convierte a:
  // {
  //   id: "prod-1",
  //   name: "PS5",          // ← nombre → name
  //   description: "...",   // ← descripcion → description
  //   price: 500,           // ← precio → price
  //   category: "Consolas", // ← categoria → category
  //   image: "/images/ps5.jpg", // ← imagen → image
  //   stock: 10
  // }
};

// 2. GET BY ID - Obtener producto específico
productsAPI.getById = (productId) => {
  // GET http://localhost:8080/products/{productId}
  // Ej: GET /products/prod-1
  
  // Respuesta (200):
  return {
    id: "prod-1",
    nombre: "PS5",
    descripcion: "Consola",
    precio: 500,
    categoria: "Consolas",
    imagen: "/images/ps5.jpg",
    stock: 10
  };
  
  // Errores:
  // 404: Producto no encontrado
};

// 3. CREATE - Crear producto nuevo (solo admin)
productsAPI.create = (productData) => {
  // POST http://localhost:8080/products/create
  // Header: Authorization: Bearer {token}
  // Body:
  // {
  //   nombre: "Xbox Series X",
  //   descripcion: "Consola Microsoft",
  //   precio: 400,
  //   categoria: "Consolas",
  //   imagen: "/images/xbox.jpg",
  //   stock: 5
  // }
  
  // Respuesta (201):
  return {
    id: "prod-2",
    nombre: "Xbox Series X",
    descripcion: "Consola Microsoft",
    precio: 400,
    categoria: "Consolas",
    imagen: "/images/xbox.jpg",
    stock: 5
  };
  
  // Errores:
  // 401: No logueado
  // 403: No es admin
  // 400: Datos incompletos
};

// 4. UPDATE - Actualizar producto (solo admin)
productsAPI.update = (productId, updateData) => {
  // PUT http://localhost:8080/products/update/{productId}
  // Header: Authorization: Bearer {token}
  // Body: (solo los campos a actualizar)
  // {
  //   precio: 450,  // Cambiar precio
  //   stock: 8      // Cambiar stock
  // }
  
  // Respuesta (200):
  return {
    id: "prod-1",
    nombre: "PS5",
    precio: 450,  // ← Actualizado
    stock: 8      // ← Actualizado
    // ... resto de campos igual
  };
};

// 5. DELETE - Eliminar producto (solo admin)
productsAPI.delete = (productId) => {
  // DELETE http://localhost:8080/products/delete/{productId}
  // Header: Authorization: Bearer {token}
  
  // Respuesta (204):
  // (sin contenido)
  
  // Errores:
  // 401: No logueado
  // 403: No es admin
  // 404: Producto no encontrado
};

// 6. IMPORT - Importar múltiples productos (solo admin)
productsAPI.import = (productsArray) => {
  // POST http://localhost:8080/products/import
  // Header: Authorization: Bearer {token}
  // Body: Array de productos
  // [
  //   { nombre: "PS5", precio: 500, ... },
  //   { nombre: "Xbox", precio: 400, ... },
  //   ...
  // ]
  
  // Respuesta (200):
  return {
    imported: 36,
    duplicates: 2,
    errors: 0,
    message: "36 productos importados exitosamente"
  };
};
```

---

##### **Órdenes/Compras (ordersAPI)**

```javascript
// 1. CREATE - Crear orden (checkout)
ordersAPI.create = (userId, orderData) => {
  // POST http://localhost:8080/orders/create/{userId}
  // Header: Authorization: Bearer {token}
  // Body:
  // {
  //   items: [
  //     { productId: "prod-1", quantity: 2, price: 500 },
  //     { productId: "prod-2", quantity: 1, price: 400 }
  //   ],
  //   shippingAddress: "Calle 123, Depto 4",
  //   shippingPhone: "+56912345678",
  //   shippingCity: "Santiago"
  // }
  
  // Respuesta (201):
  return {
    id: "order-123",
    userId: userId,
    status: "pending",  // "pending", "completed", "failed"
    items: [
      { productId: "prod-1", quantity: 2, price: 500 },
      { productId: "prod-2", quantity: 1, price: 400 }
    ],
    total: 1400,
    createdAt: "2025-01-15T14:30:00Z"
  };
  
  // Errores:
  // 400: Stock insuficiente
  // 400: Datos incompletos
  // 500: Error al decrementar stock (race condition)
};

// 2. GET BY USER - Obtener órdenes del usuario
ordersAPI.getByUser = (userId) => {
  // GET http://localhost:8080/orders/user/{userId}
  // Header: Authorization: Bearer {token}
  
  // Respuesta (200):
  return [
    {
      id: "order-123",
      status: "completed",
      total: 1400,
      createdAt: "2025-01-15T14:30:00Z",
      items: [...]
    },
    {
      id: "order-456",
      status: "pending",
      total: 500,
      createdAt: "2025-01-20T10:00:00Z",
      items: [...]
    }
  ];
};

// 3. GET ALL - Obtener todas las órdenes (solo admin)
ordersAPI.getAll = () => {
  // GET http://localhost:8080/orders
  // Header: Authorization: Bearer {token}
  
  // Respuesta (200):
  return [
    // array de todas las órdenes
  ];
  
  // Errores:
  // 403: No es admin
};

// 4. UPDATE STATUS - Cambiar estado de orden (solo admin)
ordersAPI.updateStatus = (orderId, newStatus) => {
  // PUT http://localhost:8080/orders/{orderId}/status
  // Header: Authorization: Bearer {token}
  // Body: { status: "completed" }
  
  // Estados válidos: "pending", "completed", "failed", "shipped"
};
```

---

##### **Pago (transbankAPI - Simulated)**

```javascript
// 1. CREATE TRANSACTION - Simular pago
transbankAPI.createTransaction = (orderId, userId, amount, returnUrl) => {
  // POST http://localhost:8080/pay/create
  // Header: Authorization: Bearer {token}
  // Body:
  // {
  //   orderId: "order-123",
  //   userId: userId,
  //   amount: 1400,
  //   returnUrl: "http://localhost:3000/checkout/confirm"
  // }
  
  // Respuesta (200):
  return {
    token: "transbank-token-abc123",
    url: "https://webpay3gint.transbank.cl/initTransaction",
    status: "pending"
  };
  
  // En producción, esto redirige a Transbank
  // En simulación, genera un token ficticio
};

// 2. CONFIRM TRANSACTION - Confirmar pago
transbankAPI.confirmTransaction = (transbankToken) => {
  // GET http://localhost:8080/pay/confirm/{transbankToken}
  
  // Respuesta (200) - Si fue exitoso (70% probabilidad):
  return {
    transactionId: "trans-123",
    orderId: "order-123",
    status: "success",
    amount: 1400,
    currency: "CLP",
    responseCode: "0",  // 0 = éxito, otro = error
    message: "Transacción aprobada"
  };
  
  // Respuesta (200) - Si fue fallida (30% probabilidad):
  return {
    transactionId: "trans-124",
    orderId: "order-123",
    status: "failed",
    amount: 1400,
    responseCode: "-1",  // Número de error
    message: "Transacción rechazada"
  };
};
```
  stock: 10
}
```

---

### 3️⃣ FLUJOS PRINCIPALES

#### **FLUJO 1: Autenticación (Login)**

```
Usuario entra a /login
    ↓
Página Login.js carga
    ↓
Usuario completa formulario + click "Iniciar Sesión"
    ↓
handleLogin() → authAPI.login(email, password)
    ↓
Backend valida credenciales
    ├─ ✅ Correctas → devuelve { token, user }
    └─ ❌ Incorrectas → error 401
    ↓
Si ✅ Éxito:
  1. Guardar token en localStorage
  2. Guardar usuario en localStorage (auth_user)
  3. Llamar AuthContext.login(userData)
  4. Redirigir a home (navigate("/"))
    ↓
Si ❌ Error:
  1. Mostrar mensaje de error
  2. No redirigir (usuario permanece en /login)
```

#### **FLUJO 2: Agregar al Carrito**

```
Usuario en página de producto
    ↓
Click "Agregar al Carrito"
    ↓
addToCart(product, quantity)
    ↓
CartContext valida:
  ✓ ¿Ya existe producto en carrito?
    - SÍ → incrementar quantity
    - NO → agregar nuevo item
    ↓
  ✓ Guardar en localStorage (cart_${userId})
    ↓
  ✓ Mostrar notificación "Agregado!"
```

#### **FLUJO 3: Checkout y Pago**

```
Usuario en carrito
    ↓
Click "Proceder al checkout"
    ↓
Página Checkout.js
    ↓
Usuario completa:
  - Datos de envío (dirección, teléfono)
  - Datos de pago (tarjeta simulada)
    ↓
Click "Pagar Ahora"
    ↓
handlePayment() → ordersAPI.create(userId, items)
    ↓
Backend crea orden en MongoDB
    ├─ Valida stock
    ├─ Decrementa stock
    └─ Retorna orden con ID
    ↓
transbankAPI.createTransaction(orderId, ...)
    ↓
Simula transacción Transbank
    └─ 70% éxito / 30% error
    ↓
Si ✅ Éxito:
  1. Actualizar estado orden a "Completada"
  2. Vaciar carrito
  3. Redirigir a /checkout/success/{orderId}
    ↓
Si ❌ Error:
  1. Actualizar estado orden a "Failed"
  2. Mantener carrito
  3. Redirigir a /checkout/error
```

#### **FLUJO 4: Admin CRUD Productos**

```
Admin entra a /admin
    ↓
AdminPanel.js carga
    ↓
useEffect valida:
  ✓ ¿user existe?
    - NO → redirigir a /login
  ✓ ¿user.role === "admin"?
    - NO → redirigir a /
    ↓
  ✓ Llamar loadData()
    ├─ productsAPI.getAll() → setProducts
    ├─ usersAPI.getAll() → setUsers
    ├─ ordersAPI.getAll() → setOrders
    └─ offersAPI.getAll() → setOfertas
    ↓
Admin ve lista de productos
    ↓
    ├─ CREAR: Click "Nuevo Producto"
    │  → mostrar formulario vacío
    │  → usuario completa campos
    │  → click "Guardar"
    │  → productsAPI.create(productData)
    │  → backend retorna producto nuevo
    │  → agregar a lista local (setProducts)
    │
    ├─ EDITAR: Click "Editar" en un producto
    │  → llenar formulario con datos del producto
    │  → usuario modifica campos
    │  → click "Actualizar"
    │  → productsAPI.update(id, productData)
    │  → actualizar en lista local
    │
    ├─ ELIMINAR: Click "Eliminar"
    │  → confirmar con window.confirm()
    │  → productsAPI.delete(id)
    │  → remover de lista local
    │
    └─ VER DETALLES: Click en un producto
       → ProductDetail.js
       → cargar producto del backend
       → mostrar datos completos
```

---

## 🎯 PÁGINAS PRINCIPALES - ANÁLISIS DETALLADO

### **Home.js - Página Principal**

**Propósito:** Primera página que ve el usuario. Muestra productos destacados, estadísticas y promociones.

**Hooks y Contextos Usados:**
```javascript
const { user } = useContext(AuthContext);           // Saber si está logueado
const [productsDestacados, setProductsDestacados] = useState([]);
const [estadisticas, setEstadisticas] = useState(null);
const [loading, setLoading] = useState(true);
```

**Estructura de Código:**
```javascript
function Home() {
  const { user } = useContext(AuthContext);
  const [productsDestacados, setProductsDestacados] = useState([]);
  const [estadisticas, setEstadisticas] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Cargar datos cuando el componente monta
  useEffect(() => {
    const cargarDatos = async () => {
      try {
        // 1. Cargar productos destacados
        const prods = await productsAPI.getAll();
        const top6 = prods.slice(0, 6);  // Primeros 6
        setProductsDestacados(top6);
        
        // 2. Cargar estadísticas
        const stats = await fetch('/api/stats').then(r => r.json());
        setEstadisticas(stats);
      } catch (err) {
        console.error('Error:', err);
      } finally {
        setLoading(false);
      }
    };
    
    cargarDatos();
  }, []);  // [] = solo ejecutar una vez al cargar
  
  if (loading) return <div>Cargando...</div>;
  
  return (
    <div className="home">
      <section className="hero">
        <h1>Bienvenido a Panda Gamers</h1>
        <p>Los mejores productos gaming a tu alcance</p>
        {user ? (
          <p>Hola, {user.name}!</p>
        ) : (
          <button onClick={() => navigate('/login')}>Inicia Sesión</button>
        )}
      </section>
      
      <section className="estadisticas">
        <div className="stat">
          <h3>{estadisticas?.totalProductos}</h3>
          <p>Productos</p>
        </div>
        <div className="stat">
          <h3>{estadisticas?.totalUsuarios}</h3>
          <p>Usuarios</p>
        </div>
        <div className="stat">
          <h3>{estadisticas?.ordenesPendientes}</h3>
          <p>Órdenes Pendientes</p>
        </div>
      </section>
      
      <section className="featured">
        <h2>Productos Destacados</h2>
        <div className="grid">
          {productsDestacados.map(product => (
            <ProductoCard key={product.id} product={product} />
          ))}
        </div>
      </section>
    </div>
  );
}
```

**Flujo Visual:**
```
Usuario entra a /
    ↓
Home carga
    ↓
useEffect se ejecuta ([] = una sola vez)
    ↓
Hace dos peticiones en paralelo:
  1. GET /products → productsDestacados
  2. GET /stats → estadísticas
    ↓
setLoading(false)
    ↓
Renderiza página con datos
    ├─ Saludo personalizado (si está logueado)
    ├─ Tarjetas de estadísticas
    └─ Grid de 6 productos destacados
```

---

### **Productos.js - Catálogo Completo**

**Propósito:** Mostrar todos los productos con filtros y opciones de búsqueda.

**Hooks y Contextos:**
```javascript
const [productos, setProductos] = useState([]);
const [filtroCategoria, setFiltroCategoria] = useState('');
const [busqueda, setBusqueda] = useState('');
const [loading, setLoading] = useState(true);
```

**Estructura:**
```javascript
function Productos() {
  const [productos, setProductos] = useState([]);
  const [filtroCategoria, setFiltroCategoria] = useState('');
  const [busqueda, setBusqueda] = useState('');
  
  // Cargar todos los productos
  useEffect(() => {
    const cargar = async () => {
      const todos = await productsAPI.getAll();
      setProductos(todos);
    };
    cargar();
  }, []);
  
  // Filtrar según búsqueda y categoría
  const productosFiltrados = productos.filter(p => {
    const cumpleBusqueda = p.name.toLowerCase().includes(busqueda.toLowerCase());
    const cumpleCategoria = !filtroCategoria || p.category === filtroCategoria;
    return cumpleBusqueda && cumpleCategoria;
  });
  
  return (
    <div className="productos">
      <aside className="filtros">
        <h3>Filtrar</h3>
        
        <input 
          type="text"
          placeholder="Buscar producto..."
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
        />
        
        <select 
          value={filtroCategoria}
          onChange={(e) => setFiltroCategoria(e.target.value)}
        >
          <option value="">Todas las categorías</option>
          <option value="Consolas">Consolas</option>
          <option value="Juegos">Juegos</option>
          <option value="Accesorios">Accesorios</option>
          <option value="Sillas">Sillas</option>
        </select>
      </aside>
      
      <main className="grid">
        {productosFiltrados.map(prod => (
          <ProductoCard key={prod.id} product={prod} />
        ))}
      </main>
    </div>
  );
}
```

**Flujo:**
```
Usuario va a /productos
    ↓
useEffect carga TODOS los productos
    ↓
Usuario escribe "PS" en búsqueda
    ↓
setBusqueda("PS")
    ↓
useEffect/filter se ejecuta
    ↓
Filtra: productos que contengan "PS"
    ↓
Muestra solo los que cumplan filtros
```

---

### **ProductDetail.js - Detalles de un Producto**

**Propósito:** Mostrar información completa de 1 producto específico.

**Hooks:**
```javascript
const { id } = useParams();  // Obtener ID de URL
const { addToCart } = useContext(CartContext);
const [producto, setProducto] = useState(null);
const [cantidad, setCantidad] = useState(1);
const [loading, setLoading] = useState(true);
```

**Código:**
```javascript
function ProductDetail() {
  const { id } = useParams();  // /producto/prod-1 → id = "prod-1"
  const { addToCart } = useContext(CartContext);
  const [producto, setProducto] = useState(null);
  const [cantidad, setCantidad] = useState(1);
  
  useEffect(() => {
    const cargar = async () => {
      const prod = await productsAPI.getById(id);
      setProducto(prod);
    };
    cargar();
  }, [id]);  // Si id cambia, volver a cargar
  
  const handleAgregarCarrito = () => {
    addToCart(producto, cantidad);
    alert(`${cantidad} ${producto.name} agregado al carrito!`);
  };
  
  if (!producto) return <div>Cargando...</div>;
  
  return (
    <div className="product-detail">
      <div className="imagen">
        <img src={producto.image} alt={producto.name} />
      </div>
      
      <div className="info">
        <h1>{producto.name}</h1>
        <p className="categoria">{producto.category}</p>
        <p className="descripcion">{producto.description}</p>
        
        <div className="precio-stock">
          <h2>${producto.price}</h2>
          <p>Stock: {producto.stock > 0 ? producto.stock : 'Agotado'}</p>
        </div>
        
        <div className="seleccionar-cantidad">
          <button onClick={() => setCantidad(Math.max(1, cantidad - 1))}>-</button>
          <input type="number" value={cantidad} readOnly />
          <button onClick={() => setCantidad(cantidad + 1)}>+</button>
        </div>
        
        <button 
          onClick={handleAgregarCarrito}
          disabled={producto.stock === 0}
        >
          {producto.stock > 0 ? 'Agregar al Carrito' : 'Agotado'}
        </button>
      </div>
    </div>
  );
}
```

---

### **Carrito.js - Carrito de Compras**

**Propósito:** Mostrar items del carrito, permitir editar cantidades y proceder a checkout.

**Hooks y Contextos:**
```javascript
const { cart, removeFromCart, updateQuantity, clearCart } = useContext(CartContext);
const { user } = useContext(AuthContext);
const navigate = useNavigate();
const [total, setTotal] = useState(0);
```

**Código Detallado:**
```javascript
function Carrito() {
  const { cart, removeFromCart, updateQuantity, clearCart } = useContext(CartContext);
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  
  // Calcular total cada vez que cart cambia
  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  
  if (cart.length === 0) {
    return (
      <div className="carrito-vacio">
        <p>Tu carrito está vacío</p>
        <button onClick={() => navigate('/productos')}>
          Seguir Comprando
        </button>
      </div>
    );
  }
  
  return (
    <div className="carrito">
      <h1>Mi Carrito</h1>
      
      <table>
        <thead>
          <tr>
            <th>Producto</th>
            <th>Precio</th>
            <th>Cantidad</th>
            <th>Subtotal</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {cart.map(item => (
            <tr key={item.id}>
              <td>
                <img src={item.image} alt={item.name} width="50" />
                {item.name}
              </td>
              <td>${item.price}</td>
              <td>
                <button onClick={() => updateQuantity(item.id, item.quantity - 1)}>
                  -
                </button>
                {item.quantity}
                <button onClick={() => updateQuantity(item.id, item.quantity + 1)}>
                  +
                </button>
              </td>
              <td>${item.price * item.quantity}</td>
              <td>
                <button onClick={() => removeFromCart(item.id)}>
                  Eliminar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      
      <div className="resumen">
        <h3>Total: ${total}</h3>
        
        <button onClick={clearCart} className="btn-secondary">
          Vaciar Carrito
        </button>
        
        {user ? (
          <button 
            onClick={() => navigate('/checkout')}
            className="btn-primary"
          >
            Proceder al Checkout
          </button>
        ) : (
          <button onClick={() => navigate('/login')} className="btn-primary">
            Inicia Sesión para Checkout
          </button>
        )}
      </div>
    </div>
  );
}
```

---

### **Checkout.js - Proceso de Pago**

**Propósito:** Recopilar datos de envío, confirmar orden y procesar pago.

**Hooks y Contextos:**
```javascript
const { user } = useContext(AuthContext);
const { cart, clearCart } = useContext(CartContext);
const navigate = useNavigate();
const [formData, setFormData] = useState({
  address: '',
  phone: '',
  city: ''
});
const [loading, setLoading] = useState(false);
```

**Código:**
```javascript
function Checkout() {
  const { user } = useContext(AuthContext);
  const { cart, clearCart } = useContext(CartContext);
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    address: '',
    phone: '',
    city: ''
  });
  const [loading, setLoading] = useState(false);
  
  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({...formData, [name]: value});
  };
  
  const handlePago = async () => {
    // Validar datos
    if (!formData.address || !formData.phone || !formData.city) {
      alert('Completa todos los datos');
      return;
    }
    
    setLoading(true);
    try {
      // 1. Crear orden
      const orderResponse = await ordersAPI.create(user.id, {
        items: cart,
        ...formData
      });
      
      // 2. Procesar pago
      const payResponse = await transbankAPI.createTransaction(
        orderResponse.id,
        user.id,
        total,
        window.location.href
      );
      
      // 3. Confirmar pago (simular)
      const confirmResponse = await transbankAPI.confirmTransaction(payResponse.token);
      
      if (confirmResponse.status === 'success') {
        // Éxito
        clearCart();
        navigate(`/checkout/success/${orderResponse.id}`);
      } else {
        // Error
        navigate('/checkout/error');
      }
    } catch (err) {
      console.error('Error:', err);
      navigate('/checkout/error');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="checkout">
      <h1>Checkout</h1>
      
      <div className="formulario">
        <input 
          name="address"
          value={formData.address}
          onChange={handleChange}
          placeholder="Dirección de envío"
        />
        <input 
          name="phone"
          value={formData.phone}
          onChange={handleChange}
          placeholder="Teléfono"
        />
        <input 
          name="city"
          value={formData.city}
          onChange={handleChange}
          placeholder="Ciudad"
        />
      </div>
      
      <div className="resumen">
        <h2>Resumen de Compra</h2>
        {cart.map(item => (
          <div key={item.id}>
            {item.name} x {item.quantity} = ${item.price * item.quantity}
          </div>
        ))}
        <h3>Total: ${total}</h3>
      </div>
      
      <button 
        onClick={handlePago}
        disabled={loading}
      >
        {loading ? 'Procesando...' : 'Pagar Ahora'}
      </button>
    </div>
  );
}
```

---

### **AdminPanel.js - Panel de Administración (1885 líneas)**

**Propósito:** CRUD completo para productos, usuarios, órdenes y ofertas.

**Estructura Principal:**
```javascript
function AdminPanel() {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  
  // Proteger ruta
  useEffect(() => {
    if (!user || user.role !== 'admin') {
      navigate('/');
    }
  }, [user, navigate]);
  
  // Estados principales
  const [activeTab, setActiveTab] = useState(() => localStorage.getItem('adminActiveTab') || 'dashboard');
  const [products, setProducts] = useState([]);
  const [users, setUsers] = useState([]);
  const [orders, setOrders] = useState([]);
  const [offers, setOffers] = useState([]);
  
  // Cargar datos al montar
  useEffect(() => {
    const loadData = async () => {
      try {
        const [prods, usrs, ordr, ofr] = await Promise.all([
          productsAPI.getAll(),
          usersAPI.getAll(),
          ordersAPI.getAll(),
          offersAPI.getAll()
        ]);
        setProducts(prods);
        setUsers(usrs);
        setOrders(ordr);
        setOffers(ofr);
      } catch (err) {
        console.error('Error cargando:', err);
      }
    };
    loadData();
  }, []);
  
  return (
    <div className="admin">
      <nav className="admin-tabs">
        <button onClick={() => setActiveTab('dashboard')}>Dashboard</button>
        <button onClick={() => setActiveTab('productos')}>Productos</button>
        <button onClick={() => setActiveTab('usuarios')}>Usuarios</button>
        <button onClick={() => setActiveTab('ordenes')}>Órdenes</button>
        <button onClick={() => setActiveTab('ofertas')}>Ofertas</button>
      </nav>
      
      {activeTab === 'dashboard' && <DashboardTab stats={{products, users, orders}} />}
      {activeTab === 'productos' && <ProductosTab products={products} setProducts={setProducts} />}
      {activeTab === 'usuarios' && <UsuariosTab users={users} setUsers={setUsers} />}
      {activeTab === 'ordenes' && <OrdenesTab orders={orders} />}
      {activeTab === 'ofertas' && <OfertasTab offers={offers} setOffers={setOffers} />}
    </div>
  );
}
```

**Cada tab hace CRUD completo** (Create, Read, Update, Delete)

---

### **Login.js - Autenticación**

**Propósito:** Permitir login y registro de usuarios.

**Código:**
```javascript
function Login() {
  const { login, register } = useContext(AuthContext);
  const navigate = useNavigate();
  
  const [isRegistering, setIsRegistering] = useState(false);
  const [form, setForm] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    adminCode: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({...form, [name]: value});
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    
    try {
      if (isRegistering) {
        // Registro
        if (form.password !== form.confirmPassword) {
          throw new Error('Las contraseñas no coinciden');
        }
        await register(form.email, form.password, form.adminCode);
      } else {
        // Login
        await login(form.email, form.password);
      }
      navigate('/');
    } catch (err) {
      setError(err.message || 'Error autenticando');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="login-page">
      <form onSubmit={handleSubmit}>
        <h1>{isRegistering ? 'Registrarse' : 'Iniciar Sesión'}</h1>
        
        {error && <p className="error">{error}</p>}
        
        <input 
          name="email"
          type="email"
          value={form.email}
          onChange={handleChange}
          placeholder="Email"
          required
        />
        
        <input 
          name="password"
          type="password"
          value={form.password}
          onChange={handleChange}
          placeholder="Contraseña"
          required
        />
        
        {isRegistering && (
          <>
            <input 
              name="confirmPassword"
              type="password"
              value={form.confirmPassword}
              onChange={handleChange}
              placeholder="Confirmar contraseña"
              required
            />
            <input 
              name="adminCode"
              value={form.adminCode}
              onChange={handleChange}
              placeholder="Código admin (opcional)"
            />
          </>
        )}
        
        <button type="submit" disabled={loading}>
          {loading ? 'Procesando...' : isRegistering ? 'Registrarse' : 'Iniciar Sesión'}
        </button>
        
        <p>
          {isRegistering ? 'Ya tienes cuenta? ' : 'No tienes cuenta? '}
          <button 
            type="button"
            onClick={() => setIsRegistering(!isRegistering)}
          >
            {isRegistering ? 'Inicia Sesión' : 'Registrate'}
          </button>
        </p>
      </form>
    </div>
  );
}
```

---

## 🎣 REACT HOOKS (Los Pilares del Frontend)

**¿Qué es un Hook?** Una función especial de React que te permite "enganchar" funcionalidad a componentes funcionales. Sin Hooks, necesitarías clases. Con Hooks, es más simple.

### Hook #1: **useState** - Guardar Estado

**¿Para qué sirve?** Para recordar datos en el componente. Si cambias el estado, el componente se re-renderiza automáticamente.

**¿Por qué necesitamos esto?** Sin useState, cada vez que el usuario interactúa, el componente se "olvida" de los cambios. Con useState, React recuerda los datos.

```javascript
// Sintaxis básica
const [estado, setEstado] = useState(valorInicial);
//    ↑             ↑                    ↑
//  variable   función para         valor al
//   para      actualizar           comenzar
//   leer
```

**Entendimiento profundo:**
- **estado** → Variable que puedes leer. Si cambias, todo el componente se re-renderiza
- **setEstado** → Función para cambiar el estado. SIEMPRE debes usar esta función, nunca modificar directamente
- **valorInicial** → El valor que tendrá la primera vez que el componente carga

**¿Qué NO hacer?**
```javascript
// ❌ INCORRECTO - No cambies estado directamente
state = newValue;  // React no se da cuenta del cambio

// ✅ CORRECTO - Usa la función setter
setState(newValue);  // React detecta el cambio y re-renderiza
```

---

#### **Ejemplo 1: Contador Simple**

```javascript
function Contador() {
  const [count, setCount] = useState(0);  // Empieza en 0
  
  const incrementar = () => {
    setCount(count + 1);  // Suma 1 al contador
  };
  
  return (
    <div>
      <p>Contador: {count}</p>
      <button onClick={incrementar}>+1</button>
      {/* Cada click incrementa count y re-renderiza */}
    </div>
  );
}

// Flujo:
// 1. Componente carga: count = 0
// 2. Usuario hace click
// 3. onClick → incrementar()
// 4. setCount(0 + 1) → count = 1
// 5. React re-renderiza: <p>Contador: 1</p>
// 6. Usuario ve el cambio inmediatamente
```

---

#### **Ejemplo 2: Toggle (Mostrar/Ocultar)**

```javascript
function Menu() {
  const [abierto, setAbierto] = useState(false);  // Empieza cerrado
  
  const toggleMenu = () => {
    setAbierto(!abierto);  // Cambiar entre true/false
  };
  
  return (
    <div>
      <button onClick={toggleMenu}>
        {abierto ? 'Cerrar Menú' : 'Abrir Menú'}
      </button>
      {abierto && (
        <ul>
          <li>Inicio</li>
          <li>Productos</li>
          <li>Contacto</li>
        </ul>
      )}
    </div>
  );
}

// Estado visual:
// abierto = false → botón muestra "Abrir Menú" + lista oculta
// abierto = true  → botón muestra "Cerrar Menú" + lista visible
```

---

#### **Ejemplo 3: Formulario con Objeto**

```javascript
function Registro() {
  const [form, setForm] = useState({
    nombre: '',
    email: '',
    contraseña: ''
  });
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    // Actualizar SOLO el campo que cambió, mantener otros
    setForm({
      ...form,  // Copiar todos los campos actuales
      [name]: value  // Reemplazar el que cambió
    });
  };
  
  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Enviando:', form);
    // Aquí normalmente haríamos una llamada API
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <input 
        name="nombre"
        value={form.nombre}
        onChange={handleChange}
        placeholder="Tu nombre"
      />
      <input 
        name="email"
        value={form.email}
        onChange={handleChange}
        placeholder="Tu email"
      />
      <input 
        name="contraseña"
        type="password"
        value={form.contraseña}
        onChange={handleChange}
        placeholder="Tu contraseña"
      />
      <button type="submit">Registrarse</button>
    </form>
  );
}

// Patrón importante: ...form
// Esto copia TODOS los campos del objeto
// Luego sobrescribimos el que cambió
// Así no perdemos los demás campos
```

---

#### **Ejemplo 4: Array con Estados**

```javascript
function ListaTareas() {
  const [tareas, setTareas] = useState([]);  // Empieza vacía
  const [inputValue, setInputValue] = useState('');
  
  const agregarTarea = () => {
    const nuevaTarea = {
      id: Date.now(),
      texto: inputValue,
      completada: false
    };
    setTareas([...tareas, nuevaTarea]);  // Agregar al final
    setInputValue('');  // Limpiar input
  };
  
  const eliminarTarea = (id) => {
    setTareas(tareas.filter(t => t.id !== id));  // Filtrar el eliminado
  };
  
  const marcarCompletada = (id) => {
    setTareas(tareas.map(t => 
      t.id === id ? {...t, completada: !t.completada} : t
    ));
  };
  
  return (
    <div>
      <input 
        value={inputValue}
        onChange={(e) => setInputValue(e.target.value)}
        placeholder="Nueva tarea"
      />
      <button onClick={agregarTarea}>Agregar</button>
      
      <ul>
        {tareas.map(tarea => (
          <li key={tarea.id}>
            <input 
              type="checkbox"
              checked={tarea.completada}
              onChange={() => marcarCompletada(tarea.id)}
            />
            <span style={{
              textDecoration: tarea.completada ? 'line-through' : 'none'
            }}>
              {tarea.texto}
            </span>
            <button onClick={() => eliminarTarea(tarea.id)}>Eliminar</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

// Patrones clave:
// Agregar: [...tareas, nuevaTarea]
// Eliminar: tareas.filter(t => t.id !== id)
// Actualizar: tareas.map(t => t.id === id ? {...t, campo: valor} : t)
```

---

#### **Ejemplo 5: Lazy Initialization (Leer desde localStorage)**

```javascript
function Usuario() {
  // ❌ INCORRECTO - Lee localStorage cada render (lento)
  const [user, setUser] = useState(
    JSON.parse(localStorage.getItem('user'))
  );
  
  // ✅ CORRECTO - Lee localStorage SOLO la primera vez
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });
  // La función dentro de useState se ejecuta UNA sola vez
  
  return (
    <div>
      {user ? (
        <p>Hola, {user.nombre}</p>
      ) : (
        <p>No hay usuario</p>
      )}
    </div>
  );
}

// ¿Por qué lazy initialization?
// localStorage es lento. Si lo lees cada render, es ineficiente.
// Con lazy init, lo lees solo una vez al cargar.
// Después, React mantiene el estado en memoria (muy rápido).

// Ejemplo real en AdminPanel.js:
const [activeTab, setActiveTab] = useState(() => {
  return localStorage.getItem('adminActiveTab') || 'dashboard';
});
// Primera carga: lee localStorage
// Cambios posteriores: usa estado en memoria
```

---

#### **Errores Comunes con useState**

```javascript
// ❌ ERROR 1: Modificar estado directamente
const [count, setCount] = useState(0);
count = count + 1;  // NO FUNCIONA - React no detecta cambio
setCount(count + 1);  // CORRECTO

// ❌ ERROR 2: No usar setter correctamente
const [form, setForm] = useState({nombre: '', email: ''});
form.nombre = 'Juan';  // MALO - React no re-renderiza
setForm({...form, nombre: 'Juan'});  // BUENO

// ❌ ERROR 3: setState es asincrónico
const [count, setCount] = useState(0);
setCount(count + 1);
console.log(count);  // Aún es 0! setState es asincrónico
// El cambio se refleja DESPUÉS del render siguiente

// ❌ ERROR 4: Usar índice como key
{items.map((item, index) => <div key={index}>{item}</div>)}
// Si la lista cambia de orden, todo falla. Usa ID único
{items.map((item) => <div key={item.id}>{item}</div>)}

// ✅ CORRECCIÓN 4:
// Siempre usa identificador único, no índice
```

---

### Hook #2: **useEffect** - Ejecutar Efectos Secundarios

**¿Para qué sirve?** Ejecutar código cuando el componente carga, cuando cambian datos específicos, o cuando se desmonta.

**¿Por qué es importante?** Sin useEffect, no podrías hacer cosas como:
- Cargar datos del backend
- Guardar datos en localStorage
- Suscribirse a eventos
- Hacer limpiezas cuando el componente desaparece

```javascript
// Sintaxis completa
useEffect(() => {
  // CÓDIGO QUE SE EJECUTA
  console.log('El componente cargó o algo cambió');
  
  // Cargar datos
  const datos = fetch('/api/data').then(r => r.json());
  
  return () => {
    // CLEANUP (opcional)
    // Se ejecuta ANTES de desmontar o antes del siguiente useEffect
    console.log('Limpiando...');
  };
}, [dependencia1, dependencia2]);
//   ↑
// ARRAY DE DEPENDENCIAS (crucial!)
```

---

#### **Array de Dependencias - La Parte Más Importante**

```javascript
// 1️⃣ [] - Se ejecuta SOLO UNA VEZ (cuando el componente carga)
useEffect(() => {
  console.log('Componente montado, solo una vez');
  
  // Perfecto para:
  // - Cargar datos iniciales
  // - Inicializar variables
  // - Configurar listeners
}, []);

// 2️⃣ [variable] - Se ejecuta cuando la variable cambia
const [userId, setUserId] = useState(null);
useEffect(() => {
  console.log('userId cambió:', userId);
  // Se ejecuta:
  // - Primera carga
  // - Cada vez que userId cambia
}, [userId]);

// 3️⃣ [var1, var2] - Se ejecuta si ALGUNA de estas cambia
useEffect(() => {
  console.log('var1 o var2 cambió');
  // Se ejecuta si:
  // - var1 cambia
  // - var2 cambia
  // - ambas cambian
}, [var1, var2]);

// 4️⃣ SIN ARRAY - Se ejecuta CADA RENDER (¡PELIGRO!)
useEffect(() => {
  console.log('Cada render, cada render, cada render...');
  // Causa loops infinitos fácilmente
  // Casi NUNCA debes usar esto
});
```

---

#### **Ejemplo 1: Cargar Datos del Backend**

```javascript
function ProductosList() {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    // Función async para cargar datos
    const cargarProductos = async () => {
      try {
        setLoading(true);
        const response = await fetch('http://localhost:8080/products');
        if (!response.ok) throw new Error('Error en la petición');
        const data = await response.json();
        setProductos(data);  // Guardar datos
        setError(null);      // Limpiar error anterior
      } catch (err) {
        setError(err.message);
        setProductos([]);  // Limpiar productos en error
      } finally {
        setLoading(false);  // Siempre terminar carga
      }
    };
    
    cargarProductos();
  }, []);  // [] = ejecutar solo una vez al cargar
  
  if (loading) return <p>Cargando...</p>;
  if (error) return <p>Error: {error}</p>;
  
  return (
    <div>
      {productos.map(p => (
        <div key={p.id}>{p.name} - ${p.price}</div>
      ))}
    </div>
  );
}

// Flujo:
// 1. Componente carga
// 2. useEffect detecta [] (nunca cambio)
// 3. Ejecuta la función
// 4. Hace petición GET a backend
// 5. setProductos(data) actualiza estado
// 6. Componente re-renderiza con datos
```

---

#### **Ejemplo 2: Reaccionar a Cambios (Dependencias)**

```javascript
function FiltroPorCategoria() {
  const [categoria, setCategoria] = useState('Consolas');
  const [productos, setProductos] = useState([]);
  
  useEffect(() => {
    console.log('Categoría cambió a:', categoria);
    
    // Cargar productos de esta categoría
    const cargarPorCategoria = async () => {
      const response = await fetch(
        `http://localhost:8080/products?category=${categoria}`
      );
      const data = await response.json();
      setProductos(data);
    };
    
    cargarPorCategoria();
  }, [categoria]);  // ← Se ejecuta cada vez que categoria cambia
  
  return (
    <div>
      <select value={categoria} onChange={(e) => setCategoria(e.target.value)}>
        <option>Consolas</option>
        <option>Juegos</option>
        <option>Accesorios</option>
      </select>
      
      <div>
        {productos.map(p => (
          <div key={p.id}>{p.name}</div>
        ))}
      </div>
    </div>
  );
}

// Flujo:
// 1. Usuario selecciona "Juegos"
// 2. setCategoria('Juegos') actualiza estado
// 3. useEffect detecta: categoria está en [categoria]
// 4. Se ejecuta el código
// 5. Hace petición con category=Juegos
// 6. Muestra productos de esa categoría
```

---

#### **Ejemplo 3: Proteger Rutas (AdminPanel)**

```javascript
function AdminPanel() {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  const [admin, setAdmin] = useState(null);
  
  useEffect(() => {
    // Protejer la ruta
    if (!user) {
      // Usuario no logueado
      navigate('/login');
      return;
    }
    
    if (user.role !== 'admin') {
      // Usuario logueado pero no es admin
      navigate('/');
      return;
    }
    
    // Usuario es admin, cargar datos
    cargarDatos();
  }, [user, navigate]);
  // Se ejecuta si user o navigate cambian
  
  const cargarDatos = async () => {
    try {
      const [prods, users, orders] = await Promise.all([
        fetch('http://localhost:8080/products').then(r => r.json()),
        fetch('http://localhost:8080/users').then(r => r.json()),
        fetch('http://localhost:8080/orders').then(r => r.json()),
      ]);
      // setAdmin({prods, users, orders});
    } catch (err) {
      console.error('Error cargando admin:', err);
    }
  };
  
  return (
    <div>
      <h1>Panel de Administración</h1>
      {/* contenido admin */}
    </div>
  );
}

// Seguridad:
// 1. Usuario NO logueado → redirige a /login
// 2. Usuario sin permisos → redirige a /
// 3. Usuario admin → muestra panel
```

---

#### **Ejemplo 4: Cleanup Function (Limpiar Recursos)**

```javascript
function Temporizador() {
  const [segundos, setSegundos] = useState(0);
  
  useEffect(() => {
    console.log('Iniciando temporizador');
    
    // Crear intervalo que incrementa cada segundo
    const intervalo = setInterval(() => {
      setSegundos(s => s + 1);
    }, 1000);
    
    // CLEANUP: Se ejecuta cuando el componente se desmonta
    return () => {
      console.log('Limpiando temporizador');
      clearInterval(intervalo);  // Detener el intervalo
    };
  }, []);
  // Sin [] es un error común - el intervalo se crearía infinitas veces
  
  return (
    <div>
      <p>Segundos: {segundos}</p>
    </div>
  );
}

// Sin cleanup:
// - Intervalo se crearía múltiples veces
// - Consumiría memoria
// - Hacer timer múltiple

// Con cleanup:
// - Intervalo se detiene al desmontar
// - Se libera memoria
// - Función correcta
```

---

#### **Ejemplo 5: Sincronizar con localStorage**

```javascript
function GuardarNombreUsuario() {
  const [nombre, setNombre] = useState(() => {
    return localStorage.getItem('nombre_usuario') || '';
  });
  
  // Cada vez que nombre cambia, guardar en localStorage
  useEffect(() => {
    localStorage.setItem('nombre_usuario', nombre);
  }, [nombre]);
  // Se ejecuta si nombre cambia
  
  return (
    <div>
      <input 
        value={nombre}
        onChange={(e) => setNombre(e.target.value)}
        placeholder="Tu nombre"
      />
      <p>Nombre guardado: {nombre}</p>
    </div>
  );
}

// Flujo:
// 1. Cargar nombre de localStorage
// 2. Usuario escribe en input
// 3. setNombre(valor) actualiza estado
// 4. useEffect detecta nombre cambió
// 5. Guarda en localStorage
// 6. Si recarga página, nombre está salvado
```

---

#### **Errores Comunes con useEffect**

```javascript
// ❌ ERROR 1: Loop Infinito
useEffect(() => {
  setData([...data, newItem]);  // Modifica data
}, [data]);  // Se ejecuta cuando data cambia
// Resultado: data cambia → useEffect se ejecuta → data cambia → loop infinito!

// ✅ SOLUCIÓN:
useEffect(() => {
  // Solo haz cambios si es necesario, o usa []
}, []);

// ❌ ERROR 2: Olvidar dependencias
const userId = 5;
useEffect(() => {
  fetch(`/user/${userId}`);
}, []);  // userId está en el código pero NO en dependencias!
// Si userId cambia, el efecto no se ejecuta

// ✅ SOLUCIÓN:
useEffect(() => {
  fetch(`/user/${userId}`);
}, [userId]);  // Agregar userId a dependencias

// ❌ ERROR 3: No limpiar recursos
useEffect(() => {
  const listener = () => console.log('evento');
  window.addEventListener('click', listener);
  // Si el componente se desmonta, listener sigue activado!
}, []);

// ✅ SOLUCIÓN:
useEffect(() => {
  const listener = () => console.log('evento');
  window.addEventListener('click', listener);
  
  return () => {
    window.removeEventListener('click', listener);  // Limpiar
  };
}, []);

// ❌ ERROR 4: Async directo en useEffect
useEffect(async () => {  // NO HACER
  const data = await fetch('/api');
}, []);

// ✅ SOLUCIÓN:
useEffect(() => {
  const fetchData = async () => {  // Función async separada
    const data = await fetch('/api');
  };
  fetchData();
}, []);

---

### Hook #3: **useContext** - Acceder al Estado Global

**¿Para qué sirve?** Acceder a datos guardados en un Contexto sin pasar props nivel por nivel.

```javascript
// Sintaxis
const contexto = useContext(NombreDelContexto);

// Ejemplos reales:
const { user, login, logout } = useContext(AuthContext);
const { cart, addToCart, removeFromCart } = useContext(CartContext);

// Uso:
if (!user) {
  return <button onClick={login}>Inicia sesión</button>;
}
```

**¿Cómo fluye el estado?**
```
┌─────────────────────────────────────────────────────────────┐
│  App.js                                                      │
│  <AuthProvider>                                              │
│    <CartProvider>                                            │
│      <Header />  ← puede usar                                │
│      <Home />    ← useContext()                              │
│      <AdminPanel />                                          │
│    </CartProvider>                                           │
│  </AuthProvider>                                             │
└─────────────────────────────────────────────────────────────┘
```

**Sin Contextos (pesadilla):**
```javascript
<App user={user}>
  <Header user={user} setUser={setUser} />
  <Home user={user} setUser={setUser} />
  <AdminPanel user={user} setUser={setUser} />
</App>
// Pasando props en cada nivel... ¡qué horror!
```

**Con Contextos (simple):**
```javascript
const Header = () => {
  const { user, logout } = useContext(AuthContext);
  // Acceso directo, sin props
};
```

---

### Hook #4: **useNavigate** - Navegar entre Páginas

**¿Para qué sirve?** Redirigir a otras rutas desde código.

```javascript
const navigate = useNavigate();

// Navegar a ruta
navigate('/home');

// Con parámetros
navigate(`/producto/${id}`);

// Ir atrás
navigate(-1);

// Ejemplo: Después de login exitoso
const handleLogin = async (email, password) => {
  await authAPI.login(email, password);
  navigate('/'); // Ir a home después de login
};
```

---

## 🔐 Seguridad Implementada

### **1. Token JWT en localStorage**

**¿Qué es?** Un token (código seguro) que prueba que eres quien dices ser. Cuando haces login, backend te da un token. Lo guardas en localStorage y lo usas en TODAS las peticiones futuras.

```javascript
localStorage.setItem('token', 'eyJhbG...');
localStorage.setItem('auth_user', JSON.stringify(userData));
```

**Flujo:**
1. Usuario hace login
2. Backend valida email/contraseña
3. Backend genera token JWT
4. Frontend guarda token en localStorage
5. Próximas peticiones → adjuntan token
6. Backend valida token → permite operación

**Ventajas:**
- ✅ Persiste después de recargar página
- ✅ Accesible en todos los componentes
- ✅ Backend puede verificar que eres tú

**Riesgos:**
- ❌ Vulnerable a XSS (si alguien inyecta JS malicioso)
- ❌ Visible en Developer Tools
- ❌ Se roba si alguien accede a tu computadora

**Mitigación implementada:**
- ✅ Validar entrada del usuario (no ejecutar código extraño)
- ✅ Sanitizar datos del backend
- ✅ HTTPS en producción (encripta el token en tránsito)

### **2. Interceptor HTTP (adjunta token)**

**¿Qué es?** Un "intermediario" que intercepta todas las peticiones antes de enviarlas. Es como un portero que automáticamente adjunta tu credencial a cada petición.

```javascript
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;  // ← Se agrega automáticamente
  }
  return config;
});
```

**¿Qué hace?**
- ANTES de CADA llamada HTTP al backend:
  1. Lee token de localStorage
  2. Lo agrega a headers: `Authorization: Bearer {token}`
  3. Envía petición CON el token
  4. Backend valida el token

**Ventaja:** No necesitas hacer `config.headers.Authorization = ...` en CADA component. Se hace automáticamente una sola vez en `api.js`.

---

### **3. Rutas Protegidas (Guards)**

**¿Qué es?** Proteger páginas sensibles (como Admin) para que solo usuarios autorizados accedan.

```javascript
// En AdminPanel.js:
useEffect(() => {
  if (!user || user.role !== 'admin') {
    navigate('/');  // Redirigir si no es admin
    return;
  }
  loadData();
}, [user]);
```

**¿Cómo funciona?**
1. Usuario intenta ir a /admin
2. AdminPanel carga
3. useEffect verifica: ¿Este usuario es admin?
4. Si NO → redirigir a home ("/")
5. Si SÍ → mostrar AdminPanel

**Flujos de acceso:**
```
Usuario NO logueado
  → Intenta /admin
  → AdminPanel: user = null
  → Valida: !user? SÍ
  → navigate('/') 
  → Va a Home

Usuario logueado pero NO admin
  → Intenta /admin
  → AdminPanel: user.role = 'USER'
  → Valida: user.role !== 'admin'? SÍ
  → navigate('/') 
  → Va a Home

Usuario admin
  → Intenta /admin
  → AdminPanel: user.role = 'ADMIN'
  → Valida: usuario ES admin? SÍ
  → Muestra AdminPanel
```

---

### **4. Manejo de Errores 401 (Token Expirado)**

**¿Qué es?** Cuando tu token expira o es inválido, backend retorna error 401. Debes volver a login.

```javascript
// Interceptor de respuesta
api.interceptors.response.use(
  response => response,  // Todo bien, retorna la respuesta
  error => {
    if (error.response?.status === 401) {
      // Token expirado o inválido
      localStorage.removeItem('token');      // Borrar token viejo
      localStorage.removeItem('auth_user');  // Borrar usuario
      window.location.href = '/login';       // Redirigir a login
    }
    return Promise.reject(error);
  }
);
```

**Flujo:**
1. Frontend hace petición con token viejo
2. Backend: "Este token no es válido" → Respuesta 401
3. Frontend detecta 401
4. Frontend: Borra token
5. Frontend: Redirige a /login
6. Usuario hace login de nuevo
7. Obtiene token nuevo

---

## 🎨 Componentes Reutilizables

**¿Por qué "reutilizables"?** Son componentes pequeños que aparecen en múltiples páginas. En lugar de duplicar código, los hacemos UNA VEZ y los reutilizamos.

Ejemplo: **ProductoCard** aparece en:
- Home.js (productos destacados)
- Productos.js (todos los productos)
- Ofertas.js (productos en oferta)

### **Header.js** - Barra de Navegación

**¿Qué muestra?**
- Logo / Marca de la empresa
- Navegación (Home, Productos, Admin, Blog, Contacto, etc.)
- Carrito con badge mostrando cantidad de items
- Usuario logueado (nombre + Logout) o botón de Login

**Estados:**
- `cart.length` → para mostrar el número en la esquina del carrito

**Contextos usados:**
- `AuthContext` → para saber si está logueado
- `CartContext` → para contar items del carrito

---

### **Footer.js** - Pie de Página

**¿Qué muestra?**
- Enlaces importantes (Home, Contacto, Blog, etc.)
- Información de contacto
- Redes sociales
- Copyright

---

### **ProductoCard.js** - Tarjeta de Producto

**Props que recibe:**
```javascript
{
  id: "prod-1",
  name: "PS5",
  price: 500,
  image: "/images/ps5.jpg",
  category: "Consolas",
  stock: 5
}
```

**¿Qué renderiza?**
```
┌──────────────────────────┐
│   [IMAGEN]               │
│   PS5                    │
│   Consolas               │
│   $500                   │
│                          │
│  [Ver Detalles] [Carrito]│
└──────────────────────────┘
```

**Botones:**
1. **Ver Detalles** → navegar a `/producto/{id}`
2. **Agregar al Carrito** → `addToCart(producto, 1)`

---

### **CarritoItem.js** - Item Dentro del Carrito

**Props que recibe:**
```javascript
{
  id: "prod-1",
  name: "PS5",
  price: 500,
  quantity: 2,
  image: "/images/ps5.jpg"
}
```

**¿Qué muestra?**
```
┌──────────────────────────────────┐
│ [IMG] PS5                        │
│       $500 c/u                   │
│       Cantidad: [- 1 +]          │
│       Subtotal: $1000            │
│       [Eliminar]                 │
└──────────────────────────────────┘
```

**Interacciones:**
- **-** → `updateQuantity(id, quantity - 1)`
- **+** → `updateQuantity(id, quantity + 1)`
- **Eliminar** → `removeFromCart(id)`
- Nombre
- Precio con formato
- Categoría
- Botón: "Ver Detalles" / "Agregar al Carrito"
```

### **CarritoItem.js**
```javascript
// Props:
{
  id: "prod-1",
  name: "PS5",
  price: 500,
  quantity: 2,
  image: "/images/ps5.jpg"
}

// Mostrar:
- Miniatura imagen
- Nombre + Precio
- Cantidad (con +/- para cambiar)
- Subtotal (price × quantity)
- Botón eliminar
```

---

## 🌐 Ciclo de Vida de un Componente Típico

```javascript
function MiComponente() {
  // 1. INICIALIZACIÓN - Se ejecuta UNA SOLA VEZ
  const { user } = useContext(AuthContext);           // Lee contexto global
  const [datos, setDatos] = useState([]);             // Crea estado local
  const navigate = useNavigate();                      // Setup para navegación

  // 2. EFECTOS - Se ejecutan DESPUÉS de renderizar
  useEffect(() => {
    // Si no hay usuario → redirigir a login
    if (!user) {
      navigate('/login');
      return;
    }
    
    // Cargar datos desde backend
    fetch('/api/datos')
      .then(res => setDatos(res.data))
      .catch(err => console.error(err));
  }, [user, navigate]); // Se re-ejecuta si user o navigate cambian

  // 3. RENDERIZADO - Dibuja la UI
  return (
    <div>
      <h1>Bienvenido, {user?.name}</h1>
      {datos.map(item => (
        <div key={item.id}>{item.name}</div>
      ))}
    </div>
  );
}

// 4. ACTUALIZACIÓN - Cuando user, datos, etc. cambian
//    React re-renderiza automáticamente

// 5. DESMONTAJE - Se ejecuta cuando el componente se elimina
//    (no usado en este ejemplo, pero importante limpiar eventos)
```

---

## 📊 Flujo de Datos General

```
           ┌──────────────────────────────┐
           │       Usuario en Browser     │
           │   Interactúa con componente  │
           └──────────┬───────────────────┘
                      │
                      ▼
           ┌──────────────────────────────┐
           │   Componente (e.g., Home)    │
           │  - useState para estado local│
           │  - useContext para global    │
           │  - useEffect para efectos    │
           └──────────┬───────────────────┘
                      │
        ┌─────────────┼─────────────┐
        │             │             │
        ▼             ▼             ▼
  AuthContext   CartContext    api.js
   (user)       (cart)        (axios)
        │             │             │
        └─────────────┼─────────────┘
                      │
                      ▼
            localStorage (persistencia)
                      │
                      ▼
        http://localhost:8080
         (Backend Spring Boot)
                      │
                      ▼
           MongoDB (Base de datos)
```

---

## 🚀 Resumen: De Usuario a Pantalla

```
1. Usuario escribe email/password
2. onClick → handleLogin()
3. authAPI.login(email, password)
4. axios.post('/auth/login', ...)
5. Backend valida en MongoDB
6. Retorna { token, user }
7. Guardar en localStorage
8. AuthContext.login(userData)
9. Componentes leen useContext(AuthContext)
10. Se actualizan con nuevo user
11. Componentes re-renderizen
12. Usuario ve su nombre en Header
```

---

## 📝 Nota sobre Fallbacks

**¿Qué es un fallback?** Un plan B. Si algo falla, tenemos una alternativa.

Si el backend falla (error 500, timeout, etc.):
```javascript
try {
  const response = await productsAPI.getAll();
  setProducts(response.data);
} catch (error) {
  console.error("Error:", error);
  // Fallback: usar dataStore local
  setProducts(dataStore.getProducts());
  console.log("Backend no disponible, usando datos locales");
}
```

**dataStore.js** contiene todos los productos en memoria como respaldo. Así si Spring Boot falla, la app sigue funcionando con datos de ejemplo.

---

## 🎓 Resumen Rápido

| Concepto | ¿Qué es? | Ejemplo |
|----------|----------|---------|
| **useState** | Guardar estado en el componente | `const [user, setUser] = useState(null)` |
| **useEffect** | Ejecutar código después del render | `useEffect(() => { loadData(); }, [])` |
| **useContext** | Acceder estado global | `const {user} = useContext(AuthContext)` |
| **useNavigate** | Cambiar de página | `navigate('/admin')` |
| **Contexto** | Estado compartido entre componentes | AuthContext, CartContext |
| **api.js** | Cliente HTTP centralizado | `productsAPI.getAll()` |
| **JWT Token** | Credencial de seguridad | Guardado en localStorage |
| **Interceptor** | Adjunta token automáticamente | Se agregaa todos los headers |
| **Rutas Protegidas** | Verificar permisos | `if (!user.isAdmin) navigate('/')` |

---

## 🚀 Flujo Completo de Compra (Visual)

```
1. USUARIO LLEGA
   ↓
2. Header muestra "Login"
   ↓
3. Usuario hace click en "Iniciar Sesión"
   ↓
4. Va a /login
   ↓
5. Ingresa email + contraseña
   ↓
6. authAPI.login(email, password)
   ↓
7. Backend valida
   ↓
8. Retorna token + user
   ↓
9. Frontend guarda en localStorage
   ↓
10. AuthContext.login(userData) actualiza
   ↓
11. Header se actualiza: "Bienvenido, Juan!"
   ↓
12. Usuario va a /productos
   ↓
13. Se cargan productos: productsAPI.getAll()
   ↓
14. Usuario ve ProductoCard para cada producto
   ↓
15. Usuario hace click en "Agregar al Carrito"
   ↓
16. CartContext.addToCart(producto, cantidad)
   ↓
17. Se guarda en localStorage (cart_${userId})
   ↓
18. Header muestra badge con cantidad
   ↓
19. Usuario va a /carrito
   ↓
20. Se muestran todos los items con CartContext.cart
   ↓
21. Usuario hace click en "Proceder al Checkout"
   ↓
22. Va a /checkout
   ↓
23. Formulario de envío + pago
   ↓
24. Usuario hace click "Pagar Ahora"
   ↓
25. handlePayment() → ordersAPI.create(userId, items)
   ↓
26. Backend crea orden en MongoDB
   ↓
27. transbankAPI.createTransaction() simula pago
   ↓
28. Si éxito: navigate(/checkout/success/{orderId})
   ↓
29. Si error: navigate(/checkout/error)
   ↓
30. Usuario ve confirmación o error
```

---

## 🏆 Estructura Resumida

```javascript
// App.js
<BrowserRouter>
  <AuthProvider>          // ← Proporciona AuthContext
    <CartProvider>        // ← Proporciona CartContext
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/productos" element={<Productos />} />
        <Route path="/carrito" element={<Carrito />} />
        <Route path="/admin" element={<AdminPanel />} />
        // ... más rutas
      </Routes>
    </CartProvider>
  </AuthProvider>
</BrowserRouter>

// Cualquier componente puede:
// 1. Usar hooks: const [estado, setEstado] = useState()
// 2. Acceder contextos: const {user} = useContext(AuthContext)
// 3. Hacer peticiones: await productsAPI.getAll()
// 4. Navegar: navigate('/home')
```

---

## ⚠️ Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `Cannot read property 'user' of undefined` | No envolviste en AuthProvider | Asegura que App esté dentro de `<AuthProvider>` |
| `useContext must be used inside Provider` | Usas contexto fuera del Provider | Mueve el componente dentro de `<AuthProvider>` |
| `"GET http://localhost:8080 failed"` | Backend no está corriendo | Inicia backend: `./gradlew bootRun` |
| `Token expirado` | JWT vencido | Vuelve a hacer login |
| `Cart vacío después de recargar` | localStorage no se sincroniza | Verifica que el navegador guarde localStorage |

---

¡Espero que esta guía completa te haya aclarado todo del frontend de Panda Gamers! 🎮🐼
