import {Map as GeoMap, Marker} from "react-map-gl/maplibre";
import maplibregl from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";
import React, {Fragment, useEffect, useMemo, useState} from "react";
import "./App.css";
import {Coordinates, Event} from "./entity/Entity";
import {ColumnDef, flexRender, getCoreRowModel, PaginationState, Row, useReactTable} from "@tanstack/react-table";

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
            {
                id: 'expander',
                minSize: 10, maxSize: 10,
                header: () => null,
                cell: ({ row }) => {
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
                                style: { cursor: 'pointer' },
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
            {header: "Округ", accessorKey: "event.region"},
            {header: "Группа", accessorKey: "building.type"},
            {header: "Адрес", accessorKey: "event.address"},
            {header: "k", accessorKey: "building.weightedEfficiency"},
            {header: "w", accessorKey: "building.coolingSpeed"}
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
            try {
                let geo: Coordinates = JSON.parse(event.building
                    .centerCoordinates.replace("coordinates=", '"coordinates": ')
                    .replace("type=", '"type": ')
                    .replace("Point", '"Point"')
                );

                const priority = event.building.weightedEfficiency;
                let color;
                if (priority > 0.85) {
                    color = 'red'
                } else if (priority < 0.35) {
                    color = 'green'
                } else if (priority > 0.34 && priority < 0.86) {
                    color = 'yellow'
                }
                b_markers[i] = <Marker
                    longitude={geo.coordinates[0]}
                    latitude={geo.coordinates[1]}
                    color={color}
                />
            } catch (e) {
                console.log(e)
            }
        }
        console.log(b_markers)
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
        getRowCanExpand: (row: Row<TData>) => Boolean
    });

    const [data, setData] = useState([]);

    const widths_max = new Map();
    widths_max.set('Источник', '10vw')
    widths_max.set("Тип", '10vw')
    widths_max.set("Округ", '2vw')
    widths_max.set("Группа", '2vw')
    widths_max.set("Адрес", '10vw')
    widths_max.set("k", '1vw')
    widths_max.set("w", '1vw')

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
    widths_min.set("Тип", '5vw')
    widths_min.set("Округ", '4vw')
    widths_min.set("Группа", '4vw')
    widths_min.set("Адрес", '10vw')
    widths_min.set("k", '3vw')
    widths_min.set("w", '3vw')

    const mapTilerMapStyle = useMemo(() => {
        return `https://api.maptiler.com/maps/basic-v2/style.json?key=${MAPTILER_API_KEY}`;
    }, []);

    const renderSubComponent = ({ row }: { row: Row<Event> }) => {
        return (
            <pre style={{ fontSize: 'medium', left: "30px", textAlign: "justify" }}>
            <code>{row.original.info}</code>
    </pre>
        )
    }

    return (
        <>
            <div className="p-2">
                <table style={{
                    position: "sticky",
                    top: "0vw",
                    maxWidth: "40vw",
                    minHeight: "90vh",
                    maxHeight: "90vh",
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
                                        {/* 2nd row is a custom 1 cell row */}
                                        <td colSpan={row.getVisibleCells().length}>
                                            {renderSubComponent({ row })}
                                        </td>
                                    </tr>
                                )}
                            </Fragment>
                        );
                    })}
                    </tbody>
                </table>

                <div style={{left: "2vw", zIndex: "2", position: "sticky"}} className="flex items-center gap-2">
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
                    <span className="flex items-center gap-1">
          | Перейти на страницу:
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
                                Показать {pageSize}
                            </option>
                        ))}
                    </select>
                </div>
                <div>{table.getRowModel().rows.length} строк</div>
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
                {markers}
            </GeoMap>
        </>
    );
}