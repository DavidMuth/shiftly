<template>
  <v-container class="d-flex justify-center align-center" style="min-height: 100vh;">
    <v-card style="max-width: 540px; width: 100%;" elevation="2" rounded="xl">
      <v-card-text class="pa-8">
        <h1 class="text-h4 font-weight-bold mb-8">Settings</h1>
        
        <!-- Default Working Hours Section -->
        <v-form @submit.prevent="handleChangeWorkingHours">
          <div class="mb-6">
            <h2 class="text-h6 font-weight-semibold mb-4">Default Working Hours</h2>
            <v-text-field
              v-model="workingHours.workingHours"
              label="Working hours per day"
              type="number"
              variant="outlined"
              density="comfortable"
              rounded="lg"
              :disabled="loading"
              hint="Set your default working hours"
              persistent-hint
            />
          </div>
          
          <v-btn
            type="submit"
            color="primary"
            size="large"
            block
            rounded="lg"
            :loading="loading"
            :disabled="loading"
          >
            Save Working Hours
          </v-btn>
        </v-form>

        <v-divider class="my-8"></v-divider>

        <!-- Reset Password Section -->
        <v-form ref="passwordForm" @submit.prevent="handlePasswordReset">
          <div class="mb-6">
            <h2 class="text-h6 font-weight-semibold mb-4">Reset Password</h2>
            
            <v-text-field
              v-model="passwordReset.currentPassword"
              label="Current Password"
              :type="showCurrentPassword ? 'text' : 'password'"
              variant="outlined"
              density="comfortable"
              rounded="lg"
              class="mb-3"
              :disabled="loadingPassword"
              :append-inner-icon="showCurrentPassword ? 'mdi-eye-off' : 'mdi-eye'"
              @click:append-inner="showCurrentPassword = !showCurrentPassword"
            />
            
            <v-text-field
              v-model="passwordReset.newPassword"
              label="New Password"
              :type="showNewPassword ? 'text' : 'password'"
              variant="outlined"
              density="comfortable"
              rounded="lg"
              class="mb-3"
              :disabled="loadingPassword"
              :append-inner-icon="showNewPassword ? 'mdi-eye-off' : 'mdi-eye'"
              @click:append-inner="showNewPassword = !showNewPassword"
            />
            
            <v-text-field
              v-model="passwordReset.confirmNewPassword"
              label="Confirm New Password"
              :type="showConfirmPassword ? 'text' : 'password'"
              variant="outlined"
              density="comfortable"
              rounded="lg"
              :disabled="loadingPassword"
              :append-inner-icon="showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'"
              @click:append-inner="showConfirmPassword = !showConfirmPassword"
              :error-messages="passwordMatchError"
            />
          </div>
          
          <v-btn
            type="submit"
            color="primary"
            size="large"
            block
            rounded="lg"
            :loading="loadingPassword"
            :disabled="loadingPassword || !isPasswordValid"
          >
            Reset Password
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/User'
import { onMounted, ref, computed, reactive, watch } from 'vue'

const userStore = useUserStore();
const loading = ref(false)
const loadingPassword = ref(false)
const currentUser = computed(() => userStore.getUser)

// Password visibility toggles
const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

// Form ref for resetting
const passwordForm = ref<any>(null)

const workingHours = reactive({
  workingHours: 0
})

const passwordReset = reactive({
  currentPassword: '',
  newPassword: '',
  confirmNewPassword: ''
})

// Password validation
const passwordMatchError = computed(() => {
  if (passwordReset.confirmNewPassword && passwordReset.newPassword !== passwordReset.confirmNewPassword) {
    return 'Passwords do not match'
  }
  return ''
})

const isPasswordValid = computed(() => {
  return passwordReset.currentPassword && 
         passwordReset.newPassword && 
         passwordReset.confirmNewPassword &&
         passwordReset.newPassword === passwordReset.confirmNewPassword
})

onMounted(async () => {
  loading.value = true
  try {
    await userStore.getCurrentUser()
  } finally {
    loading.value = false
  }
})

watch(
  currentUser,
  (user) => {
    if (user) {
      workingHours.workingHours = user.workingHours
    }
  },
  { immediate: true }
)

const handleChangeWorkingHours = async () => {
  loading.value = true
  try {
    await userStore.updateWorkingHours(workingHours.workingHours)
  } finally {
    loading.value = false
  }
}

const handlePasswordReset = async () => {
  loadingPassword.value = true
  try {
    await userStore.updateUserPassword(passwordReset)
    
    // Clear password fields after successful reset
    passwordReset.currentPassword = ''
    passwordReset.newPassword = ''
    passwordReset.confirmNewPassword = ''
    
    // Reset password visibility toggles
    showCurrentPassword.value = false
    showNewPassword.value = false
    showConfirmPassword.value = false
    
    // Reset form validation state
    if (passwordForm.value) {
      passwordForm.value.reset()
    }
  } finally {
    loadingPassword.value = false
  }
}
</script>