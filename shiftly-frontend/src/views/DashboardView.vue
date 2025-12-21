<template>
  <v-app>
    <v-main >
      <v-container fluid class="pa-8">
        <!-- Header Section -->
        <v-row class="mb-6">
          <v-col cols="12" md="6">
            <h1 class="text-h4 font-weight-bold">Dashboard</h1>
          </v-col>
          <v-col cols="12" md="6" class="d-flex justify-end align-center ga-4">
            <v-btn
              color="primary"
              variant="elevated"
              prepend-icon="mdi-file-pdf-box"
              size="large"
            >
              Export PDF
            </v-btn>
            
            <v-menu
              v-model="menu"
              :close-on-content-click="false"
              transition="scale-transition"
              offset-y
              min-width="290px"
            >
              <template #activator="{ props }">
                <v-btn
                  variant="outlined"
                  size="large"
                  v-bind="props"
                  append-icon="mdi-chevron-down"
                >
                  {{ weekRange }}
                </v-btn>
              </template>
              <v-date-picker
                v-model="selectedDate"
                :first-day-of-week="1"
                @update:model-value="onDateSelect"
                color="primary"
              ></v-date-picker>
            </v-menu>
          </v-col>
        </v-row>

        <!-- Time Tracking Card -->
        <v-card class="mb-6" elevation="2" rounded="lg">
          <v-card-title class="text-h6 font-weight-bold pa-6 pb-4">
            Time Tracking
          </v-card-title>
          
          <v-card-text class="pa-6 pt-0">
            <!-- Stats Row -->
            <v-row class="mb-6">
              <v-col cols="12" sm="6">
                <div class="text-body-2 text-grey-darken-1 mb-1">Working Hours</div>
                <div class="text-h4 font-weight-bold">{{ totalWorkingHours }}h</div>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="text-body-2 text-grey-darken-1 mb-1">Break Time</div>
                <div class="text-h4 font-weight-bold">{{ totalBreakTime }}h</div>
              </v-col>
            </v-row>

            <!-- Chart -->
            <div v-if="chartData && chartData.length > 0" class="chart-container">
              <Chart
                :size="{ width: chartWidth, height: 420 }"
                :data="chartData"
                :margin="margin"
                :direction="direction"
                :axis="axis"
              >
                <template #layers>
                  <Grid strokeDasharray="2,2" />
                  <Bar :dataKeys="['name', 'workingHours']" :barStyle="{ fill: '#5A6ACF' }" />
                  <Bar :dataKeys="['name', 'breakTime']" :barStyle="{ fill: '#E6E8EC' }" />
                </template>
                <template #widgets>
                  <Tooltip
                    borderColor="#5A6ACF"
                    :config="{
                      workingHours: { color: '#5A6ACF', label: 'Working Hours' },
                      breakTime: { color: '#E6E8EC', label: 'Break Time' }
                    }"
                  />
                </template>
              </Chart>
              
              <!-- Legend -->
              <div class="d-flex justify-center ga-6 mt-4">
                <div class="d-flex align-center ga-2">
                  <div class="legend-dot" style="background-color: #5A6ACF;"></div>
                  <span class="text-body-2">Working Hours</span>
                </div>
                <div class="d-flex align-center ga-2">
                  <div class="legend-dot" style="background-color: #E6E8EC;"></div>
                  <span class="text-body-2">Break Time</span>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-12">
              <v-progress-circular
                indeterminate
                color="primary"
                size="64"
              ></v-progress-circular>
              <p class="text-body-1 mt-4 text-grey-darken-1">Loading chart data...</p>
            </div>
          </v-card-text>
        </v-card>

        <!-- Working Hours Balance Card -->
        <v-card class="mb-6" elevation="2" rounded="lg">
          <v-card-title class="text-h6 font-weight-bold pa-6 pb-4">
            Time Credit
          </v-card-title>
          
          <v-card-text class="pa-6 pt-0">
            <div class="text-body-2 text-grey-darken-1 mb-2">{{ weekRangeDisplay }}</div>
            
            <!-- Balance Chart -->
            <div class="balance-bar-container">
              <!-- Scale markers -->
              <div class="scale-markers">
                <span 
                  v-for="mark in scaleMarkers" 
                  :key="mark"
                  class="scale-mark"
                  :style="{ left: getScalePosition(mark) + '%' }"
                >
                  {{ mark > 0 ? '+' : '' }}{{ mark }}
                </span>
              </div>
              
              <div class="balance-track">
                <!-- Zero line -->
                <div class="zero-line"></div>
                
                <!-- Balance bar -->
                <div 
                  class="balance-bar"
                  :class="totalBalance >= 0 ? 'positive' : 'negative'"
                  :style="getBalanceBarStyle"
                >
                  <span class="balance-value">
                    {{ totalBalance >= 0 ? '+' : '' }}{{ Math.abs(totalBalance).toFixed(1) }}h
                  </span>
                </div>
              </div>
            </div>
            
            <!-- Stats Row -->
            <v-row class="mt-8">
              <v-col cols="12" sm="4">
                <div class="text-body-2 text-grey-darken-1 mb-1">Target Hours</div>
                <div class="text-h6 font-weight-bold">{{ user?.workingHours }}h</div>
              </v-col>
              <v-col cols="12" sm="4">
                <div class="text-body-2 text-grey-darken-1 mb-1">Actual Hours</div>
                <div class="text-h6 font-weight-bold">{{ totalWorkingHours }}h</div>
              </v-col>
              <v-col cols="12" sm="4">
                <div class="text-body-2 text-grey-darken-1 mb-1">Balance</div>
                <div 
                  class="text-h6 font-weight-bold" 
                  :class="totalBalance >= 0 ? 'text-success' : 'text-error'"
                >
                  {{ totalBalance >= 0 ? '+' : '' }}{{ totalBalance.toFixed(1) }}h
                </div>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { useEventStore } from '@/stores/Event';
