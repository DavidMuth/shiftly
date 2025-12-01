<template>
  <v-row class="fill-height">
    <v-col><v-btn color="primary">Add Event</v-btn></v-col>
    <v-col >
      <v-menu
        v-model="menu"
        :close-on-content-click="false"
        transition="scale-transition"
        offset-y
        min-width="290px"
      >
        <template #activator="{ props }">
          <v-text-field
            v-model="weekRange"
            label="Select Date"
            readonly
            v-bind="props"
            :formatter="formatDay"
          ></v-text-field>
        </template>

        <v-date-picker
          v-model="selectedDate"
          :first-day-of-week="1"
          @update:model-value="menu = false"
        ></v-date-picker>
      </v-menu>
    </v-col>
  </v-row>
  <v-sheet height="600">
        <v-calendar
          ref="calendar"
          v-model="selectedDate"
          :event-color="getEventColor"
          :event-ripple="false"
          :events="events"
          color="primary"
          type="week"
          @change="getEvents"
          @mousedown:event="startDrag"
          @mousedown:time="startTime"
          @mouseleave="cancelDrag"
          @mousemove:time="mouseMove"
          @mouseup:time="endDrag"
           :weekdays="[1, 2, 3, 4, 5, 6, 0]"
        >
          <template v-slot:event="{ event, timed }">
            <div class="v-event-draggable">
              <strong>{{ getDay(event.start) }}</strong> - {{ event.name }}
            </div>
            <div
              v-if="timed"
              class="v-event-drag-bottom"
              @mousedown.stop="extendBottom(event)"
            ></div>
          </template>
        </v-calendar>
      </v-sheet>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { VCalendar } from 'vuetify/labs/VCalendar'
import { VDatePicker, VMenu, VTextField } from 'vuetify/components'

interface Tms {
  year: number
  month: number
  day: number
  hour: number
  minute: number
}

// Datum für Calendar & DatePicker
const selectedDate = ref(new Date().toISOString().substring(0, 10))
const menu = ref(false)

// Events
const events = ref<any[]>([])

// Farben & Namen für Zufallsevents
const colors = ['#2196F3', '#3F51B5', '#673AB7', '#00BCD4', '#4CAF50', '#FF9800', '#757575']
const names = ['Meeting', 'Holiday', 'PTO', 'Travel', 'Event', 'Birthday', 'Conference', 'Party']

// Drag & Create
const dragEvent = ref<any | null>(null)
const dragTime = ref<number | null>(null)
const createEvent = ref<any | null>(null)
const createStart = ref<number | null>(null)
const extendOriginal = ref<number | null>(null)

// Drag starten
const startDrag = (_e: Event, { event, timed }: { event: any; timed: boolean }) => {
  if (event && timed) {
    dragEvent.value = event
    dragTime.value = null
    extendOriginal.value = null
  }
}

// Drag oder neue Event-Erstellung starten
const startTime = (_e: Event, tms: Tms) => {
  const mouse = toTime(tms)

  if (dragEvent.value && dragTime.value === null) {
    dragTime.value = mouse - dragEvent.value.start
  } else {
    createStart.value = roundTime(mouse)
    createEvent.value = {
      name: `Event #${events.value.length}`,
      color: rndElement(colors),
      start: createStart.value,
      end: createStart.value,
      timed: true,
    }
    events.value.push(createEvent.value)
  }
}

// Event verlängern
const extendBottom = (event: any) => {
  createEvent.value = event
  createStart.value = event.start
  extendOriginal.value = event.end
}

// Mouse-Move Event
const mouseMove = (_e: Event, tms: Tms) => {
  const mouse = toTime(tms)

  if (dragEvent.value && dragTime.value !== null) {
    const duration = dragEvent.value.end - dragEvent.value.start
    const newStart = roundTime(mouse - dragTime.value)
    dragEvent.value.start = newStart
    dragEvent.value.end = newStart + duration
  } else if (createEvent.value && createStart.value !== null) {
    const mouseRounded = roundTime(mouse, false)
    createEvent.value.start = Math.min(mouseRounded, createStart.value)
    createEvent.value.end = Math.max(mouseRounded, createStart.value)
  }
}

// Drag/Erstellung beenden
const endDrag = () => {
  dragEvent.value = null
  dragTime.value = null
  createEvent.value = null
  createStart.value = null
  extendOriginal.value = null
}

