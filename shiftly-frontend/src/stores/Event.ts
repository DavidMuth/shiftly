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

    async startTracking(name: string, description: string, isBreak: boolean, startTimestamp: number): Promise<Number> {
      try {
        const response = await EventService.startTracking(name, description, isBreak, startTimestamp)
        console.log("response:", response)
        return response.data
      } catch (error) {
        console.error("Failed to start tracking event:", error)
        throw error;
      }
    },
    
    async stopTracking(id: number, endTimestamp: number): Promise<Number> {
      try {
        const response = await EventService.stopTracking(id, endTimestamp)
        console.log("response:", response)
        return response.data
      } catch (error) {
        console.error("Failed to stop tracking event:", error)
        throw error;
      }
    }
  },
});
