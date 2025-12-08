export interface EventResponse {
    eventId: number;
    name: string;
    description: string;
    startTimestamp: string;
    endTimestamp: string;
    break: boolean
}

export interface FrontEndEvent  extends EventResponse {
  start: string
  end: string
  color?: string
}
