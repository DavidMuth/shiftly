<template>
  <v-dialog v-model="internalDialog" max-width="520px" :persistent="true">
    <v-card class="pa-4">
      <!-- Close (X) rechts oben -->
      <v-btn
        icon
        variant="text"
        class="position-absolute"
        style="top: 8px; right: 8px"
        @click="internalDialog = false"
      >
        <v-icon>mdi-close</v-icon>
      </v-btn>
      <v-card-text>
        <!-- Name -->
        <v-row class="mt-4">
          <v-col cols="9">
            <div class="mb-1 font-weight-medium">Name</div>
            <v-text-field
              v-model="localEvent.name"
              label="Event title"
              variant="outlined"
              hide-details
            />
          </v-col>

          <v-col cols="3" class="d-flex align-center justify-center">
            <div class="text-center">
              <div class="mb-1 font-weight-medium">Break</div>
              <v-switch v-model="localEvent.break" color="primary" />
            </div>
          </v-col>
        </v-row>

        <!-- Description -->
        <div class="mt-4 mb-2 font-weight-medium">Description</div>
        <v-textarea
          v-model="localEvent.description"
          variant="outlined"
          rows="4"
        />

        <!-- Start -->
        <div class="mt-4 font-weight-medium">Start:</div>
        <v-dialog v-model="startPicker" max-width="350px">
          <template #activator="{ props }">
            <v-text-field
              v-bind="props"
              :value="formatDisplay(localEvent.startTimestamp)"
              readonly
              variant="outlined"
            />
          </template>

          <v-card>
            <v-date-picker v-model="startDate" />
            <v-time-picker format="24hr"  v-model="startTime" />

            <v-card-actions>
              <v-spacer />
              <v-btn color="primary" @click="applyStart">OK</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>

        <!-- End -->
        <div class="mt-4 font-weight-medium">End:</div>
        <v-dialog v-model="endPicker" max-width="350px">
          <template #activator="{ props }">
            <v-text-field
              v-bind="props"
              :value="formatDisplay(localEvent.endTimestamp)"
              readonly
              variant="outlined"
            />
          </template>

          <v-card>
            <v-date-picker v-model="endDate" />
            <v-time-picker format="24hr" v-model="endTime" />

            <v-card-actions>
              <v-spacer />
              <v-btn color="primary" @click="applyEnd">OK</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-card-text>

      <v-card-actions class="d-flex justify-space-between">
        <v-btn color="error" variant="text" @click="onDelete">
          <v-icon class="mr-1">mdi-delete</v-icon>
          Delete
        </v-btn>

        <v-btn color="primary" @click="onSave">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import type { FrontEndEvent } from '@/types/Event';

const props = defineProps<{
  event: FrontEndEvent;
  modelValue: boolean;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void;
  (e: "save", value: FrontEndEvent): void;
  (e: "delete", value: FrontEndEvent): void;
}>();

const internalDialog = ref<boolean>(props.modelValue);

// Lokales Event-Objekt für den Dialog
const localEvent = ref<FrontEndEvent>({
  ...props.event,
  startTimestamp: props.event?.startTimestamp ?? "",
  endTimestamp: props.event?.endTimestamp ?? "",
});

// Picker
const startPicker = ref(false);
const endPicker = ref(false);
const startDate = ref<string | null>(null);
const startTime = ref<string | null>(null);
const endDate = ref<string | null>(null);
const endTime = ref<string | null>(null);

// Synchronisation Dialog <-> Parent
watch(() => props.modelValue, (v) => internalDialog.value = v);
watch(internalDialog, (v) => emit("update:modelValue", v));
watch(() => props.event, (val) => {
  localEvent.value = { ...val,
    startTimestamp: val?.startTimestamp ?? "",
    endTimestamp: val?.endTimestamp ?? ""
  }
}, { deep: true });

// Formatter für Textfelder
const formatDisplay = (iso: string): string => {
  if (!iso) return "";
  const d = new Date(iso);

  const pad = (n: number) => String(n).padStart(2, "0");

  const year = d.getFullYear();
  const month = pad(d.getMonth() + 1);
  const day = pad(d.getDate());
  const hours = pad(d.getHours());
  const minutes = pad(d.getMinutes());
  const seconds = pad(d.getSeconds());

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// Initialisiere Picker-Werte beim Öffnen des Dialogs
watch(internalDialog, (open) => {
  if (!open || !localEvent.value) return;

  if (localEvent.value.startTimestamp) {
    const [date, time] = localEvent.value.startTimestamp.split("T");
    startDate.value = date ?? null;
    startTime.value = time ? time.substring(0, 5) : null; // HH:mm
    console.log(time)
  } else {
    startDate.value = null;
    startTime.value = null;
  }

  if (localEvent.value.endTimestamp) {
    const [date, time] = localEvent.value.endTimestamp.split("T");
    endDate.value = date ?? null;
    endTime.value = time ? time.substring(0, 5) : null; // HH:mm
  } else {
    endDate.value = null;
    endTime.value = null;
  }
});

// Apply Buttons
const applyStart = () => {
  if (startDate.value && startTime.value) {
    localEvent.value.startTimestamp = `${startDate.value} ${startTime.value}:00`;
  } else {
    localEvent.value.startTimestamp = "";
  }
  startPicker.value = false;
};

const applyEnd = () => {
  if (endDate.value && endTime.value) {
    localEvent.value.endTimestamp = `${endDate.value} ${endTime.value}:00`;
  } else {
    localEvent.value.endTimestamp = "";
  }
  endPicker.value = false;
};

// Save/Delete
const onSave = () => {
  emit("save", { ...localEvent.value });
  emit("update:modelValue", false);
};

const onDelete = () => {
  emit("delete", { ...localEvent.value });
  emit("update:modelValue", false);
};
</script>
