export interface EventResponse {
    eventId: number;
    name: string;
    description: string;
    startTimestamp: string;
    endTimestamp: string;
    break: boolean
}

export interface NewEventRequest {
    name: string;
    description: string;
    startTimestamp: string;
    endTimestamp: string;
    isBreak: boolean,
    userId: number
}

export interface EditEventRequest {
    name: string;
    description: string;
    startTimestamp: string;
    endTimestamp: string;
    isBreak: boolean,
    userId: number,
    eventId: number
}

export interface FrontEndEvent extends EventResponse {
  start: number         // für Vuetify & dragging
  end: number
  startText: string     // Anzeige
  endText: string
  timed: true
  color: string
}