import { useUserStore } from '@/stores/User';
import type { TimeRange } from '@/types/Event';
import { getEndOfWeek, getStartOfWeek, getWeekBoundaries } from '@/utils/Date';
import { computed, onMounted, ref, watch } from 'vue';
import { Chart, Grid, Bar, Tooltip } from 'vue3-charts';
import type { ChartAxis, Direction } from 'vue3-charts/dist/types';

const userStore = useUserStore();
const eventStore = useEventStore();
const user = computed(() => userStore.getUser);
const chartData = computed(() => eventStore.dailyChartData); // Data for the Time Tracking chart, formatted: { date: string; name: string; workingHours: number; breakTime: number }

const menu = ref(false);
const selectedDate = ref<Date>(new Date());
const currentWeekStart = ref<number>(getStartOfWeek());
const currentWeekEnd = ref<number>(getEndOfWeek());
const chartWidth = ref(1000);

const direction: Direction = 'horizontal';
const margin = {
  left: 60,
  top: 20,
  right: 40,
  bottom: 60
};
const axis: ChartAxis = {
  primary: {
    type: 'band',
    domain: ['dataMin', 'dataMax']
  },
  secondary: {
    domain: ['dataMin', 'dataMax + 5'],
    type: 'linear',
    ticks: 6
  }
};

// Calculate total work & break hours
const totalWorkingHours = computed(() => {
  if (!chartData.value || chartData.value.length === 0) return 0;
  return chartData.value.reduce((sum, day) => sum + day.workingHours, 0).toFixed(1);
});

const totalBreakTime = computed(() => {
  if (!chartData.value || chartData.value.length === 0) return 0;
  return chartData.value.reduce((sum, day) => sum + day.breakTime, 0).toFixed(1);
});

// Format the week range for the date range picker
const weekRange = computed(() => {
  const start = new Date(currentWeekStart.value);
  const end = new Date(currentWeekEnd.value - 1);
  
  const formatDate = (date: Date) => {
    return date.toLocaleDateString('de', { 
      day: 'numeric',
      month: 'short',
      year: '2-digit'
    });
  };
  
  return `${formatDate(start)} - ${formatDate(end)}`;
});

