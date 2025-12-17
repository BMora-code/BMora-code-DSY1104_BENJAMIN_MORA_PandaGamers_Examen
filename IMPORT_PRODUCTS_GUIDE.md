# Endpoint de Importación de Productos

## Descripción
El endpoint `POST /products/import` permite importar un lote de productos desde el frontend al backend. Los productos se normalizan automáticamente del formato frontend (name, description, price, category, image, stock) al formato backend (nombre, descripcion, precio, categoria, imagen, stock).

## Endpoint
```
POST http://localhost:8080/products/import
```

## Autenticación
Requiere rol **ADMIN** (token JWT en header Authorization).

## Body Request
```json
{
  "products": [
    {
      "id": "1",
      "name": "PlayStation 5",
      "description": "Consola de última generación",
      "price": 499.99,
      "category": "Consolas",
      "image": "/images/Consolas/ps5.avif",
      "stock": 10
    },
    {
      "id": "2",
      "name": "Xbox Series X",
      "description": "Consola Microsoft última gen",
      "price": 499.99,
      "category": "Consolas",
      "image": "/images/Consolas/xbox.avif",
      "stock": 8
    }
  ]
}
```

## Response (Success - 200)
```json
{
  "count": 2,
  "products": [
    {
      "id": "507f1f77bcf86cd799439011",
      "nombre": "PlayStation 5",
      "descripcion": "Consola de última generación",
      "precio": 499.99,
      "categoria": "Consolas",
      "imagen": "/images/Consolas/ps5.avif",
      "stock": 10,
      "createdAt": "2025-12-03T10:30:00Z"
    },
    {
      "id": "507f1f77bcf86cd799439012",
      "nombre": "Xbox Series X",
      "descripcion": "Consola Microsoft última gen",
      "precio": 499.99,
      "categoria": "Consolas",
      "imagen": "/images/Consolas/xbox.avif",
      "stock": 8,
      "createdAt": "2025-12-03T10:30:05Z"
    }
  ]
}
```

## Manejo de Duplicados
- **Si el producto NO existe**: Se crea uno nuevo en la BD
- **Si el producto YA existe** (mismo nombre): Se actualiza con los nuevos valores
- La búsqueda se realiza por el campo `nombre` (del frontend mapea `name`)

## Campos Requeridos en Request
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | String | Identificador único |
| `name` | String | Nombre del producto |
| `description` | String | Descripción detallada |
| `price` | Number | Precio numérico |
| `category` | String | Categoría (Consolas, Mouses, Sillas, etc.) |
| `image` | String | URL/ruta de la imagen |
| `stock` | Number | Cantidad disponible |

## Campos en Response (Backend Format)
| Campo | Tipo | Nota |
|-------|------|------|
| `id` | String | ID MongoDB generado |
| `nombre` | String | Campo `name` del request |
| `descripcion` | String | Campo `description` del request |
| `precio` | Number | Campo `price` del request |
| `categoria` | String | Campo `category` del request |
| `imagen` | String | Campo `image` del request |
| `stock` | Number | Campo `stock` del request |
| `createdAt` | ISO DateTime | Fecha de creación |

## Códigos de Error
| Código | Descripción |
|--------|-------------|
| `400` | Array vacío o null en request |
| `401` | Token no válido o expirado |
| `403` | Usuario sin rol ADMIN |
| `500` | Error interno del servidor |

## Ejemplo de Uso desde Frontend

### Con el Cliente axios en `api.js`
```javascript
import { productsAPI } from './api';

// Preparar productos normalizados
const productsToImport = [
  {
    id: '1',
    name: 'PlayStation 5',
    description: 'Consola de última generación Sony',
    price: 499.99,
    category: 'Consolas',
    image: '/images/Consolas/ps5.avif',
    stock: 10
  }
];

// Llamar al endpoint
try {
  const response = await productsAPI.importProducts(productsToImport);
  console.log(`${response.data.count} productos importados`);
} catch (error) {
  console.error('Error en importación:', error.response.data);
}
```

### Página de Admin (/admin/import)
Una página de administración permite:
1. **Importar productos de ejemplo** - Botón directo para cargar datos de prueba
2. **Importar desde archivo JSON** - Subir un archivo `.json` personalizado
3. **Descargar ejemplo** - Descarga plantilla JSON con formato correcto

Accede a: `http://localhost:3000/admin/import`

## Notas de Implementación
- El endpoint normalizador busca por `nombre` (no por `id`) para evitar duplicados
- Cada producto se guardará con `createdAt` = timestamp del import
- No se elimina `id` del frontend (se guarda como está, aunque MongoDB genera el suyo)
- El token JWT se envía automáticamente por el interceptor en `api.js`

## Ejemplo: Archivo JSON para Importar
```json
[
  {
    "id": "1",
    "name": "PlayStation 5",
    "description": "Consola de última generación Sony",
    "price": 499.99,
    "category": "Consolas",
    "image": "/images/Consolas/ps5.avif",
    "stock": 10
  },
  {
    "id": "2",
    "name": "Xbox Series X",
    "description": "Consola de última generación Microsoft",
    "price": 499.99,
    "category": "Consolas",
    "image": "/images/Consolas/xbox.avif",
    "stock": 8
  }
]
```

Guarda este JSON y súbelo desde `/admin/import`.
