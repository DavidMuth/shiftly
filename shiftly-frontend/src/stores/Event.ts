import type { EventResponse, NewEventRequest , EditEventRequest, TimeRange} from '@/types/Event';
import { defineStore } from 'pinia';
import EventService from '@/services/EventService';

interface EventState {
  events: EventResponse[] | null;
  currentWeekStart: number | null;
  currentWeekEnd: number | null;
}

export const useEventStore = defineStore('Event', {
  state: (): EventState => ({
    events: [],
    currentWeekStart: null,
    currentWeekEnd: null,
  }),

  getters: {
    getEvents: (state): EventResponse[] | null => state.events,

    // Format for daily chart (grouped by day)
    dailyChartData: (state): { date: string; name: string; workingHours: number; breakTime: number }[] => {
      // If no week range set return empty
      if (!state.currentWeekStart || !state.currentWeekEnd) {
        return [];
      }

      // Create array for 7 days of the week
      const dailyMap = new Map<string, {workingHours: number, breakTime: number, dayOfWeek: number}>();
      
      const startDate = new Date(state.currentWeekStart);
      
      // Initialize weekday array
      for (let i = 0; i < 7; i++) {
        const currentDay = new Date(state.currentWeekStart + (i * 24 * 60 * 60 * 1000));
        
        const dateKey = currentDay.toISOString().split("T")[0];
        const dow = currentDay.getUTCDay();
        // Convert Sunday (0) to 7 (Monday)
        const dayOfWeek = dow === 0 ? 7 : dow;
        
        dailyMap.set(dateKey!, {workingHours: 0, breakTime: 0, dayOfWeek});
      }

      // Process events if they exist
      if (state.events && state.events.length > 0) {
        state.events.forEach(event => {
          const eventStart = new Date(event.startTimestamp);
          const eventEnd = new Date(event.endTimestamp);
          
          // Start from the beginning of the day containing event start (UTC)
          const currentDate = new Date(Date.UTC(
            eventStart.getUTCFullYear(),
            eventStart.getUTCMonth(),
            eventStart.getUTCDate(),
            0, 0, 0, 0
          ));
          
          // Iterate through each day the event touches
          while (currentDate <= eventEnd) {
            const dateKey = currentDate.toISOString().split("T")[0];
            
            // Only process if this day is in our week range
            if (dailyMap.has(dateKey!)) {
              // Calculate this day's boundaries (UTC)
              const dayStart = new Date(currentDate);
              const dayEnd = new Date(Date.UTC(
                currentDate.getUTCFullYear(),
                currentDate.getUTCMonth(),
                currentDate.getUTCDate() + 1,
                0, 0, 0, 0
              ));
              
              // Find overlap between event and this specific day
              const overlapStart = new Date(Math.max(eventStart.getTime(), dayStart.getTime()));
              const overlapEnd = new Date(Math.min(eventEnd.getTime(), dayEnd.getTime()));
              
              // Calculate hours for this day only
              const hours = (overlapEnd.getTime() - overlapStart.getTime()) / (1000 * 60 * 60);
              
              const dayData = dailyMap.get(dateKey!);
              if (dayData && hours > 0) {
                if (event.break) {
                  dayData.breakTime += hours;
                } else {
                  dayData.workingHours += hours;
                }
              }
            }
            
            // Move to next day
            currentDate.setUTCDate(currentDate.getUTCDate() + 1);
          }
        });
      }

      // Convert to array and sort Monday-Sunday
      const dayNames = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
      
      return Array.from(dailyMap.entries())
        .map(([date, data]) => ({
          date: date,
          name: dayNames[data.dayOfWeek - 1] || 'Unknown', // dayOfWeek is 1-7, array is 0-6
          workingHours: Number(data.workingHours.toFixed(2)),
          breakTime: Number(data.breakTime.toFixed(2)),
          sortOrder: data.dayOfWeek
        }))
        .sort((a, b) => a.sortOrder - b.sortOrder)
        .map(({ date, name, workingHours, breakTime }) => ({
          date,
          name,
          workingHours,
          breakTime
        }));
    }
  },

  actions: {
    setWeekRange(startTs: number, endTs: number): void {
      this.currentWeekStart = startTs;
      this.currentWeekEnd = endTs;
    },

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