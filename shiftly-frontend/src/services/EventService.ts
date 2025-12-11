import apiClient from "@/utils/ApiAxios";
import type { EventResponse } from "@/types/Event";


class EventService {
    private resource = "/api/shiftly/event";

    getEventsFromUser(userId: number){
  return apiClient.get<EventResponse[]>(this.resource + "/get/" + userId);
}

}

export default new EventService();
