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
          throw new Error("Event edit failed on server");
        }
      } catch (error) {
        console.error("Failed to edit event: ", error)
        throw error;
      }
    },

    async deleteCalendarEvent(eventId: number, userId: number): Promise<void> {
      try {
        const response =  await EventService.deleteCalendarEvent(eventId)
        if(response.data){
          this.getEventsFromUser(userId);
        }
        else{
          throw new Error("Event delete failed on server");
        }
      } catch (error) {
        console.error("Failed to delete event: ", error)
        throw error;
      }
    },

  },
});
