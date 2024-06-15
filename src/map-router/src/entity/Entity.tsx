export type Event = {
    event: EventDTO;
    building: Building;
    info: string;
}
export type EventDTO = {
    name: string;
    type: string;
    //LocalDateTime registrationDatetime, resolvedDatetime;
    region: string;
    unom: string;
    address: string;
    //LocalDateTime eventEndedDatetime;
}

export type Building = {
    unom, type: string;
    centerCoordinates: string;
    workingHours, efficiency, odsIdentity, address: string;
    weightedEfficiency, coolingSpeed: number;
}

export type Coordinates = {
    coordinates: number[];
    type: string;
}
