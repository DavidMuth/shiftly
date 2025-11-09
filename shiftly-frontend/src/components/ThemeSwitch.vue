<template>
  <v-container class="py-10 text-center">
    <v-btn
      color="primary"
      @click="toggleTheme"
    >
      Toggle {{ theme.name.value === 'light' ? 'Dark' : 'Light' }} Mode
    </v-btn>
  </v-container>
</template>

<script setup lang="ts">
import { useTheme } from 'vuetify'

const theme = useTheme()

function toggleTheme() {
  // Switch between 'light' and 'dark'
  if (theme.name.value === 'dark')
    theme.change('light')
  else{theme.change('dark')}

}
</script>
