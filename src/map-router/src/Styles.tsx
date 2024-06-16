import {CircleLayer, LineLayer, SymbolLayer} from "mapbox-gl";

export const popupLayer: SymbolLayer = {
    id: "popup-ods-layer",
    type: "symbol",
    source: "popup-ods-layer",
    layout: {
        'icon-image': 'custom-marker',
        'icon-overlap': 'always'
    }
}

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

export const layerStyleUrgent: CircleLayer = {
    id: 'urgent',
    type: 'circle',
    paint: {
        'circle-radius': 10,
        'circle-color': '#FF0000'
    }
};

export const layerStyleThreeHours: CircleLayer = {
    id: 'threeHours',
    type: 'circle',
    paint: {
        'circle-radius': 10,
        'circle-color': '#FD620B'
    }
};

export const layerStyleTenHours: CircleLayer = {
    id: 'tenHours',
    type: 'circle',
    paint: {
        'circle-radius': 10,
        'circle-color': '#E7A66A'
    }
};

export const layerStyleOneDay: CircleLayer = {
    id: 'oneDay',
    type: 'circle',
    paint: {
        'circle-radius': 10,
        'circle-color': '#FFD600'
    }
};

export const layerStyleAny: CircleLayer = {
    id: 'any',
    type: 'circle',
    paint: {
        'circle-radius': 10,
        'circle-color': '#D3D98E'
    }
};