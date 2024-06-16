import {Layer as MapLayer, Map as GeoMap, Source} from "react-map-gl/maplibre";
import maplibregl from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";
import React, {Fragment, useEffect, useMemo, useState} from "react";
import "./App.css";
import {Coordinates, Event, ODSConnections} from "./entity/Entity";
import {ColumnDef, flexRender, getCoreRowModel, PaginationState, Row, useReactTable} from "@tanstack/react-table";
import ControlPanel from "./ControlPanel";
import type {FeatureCollection} from 'geojson';
import {
    layerStyleGREEN,
    layerStyleRED,
    layerStyleYELLOW,
    odsStyle,
    odsStylePoints,
    odsStylePointsBuildings
} from "./Styles";

const MAPTILER_API_KEY = import.meta.env.VITE_MAPTILER_API_KEY;
const URL = import.meta.env.BE_URL ?? "localhost:8080/";

const MAPS_DEFAULT_LOCATION = {
    latitude: 55.751244,
    longitude: 37.618423,
    zoom: 11,
};

export const App = () => {

    const [markers_Red, setMarkersRed]: FeatureCollection = useState(null);
    const [markers_Green, setMarkersGreen]: FeatureCollection = useState(null);
    const [markers_Yellow, setMarkersYellow]: FeatureCollection = useState(null);
    const [ods_Markers, setOdsMarkers]: FeatureCollection = useState(null);
    const [ods_MarkersBuildings, setOdsMarkersBuildings]: FeatureCollection = useState(null);
    const [ods_conns, setOdsConns]: FeatureCollection = useState(null);

    const columns = useMemo<ColumnDef<Event>[]>(
        () => [
            {
                id: 'expander',
                minSize: 10, maxSize: 10,
                header: () => null,
                cell: ({row}) => {
                    return row.getCanExpand() ? (
                        <button style={{
                            padding: "0px 0px",
                            fontSize: "smaller",
                            display: "inline-block",
                            borderRadius: "1px",
                            boxShadow: '0',
                            width: '2px'
                        }}
                                {...{
                                    onClick: row.getToggleExpandedHandler(),
                                    style: {cursor: 'pointer'},
                                }}
                        >
                            {row.getIsExpanded() ? 'v' : '>'}
                        </button>
                    ) : (
                        '🔵'
                    )
                },

            },
            {header: "Тип", accessorKey: "event.name"},
            {header: "Источник", accessorKey: "event.type"},
            {header: "Дата начала", accessorKey: "event.registrationDatetime"},
            {header: "Дата окончания", accessorKey: "event.eventEndedDatetime"},
            {header: "Группа", accessorKey: "building.type"},
            {header: "k", accessorKey: "building.weightedEfficiency"},
            {header: "w, ч", accessorKey: "building.coolingSpeedBelowNormal"}
        ], []
    );

    const [{pageIndex, pageSize}, setPagination] = useState<PaginationState>({
        pageIndex: 0,
        pageSize: 10,
    });

    const [tblData, setTblData] = useState<Array<Event>>([]);
    const [total, setTotal] = useState(0);

    function updateMarkers(events: Array<Event>) {
        const b_markers_yellow = []
        const b_markers_green = []
        const b_markers_red = []

        for (let i = 0; i < events.length; i++) {
            const event = events[i];
            try {
                let geo: Coordinates = JSON.parse(
                    event.building
                        .centerCoordinates
                        .replace("coordinates=", '"coordinates": ')
                        .replace("type=", '"type": ')
                        .replace("Point", '"Point"')
                );

                const priority = event.building.weightedEfficiency;
                if (priority > 0.69) {
                    b_markers_red.push(
                        {
                            "type": "Feature",
                            "geometry": {
                                "type": "Point",
                                "coordinates": [geo.coordinates[0], geo.coordinates[1]]
                            }
                        }
                    )
                } else if (priority < 0.35) {
                    b_markers_green.push(
                        {
                            "type": "Feature",
                            "geometry": {
                                "type": "Point",
                                "coordinates": [geo.coordinates[0], geo.coordinates[1]]
                            }
                        }
                    )
                } else if (priority > 0.34 && priority < 0.7) {
                    b_markers_yellow.push(
                        {
                            "type": "Feature",
                            "geometry": {
                                "type": "Point",
                                "coordinates": [geo.coordinates[0], geo.coordinates[1]]
                            }
                        }
                    )
                }
            } catch (e) {
                console.log(e)
            }
        }
        const red: FeatureCollection = {
            type: "FeatureCollection",
            features: b_markers_red
        };
        setMarkersRed(
            red
        );
        const green: FeatureCollection = {
            type: "FeatureCollection",
            features: b_markers_green
        };
        setMarkersGreen(
            green
        );
        const yellow: FeatureCollection = {
            type: "FeatureCollection",
            features: b_markers_yellow
        };
        setMarkersYellow(
            yellow
        );
    }

    function updateLines(coordinates: Array<ODSConnections>) {

        const ods_markers_new = []
        const ods_lines_new = []
        const ods_buildings = []

        for (let i = 0; i < coordinates.length; i++) {
            var curr = coordinates[i];
            let geo: Coordinates = JSON.parse(
                curr.geoJSON
                    .replace("coordinates=", '"coordinates": ')
                    .replace("type=", '"type": ')
                    .replace("Point", '"Point"')
            );
            ods_markers_new.push(
                {
                    "type": "Feature",
                    "geometry": {
                        "type": "Point",
                        "coordinates": [geo.coordinates[0], geo.coordinates[1]]
                    }
                }
            );

            for (let j = 0; j < curr.connected.length; j++) {
                var arr = curr.connected[j];
                ods_buildings.push(
                    {
                        "type": "Feature",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [arr[0], arr[1]]
                        }
                    }
                );

                ods_lines_new.push(
                    {
                        "type": "Feature",
                        "geometry": {
                            "type": "LineString",
                            "coordinates": [
                                [geo.coordinates[0], geo.coordinates[1]],
                                [arr[0], arr[1]]
                            ]
                        }
                    }
                )
            }


        }
        const ods_collection: FeatureCollection = {
            type: "FeatureCollection",
            features: ods_markers_new
        };
        setOdsMarkers(
            ods_collection
        )

        const ods_buildings_collection: FeatureCollection = {
            type: "FeatureCollection",
            features: ods_buildings
        };
        setOdsMarkersBuildings(
            ods_buildings_collection
        )

        const lines: FeatureCollection = {
            type: "FeatureCollection",
            features: ods_lines_new
        }
        setOdsConns(lines)
    }

    const getPageData = (page: number, size: number) => {
        fetch(
            `http://${URL}update/gather?page=${page}&size=${size}`
        )
            .then(async (response) => {
                var parsed = await response.json();
                setTotal(
                    Number.parseInt(parsed.page.totalPages)
                )
                return parsed;
            })
            .then((json) => {
                console.log(json)
                setTblData(json.page.content as Array<Event>)
                updateMarkers(json.page.content as Array<Event>)
                updateLines(json.connections as Array<ODSConnections>)
            });
    };

    useEffect(() => {
        getPageData(pageIndex, pageSize);
    }, [pageIndex, pageSize]);

    const defaultData = useMemo(() => [], []);

    const pagination = useMemo(
        () => ({
            pageIndex,
            pageSize,
        }),
        [pageIndex, pageSize]
    );

    const table = useReactTable({
        columns,
        data: tblData ?? defaultData,
        pageCount: total,
        state: {
            pagination,
        },
        onPaginationChange: setPagination,
        getCoreRowModel: getCoreRowModel(),
        manualPagination: true,
        debugTable: true,
        getRowCanExpand: (row: Row<TData>) => Boolean
    });

    //region MAPS
    const widths_max = new Map();
    widths_max.set('Источник', '10vw')
    widths_max.set("Тип", '30vw')
    widths_max.set("Округ", '2vw')
    widths_max.set("Группа", '2vw')
    widths_max.set("k", '1vw')
    widths_max.set("w, ч", '1vw')
    widths_max.set("Дата начала", '10vw')
    widths_max.set("Дата окончания", '10vw')

    const place = new Map();
    place.set(0, 'justify')
    place.set(1, 'justify')
    place.set(2, 'center')
    place.set(3, 'center')
    place.set(4, 'center')
    place.set(5, 'center')
    place.set(6, 'center')

    const widths_min = new Map();
    widths_min.set('Источник', '5vw')
    widths_min.set("Тип", '10vw')
    widths_min.set("Округ", '4vw')
    widths_min.set("Группа", '4vw')
    widths_min.set("k", '3vw')
    widths_min.set("w, ч", '3vw')
    widths_min.set("Дата начала", '5vw')
    widths_min.set("Дата окончания", '5vw')
    //endregion

    const mapTilerMapStyle = useMemo(() => {
        return `https://api.maptiler.com/maps/basic-v2/style.json?key=${MAPTILER_API_KEY}`;
    }, []);

    const renderSubComponent = ({row}: { row: Row<Event> }) => {
        return (
            <pre style={{fontSize: 'medium', left: "30px", textAlign: "justify"}}>
            <code>{row.original.info}</code>
    </pre>
        )
    }

    function update() {
        getPageData(0, 10)
        setPagination(
            {
                pageIndex: 0,
                pageSize: 10,
            }
        )
    }

    const [layersVisibility, setLayersVisibility] = React.useReducer(
        (state, updates) => ({...state, ...updates}
        ), {}
    );

    return (
        <>
            <button style={{bottom: "98vh", minWidth: "40vw", maxWidth: "40vw"}} onClick={update}>
                Обновить данные
            </button>
            <div className="p-2">
                <table style={{
                    position: "sticky",
                    top: "1vw",
                    minWidth: "40vw",
                    maxWidth: "40vw",
                    minHeight: "87vh",
                    maxHeight: "87vh",
                    zIndex: "-1 !important",
                    overflowY: "auto"
                }}>
                    <thead>
                    {table.getHeaderGroups().map((headerGroup) => (
                        <tr key={headerGroup.id}>
                            {headerGroup.headers.map((header) => {
                                return (
                                    <th
                                        key={header.id}
                                        colSpan={header.colSpan}
                                        style={{
                                            border: "1px solid black",
                                            borderCollapse: "collapse",
                                            padding: "2px",
                                            maxWidth: `${widths_max.get(header.column.columnDef.header)}`,
                                            minWidth: `${widths_min.get(header.column.columnDef.header)}`,
                                            textAlign: 'center',
                                            fontSize: 'small'
                                        }}
                                    >
                                        {header.isPlaceholder ? null : (
                                            <div>
                                                {flexRender(
                                                    header.column.columnDef.header,
                                                    header.getContext()
                                                )}
                                            </div>
                                        )}
                                    </th>
                                );
                            })}
                        </tr>
                    ))}
                    </thead>
                    <tbody>
                    {table.getRowModel().rows.map((row) => {
                        return (
                            <Fragment key={row.id}>
                                <tr>
                                    {row.getVisibleCells().map((cell) => {
                                        return (
                                            <td
                                                key={cell.id}
                                                style={{
                                                    border: "1px solid black",
                                                    borderCollapse: "collapse",
                                                    padding: "3px",

                                                }}
                                            >
                                                {flexRender(
                                                    cell.column.columnDef.cell,
                                                    cell.getContext()
                                                )}
                                            </td>
                                        );
                                    })}
                                </tr>
                                {row.getIsExpanded() && (
                                    <tr>
                                        <td colSpan={row.getVisibleCells().length}>
                                            {renderSubComponent({row})}
                                        </td>
                                    </tr>
                                )}
                            </Fragment>
                        );
                    })}
                    </tbody>
                </table>

                <div style={{left: "2vw", zIndex: "2", position: "sticky", marginLeft: '5px'}}
                     className="flex items-center gap-2">
                    <button
                        className="border rounded p-1"
                        onClick={() => table.setPageIndex(0)}
                        disabled={!table.getCanPreviousPage()}
                    >
                        {"<<"}
                    </button>
                    <button
                        className="border rounded p-1"
                        onClick={() => table.previousPage()}
                        disabled={!table.getCanPreviousPage()}
                    >
                        {"<"}
                    </button>
                    <button
                        className="border rounded p-1"
                        onClick={() => table.nextPage()}
                        disabled={!table.getCanNextPage()}
                    >
                        {">"}
                    </button>
                    <button
                        className="border rounded p-1"
                        onClick={() => table.setPageIndex(table.getPageCount() - 1)}
                        disabled={!table.getCanNextPage()}
                    >
                        {">>"}
                    </button>
                    <span style={{left: "2vw"}} className="flex items-center gap-1">
          <div>Страница</div>
          <strong>
            {table.getState().pagination.pageIndex + 1} of{" "}
              {table.getPageCount()}
          </strong>
        </span>
                    <span className="flex items-center gap-1" style={{marginLeft: '5px', marginRight: '5px'}}>
             | Перейти на страницу:
          <input
              type="number"
              defaultValue={table.getState().pagination.pageIndex + 1}
              onChange={(e) => {
                  const page = e.target.value ? Number(e.target.value) - 1 : 0;
                  table.setPageIndex(page);
              }}
              style={{width: '30px', marginLeft: '5px'}}
          />
        </span>
                    <select
                        value={table.getState().pagination.pageSize}
                        onChange={(e) => {
                            table.setPageSize(Number(e.target.value));
                        }}
                    >
                        {[10, 20, 30].map((pageSize) => (
                            <option key={pageSize} value={pageSize}>
                                Показать {pageSize}
                            </option>
                        ))}
                    </select>
                </div>
                <div style={{marginLeft: '5px'}}>{table.getRowModel().rows.length} строк</div>
            </div>
            <GeoMap
                initialViewState={{
                    ...MAPS_DEFAULT_LOCATION,
                }}
                style={{
                    width: "60wh",
                    height: "100vh",
                    position: "absolute",
                    zIndex: "10 !important",
                    top: 0,
                    bottom: 0,
                    left: "40%",
                    maxWidth: "60vw",
                    right: 0,
                }}
                hash
                mapLib={maplibregl}
                mapStyle={mapTilerMapStyle}
            >

                <Source key={'ods'} type="geojson" data={ods_Markers}>
                    <MapLayer key='ods' type={'circle'} {...odsStylePoints}
                              layout={{visibility: layersVisibility["ods"]}}/>
                </Source>

                <Source key={'ods-buildings'} type="geojson" data={ods_MarkersBuildings}>
                    <MapLayer key='ods-buildings' type={'circle'} {...odsStylePointsBuildings}
                              layout={{visibility: layersVisibility["odsRest"]}}/>
                </Source>

                <Source key={'ods-lines'} type="geojson" data={ods_conns}>
                    <MapLayer key='ods-lines' type={'circle'} {...odsStyle}
                              layout={{visibility: layersVisibility["odsRest"]}}/>
                </Source>


                <Source key='high' type="geojson" data={markers_Red}>
                    <MapLayer key={'high'} type={'circle'} {...layerStyleRED}
                              layout={{visibility: layersVisibility["high"]}}/>
                </Source>
                <Source key='mid' type="geojson" data={markers_Yellow}>
                    <MapLayer key={'mid'} type={'circle'} {...layerStyleYELLOW}
                              layout={{visibility: layersVisibility["mid"]}}/>
                </Source>
                <Source key={'low'} type="geojson" data={markers_Green}>
                    <MapLayer key='low' type={'circle'} {...layerStyleGREEN}
                              layout={{visibility: layersVisibility["low"]}}/>
                </Source>

            </GeoMap>
            <ControlPanel onChange={setLayersVisibility}/>
        </>
    );
}