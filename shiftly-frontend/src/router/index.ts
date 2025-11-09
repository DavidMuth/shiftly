import DashboardView from '@/views/DashboardView.vue'
import SettingsView from '@/views/SettingsView.vue'
import TimeTrackerView from '@/views/TimeTrackerView.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: DashboardView,
    },
    {
      path: '/time-tracker',
      name: 'time-tracker',
      component: TimeTrackerView,  
    },
    {
      path: '/settings',
      name: 'settings',
      component: SettingsView,  
    },
  ],
})

export default router
