<template>
  <v-container class="d-flex justify-center align-center" style="height: 100vh;">
    <v-row style="max-width: 420px; width: 100%;">
      <v-col cols="12">

        <!-- LOGIN CARD -->
        <v-card elevation="3" class="pa-6 rounded-lg">
          <h1 class="text-h4 font-weight-bold mb-2">Login</h1>
          <p class="text-body-2 mb-6">
            Login with the data you entered during your registration.
          </p>

          <!-- Username -->
          <v-text-field
            v-model="username"
            label="Username"
            variant="outlined"
            density="comfortable"
            rounded="lg"
          />

          <!-- Password -->
          <v-text-field
            v-model="password"
            label="Password"
            type="password"
            variant="outlined"
            density="comfortable"
            rounded="lg"
          />

          <!-- Login Button -->
          <v-btn
            color="blue-darken-3"
            class="mt-4"
            size="large"
            block
            @click="handleLogin"
          >
            Log in
          </v-btn>
        </v-card>

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


const username = ref('')
const password = ref('')

const credentials = reactive<LoginCredentials>({
  username: '',
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
      const redirect = route.query.redirect as string || '/dashboard'
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

const goToSignup = (): void => {
  router.push('/signup')
}
</script>

<style scoped>
</style>
