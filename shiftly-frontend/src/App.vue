<script setup lang="ts">
import { RouterView, useRoute, useRouter } from 'vue-router'
import NavBar from './components/NavBar.vue'
import { useTheme } from 'vuetify'
import logo from '@/assets/logo.svg'
import { onMounted, ref, computed } from 'vue'
import { useUserStore } from '@/stores/User'

const userStore = useUserStore()
const theme = useTheme()
const route = useRoute()
const loading = ref(true)

// HIDE LAYOUT ON LOGIN or SIGNUP route
const hideLayout = computed(() => route.path === "/login" || route.path === "/signup")

function toggleTheme() {
  if (theme.name.value === 'dark') theme.change('light')
  else theme.change('dark')
}

onMounted(async () => {
  try {
    await userStore.getCurrentUser()
    console.log("Aktueller User:", userStore.getUser)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <v-app>

    <!-- APP BAR -> deaktiviert bei Login -->
    <v-app-bar v-if="!hideLayout" app dark>
      <v-img :src="logo" :max-width="150" :max-height="400" />
      <h3>
          Hello <span class="text-primary">{{ userStore.getUser?.name }}</span>, let us track your day!
      </h3>
      <v-spacer></v-spacer>

      <v-btn @click="toggleTheme" :icon="theme.name.value === 'light' ? 'mdi-weather-night' : 'mdi-weather-sunny'"></v-btn>

    </v-app-bar>

    <!-- SIDEBAR -> deaktiviert bei Login -->
    <NavBar v-if="!hideLayout" />

    <!-- MAIN -->
    <v-main>
      <v-container>
        <RouterView />
      </v-container>
    </v-main>

  </v-app>
</template>
