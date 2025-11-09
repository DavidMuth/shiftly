<template>
  <v-navigation-drawer
    expand-on-hover
    permanent
    rail
    app
    color="primary"
  >
    <!-- User Info -->
    <v-list>
      <v-list-item
        prepend-avatar="https://randomuser.me/api/portraits/women/85.jpg"
        subtitle="sandra_a88@gmail.com"
        title="Sandra Adams"
      ></v-list-item>
    </v-list>

    <v-divider></v-divider>

    <!-- Navigation -->
    <v-list density="compact" nav>
      <v-list-item prepend-icon="mdi-home" title="Dashboard" :to="{ name: 'dashboard' }"></v-list-item>
      <v-list-item prepend-icon="mdi-home" title="Time Tracker" :to="{ name: 'time-tracker' }"></v-list-item>
      <v-list-item prepend-icon="mdi-home" title="Settings" :to="{ name: 'settings' }"></v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
// Keine zusätzliche Logik nötig
</script>
