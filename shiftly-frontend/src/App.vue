<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
// @ts-ignore
import ThemeSwitch from './components/ThemeSwitch.vue';
</script>

<template>
  <v-app style="">
    <header>
      <div class="wrapper">
        <nav>
          <RouterLink to="/">Home</RouterLink>
          <RouterLink to="/about">About</RouterLink>
          <ThemeSwitch />
        </nav>
      </div>
    </header>

    <RouterView />
    </v-app>
</template>
