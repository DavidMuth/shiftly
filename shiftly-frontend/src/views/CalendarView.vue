<template>
  <v-row class="fill-height">
    <v-col>
      <v-sheet height="600">
        <v-calendar
          ref="calendar"
          v-model="value"
          :event-color="getEventColor"
          :event-ripple="false"
          :events="events"
          color="primary"
          type="4day"
          @change="getEvents"
          @mousedown:event="startDrag"
          @mousedown:time="startTime"
          @mouseleave="cancelDrag"
          @mousemove:time="mouseMove"
          @mouseup:time="endDrag"
        >
          <template v-slot:event="{ event, timed, eventSummary }">
            <div class="v-event-draggable">
              <component :is="eventSummary"></component>
            </div>
            <div
              v-if="timed"
              class="v-event-drag-bottom"
              @mousedown.stop="extendBottom(event)"
            ></div>
          </template>
        </v-calendar>
      </v-sheet>
    </v-col>
  </v-row>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { VCalendar } from 'vuetify/labs/VCalendar'
import { VRow, VCol, VSheet } from 'vuetify/components'

/** Typ für Mouse/Time Event */
interface Tms {
  year: number
  month: number
  day: number
  hour: number
  minute: number
}

const value = ref<string>('')
const events = ref<any[]>([]) // Vuetify-internes Event, daher any

/** Farben und Namen */
const colors = ['#2196F3', '#3F51B5', '#673AB7', '#00BCD4', '#4CAF50', '#FF9800', '#757575']
const names = ['Meeting', 'Holiday', 'PTO', 'Travel', 'Event', 'Birthday', 'Conference', 'Party']

/** Drag & Create Events */
const dragEvent = ref<any | null>(null)
const dragTime = ref<number | null>(null)
const createEvent = ref<any | null>(null)
const createStart = ref<number | null>(null)
const extendOriginal = ref<number | null>(null)

/** Drag starten */
function startDrag(_nativeEvent: MouseEvent, { event, timed }: { event: any, timed: boolean }) {
  if (event && timed) {
    dragEvent.value = event
    dragTime.value = null
    extendOriginal.value = null
  }
}

/** Neue Event-Erstellung oder Drag-Start */
function startTime(_nativeEvent: MouseEvent, tms: Tms) {
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

/** Event verlängern */
function extendBottom(event: any) {
  createEvent.value = event
  createStart.value = event.start
  extendOriginal.value = event.end
}

/** Mouse-Move Event für Drag oder Erstellung */
function mouseMove(_nativeEvent: MouseEvent, tms: Tms) {
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

/** Drag/Erstellung beenden */
function endDrag() {
  dragEvent.value = null
  dragTime.value = null
  createEvent.value = null
  createStart.value = null
  extendOriginal.value = null
}

/** Drag/Erstellung abbrechen */
function cancelDrag() {
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

/** Runden auf 15 Minuten */
function roundTime(time: number, down = true): number {
  const roundTo = 15 * 60 * 1000
  return down ? time - (time % roundTo) : time + (roundTo - (time % roundTo))
}

/** Tms → Timestamp */
function toTime(tms: Tms): number {
  return new Date(tms.year, tms.month - 1, tms.day, tms.hour, tms.minute).getTime()
}

/** Event-Farbe (TypeScript-kompatibel für Vuetify) */
function getEventColor(event: any): string {
  const rgb = parseInt(event.color.slice(1), 16)
  const r = (rgb >> 16) & 0xff
  const g = (rgb >> 8) & 0xff
  const b = rgb & 0xff

  return event === dragEvent.value || event === createEvent.value
    ? `rgba(${r},${g},${b},0.7)`
    : event.color
}

/** Zufällige Events generieren */
function getEvents({ start, end }: { start: { date: string }, end: { date: string } }) {
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

/** Zufallszahl */
function rnd(a: number, b: number): number {
  return Math.floor(Math.random() * (b - a + 1)) + a
}

/** Zufälliges Array-Element */
function rndElement<T>(arr: T[]): T {
  if (arr.length === 0) throw new Error('Array cannot be empty')
  const index = Math.floor(Math.random() * arr.length)
  return arr[index]!
}
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
