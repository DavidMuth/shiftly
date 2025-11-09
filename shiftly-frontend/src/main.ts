import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'
import { aliases, mdi } from 'vuetify/iconsets/mdi'

import App from './App.vue'
import router from './router'

const vuetify = createVuetify({
    components,
    directives,
    icons: {
    defaultSet: 'mdi',
    aliases,
    sets: { mdi },
  },
    theme: {
      defaultTheme: 'light',
    themes: {
      light: {
         colors: {
        primary: '#3B82F6', // Bright blue accent (buttons, highlights)
        secondary: '#64748B', // Muted blue-gray for UI elements
        accent: '#2563EB', // Stronger blue for active states
        background: '#F9FAFB', // App background
        surface: '#FFFFFF', // Cards, panels
        info: '#0EA5E9', // Info messages or calendar highlights
        success: '#10B981', // Success (e.g. positive status)
        warning: '#F59E0B', // Warning elements
        error: '#EF4444', // Error highlights
        textPrimary: '#1E293B', // Main text (dark slate)
        textSecondary: '#475569', // Subtext (grayish)
        border: '#E2E8F0', // Borders and dividers
      },
      },
      dark: {
        colors: {
        primary: '#60A5FA',
        secondary: '#94A3B8',
        accent: '#3B82F6',
        background: '#0F172A',
        surface: '#1E293B',
        info: '#38BDF8',
        success: '#34D399',
        warning: '#FBBF24',
        error: '#F87171',
        textPrimary: '#F1F5F9',
        textSecondary: '#CBD5E1',
        border: '#334155',
      },
      },
    },
    }
})

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(vuetify)

app.mount('#app')
