import type { EventResponse, NewEventRequest , EditEventRequest} from '@/types/Event';
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

    async createCalendarEvent(newEvent: NewEventRequest): Promise<void> {
      try {
        const response =  await EventService.createCalendarEvent(newEvent)
        if(response.data){
          this.getEventsFromUser(newEvent.userId);
        }
        else{
          throw new Error("Event creation failed on server");
        }
      } catch (error) {
        console.error("Failed to create event: ", error)
        throw error;
      }
    },

    async editCalendarEvent(editEvent: EditEventRequest): Promise<void> {
      try {
        const response =  await EventService.editCalendarEvent(editEvent)
        if(response.data){
          this.getEventsFromUser(editEvent.userId);
        }
        else{
          throw new Error("Event edtion failed on server");
        }
      } catch (error) {
        console.error("Failed to edit event: ", error)
        throw error;
      }
    },
  },
});
