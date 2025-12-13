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
              v-model="formattedStart"
              readonly
              variant="outlined"
            />
          </template>

          <v-card>
            <v-date-picker v-model="startDate" />
            <v-time-picker v-model="startTime" />

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
              v-model="formattedEnd"
              readonly
              variant="outlined"
            />
          </template>

          <v-card>
            <v-date-picker v-model="endDate" />
            <v-time-picker v-model="endTime" />

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
import { ref, watch, computed } from "vue";
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

watch(
  () => props.modelValue,
  (v) => (internalDialog.value = v)
);

watch(internalDialog, (v) => emit("update:modelValue", v));

const localEvent = ref<FrontEndEvent>({ ...props.event });

watch(
  () => props.event,
  (val) => (localEvent.value = { ...val }),
  { deep: true }
);

const startPicker = ref(false);
const endPicker = ref(false);

const startDate = ref<string | null>(null);
const startTime = ref<string | null>(null);
const endDate = ref<string | null>(null);
const endTime = ref<string | null>(null);

watch(
  localEvent,
  (ev) => {
    if (ev.startTimestamp) {
      const parts = ev.startTimestamp.split(" ");
      startDate.value = parts[0] ?? null;
      startTime.value = parts[1] ?? null;
    }

    if (ev.endTimestamp) {
      const parts = ev.endTimestamp.split(" ");
      endDate.value = parts[0] ?? null;
      endTime.value = parts[1] ?? null;
    }
  },
  { immediate: true }
);

const formatDT = (d: string | null, t: string | null): string =>
  d && t ? `${d} ${t}` : "";

const formattedStart = computed(() => formatDT(startDate.value, startTime.value));
const formattedEnd = computed(() => formatDT(endDate.value, endTime.value));

const applyStart = () => {
  localEvent.value.startTimestamp = formatDT(startDate.value, startTime.value);
  startPicker.value = false;
};

const applyEnd = () => {
  localEvent.value.endTimestamp = formatDT(endDate.value, endTime.value);
  endPicker.value = false;
};

const onSave = () => {
  emit("save", { ...localEvent.value });
  emit("update:modelValue", false);
};

const onDelete = () => {
  emit("delete", { ...localEvent.value });
  emit("update:modelValue", false);
};
</script>
