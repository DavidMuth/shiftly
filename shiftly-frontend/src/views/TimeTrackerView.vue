<template>
  <v-app>
    <v-main>
      <v-container fluid class="fill-height pa-0">
        <v-row no-gutters class="fill-height">
          <!-- Main Content Area -->
          <v-col cols="12" class="d-flex flex-column align-center justify-center pa-0">
            <!-- Tracking Circle Container -->
            <div class="tracking-container">
              <!-- Animated Circle Border -->
              <svg class="progress-ring" width="900" height="900" viewBox="0 0 900 900">
                <circle
                  class="progress-ring-circle-bg"
                  :stroke="theme.current.value.dark ? '#424242' : '#E8EAF6'"
                  stroke-width="28"
                  fill="transparent"
                  r="420"
                  cx="450"
                  cy="450"
                />
                <circle
                  v-if="isTracking"
                  class="progress-ring-circle"
                  :stroke="isBreak ? '#FF6F00' : '#5C6BC0'"
                  stroke-width="28"
                  fill="transparent"
                  r="420"
                  cx="450"
                  cy="450"
                  :style="{ animationDuration: `${animationSpeed}s` }"
                />
              </svg>

              <!-- Not Tracking State -->
              <v-card
                v-if="!isTracking"
                class="tracking-content elevation-8"
                rounded="xl"
                width="400"
                height="500"
              >
                <v-card-text class="d-flex flex-column align-center justify-center fill-height pa-10">
                  <h2 class="text-h4 font-weight-bold mb-8" :class="theme.current.value.dark ? 'text-indigo-lighten-2' : 'text-indigo-darken-1'">
                    Start Tracking
                  </h2>

                  <!-- Break Toggle -->
                  <div class="toggle-container mb-6">
                    <v-icon :color="isBreak ? 'orange-darken-2' : 'grey'" class="mr-2">
                      mdi-coffee
                    </v-icon>
                    <span class="text-body-1 font-weight-medium mr-4">Break</span>
                    <v-switch
                      v-model="isBreak"
                      :color="isBreak ? 'orange-darken-2' : 'indigo'"
                      hide-details
                      inset
                      density="compact"
                    ></v-switch>
                  </div>

                  <!-- Event Name Input -->
                  <v-text-field
                    v-model="eventName"
                    label="Event Name"
                    placeholder="My Event Title"
                    variant="outlined"
                    density="comfortable"
                    rounded="lg"
                    class="mb-4"
                    style="max-width: 360px; min-width: 300px;"
                    hide-details
                    clearable
                  >
                    <template v-slot:prepend-inner>
                      <v-icon size="small" color="indigo">mdi-text</v-icon>
                    </template>
                  </v-text-field>

                  <!-- Event Description Input -->
                  <v-textarea
                    v-model="eventDescription"
                    label="Event Description"
                    placeholder="Some Event to track's Description"
                    variant="outlined"
                    density="comfortable"
                    rounded="lg"
                    rows="2"
                    style="max-width: 360px; min-width: 300px;"
                    hide-details
                    clearable
                  >
                    <template v-slot:prepend-inner>
                      <v-icon size="small" color="indigo">mdi-text-box</v-icon>
                    </template>
                  </v-textarea>

                  <!-- Start Button -->
                  <v-btn
                    :color="isBreak ? 'orange-darken-2' : 'indigo'"
                    size="x-large"
                    rounded="xl"
                    class="mt-8 px-12"
                    elevation="4"
                    @click="startTracking"
                    :disabled="!eventName"
                  >
                    <v-icon start size="large">mdi-play-circle</v-icon>
                    <span class="text-h6">Start Tracking</span>
                  </v-btn>
                </v-card-text>
              </v-card>

              <!-- Active Tracking State -->
              <v-card
                v-else
                class="tracking-content-active elevation-12"
                rounded="xl"
                width="400"
                height="500"
                :color="isBreak ? 'orange-darken-2' : 'indigo'"
              >
                <v-card-text class="d-flex flex-column align-center justify-center fill-height pa-10">
                  <!-- Break Badge -->
                  <v-chip
                    v-if="currentIsBreak"
                    color="orange-lighten-4"
                    size="small"
                    class="mb-4"
                  >
                    <v-icon start size="small">mdi-coffee</v-icon>
                    Break Time
                  </v-chip>

                  <!-- Event Name -->
                  <h2 class="text-h4 text-white font-weight-bold mb-2 text-center px-4">
                    {{ currentEventName }}
                  </h2>

                  <!-- Event Description -->
                  <p 
                    v-if="currentEventDescription" 
                    class="text-body-1 text-white opacity-90 text-center mb-8 px-6"
                    style="max-width: 380px;"
                  >
                    {{ currentEventDescription }}
                  </p>

                  <!-- Elapsed Time Display -->
                  <div class="time-display mb-8">
                    <p class="text-subtitle-1 text-white opacity-75 font-weight-medium mb-2 text-center">
                      Elapsed Time
                    </p>
                    <div class="d-flex align-center justify-center gap-2">
                      <div class="time-unit">
                        <span class="time-value">{{ hours }}</span>
                        <span class="time-label">hours</span>
                      </div>
                      <span class="time-separator">:</span>
                      <div class="time-unit">
                        <span class="time-value">{{ minutes }}</span>
                        <span class="time-label">min</span>
                      </div>
                      <span class="time-separator">:</span>
                      <div class="time-unit">
                        <span class="time-value">{{ seconds }}</span>
                        <span class="time-label">sec</span>
                      </div>
                    </div>
                  </div>

                  <!-- Stop Button -->
                  <v-btn
                    color="white"
                    size="x-large"
                    rounded="xl"
                    class="px-16 py-3"
                    elevation="0"
                    variant="elevated"
                    @click="stopTracking"
                  >
                    <v-icon :color="isBreak ? 'orange-darken-2' : 'indigo'" start size="large">
                      mdi-stop-circle
                    </v-icon>
                    <span class="text-h6 font-weight-bold" :class="isBreak ? 'text-orange-darken-2' : 'text-indigo'">
                      Stop Tracking
                    </span>
                  </v-btn>
                </v-card-text>
              </v-card>
            </div>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import { useTheme } from 'vuetify'

