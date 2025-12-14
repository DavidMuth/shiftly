<template>
  <v-dialog v-model="internalDialog" max-width="520px" :persistent="true">
    <v-card class="pa-4">
      <!-- Close (X) -->
      <v-btn
        icon
        variant="text"
        class="position-absolute"
        style="top: 8px; right: 8px"
        @click="$emit('cancel')"
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

          <v-card >
            <v-card-text >
              <v-date-picker v-model="startDate" value-format="YYYY-MM-DD" @update:model-value="onStartDateChange"/>
              <v-time-picker
                v-model="startTime"
                format="24hr"
                scrollable
              />
            </v-card-text>

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

          <v-card >
            <v-card-text >
              <v-date-picker v-model="endDate" value-format="YYYY-MM-DD" />
              <v-time-picker
                v-model="endTime"
                format="24hr"
                scrollable
              />
            </v-card-text>

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
import type { FrontEndEvent } from "@/types/Event";

const props = defineProps<{
  event: FrontEndEvent;
  modelValue: boolean;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void;
  (e: "save", value: FrontEndEvent): void;
  (e: "delete", value: FrontEndEvent): void;
  (e: "cancel"): void;
}>();

const internalDialog = ref<boolean>(props.modelValue);

// Local copy
const localEvent = ref<FrontEndEvent>({
  ...props.event,
  startTimestamp: props.event?.startTimestamp ?? "",
  endTimestamp: props.event?.endTimestamp ?? "",
});

// Picker states
const startPicker = ref(false);
const endPicker = ref(false);
const startDate = ref<string | null>(null);
const startTime = ref<string | null>(null);
const endDate = ref<string | null>(null);
const endTime = ref<string | null>(null);

// Dialog sync
watch(() => props.modelValue, v => (internalDialog.value = v));
watch(internalDialog, v => emit("update:modelValue", v));

// Fehler handling wenn timepicker null ist
watch(startDate, (val) => {
  if (val && !startTime.value) {
    startTime.value = "00:00";
  }
});

watch(endDate, (val) => {
  if (val && !endTime.value) {
    endTime.value = "00:00";
  }
});

watch(
  () => props.event,
  val => {
    localEvent.value = {
      ...val,
      startTimestamp: val?.startTimestamp ?? "",
      endTimestamp: val?.endTimestamp ?? "",
    };
  },
  { deep: true }
);

const onStartDateChange = (val: string | Date) => {
  if (!val) {
    startDate.value = null;
    return;
  }

  if (val instanceof Date) {
    startDate.value = val.toISOString().slice(0, 10);
  } else {
    startDate.value = val;
  }

  // Default-Zeit setzen (UX!)
  if (!startTime.value) {
    startTime.value = "09:00";
  }
};

// Display formatter
const formatDisplay = (iso: string): string => {
  if (!iso) return "";

  const d = new Date(iso);

  if (isNaN(d.getTime())) {
    return "";
  }

  const pad = (n: number) => String(n).padStart(2, "0");

  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(
    d.getDate()
  )} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
};


// Init pickers on open
watch(internalDialog, open => {
  if (!open) return;

  if (localEvent.value.startTimestamp) {
    const d = new Date(localEvent.value.startTimestamp);
    if (!isNaN(d.getTime())) {
      startDate.value = d.toISOString().slice(0, 10);
      startTime.value = d.toTimeString().slice(0, 5);
    }
  }

  if (localEvent.value.endTimestamp) {
    const d = new Date(localEvent.value.endTimestamp);
    if (!isNaN(d.getTime())) {
      endDate.value = d.toISOString().slice(0, 10);
      endTime.value = d.toTimeString().slice(0, 5);
    }
  }
});

// Apply buttons (ISO FORMAT!)
const applyStart = () => {
  if (!startDate.value || !startTime.value) {
    startPicker.value = false;
    return;
  }

  localEvent.value.startTimestamp =
    `${startDate.value}T${startTime.value}:00`;

  startPicker.value = false;
};

const applyEnd = () => {
  if (!endDate.value || !endTime.value) {
    endPicker.value = false;
    return;
  }

  localEvent.value.endTimestamp =
    `${endDate.value}T${endTime.value}:00`;

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
