import React, { useState } from 'react';
import { productsAPI } from '../api';
import '../styles/AdminImport.css';

export default function AdminImport() {
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState(''); // 'success', 'error', 'info'

  // Array de productos de ejemplo para importar (reemplaza con tus datos reales)
  const sampleProducts = [
    {
      id: '1',
      name: 'PlayStation 5',
      description: 'Consola de última generación Sony',
      price: 499.99,
      category: 'Consolas',
      image: '/images/Consolas/PlayStation 4 Pro.avif',
      stock: 10
    },
    {
      id: '2',
      name: 'Xbox Series X',
      description: 'Consola de última generación Microsoft',
      price: 499.99,
      category: 'Consolas',
      image: '/images/Consolas/PlayStation 4 Pro.avif',
      stock: 8
    },
    {
      id: '3',
      name: 'HyperX Fury S',
      description: 'Mousepad profesional para gaming',
      price: 39.99,
      category: 'Accesorios',
      image: '/images/Portamouse/HyperX Fury S.avif',
      stock: 25
    },
    {
      id: '4',
      name: 'Logitech G Pro',
      description: 'Ratón gaming profesional',
      price: 129.99,
      category: 'Mouses',
      image: '/images/Mouses/HyperX Fury S.avif',
      stock: 15
    },
    {
      id: '5',
      name: 'Secretlab Titan',
      description: 'Silla gaming premium',
      price: 399.99,
      category: 'Sillas',
      image: '/images/Sillas/HyperX Fury S.avif',
      stock: 5
    }
  ];

  // Productos del dataStore de PandaGamers (36 productos reales)
  const dataStoreProducts = [
    {
      id: "1",
      name: "Auriculares HyperX",
      description: "Auriculares gaming de alta calidad con sonido inmersivo.",
      price: 79990,
      category: "Accesorios",
      image: "/images/Accesorios/Auriculares HyperX.webp",
      stock: 15
    },
    {
      id: "2",
      name: "Control Inalámbrico",
      description: "Control inalámbrico para consolas, cómodo y preciso.",
      price: 59990,
      category: "Accesorios",
      image: "/images/Accesorios/Control inalámbrico.jpg",
      stock: 20
    },
    {
      id: "3",
      name: "Mousepad RGB",
      description: "Mousepad con iluminación RGB para setups gaming.",
      price: 29990,
      category: "Accesorios",
      image: "/images/Accesorios/Mousepad RGB.webp",
      stock: 25
    },
    {
      id: "4",
      name: "Teclado Razer",
      description: "Teclado mecánico RGB con switches ópticos.",
      price: 149990,
      category: "Accesorios",
      image: "/images/Accesorios/Teclado Razer.webp",
      stock: 10
    },
    {
      id: "5",
      name: "Nintendo Switch",
      description: "Consola híbrida para gaming en casa o movilidad.",
      price: 349990,
      category: "Consolas",
      image: "/images/Consolas/Nintendo Switch.png",
      stock: 8
    },
    {
      id: "6",
      name: "PlayStation 4 Pro",
      description: "Consola de última generación con 4K HDR.",
      price: 399990,
      category: "Consolas",
      image: "/images/Consolas/PlayStation 4 Pro.avif",
      stock: 5
    },
    {
      id: "7",
      name: "PlayStation 5",
      description: "La consola más potente con ray tracing y SSD ultra rápido.",
      price: 599990,
      category: "Consolas",
      image: "/images/Consolas/PlayStation 5.webp",
      stock: 3
    },
    {
      id: "8",
      name: "Xbox Series X",
      description: "Consola Xbox de nueva generación con 4K gaming.",
      price: 549990,
      category: "Consolas",
      image: "/images/Consolas/Xbox Series X.jpg",
      stock: 4
    },
    {
      id: "9",
      name: "Carcassonne",
      description: "Juego de estrategia medieval para construir ciudades.",
      price: 49990,
      category: "Juegos de mesa",
      image: "/images/Juegos de mesa/Carcassonne.jpg",
      stock: 12
    },
    {
      id: "10",
      name: "Catan",
      description: "Juego de colonización y comercio en una isla.",
      price: 59990,
      category: "Juegos de mesa",
      image: "/images/Juegos de mesa/Catán.webp",
      stock: 10
    },
    {
      id: "11",
      name: "Monopoly",
      description: "Clásico juego de propiedades y negocios.",
      price: 39990,
      category: "Juegos de mesa",
      image: "/images/Juegos de mesa/Monopoly.jpg",
      stock: 15
    },
    {
      id: "12",
      name: "Risk",
      description: "Juego de estrategia global de conquista territorial.",
      price: 54990,
      category: "Juegos de mesa",
      image: "/images/Juegos de mesa/Risk.jpg",
      stock: 8
    },
    {
      id: "13",
      name: "HyperX Pulsefire",
      description: "Mouse gaming ergonómico con sensor óptico preciso.",
      price: 69990,
      category: "Mouses",
      image: "/images/Mouses/HyperX Pulsefire.webp",
      stock: 18
    },
    {
      id: "14",
      name: "Logitech G502",
      description: "Mouse gaming con 11 botones programables.",
      price: 89990,
      category: "Mouses",
      image: "/images/Mouses/Logitech G502.webp",
      stock: 14
    },
    {
      id: "15",
      name: "Razer DeathAdder",
      description: "Mouse ergonómico con sensor óptico de 16,000 DPI.",
      price: 79990,
      category: "Mouses",
      image: "/images/Mouses/Razer DeathAdder.webp",
      stock: 16
    },
    {
      id: "16",
      name: "SteelSeries Rival 3",
      description: "Mouse gaming ligero con iluminación RGB.",
      price: 64990,
      category: "Mouses",
      image: "/images/Mouses/SteelSeries Rival 3 –.webp",
      stock: 20
    },
    {
      id: "17",
      name: "PC Alienware",
      description: "PC gaming de alto rendimiento con RTX 3080.",
      price: 1999990,
      category: "Pc Gamers",
      image: "/images/Pc Gamers/PC Alienware.webp",
      stock: 2
    },
    {
      id: "18",
      name: "PC ASUS ROG Strix",
      description: "PC gaming con componentes premium y RGB.",
      price: 1799990,
      category: "Pc Gamers",
      image: "/images/Pc Gamers/PC ASUS ROG Strix.png",
      stock: 3
    },
    {
      id: "19",
      name: "PC HP Omen",
      description: "PC gaming equilibrado para gaming competitivo.",
      price: 1499990,
      category: "Pc Gamers",
      image: "/images/Pc Gamers/PC HP Omen.jpg",
      stock: 4
    },
    {
      id: "20",
      name: "PC MSI Gaming",
      description: "PC gaming con enfriamiento avanzado.",
      price: 1699990,
      category: "Pc Gamers",
      image: "/images/Pc Gamers/PC MSI Gaming.jpg",
      stock: 3
    },
    {
      id: "21",
      name: "Polera Gamer 1",
      description: "Polera cómoda para gamers con diseño único.",
      price: 29990,
      category: "Poleras",
      image: "/images/Poleras/640 (1).webp",
      stock: 30
    },
    {
      id: "22",
      name: "Polera Gamer 2",
      description: "Polera con estampado de juegos.",
      price: 34990,
      category: "Poleras",
      image: "/images/Poleras/3396_1.png",
      stock: 25
    },
    {
      id: "23",
      name: "Polera God of War",
      description: "Polera inspirada en God of War.",
      price: 39990,
      category: "Poleras",
      image: "/images/Poleras/PLR-GOW.jpg",
      stock: 20
    },
    {
      id: "24",
      name: "Polera Papa Gamer",
      description: "Polera divertida para papás gamers.",
      price: 32990,
      category: "Poleras",
      image: "/images/Poleras/polera-papa-de-dia-gamer-de-noche.jpg",
      stock: 22
    },
    {
      id: "25",
      name: "Polerón Gamer 1",
      description: "Polerón abrigado para sesiones largas de gaming.",
      price: 59990,
      category: "Polerones",
      image: "/images/Polerones/1132_9.png",
      stock: 15
    },
    {
      id: "26",
      name: "Polerón Gamer 2",
      description: "Polerón con capucha y diseño moderno.",
      price: 64990,
      category: "Polerones",
      image: "/images/Polerones/9704_9.png",
      stock: 12
    },
    {
      id: "27",
      name: "Polerón Smash Bros Vintage",
      description: "Polerón inspirado en Super Smash Bros con colores vintage.",
      price: 69990,
      category: "Polerones",
      image: "/images/Polerones/poleron-smash-bros-vintage-colors.jpg",
      stock: 10
    },
    {
      id: "28",
      name: "Polerón Smash Ultimate",
      description: "Polerón de Super Smash Bros Ultimate.",
      price: 74990,
      category: "Polerones",
      image: "/images/Polerones/poleron-smash-ultimate-2.jpg",
      stock: 8
    },
    {
      id: "29",
      name: "HyperX Fury S",
      description: "Portamouse gaming con diseño ergonómico.",
      price: 39990,
      category: "Portamouse",
      image: "/images/Portamouse/HyperX Fury S.avif",
      stock: 18
    },
    {
      id: "30",
      name: "Logitech G640",
      description: "Portamouse de tela para precisión máxima.",
      price: 49990,
      category: "Portamouse",
      image: "/images/Portamouse/Logitech G640.jpg",
      stock: 16
    },
    {
      id: "31",
      name: "Razer Goliathus",
      description: "Portamouse con superficie de control óptima.",
      price: 44990,
      category: "Portamouse",
      image: "/images/Portamouse/Razer Goliathus.png",
      stock: 20
    },
    {
      id: "32",
      name: "SteelSeries QcK",
      description: "Portamouse profesional para esports.",
      price: 52990,
      category: "Portamouse",
      image: "/images/Portamouse/SteelSeries QcK.jpg",
      stock: 14
    },
    {
      id: "33",
      name: "Silla Cougar",
      description: "Silla gaming ergonómica con soporte lumbar.",
      price: 299990,
      category: "Sillas",
      image: "/images/Sillas/Silla Cougar.webp",
      stock: 5
    },
    {
      id: "34",
      name: "Silla DXRacer",
      description: "Silla premium para gaming con ajuste completo.",
      price: 399990,
      category: "Sillas",
      image: "/images/Sillas/Silla DXRacer.jpg",
      stock: 4
    },
    {
      id: "35",
      name: "Silla GT Omega",
      description: "Silla gaming cómoda con diseño moderno.",
      price: 349990,
      category: "Sillas",
      image: "/images/Sillas/Silla GT Omega.jpg",
      stock: 6
    },
    {
      id: "36",
      name: "Silla SecretLab",
      description: "Silla de alta gama con materiales premium.",
      price: 499990,
      category: "Sillas",
      image: "/images/Sillas/Silla SecretLab.webp",
      stock: 3
    }
  ];

  const handleImport = async () => {
    setIsLoading(true);
    setMessage('');

    try {
      const response = await productsAPI.importProducts(sampleProducts);
      setMessageType('success');
      setMessage(`✅ ${response.data.count} productos importados exitosamente`);
    } catch (error) {
      setMessageType('error');
      setMessage(`❌ Error al importar: ${error.response?.data?.message || error.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  const handleImportCustom = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setIsLoading(true);
    setMessage('');

    try {
      const text = await file.text();
      const products = JSON.parse(text);
      
      if (!Array.isArray(products)) {
        throw new Error('El archivo debe contener un array de productos');
      }

      const response = await productsAPI.importProducts(products);
      setMessageType('success');
      setMessage(`✅ ${response.data.count} productos importados desde archivo`);
    } catch (error) {
      setMessageType('error');
      setMessage(`❌ Error: ${error.message}`);
    } finally {
      setIsLoading(false);
      e.target.value = '';
    }
  };

  return (
    <div className="admin-import-container">
      <h2>Importar Productos al Backend</h2>
      
      {message && (
        <div className={`message message-${messageType}`}>
          {message}
        </div>
      )}

      <div className="import-options">
        <div className="import-section">
          <h3>Opción 1: Importar 5 Productos de Ejemplo</h3>
          <p>Importa un conjunto pequeño de productos de demostración.</p>
          <button
            onClick={handleImport}
            disabled={isLoading}
            className="btn-import"
          >
            {isLoading ? 'Importando...' : 'Importar Ejemplos'}
          </button>
        </div>

        <div className="import-section">
          <h3>Opción 2: Importar 36 Productos de PandaGamers</h3>
          <p>Importa TODOS los 36 productos del dataStore (tienda real).</p>
          <button
            onClick={() => {
              setIsLoading(true);
              setMessage('');
              productsAPI.importProducts(dataStoreProducts)
                .then(response => {
                  setMessageType('success');
                  setMessage(`✅ ${response.data.count} productos importados exitosamente`);
                })
                .catch(error => {
                  setMessageType('error');
                  setMessage(`❌ Error al importar: ${error.response?.data?.message || error.message}`);
                })
                .finally(() => setIsLoading(false));
            }}
            disabled={isLoading}
            className="btn-import"
            style={{ backgroundColor: '#28a745' }}
          >
            {isLoading ? 'Importando...' : 'Importar 36 Productos'}
          </button>
        </div>

        <div className="import-section">
          <h3>Opción 3: Importar desde Archivo JSON</h3>
          <p>Sube un archivo JSON con tus productos personalizados.</p>
          <p className="format-info">
            Formato esperado: <code>[{"{id, name, description, price, category, image, stock}"}]</code>
          </p>
          <input
            type="file"
            accept=".json"
            onChange={handleImportCustom}
            disabled={isLoading}
            className="file-input"
          />
        </div>

        <div className="import-section info">
          <h3>📋 Campos Requeridos</h3>
          <ul>
            <li><strong>id</strong>: Identificador único (string)</li>
            <li><strong>name</strong>: Nombre del producto</li>
            <li><strong>description</strong>: Descripción detallada</li>
            <li><strong>price</strong>: Precio numérico (en CLP)</li>
            <li><strong>category</strong>: Categoría (Consolas, Mouses, Sillas, etc.)</li>
            <li><strong>image</strong>: URL o ruta de la imagen</li>
            <li><strong>stock</strong>: Cantidad disponible</li>
          </ul>
        </div>

        <div className="import-section info warning">
          <h3>⚠️ Nota sobre Duplicados</h3>
          <p>Si un producto con el mismo nombre ya existe en la BD, será actualizado en lugar de duplicado.</p>
          <p><strong>Categorías disponibles:</strong> Accesorios, Consolas, Juegos de mesa, Mouses, Pc Gamers, Poleras, Polerones, Portamouse, Sillas</p>
        </div>
      </div>

      <div className="sample-data">
        <h3>📦 Productos Disponibles para Importar</h3>
        <p><strong>Opción 1:</strong> 5 productos de ejemplo (demostración rápida)</p>
        <p><strong>Opción 2:</strong> 36 productos reales del dataStore de PandaGamers</p>
        <p><strong>Opción 3:</strong> Tu propio JSON personalizado</p>
        <p style={{ marginTop: '15px', fontSize: '0.9em', color: '#666' }}>
          📄 Los 36 productos del dataStore incluyen:
          <ul style={{ marginTop: '8px', marginLeft: '20px' }}>
            <li>4 Accesorios (Auriculares, Control, Mousepad, Teclado)</li>
            <li>4 Consolas (Switch, PS4 Pro, PS5, Xbox Series X)</li>
            <li>4 Juegos de mesa (Carcassonne, Catan, Monopoly, Risk)</li>
            <li>4 Mouses (HyperX, Logitech, Razer, SteelSeries)</li>
            <li>4 PC Gamers (Alienware, ASUS ROG, HP Omen, MSI)</li>
            <li>4 Poleras (Gamer 1, Gamer 2, God of War, Papa Gamer)</li>
            <li>4 Polerones (Gamer 1, Gamer 2, Smash Vintage, Smash Ultimate)</li>
            <li>4 Portamouse (HyperX, Logitech, Razer, SteelSeries)</li>
            <li>4 Sillas (Cougar, DXRacer, GT Omega, SecretLab)</li>
          </ul>
        </p>
      </div>
    </div>
  );
}
