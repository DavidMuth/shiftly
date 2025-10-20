<template>
    <!-- Toggle between Light / Dark -->
    <v-btn
        @click="theme.toggle()"
        text="Toggle Light / Dark"
    ></v-btn>
</template>

<script setup lang="ts">
    import { useTheme } from 'vuetify'

    const theme = useTheme()
</script>
