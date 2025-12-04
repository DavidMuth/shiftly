<template>
  <v-container class="d-flex justify-center align-center" style="height: 100vh;">
    <v-row style="max-width: 420px; width: 100%;">
      <v-col cols="12">

        <!-- LOGIN CARD -->
        <div>

          <v-img src="/logo.png" height="160"></v-img>

          <h1 class="text-h4 font-weight-bold mb-2">Login</h1>
          <p class="text-body-2 mb-6">
            Login with the data you entered during your registration.
          </p>

          <!-- Email -->
          <v-text-field
            @prevent.default
            v-model="credentials.email"
            label="Email"
            variant="outlined"
            density="comfortable"
            rounded="lg"
            @keyup.enter="handleLogin"

          />

          <!-- Password -->
          <v-text-field
            @prevent.default
            v-model="credentials.password"
            label="Password"
            type="password"
            variant="outlined"
            density="comfortable"
            rounded="lg"
            @keyup.enter="handleLogin"
          />

          <!-- Login Button -->
          <v-btn
            color="blue-darken-2"
            class="mt-4"
            size="large"
            block
            @click="handleLogin"
            @keyup.enter="handleLogin"
            :loading="loading"
          >
            <v-progress-circular
              v-if="loading"
              indeterminate
              size="20"
              width="2"
              class="mr-2"
            ></v-progress-circular>
            {{ loading ? "Logging in..." : "Log in"}}
          </v-btn>
        </div>

        <!-- SIGN UP BOX -->
        <v-card elevation="0" class="pa-6 mt-6 rounded-lg text-center">
          <h2 class="text-h5 font-weight-bold mb-4">Sign up</h2>

          <v-btn
            color="blue-lighten-4"
            class="text-blue-darken-3"
            block
            size="large"
            variant="tonal"
            @click="goToSignup"
          >
            Create account
          </v-btn>
        </v-card>

      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/Auth'
import type { LoginCredentials } from '@/types/Auth'
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const credentials = reactive<LoginCredentials>({
  email: '',
  password: ''
})

const loading = ref(false)
const error = ref('')

const handleLogin = async (): Promise<void> => {
  loading.value = true
  error.value = ''

  try {
    const success = await authStore.login(credentials)

    if (success) {
      const redirect = route.query.redirect as string || '/'
      router.push(redirect)
    } else {
      error.value = 'Invalid username or password'
    }
  } catch (err) {
    console.log(err)
    error.value = 'An error occurred during login'
  } finally {
    loading.value = false
  }
}

const handleLogout = async (): Promise<void> => {
  loading.value = true
  error.value = ''
  await authStore.logout()

  router.push("/login")
}

const goToSignup = (): void => {
  router.push('/signup')
}
</script>

<style scoped>
</style>
