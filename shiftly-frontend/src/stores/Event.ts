import type { EventResponse } from '@/types/Event';
import { defineStore } from 'pinia';
import EventService from '@/services/EventService';

interface EventState {
  events: EventResponse[] | null;
}

export const useEventStore = defineStore('Event', {
  state: (): EventState => ({
    events: [],
  }),

  getters: {
    getEvents: (state): EventResponse[] | null => state.events,
  },

  actions: {
    async getEventsFromUser(userId: number): Promise<void> {
      try {
        const response = await EventService.getEventsFromUser(userId);
        this.events = response.data;
      } catch (error) {
        this.events = null;
        console.error("Get events failed:", error);
      }
    },
  },
});
