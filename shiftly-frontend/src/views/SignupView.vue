<template>
  <v-container class="d-flex justify-center align-center" style="height: 100vh;">
    <v-row style="max-width: 420px; width: 100%;">
      <v-col cols="20">

        <!-- Signup CARD -->
        <div>
          <v-img src="/logo.png" height="160"></v-img>

          <h1 class="text-h4 font-weight-bold mb-2">Signup</h1>
          <p class="text-body-2 mb-6">
            Create your Account.
          </p>

          <!-- Error-->
          <div>
            {{ error }}
          </div>

          <!-- Email -->
          <v-text-field
            @prevent.default
            v-model="credentials.name"
            label="Username"
            variant="outlined"
            density="comfortable"
            rounded="lg"
            @keyup.enter="handleSignup"
          />

          <!-- Email -->
          <v-text-field
            @prevent.default
            v-model="credentials.email"
            label="Email"
            variant="outlined"
            density="comfortable"
            rounded="lg"
            @keyup.enter="handleSignup"
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
            @keyup.enter="handleSignup"
          />

          <v-text-field
            @prevent.default
            v-model="credentials.confirmPassword"
            label="Confirm Password"
            type="password"
            variant="outlined"
            density="comfortable"
            rounded="lg"
            @keyup.enter="handleSignup"
          />

          <!-- Signup Button -->
          <v-btn
            color="blue-darken-2"
            class="mt-4"
            size="large"
            block
            @click="handleSignup"
            @keyup.enter="handleSignup"
            :loading="loading"
          >
            <v-progress-circular
              v-if="loading"
              indeterminate
              size="20"
              width="2"
              class="mr-2"
            ></v-progress-circular>
            {{ loading ? "Signing up..." : "Sign up"}}
          </v-btn>
        </div>

        <!-- SIGN UP BOX -->
        <v-card elevation="0" class="pa-6 mt-6 rounded-lg text-center">
          <h2 class="text-h5 font-weight-bold mb-4">Log in</h2>

          <v-btn
            color="blue-lighten-4"
            class="text-blue-darken-3"
            block
            size="large"
            variant="tonal"
            @click="goToLogin"
          >
            Log in
          </v-btn>
        </v-card>

      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/Auth'
import type { Signup } from '@/types/Auth'
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const credentials = reactive<Signup>({
  name: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)
const error = ref('')

const handleSignup = async (): Promise<void> => {
  loading.value = true
  error.value = ''

  try {
    const res = await authStore.signUp(credentials)

    if (!res.success) {
      console.log("error:", res.error)
      error.value = res.error || ""
    } else {
      const redirect = route.query.redirect as string || '/login'
      router.push(redirect)
    }
  } catch (err) {
    console.log(err)
    error.value = 'An error occurred during signup'
  } finally {
    loading.value = false
  }
}

const goToLogin = (): void => {
  router.push('/login')
}
</script>

<style scoped>
</style>
