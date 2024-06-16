import React, {useEffect, useState} from "react";

function ControlPanel(props) {
    const [visibility, setVisibility] = useState({
            low:  true,
            mid:  true,
            high: true,
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
                       checked={visibility["low"]}
                       onChange={evt => onVisibilityChange("low", evt.target.checked)}

                /> Низкий приоритет
            </h4>
            <h4>
                <input type="checkbox"
                       checked={visibility["mid"]}
                       onChange={evt => onVisibilityChange("mid", evt.target.checked)}

                /> Средний приоритет
            </h4>
            <h4 style={{marginRight: '10px'}}>
                <input type="checkbox"
                       checked={visibility["high"]}
                       onChange={evt => onVisibilityChange("high", evt.target.checked)}
                /> Высокий приоритет
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