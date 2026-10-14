import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Dev proxy: every API prefix is forwarded to the monolith backend on :8080.
// Switching to the microservices backend = change these targets (see frontend/README).
const API_PREFIXES = [
  '/order',
  '/orders',
  '/product',
  '/products',
  '/category',
  '/categories',
  '/inventory',
  '/payment',
  '/delivery',
];

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: Object.fromEntries(
      API_PREFIXES.map((p) => [p, { target: 'http://localhost:8080', changeOrigin: true }]),
    ),
  },
});
