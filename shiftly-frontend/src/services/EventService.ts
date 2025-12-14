import apiClient from "@/utils/ApiAxios";
import type { EventResponse, NewEventRequest } from "@/types/Event";


class EventService {
    private resource = "/api/shiftly/event";

    getEventsFromUser(userId: number){
  return apiClient.get<EventResponse[]>(this.resource + "/get/" + userId);}

  createCalendarEvent(newEvent: NewEventRequest) {
    return apiClient.post<boolean>(this.resource + "/create", newEvent);
  }
}

export default new EventService();
