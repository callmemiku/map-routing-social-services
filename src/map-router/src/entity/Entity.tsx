export type Event = {
    event: EventDTO;
    building: Building;
    info: string;
}
export type EventDTO = {
    name: string;
    type: string;
    registrationDatetime, resolvedDatetime: string;
    region: string;
    unom: string;
    address: string;
    eventEndedDatetime: string;
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
