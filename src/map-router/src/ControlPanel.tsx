import React, {useEffect, useState} from "react";

function ControlPanel(props) {
    const [visibility, setVisibility] = useState({
            urgent: true,
            tenHours: true,
            threeHours: true,
            any: true,
            oneDay: true,
            ods: true,
            odsRest: true,
        }
    );

    useEffect(() => {
        const visibilityState = Object.fromEntries(
            Object.entries(visibility).map(([k, v]) => [k, v ? "visible" : "none"])
        );
        props.onChange(visibilityState);
    }, [visibility]);

    const onVisibilityChange = (name, value) => {
        setVisibility({...visibility, [name]: value});
    };

    return (
        <div className={'control-panel'}>
            <h3>Слои</h3>
            <h4 style={{marginRight: '10px'}}>
                <input type="checkbox"
                       checked={visibility["any"]}
                       onChange={evt => onVisibilityChange("any", evt.target.checked)}

                /> Любое время устранения
            </h4>
            <h4>
                <input type="checkbox"
                       checked={visibility["oneDay"]}
                       onChange={evt => onVisibilityChange("oneDay", evt.target.checked)}

                /> Устранение аварии до 1 суток
            </h4>
            <h4 style={{marginRight: '10px'}}>
                <input type="checkbox"
                       checked={visibility["tenHours"]}
                       onChange={evt => onVisibilityChange("tenHours", evt.target.checked)}

                /> Устранение аварии до 10 часов
            </h4>
            <h4>
                <input type="checkbox"
                       checked={visibility["threeHours"]}
                       onChange={evt => onVisibilityChange("threeHours", evt.target.checked)}

                /> Устранение аварии до 3 часов
            </h4>
            <h4 style={{marginRight: '10px'}}>
                <input type="checkbox"
                       checked={visibility["urgent"]}
                       onChange={evt => onVisibilityChange("urgent", evt.target.checked)}
                /> Срочное устранение аварии
            </h4>
            <h4 style={{marginRight: '10px'}}>
                <input type="checkbox"
                       checked={visibility["ods"]}
                       onChange={evt => onVisibilityChange("ods", evt.target.checked)}
                /> ОДС
            </h4>
            <h4 style={{marginRight: '10px'}}>
                <input type="checkbox"
                       checked={visibility["odsRest"]}
                       onChange={evt => onVisibilityChange("odsRest", evt.target.checked)}
                /> Связь объектов с ОДС
            </h4>
        </div>
    )
}

export default React.memo(ControlPanel);