<script setup lang="ts">
import { RouterView, useRoute, useRouter } from 'vue-router'
import NavBar from './components/NavBar.vue'
import { useTheme } from 'vuetify'
import logo from '@/assets/logo.svg'
import { onMounted, ref, computed } from 'vue'
import { useUserStore } from '@/stores/User'
import { useAuthStore } from '@/stores/Auth'

const router = useRouter()
const authStore = useAuthStore()
const userStore = useUserStore()
const error = ref('')
const theme = useTheme()
const route = useRoute()
const loading = ref(true)

// HIDE LAYOUT ON LOGIN ROUTE
const hideLayout = computed(() => route.path === "/login")

function toggleTheme() {
  if (theme.name.value === 'dark') theme.change('light')
  else theme.change('dark')
}


const handleLogout = async (): Promise<void> => {
  loading.value = true
  error.value = ''
  await authStore.logout()

  router.push("/login")
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
      <v-spacer></v-spacer>


      <v-btn @click="toggleTheme">
        Toggle {{ theme.name.value === 'light' ? 'Dark' : 'Light' }} Mode
      </v-btn>
      <v-btn
        color="white bg-red"
        @click="handleLogout"
      >
        Logout
      </v-btn>


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
