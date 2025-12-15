import apiClient from "@/utils/ApiAxios";
import type { EventResponse, NewEventRequest, EditEventRequest } from "@/types/Event";


class EventService {
  private resource = "/api/shiftly/event";
  private resourceTracker = "/api/shiftly/timeTracker"

  startTracking(name: string, description: string, isBreak: boolean, startTimestamp: number) {
    return apiClient.post<Number>(this.resourceTracker + "/start", {
      name,
      description,
      startTimestamp,
      isBreak 
    })
  }

  stopTracking(id: number, endTimestamp: number) {
    return apiClient.put<Number>(this.resourceTracker + "/stop", {
      eventId: id,
      endTimestamp
    })
  }

  getEventsFromUser(userId: number){
    return apiClient.get<EventResponse[]>(this.resource + "/get/" + userId);
  }

  createCalendarEvent(newEvent: NewEventRequest) {
    return apiClient.post<boolean>(this.resource + "/create", newEvent);
  }

  editCalendarEvent(editEvent: EditEventRequest) {
    return apiClient.post<boolean>(this.resource + "/update", editEvent);
  }

  deleteCalendarEvent(eventId: number) {
    return apiClient.delete<boolean>(this.resource + "/delete/" + eventId);
  }
}

export default new EventService();