// Drag/Erstellung abbrechen
const cancelDrag = () => {
  if (createEvent.value) {
    if (extendOriginal.value !== null) {
      createEvent.value.end = extendOriginal.value
    } else {
      const i = events.value.indexOf(createEvent.value)
      if (i !== -1) events.value.splice(i, 1)
    }
  }
  dragEvent.value = null
  dragTime.value = null
  createEvent.value = null
  createStart.value = null
  extendOriginal.value = null
}

// Rundung auf 15 Minuten
const roundTime = (time: number, down = true): number => {
  const roundTo = 15 * 60 * 1000
  return down ? time - (time % roundTo) : time + (roundTo - (time % roundTo))
}

// Tms → Timestamp
const toTime = (tms: Tms): number => {
  return new Date(tms.year, tms.month - 1, tms.day, tms.hour, tms.minute).getTime()
}

// Event-Farbe
const getEventColor = (event: any): string => {
  const rgb = parseInt(event.color.slice(1), 16)
  const r = (rgb >> 16) & 0xff
  const g = (rgb >> 8) & 0xff
  const b = rgb & 0xff

  return event === dragEvent.value || event === createEvent.value
    ? `rgba(${r},${g},${b},0.7)`
    : event.color
}

// Zufällige Events generieren
const getEvents = ({ start, end }: { start: { date: string }; end: { date: string } }) => {
  const newEvents: any[] = []
  const min = new Date(`${start.date}T00:00:00`).getTime()
  const max = new Date(`${end.date}T23:59:59`).getTime()
  const days = (max - min) / 86400000
  const eventCount = rnd(days, days + 20)

  for (let i = 0; i < eventCount; i++) {
    const timed = rnd(0, 3) !== 0
    const firstTimestamp = rnd(min, max)
    const duration = rnd(2, timed ? 8 : 288) * 900000
    const startTime = firstTimestamp - (firstTimestamp % 900000)
    newEvents.push({
      name: rndElement(names),
      color: rndElement(colors),
      start: startTime,
      end: startTime + duration,
      timed,
    })
  }

  events.value = newEvents
}

// Zufallszahl
const rnd = (a: number, b: number): number => {
  return Math.floor(Math.random() * (b - a + 1)) + a
}

// Zufälliges Array-Element
const rndElement = <T>(arr: T[]): T => {
  if (arr.length === 0) throw new Error('Array cannot be empty')
  const index = Math.floor(Math.random() * arr.length)
  return arr[index]!
}

// Hilfsfunktion: Liefert nur den Tag
const getDay = (input: Date | number, padZero = false): string => {
  const date = input instanceof Date ? input : new Date(input)
  const day = date.getDate()
  return padZero ? String(day).padStart(2, '0') : String(day)
}

// Formatter für Textfeld: nur Tag
const formatDay = (date: string | Date): string => {
  const d = typeof date === 'string' ? new Date(date) : date
  return String(d.getDate()).padStart(2, '0')
}

// Berechnet automatisch Montag - Sonntag der Woche
const weekRange = computed(() => {
  if (!selectedDate.value) return '';

  const date = new Date(selectedDate.value);
  const day = date.getDay(); // 0 = Sonntag, 1 = Montag ...

  // Montag berechnen
  const monday = new Date(date);
  monday.setDate(date.getDate() + (day === 0 ? -6 : 1 - day));

  // Sonntag berechnen
  const sunday = new Date(monday);
  sunday.setDate(monday.getDate() + 6);

  // Formatierung YYYY-MM-DD
  const format = (d: Date)  => d.toISOString().substring(0, 10);

  return `${format(monday)} — ${format(sunday)}`;;
});
</script>


<style scoped>
.v-event-draggable {
  padding-left: 6px;
}

.v-event-timed {
  user-select: none;
  -webkit-user-select: none;
}

.v-event-drag-bottom {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 4px;
  height: 4px;
  cursor: ns-resize;
}

.v-event-drag-bottom::after {
  display: none;
  position: absolute;
  left: 50%;
  height: 4px;
  border-top: 1px solid white;
  border-bottom: 1px solid white;
  width: 16px;
  margin-left: -8px;
  opacity: 0.8;
  content: '';
}

.v-event-drag-bottom:hover::after {
  display: block;
}
</style>
