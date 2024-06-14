import Map, {Marker} from "react-map-gl/maplibre";
import maplibregl from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";
import React, {useEffect, useMemo, useState} from "react";
import "./App.css";
import {Coordinates, Event} from "./entity/Entity";
import {ColumnDef, flexRender, getCoreRowModel, PaginationState, useReactTable} from "@tanstack/react-table";
import {Popup} from 'react-map-gl';

const MAPTILER_API_KEY = import.meta.env.VITE_MAPTILER_API_KEY;
const URL = import.meta.env.BE_URL ?? "localhost:8080/";

const MAPS_DEFAULT_LOCATION = {
    latitude: 55.751244,
    longitude: 37.618423,
    zoom: 11,
};

export const App = () => {

    const [markers, setMarkers] = useState([]);

    const columns = useMemo<ColumnDef<Event>[]>(
        () => [
            {header: "Тип", accessorKey: "event.name"},
            {header: "Источник", accessorKey: "event.type"},
            {header: "Округ", accessorKey: "event.region"},
            {header: "Группа", accessorKey: "building.type"},
            {header: "Адрес", accessorKey: "event.address"},
            {header: "Взвешенный приоритет", accessorKey: "building.weightedEfficiency"},
            {header: "Скорость остывания", accessorKey: "building.coolingSpeed"}
        ], []
    );

    const [{pageIndex, pageSize}, setPagination] = useState<PaginationState>({
        pageIndex: 0,
        pageSize: 10,
    });

    const [tblData, setTblData] = useState<Array<Event>>([]);
    const [total, setTotal] = useState(0);

    function updateMarkers(events: Array<Event>) {
        const b_markers = []
        for (let i = 0; i < events.length; i++) {
            const event = events[i];
            let geo: Coordinates = JSON.parse(event.building
                .centerCoordinates.replace("coordinates=", '"coordinates": ')
                .replace("type=", '"type": ')
                .replace("Point", '"Point"')
            );

            const priority = event.building.weightedEfficiency;
            let color;
            if (priority > 0.75) {
                color = 'red'
            } else if (priority < 0.35) {
                color = 'green'
            } else {
                color = 'yellow'
            }
            b_markers[i] = <Marker
                longitude={geo.coordinates[0]}
                latitude={geo.coordinates[1]}
                color={color}
            />
        }
        setMarkers(b_markers);
    }

    const getPageData = (page: number, size: number) => {
        fetch(
            `http://${URL}update/gather?page=${page}&size=${size}`
        )
            .then(async (response) => {
                var parsed = await response.json();
                setTotal(
                    Number.parseInt(parsed.totalPages)
                )
                return parsed.content;
            })
            .then((json) => {
                setTblData(json as Array<Event>)
                updateMarkers(json as Array<Event>)
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
    });

    const [data, setData] = useState([]);

    const mapTilerMapStyle = useMemo(() => {
        return `https://api.maptiler.com/maps/basic-v2/style.json?key=${MAPTILER_API_KEY}`;
    }, []);

    return (
        <>
            <div className="p-2">
                <div className="h-2"/>
                <table style={{
                    position: "sticky",
                    top: "0",
                    maxWidth: "40vw",
                    minHeight: "90vh",
                    maxHeight: "90vh",
                    zIndex: "-1"
                }
                }>
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
                                            padding: "5px",
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
                            <tr key={row.id}>
                                {row.getVisibleCells().map((cell) => {
                                    return (
                                        <td
                                            key={cell.id}
                                            style={{
                                                border: "1px solid black",
                                                borderCollapse: "collapse",
                                                padding: "5px",
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
                        );
                    })}
                    </tbody>
                </table>
                <div className="h-2"/>
                <div style={{left: "2vw"}} className="flex items-center gap-2">
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
                    <span className="flex items-center gap-1">
          <div>Page</div>
          <strong>
            {table.getState().pagination.pageIndex + 1} of{" "}
              {table.getPageCount()}
          </strong>
        </span>
                    <span className="flex items-center gap-1">
          | Go to page:
          <input
              type="number"
              defaultValue={table.getState().pagination.pageIndex + 1}
              onChange={(e) => {
                  const page = e.target.value ? Number(e.target.value) - 1 : 0;
                  table.setPageIndex(page);
              }}
              className="border p-1 rounded w-16"
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
                                Show {pageSize}
                            </option>
                        ))}
                    </select>
                </div>
                <div>{table.getRowModel().rows.length} Rows</div>
            </div>
            <Map
                initialViewState={{
                    ...MAPS_DEFAULT_LOCATION,
                }}
                style={{
                    width: "60wh",
                    height: "100vh",
                    position: "absolute",
                    zIndex: "10",
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
                {markers}
            </Map>
        </>
    );
}