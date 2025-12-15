import apiClient from "@/utils/ApiAxios";
import type { EventResponse } from "@/types/Event";


class EventService {
  private resource = "/api/shiftly/event";
  private resourceTracker = "/api/shiftly/timeTracker"

  getEventsFromUser(userId: number){
    return apiClient.get<EventResponse[]>(this.resource + "/get/" + userId);
  }

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
}

export default new EventService();
