import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'

// devtools nur bei Bedarf importieren
const plugins = [vue(), vueJsx()]

if (process.env.NODE_ENV === 'development') {
  try {
    const vueDevTools = require('vite-plugin-vue-devtools').default
    plugins.push(vueDevTools())
  } catch {
    console.warn('⚠️ vite-plugin-vue-devtools konnte nicht geladen werden')
  }
}

export default defineConfig({
  plugins,
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
