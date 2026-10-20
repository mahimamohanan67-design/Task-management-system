
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  // GitHub Pages serves this repository from a subpath; other hosts use the domain root.
  base: process.env.GITHUB_ACTIONS === 'true' ? '/Task-management-system/' : '/',
  plugins: [react()],
});
