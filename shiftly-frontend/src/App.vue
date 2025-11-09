<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
// @ts-ignore
import NavBar from './components/NavBar.vue';
import { useTheme } from 'vuetify'

const theme = useTheme()

function toggleTheme() {
  // Switch between 'light' and 'dark'
  if (theme.name.value === 'dark')
    theme.change('light')
  else{theme.change('dark')}

}
</script>

<template>
  <v-app>
    <!-- Navbar / AppBar -->
    <v-app-bar app color="primary" dark>
      <v-toolbar-title>Meine App</v-toolbar-title>
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
      <v-container class="py-10">
        <h1>Willkommen!</h1>
        <p>Hier kommt der Hauptinhalt.</p>
      </v-container>
    </v-main>

    <!-- Footer -->
    <v-footer app padless>
      <v-col class="text-center" cols="12">
        &copy; 2025 Meine App
      </v-col>
    </v-footer>
  </v-app>
</template>

