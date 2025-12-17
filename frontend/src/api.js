import axios from 'axios';

// Función para normalizar producto del backend al formato frontend
const normalizeProduct = (backendProduct) => {
  return {
    id: backendProduct.id,
    name: backendProduct.nombre,
    description: backendProduct.descripcion,
    price: backendProduct.precio,
    category: backendProduct.categoria,
    image: backendProduct.imagen,
    stock: backendProduct.stock
  };
};

// Función para normalizar orden del backend al formato frontend
const normalizeOrder = (backendOrder) => {
  // Normalizar items dentro de la orden
  const normalizedItems = (backendOrder.items || backendOrder.productos || []).map(item => ({
    // Mantener campos originales
    id: item.id || item.productId,
    productId: item.productId || item.id,
    nombre: item.nombre || item.productName,
    productName: item.productName || item.nombre,
    precio: item.precio || item.price,
    price: item.price || item.precio,
    cantidad: item.cantidad,
    subtotal: item.subtotal,
    imageUrl: item.imageUrl || item.imagen || item.image,
    imagen: item.imagen || item.imageUrl || item.image,
    image: item.image || item.imageUrl || item.imagen,
    // Campos adicionales para compatibilidad
    ...item
  }));

  return {
    id: backendOrder.id,
    userId: backendOrder.userId,
    items: normalizedItems,
    total: backendOrder.total,
    paymentMethod: backendOrder.paymentMethod || backendOrder.metodoPago || 'Transbank',
    status: (backendOrder.status || backendOrder.estado || 'PENDING').toLowerCase() === 'completed' ? 'completed' : (backendOrder.status || backendOrder.estado || 'PENDING').toLowerCase(),
    date: backendOrder.date || backendOrder.fecha || new Date().toISOString(),
    // Mantener campos adicionales por compatibilidad
    shippingInfo: backendOrder.shippingInfo || {},
    paymentInfo: backendOrder.paymentInfo || {},
    subtotal: backendOrder.subtotal,
    shippingCost: backendOrder.shippingCost || 0,
    iva: backendOrder.iva || 0,
    deliveryOption: backendOrder.deliveryOption || 'standard'
  };
};

// Crear instancia de axios con baseURL apuntando al backend Spring Boot
const api = axios.create({
  // Usar explícitamente el backend en localhost según requerimiento
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para agregar el token JWT a cada petición (excepto auth)
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token && !config.url?.includes('/auth/')) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para manejar errores 401 (token expirado)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expirado o inválido - limpiar localStorage y redirigir
      localStorage.removeItem('token');
      localStorage.removeItem('auth_user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// === Auth Endpoints ===
export const authAPI = {
  // El backend espera el campo `nombre` en lugar de `name`
  register: (name, email, password, role, adminCode) => {
    const payload = { nombre: name, email, password };
    if (role && role.toLowerCase() === 'admin') {
      // Enviar adminCode si el usuario intenta registrarse como admin
      payload.adminCode = adminCode;
    }
    return api.post('/auth/register', payload);
  },
  
  login: (email, password) =>
    api.post('/auth/login', { email, password })
};

// === Products Endpoints ===
export const productsAPI = {
  getAll: async () => {
    const response = await api.get('/products');
    return {
      data: Array.isArray(response.data) 
        ? response.data.map(normalizeProduct)
        : response.data
    };
  },
  
  getById: async (id) => {
    const response = await api.get(`/products/${id}`);
    return {
      data: normalizeProduct(response.data)
    };
  },
  
  create: (productData) =>
    api.post('/products/create', productData),
  
  update: (id, productData) =>
    api.put(`/products/update/${id}`, productData),
  
  // Actualización parcial de stock (no reemplaza el documento)
  updateStock: (id, stockPayload) =>
    api.put(`/products/admin/stock/${id}`, stockPayload),
  
  delete: (id) =>
    api.delete(`/products/delete/${id}`),
  
  // Importar productos normalizados del frontend
  importProducts: (products) =>
    api.post('/products/import', { products })
};

// === Users Endpoints (admin) ===
export const usersAPI = {
  getAll: () => api.get('/users/admin/all'),
  delete: (id) => api.delete(`/users/admin/${id}`),
  create: (userData) => api.post('/users/admin/create', userData)
};

// === Offers Endpoints ===
export const offersAPI = {
  getAll: () => api.get('/offers/admin/all'),
  getPublic: () => api.get('/offers/all/public'),
  create: (offer) => api.post('/offers/create', offer),
  update: (id, offer) => api.put(`/offers/update/${id}`, offer),
  delete: (id) => api.delete(`/offers/delete/${id}`)
};

// === Orders Endpoints ===
export const ordersAPI = {
  create: (userId, orderItems) =>
    api.post(`/orders/create/${userId}`, orderItems),
  getAll: () => api.get('/orders/admin/all'),
  getByUser: (userId) =>
    api.get(`/orders/user/${userId}`),

  getById: async (orderId) => {
    const response = await api.get(`/orders/${orderId}`);
    return {
      data: normalizeOrder(response.data)
    };
  },

  delete: (orderId) => api.delete(`/orders/admin/${orderId}`),

  completeOrder: (orderId) =>
    api.put(`/orders/${orderId}/complete`),
  
  updateOrderStatus: (orderId, status) =>
    api.put(`/orders/${orderId}/status`, { status })
};

// === Transbank Endpoints ===
export const transbankAPI = {
  createTransaction: (orderId) =>
    api.post(`/pay/create?orderId=${orderId}`),
  
  confirmTransaction: (token) =>
    api.get(`/pay/confirm/${token}`)
};

export default api;
