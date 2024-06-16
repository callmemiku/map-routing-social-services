import {CircleLayer, LineLayer} from "mapbox-gl";

export const odsStyle: LineLayer = {
    type: "line",
    id: "ods-conns",
    paint: {
        "line-width": 4,
        "line-blur": 2,
        "line-color": 'gray',
        "line-opacity": 1
    }
}

export const odsStylePoints: CircleLayer = {
    type: "circle",
    id: "ods-conns-points",
    paint: {
        'circle-radius': 15,
        'circle-color': 'gray'
    }
}

export const odsStylePointsBuildings: CircleLayer = {
    type: "circle",
    id: "ods-conns-points-buildings",
    paint: {
        'circle-radius': 9,
        'circle-color': 'gray'
    }
}

export const layerStyleRED: CircleLayer = {
    id: 'red',
    type: 'circle',
    paint: {
        'circle-radius': 10,
        'circle-color': 'red'
    }
};

export const layerStyleYELLOW: CircleLayer = {
    id: 'yellow',
    type: 'circle',
    paint: {
        'circle-radius': 10,
        'circle-color': 'yellow'
    }
};

export const layerStyleGREEN: CircleLayer = {
    id: 'green',
    type: 'circle',
    paint: {
        'circle-radius': 10,
        'circle-color': 'green'
    }
};