const theme = useTheme()

// State
const isTracking = ref(false)
const isBreak = ref(false)
const eventName = ref('')
const eventDescription = ref('')
const currentEventName = ref('')
const currentEventDescription = ref('')
const currentIsBreak = ref(false)

// Timer state
const elapsedSeconds = ref(0)
const animationSpeed = ref(60) // speed of the circle -> one minute
const startTime = ref(null)
let timerInterval = null

// Computed time values
const hours = computed(() => String(Math.floor(elapsedSeconds.value / 3600)).padStart(2, '0'))
const minutes = computed(() => String(Math.floor((elapsedSeconds.value % 3600) / 60)).padStart(2, '0'))
const seconds = computed(() => String(elapsedSeconds.value % 60).padStart(2, '0'))

// Timer functions
const updateTimer = () => {
  if (startTime.value) {
    const now = Date.now()
    const elapsed = Math.floor((now - startTime.value) / 1000)
    elapsedSeconds.value = elapsed
  }
}

const startTimer = () => {
  startTime.value = Date.now()
  timerInterval = setInterval(updateTimer, 1000)
}

const stopTimer = () => {
  if (timerInterval) {
    clearInterval(timerInterval)
    timerInterval = null
  }
  startTime.value = null
}

// Methods
const startTracking = () => {
  if (!eventName.value) return
  
  currentEventName.value = eventName.value
  currentEventDescription.value = eventDescription.value
  currentIsBreak.value = isBreak.value
  isTracking.value = true
  elapsedSeconds.value = 0
  
  // Start the frontend timer
  startTimer()
  
  // TODO: Add API call to backend
  // const payload = {
  //   name: eventName.value,
  //   description: eventDescription.value,
  //   startTimestamp: Date.now(),
  //   isBreak: isBreak.value
  // }
  // await api.post('/time-track/start', payload)
}

const stopTracking = () => {
  // Stop the frontend timer
  stopTimer()
  
  // TODO: Add API call to backend
  // const payload = {
  //   eventId: currentEventId,
  //   endTimestamp: Date.now()
  // }
  // await api.put('/time-track/stop', payload)
  
  // Reset state
  isTracking.value = false
  eventName.value = ''
  eventDescription.value = ''
  isBreak.value = false
  elapsedSeconds.value = 0
}

// Cleanup on component unmount
onUnmounted(() => {
  stopTimer()
})
</script>

<style scoped>
.tracking-container {
  position: relative;
  width: 900px;
  height: 900px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.progress-ring {
  position: absolute;
  top: 0;
  left: 0;
  transform: rotate(-90deg);
  width: 100%;
  height: 100%;
}

.progress-ring-circle-bg {
  stroke-dasharray: 2639;
  stroke-dashoffset: 0;
}

.progress-ring-circle {
  stroke-dasharray: 2639;
  stroke-dashoffset: 2639;
  animation: progress infinite linear;
  stroke-linecap: round;
  filter: drop-shadow(0 0 12px currentColor);
}

@keyframes progress {
  from {
    stroke-dashoffset: 2639;
  }
  to {
    stroke-dashoffset: 0;
  }
}

.tracking-content,
.tracking-content-active {
  position: relative;
  z-index: 1;
}

.tracking-content-active {
  background: linear-gradient(135deg, #5C6BC0 0%, #3949AB 100%);
  animation: gentle-pulse 3s ease-in-out infinite;
}

@keyframes gentle-pulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 8px 32px rgba(63, 81, 181, 0.3);
  }
  50% {
    transform: scale(1.01);
    box-shadow: 0 12px 48px rgba(63, 81, 181, 0.4);
  }
}

.toggle-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 20px;
  background: rgba(92, 107, 192, 0.1);
  border-radius: 12px;
  min-width: 200px;
}

.time-display {
  background: rgba(255, 255, 255, 0.15);
  padding: 20px 32px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

.time-unit {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.time-value {
  font-size: 2.5rem;
  font-weight: 700;
  color: white;
  line-height: 1;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.time-label {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
  margin-top: 4px;
}

.time-separator {
  font-size: 2rem;
  color: white;
  font-weight: 700;
  margin: 0 8px;
  opacity: 0.7;
}

.fill-height {
  height: 100%;
}

.opacity-90 {
  opacity: 0.9;
}

.opacity-75 {
  opacity: 0.75;
}
</style>