// Handle date selection
const onDateSelect = (date: Date | Date[]) => {
  menu.value = false;
  
  const selectedDateValue = Array.isArray(date) ? date[0] : date;
  if (!selectedDateValue) return;
  
  selectedDate.value = selectedDateValue;
  const { startTs, endTs } = getWeekBoundaries(selectedDateValue);
  
  currentWeekStart.value = startTs;
  currentWeekEnd.value = endTs;
  
  loadEvents();
};

const loadEvents = async () => {
  if (!user.value?.id) return;
  
  const timeRange: TimeRange = {
    startTs: currentWeekStart.value,
    endTs: currentWeekEnd.value
  };
  
  // Set the week range in the store so the getter knows what week to display
  eventStore.setWeekRange(currentWeekStart.value, currentWeekEnd.value);
  
  await eventStore.getEventsFromUserWithTimerange(user.value.id, timeRange);
};

// Responsive chart width
const updateChartWidth = () => {
  const container = document.querySelector('.chart-container');
  if (container) {
    chartWidth.value = container.clientWidth - 40;
  }
};

onMounted(async () => {
  await loadEvents();
  updateChartWidth();
  window.addEventListener('resize', updateChartWidth);
});


// Update events on user change
watch(user, async (newUser) => {
  if (newUser?.id) {
    await loadEvents();
  }
}, { immediate: true });

// Calculate total balance (actual - target)
const totalBalance = computed(() => {
  const actual = parseFloat(totalWorkingHours.value as string);
  const target = user.value?.workingHours || 0;
  return actual - target;
});

// Week range display for the balance card
const weekRangeDisplay = computed(() => {
  const start = new Date(currentWeekStart.value);
  const end = new Date(currentWeekEnd.value - 1);
  
  const formatDate = (date: Date) => {
    return date.toLocaleDateString('de', { 
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
  };
  
  return `From ${formatDate(start)} - ${formatDate(end)}`;
});

// Generate scale markers (e.g., -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5)
const scaleMarkers = computed(() => {
  const maxRange = Math.max(Math.abs(totalBalance.value), 5);
  const step = maxRange > 10 ? 2 : 1;
  const markers = [];
  
  for (let i = -Math.ceil(maxRange); i <= Math.ceil(maxRange); i += step) {
    markers.push(i);
  }
  
  // Always include 0
  if (!markers.includes(0)) {
    markers.push(0);
    markers.sort((a, b) => a - b);
  }
  
  return markers;
});

// Calculate position for scale markers (percentage)
const getScalePosition = (value: number) => {
  const maxRange = Math.max(Math.abs(totalBalance.value), 5);
  const range = maxRange * 2; // total range (negative to positive)
  const position = ((value + maxRange) / range) * 100;
  return Math.max(0, Math.min(100, position));
};

// Get style for the balance bar
const getBalanceBarStyle = computed(() => {
  const maxRange = Math.max(Math.abs(totalBalance.value), 5);
  const percentage = (Math.abs(totalBalance.value) / maxRange) * 50; // 50% is half the width
  
  return {
    width: `${Math.min(percentage, 50)}%`
  };
});

</script>

<style scoped>
.legend-dot { 
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.chart-container {
  width: 100%;
  overflow-x: auto;
}

.balance-bar-container {
  position: relative;
  padding: 20px 0;
}


.scale-markers {
  position: relative;
  height: 20px;
  margin-bottom: 8px;
}

.scale-mark {
  position: absolute;
  transform: translateX(-50%);
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.balance-track {
  position: relative;
  height: 60px;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: visible;
}

.zero-line {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #999;
  z-index: 1;
}

.balance-bar {
  position: absolute;
  top: 0;
  height: 100%;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  z-index: 2;
}

.balance-bar.positive {
  background: #4CAF50;
  left: 50%;
}

.balance-bar.negative {
  background: #F44336;
  right: 50%;
}

.balance-value {
  color: white;
  font-weight: bold;
  font-size: 18px;
  white-space: nowrap;
  padding: 0 16px;
}
</style>