<template>
  <v-navigation-drawer
    expand-on-hover
    permanent
    rail
    app
    color="primary"
  >
    <v-list>
      <v-list-item
        :subtitle="user?.email || ''"
        :title="cleanName"
      >
        <template v-slot:prepend>
          <v-avatar color="secondary">
            <span style="font-size: large;">
              {{ initials }}
            </span>
          </v-avatar>
        </template>
      </v-list-item>
    </v-list>
    <v-divider></v-divider>
    <v-list density="compact" nav>
      <v-list-item link prepend-icon="mdi-home" title="Dashboard" :to="{ name: 'dashboard' }"></v-list-item>
      <v-list-item link prepend-icon="mdi-clock-outline" title="Time Tracker" :to="{ name: 'time-tracker' }"></v-list-item>
      <v-list-item link prepend-icon="mdi-calendar-multiselect" title="Calendar" :to="{ name: 'calendar' }"></v-list-item>
      <v-list-item link prepend-icon="mdi-cog-outline" title="Settings" :to="{ name: 'settings' }"></v-list-item>
    </v-list>
    
    <template v-slot:append>
      <v-list density="compact" nav>
        <v-list-item
          link
          prepend-icon="mdi-logout"
          title="Logout"
          @click="handleLogout"
        ></v-list-item>
      </v-list>
    </template>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useUserStore } from '@/stores/User'
import { useAuthStore } from '@/stores/Auth'
import { useRouter } from 'vuetify/lib/composables/router.mjs'

const loading = ref(true)
const error = ref('')

const router = useRouter()
const authStore = useAuthStore()
const userStore = useUserStore()
const user = computed(() => userStore.getUser)

// Name ohne Ziffern am Ende, z. B. "John Doe5" -> "John Doe"
const cleanName = computed(() => {
  if (!user.value?.name) return ''
  return user.value.name.replace(/[0-9]+$/, '')
})

// Initialen erzeugen, z. B. "John Doe" -> "JD"
const initials = computed(() => {
  if (!cleanName.value) return ''
  return cleanName.value
    .split(' ')
    .map(n => n[0]?.toUpperCase())
    .join('')
})

const handleLogout = async (): Promise<void> => {
  loading.value = true
  error.value = ''
  await authStore.logout()

  router!.push("/login")
}
</script>
