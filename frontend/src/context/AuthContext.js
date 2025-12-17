// src/context/AuthContext.js
import { createContext, useState, useEffect } from "react";
import api from "../api";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);

  // Cargar usuario desde localStorage al iniciar
  useEffect(() => {
    const savedUser = localStorage.getItem('auth_user');
    if (savedUser) {
      try {
        const parsed = JSON.parse(savedUser);
        // Normalizar campos que el backend devuelve en español (nombre, _id, etc.)
        const normalized = {
          ...parsed,
          id: parsed.id || parsed._id || parsed.userId || parsed._id,
          name: parsed.nombre || parsed.name || parsed.nombreCompleto || '',
          nombre: parsed.nombre || parsed.name || parsed.nombreCompleto || '',
          email: parsed.email || parsed.correo || ''
        };
        // Calcular descuento DUOC si corresponde
        const hasDuocDiscount = normalized.email && normalized.email.toLowerCase().includes('@duocuc');
        // If name is missing but token exists, try to fetch profile from backend to fill missing fields
        const token = localStorage.getItem('token');
        if ((!normalized.name || normalized.name.trim() === '') && token) {
          api.get('/users/profile')
            .then(resp => {
              const backend = resp.data || {};
              const filled = {
                ...normalized,
                name: backend.name || backend.nombre || normalized.name,
                nombre: backend.nombre || backend.name || normalized.nombre,
                email: backend.email || normalized.email
              };
              const hd = filled.email && filled.email.toLowerCase().includes('@duocuc');
              setUser({ ...filled, hasDuocDiscount: hd });
              localStorage.setItem('auth_user', JSON.stringify({ ...filled, hasDuocDiscount: hd }));
            })
            .catch(err => {
              // If profile fetch fails, set whatever we have
              setUser({ ...normalized, hasDuocDiscount });
            });
        } else {
          setUser({ ...normalized, hasDuocDiscount });
        }
      } catch (error) {
        console.warn('Error loading user from localStorage:', error);
        localStorage.removeItem('auth_user');
      }
    }
  }, []);

  const login = (userData) => {
    // Normalizar la respuesta del backend para mantener propiedades esperadas en UI
    const normalized = {
      ...userData,
      id: userData.id || userData._id || userData.userId,
      name: userData.nombre || userData.name || '',
      nombre: userData.nombre || userData.name || '',
      email: userData.email || userData.correo || ''
    };
    const hasDuocDiscount = normalized.email && normalized.email.toLowerCase().includes('@duocuc');
    const userWithDiscount = { ...normalized, hasDuocDiscount };
    setUser(userWithDiscount);
    // Guardar en localStorage
    localStorage.setItem('auth_user', JSON.stringify(userWithDiscount));
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('auth_user');
    localStorage.removeItem('token');
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
