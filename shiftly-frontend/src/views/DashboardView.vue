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
              @click="exportPDF"
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
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

const userStore = useUserStore();
const eventStore = useEventStore();
const user = computed(() => userStore.getUser);
const chartData = computed(() => eventStore.dailyChartData);
const events = computed(() => eventStore.getEvents);

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

// Calculate total hours
const totalWorkingHours = computed(() => {
  if (!chartData.value || chartData.value.length === 0) return 0;
  return chartData.value.reduce((sum, day) => sum + day.workingHours, 0).toFixed(1);
});

const totalBreakTime = computed(() => {
  if (!chartData.value || chartData.value.length === 0) return 0;
  return chartData.value.reduce((sum, day) => sum + day.breakTime, 0).toFixed(1);
});

// Format the week range display
const weekRange = computed(() => {
  const start = new Date(currentWeekStart.value);
  const end = new Date(currentWeekEnd.value - 1);

  const formatDate = (date: Date) => {
    return date.toLocaleDateString('en-US', {
      day: 'numeric',
      month: 'short',
      year: '2-digit'
    });
  };

  return `${formatDate(start)} - ${formatDate(end)}`;
});

const exportPDF = () => {
  if (!events.value || events.value.length === 0) {
    console.warn('No events to export.');
    return;
  }

  const doc = new jsPDF();

  doc.setFontSize(16);
  doc.text('Event Overview', 14, 20);

  doc.setFontSize(11);
  doc.text(`Week: ${weekRange.value}`, 14, 28);

  const tableColumn = ['Event ID', 'Name', 'Description', 'Start', 'End', 'Break'];

  const tableRows = weeklyEvents.value.map(event => [
    event.eventId,
    event.name,
    event.description,
    new Date(event.startTimestamp).toLocaleString(),
    new Date(event.endTimestamp).toLocaleString(),
    event.break ? 'Yes' : 'No'
  ]);

  autoTable(doc, {
    head: [tableColumn],
    body: tableRows,
    startY: 32,
    styles: { fontSize: 10, cellPadding: 3 },
    headStyles: { fillColor: [25, 118, 210], textColor: 255 },
    alternateRowStyles: { fillColor: [240, 240, 240] }
  });

  // "Dec 16, 2025 - Dec 22, 2025" → "events_Dec_16_2025_Dec_22_2025.pdf"
  const safeWeekRange = weekRange.value.replace(/[^a-zA-Z0-9]/g, '_');
  doc.save(`events_${safeWeekRange}.pdf`);
};

const weeklyEvents = computed(() => {
  if (!events.value || !selectedDate.value) return [];

  // Berechne Start und Ende der Woche für selectedDate
  const { startTs, endTs } = getWeekBoundaries(selectedDate.value);

  return events.value.filter(event => {
    const eventStart = new Date(event.startTimestamp).getTime();
    const eventEnd = new Date(event.endTimestamp).getTime();

    // Event startet oder endet innerhalb der Woche oder deckt die ganze Woche ab
    return (
      (eventStart >= startTs && eventStart < endTs) ||
      (eventEnd >= startTs && eventEnd < endTs) ||
      (eventStart <= startTs && eventEnd >= endTs)
    );
  });
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

watch(user, async (newUser) => {
  if (newUser?.id) {
    await loadEvents();
  }
}, { immediate: true });
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
</style>
