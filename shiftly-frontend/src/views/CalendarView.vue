<template>
  <v-row class="fill-height">
    <v-col ><v-btn color="primary" @click="openNewEvent">Add Event</v-btn></v-col>
    <v-spacer></v-spacer>
    <v-col cols="3">
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
          :event-ripple="false"
          :events="events"
          color="primary"
          type="week"
          @mousedown:event="startDrag"
          @mousedown:time="startTime"
          @mouseleave="cancelDrag"
          @mousemove:time="mouseMove"
          @mouseup:time="endDrag"
           :weekdays="[1, 2, 3, 4, 5, 6, 0]"
        >
          <template v-slot:event="{ event, timed }">
            <div class="v-event-draggable" @dblclick.stop="openEditEvent(event)">
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
      <EventDialog
      v-model="dialogOpen"
      :event="selectedEvent"
      @save="onSaveEvent"
      @delete="onDeleteEvent"
      />
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, watch } from 'vue'
import { VCalendar } from 'vuetify/labs/VCalendar'
import { VDatePicker, VMenu, VTextField } from 'vuetify/components'
import { useEventStore } from '@/stores/Event'
import type { EventResponse, FrontEndEvent } from '@/types/Event';
import { useUserStore } from '@/stores/User'
import EventDialog from '@/components/EventDialog.vue'

const userStore = useUserStore()
const eventStore = useEventStore()

const loadEvents = async (userId: number) => {
  await eventStore.getEventsFromUser(userId)
}

const user = computed(() => userStore.getUser)
onMounted(() => {
  if (user.value?.id) {
    loadEvents(user.value.id)}
})

// Lade Events neu, wenn sich der User ändert
watch(
  () => user.value,
  (u) => { if (u?.id) loadEvents(u.id) },
  { immediate: true }
)

interface Tms {
  year: number
  month: number
  day: number
  hour: number
  minute: number
}

const emptyEvent: FrontEndEvent = {
  eventId: -1,
  name: '',
  description: '',
  startTimestamp: '',
  endTimestamp: '',
  startText: '',
  endText: '',
  timed: true,
  break: false,
  color: 'orange',
  start : 0,
  end: 0,
}

const dialogOpen = ref(false)

// Event Dialog öffnen
const openNewEvent = () => {
  selectedEvent.value = { ...emptyEvent }
  dialogOpen.value = true
}

// EditeriDialog öffnen
const openEditEvent = (event: FrontEndEvent) => {
  const ev = findCalendarEvent(event)
  if (!ev) return

  selectedEvent.value = { ...ev }
  dialogOpen.value = true
}

// Speichern untersschied zwischen neu und editieren mit Hilfe der ID
const onSaveEvent = (ev: FrontEndEvent) => {
  const index = calendarEvents.value.findIndex(e => e.eventId === ev.eventId)

  if (index !== -1) {
    calendarEvents.value[index] = { ...ev }
  } else {
    calendarEvents.value.push(ev)
  }
}

const onDeleteEvent = (ev: FrontEndEvent) => {
  const index = calendarEvents.value.findIndex(e => e.eventId === ev.eventId)
  if (index !== -1) {
    calendarEvents.value.splice(index, 1)
  }
}

const selectedEvent = ref<FrontEndEvent>({ ...emptyEvent })

// Datum für Calendar & DatePicker
const selectedDate = ref(new Date().toISOString().substring(0, 10))
const menu = ref(false)

// Backend bleibt unverändert, bis du die Änderungen explizit zurückspeicherst.
const calendarEvents = ref<FrontEndEvent[]>([])

const mapBackendToCalendar = (backend: EventResponse[]): FrontEndEvent[] => {
  return backend.map(e => {
    const start = new Date(e.startTimestamp).getTime()
    const end = new Date(e.endTimestamp).getTime()
    return {
      ...e,
      start,
      end,
      startText: formatDate(e.startTimestamp),
      endText: formatDate(e.endTimestamp),
      timed: true,
      color: e.break ? 'green' : 'orange',
    } as FrontEndEvent
  })
}

watch(
  () => eventStore.getEvents,
  (val) => {
    const backend = val as EventResponse[] | undefined

    // Mappe die Backend-Events in FrontEndEvents für den Kalender
    // Wenn keine Events vorhanden → leeres Array
    calendarEvents.value = backend ? mapBackendToCalendar(backend) : []
  },
  { immediate: true, deep: true }
)

// Vorteil: Man kann noch Filter, Sortierung oder Mapping einfügen, bevor die Events im Template landen.
const events = calendarEvents

