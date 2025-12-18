import type { EventResponse, NewEventRequest , EditEventRequest, TimeRange} from '@/types/Event';
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

    // Format for daily chart (grouped by day)
    dailyChartData: (state) => {
      const dailyMap = new Map<string, {workingHours:number, breakTime:number}>();

      state.events?.forEach(event => {
        const date = event.startTimestamp.split("T")[0] // 2025-12-07
        const start = new Date(event.startTimestamp).getTime();
        const end = new Date(event.endTimestamp).getTime();
        const hours = (end - start) / (1000*60*60); // convert ms to 
        
        if (!date) {return}

        if (!dailyMap.has(date)) {
          dailyMap.set(date, {workingHours: 0, breakTime: 0})
        }

        const dayData = dailyMap.get(date);
        if (!dayData) {return}
        if (event.break) {
          dayData.breakTime += hours;
        } else {
          dayData.workingHours += hours;
        }
      });

      // Convert to array for vue3charts
      return Array.from(dailyMap.entries())
        .map(([date, data]) => ({
          name: new Date(date).toLocaleDateString('en-US', {weekday: 'short'}), // short -> Mon, Tue, Wed....
          workingHours: Number(data.workingHours.toFixed(2)),
          breakTime: Number(data.breakTime.toFixed(2))
        }))
        .sort((a,b) => new Date(a.name).getTime() - new Date(b.name).getTime());
    }
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

    async getEventsFromUserWithTimerange(userId: number, timeRange: TimeRange): Promise<void> {
      try {
        const response = await EventService.getEventsFromUserWithTimeRange(userId, timeRange.startTs, timeRange.endTs);
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
