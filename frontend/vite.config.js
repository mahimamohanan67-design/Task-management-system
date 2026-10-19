
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  // This repository is published at /Task-management-system/ on GitHub Pages.
  // Keep the root path for local development.
  base: process.env.NODE_ENV === 'production' ? '/Task-management-system/' : '/',
  plugins: [react()],
});