function formatDate(iso: string): string {
  const d = new Date(iso)
  const pad = (n: number) => String(n).padStart(2, "0")

  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} `
       + `${pad(d.getHours())}:${pad(d.getMinutes())}`
}

const dragEvent = ref<FrontEndEvent | null>(null)
const dragTime = ref<number | null>(null)
const createEvent = ref<FrontEndEvent | null>(null)
const createStart = ref<number | null>(null)
const extendOriginal = ref<number | null>(null)

// findet das "echte" Event-Objekt im Calendar-Array
const findCalendarEvent = (maybeEvent: FrontEndEvent): FrontEndEvent | undefined => {
  if (!maybeEvent) return undefined
  //Bevorzugt: Suche nach eventId
  if (maybeEvent.eventId != null) {
    return calendarEvents.value.find(e => e.eventId === maybeEvent.eventId)
  }
  // fallback: Suche nach Kombination aus Start, End und Name -> falls kein ID vorhanden ist
  return calendarEvents.value.find(e => e.start === maybeEvent.start && e.end === maybeEvent.end && e.name === maybeEvent.name)
}

// Drag starten
const startDrag = (_e: Event, { event, timed }: { event: any; timed: boolean }) => {
    // Hole die "echte" Referenz des Events aus dem Kalender
    // Vuetify rendert Events möglicherweise als neue Objekte → stabile Referenz nötig
  const ev = findCalendarEvent(event)
 // Nur Events mit Zeit (timed) können gezogen werden
  if (ev && timed) {
    // Markiere das Event als aktuell gezogenen Event
    dragEvent.value = ev
    // Setze Offset noch nicht → wird beim ersten Mouse-Move berechnet
    dragTime.value = null
    // Alte Endzeit merken, falls Drag abgebrochen wird
    extendOriginal.value = null
  }
}

// Drag oder neue Event-Erstellung starten
const startTime = (_e: Event, tms: Tms) => {
  const mouse = toTime(tms)
//Wenn ein bestehendes Event gerade gezogen wird
  if (dragEvent.value && dragTime.value === null) {
    // Berechne Offset zwischen Maus und Event-Start
    // Damit das Event beim Drag unter der Maus bleibt
    dragTime.value = mouse - (dragEvent.value.start as number)
  } else {
    createStart.value = roundTime(mouse)
    const newEv: FrontEndEvent = {
      // Temporäre negative ID, bis Backend eine echte vergibt
      eventId: Math.floor(Math.random() * -1000000),
      name: `Event #${calendarEvents.value.length + 1}`,
      description: '',
      start: createStart.value,
      end: createStart.value,
      startText: formatDate(new Date(createStart.value!).toISOString()),
      endText: formatDate(new Date(createStart.value!).toISOString()),
      timed: true,
      break: false,
      color: 'orange',
    } as FrontEndEvent

    createEvent.value = newEv
    calendarEvents.value.push(newEv)
  }
}

// Event verlängern (resize bottom)
const extendBottom = (event: any) => {
  const ev = findCalendarEvent(event)
  if (!ev) return
  createEvent.value = ev
  createStart.value = ev.start
  extendOriginal.value = ev.end
}

// Mouse-Move Event -> Diese Funktion wird jedes Mal ausgeführt, wenn die Maus sich innerhalb des Kalenders bewegt während du ein Event ziehst oder neu erstellst.
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

    createEvent.value.startText = new Date(createEvent.value.start).toISOString()
    createEvent.value.endText = new Date(createEvent.value.end).toISOString()
  }
}

// Drag/Erstellung beenden
const endDrag = async () => {
  dragEvent.value = null
  dragTime.value = null
  createEvent.value = null
  createStart.value = null
  extendOriginal.value = null
}

// Drag/Erstellung abbrechen -> wird von Vuetify automatisch aufgerufen, und zwar immer dann, wenn du die Maus aus dem <v-calendar>-Bereich herausbewegst
const cancelDrag = () => {
  if (createEvent.value) {
    if (extendOriginal.value !== null) {
      createEvent.value.end = extendOriginal.value
    } else {
      const i = calendarEvents.value.indexOf(createEvent.value)
      if (i !== -1) calendarEvents.value.splice(i, 1)
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

// Hilfsfunktion: Liefert nur den Tag
const getDay = (input: Date | number, padZero = false): string => {
  const date = typeof input === 'number' ? new Date(input) : (input instanceof Date ? input : new Date(input))
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

  // Lokales Datum formatieren YYYY-MM-DD
  const format = (d: Date) => {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  return `${format(monday)} — ${format(sunday)}`;
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
