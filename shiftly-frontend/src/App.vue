<script setup lang="ts">
import { RouterView } from 'vue-router'
import NavBar from './components/NavBar.vue';
import { useTheme } from 'vuetify'
import logo from '@/assets/logo.svg'
import { onMounted, ref } from 'vue';
import UserService from './services/UserService';
import type { User } from './types/User';

const theme = useTheme()

const users = ref<User>();
const loading = ref(true);

function toggleTheme() {
  // Switch between 'light' and 'dark'
  if (theme.name.value === 'dark')
    theme.change('light')
  else{theme.change('dark')}

}

onMounted(async () => {
  try {
    const { data } = await UserService.get(6)
    console.log(data)
  } finally {
    loading.value = false;
  }
})
</script>

<template>
  <v-app>
    <!-- Navbar / AppBar -->
    <v-app-bar app dark>
      <v-img
        :src="logo"
        :max-width="150"
        :max-height="400"
      ></v-img>

      <v-spacer></v-spacer>

       <v-btn
      @click="toggleTheme"
    >
      Toggle {{ theme.name.value === 'light' ? 'Dark' : 'Light' }} Mode
    </v-btn>
    </v-app-bar>

    <!-- Sidebar -->
    <NavBar />

    <!-- Hauptinhalt -->
    <v-main>
      <v-container>
        <RouterView />
      </v-container>
    </v-main>
  </v-app>
</template>

