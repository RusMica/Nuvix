import "./Events.css";
import {useState, useEffect, useCallback} from "react";
//import Papa from 'papaparse';
import * as XLSX from "xlsx";
import {motion} from "framer-motion";
import Footer from "../footer/Footer";
import {Navbar} from "../navbar/Navbar";
import "./handleEvents";
import {createEvento, createParticipantsFromList, uploadPDF, uploadXLSX, finishEvento, listarEventos, resendInvitations} from "./handleEvents";

export function Events({eventos, setEventos}) {
    const [registrarEventoVisible, setRegistrarEventoVisible] = useState(false);
    const [nuevoEvento, setNuevoEvento] = useState("");
    const [fecha, setFecha] = useState("");
    const [dataFile, setDataFile] = useState(null);
    const [pdfFile, setPdfFile] = useState(null);
    const [parsedData, setParsedData] = useState(null);
    const [filtroEvento, setFiltroEvento] = useState("");

    const cargarEventos = useCallback(async () => {
        try {
            const eventosObtenidos = await listarEventos();
            setEventos(eventosObtenidos || []);
        } catch (error) {
            console.error("Error al cargar los eventos:", error);
            alert(`No se pudieron cargar los eventos: ${error.message}`);
        }
    }, [setEventos]);

    useEffect(() => {
        cargarEventos();
    }, [cargarEventos]);

    const parseXLSX = (file) => {
        const reader = new FileReader();
        reader.onload = (e) => {
            const data = new Uint8Array(e.target.result);
            const workbook = XLSX.read(data, {type: "array"});
            const sheetName = workbook.SheetNames[0];
            const worksheet = workbook.Sheets[sheetName];
            const rows = XLSX.utils.sheet_to_json(worksheet, {header: 1, defval: ""});
            const headerRow = rows[0] || [];
            const normalize = str => str && str.toString().trim().toLowerCase()
                .replace(/\s+/g, "").normalize("NFD")
                .replace(/[\u0300-\u036f]/g, "");
            const requiredColumnsRaw = ["Apellido", "Nombre", "DNI", "Email", "Telefono"];
            const requiredColumns = requiredColumnsRaw.map(normalize);
            const normalizedHeaders = headerRow.map(normalize);
            const headerMap = {};
            normalizedHeaders.forEach((h, i) => { headerMap[h] = headerRow[i]; });
            const missingColumns = requiredColumns.filter(col => !normalizedHeaders.includes(col));
            if (missingColumns.length > 0) {
                alert(`El archivo XLSX debe contener las columnas: ${requiredColumnsRaw.join(", ")}\nDetectadas: ${headerRow.join(", ")}\nFaltan (normalizados): ${missingColumns.join(", ")}`);
                setParsedData(null);
                setDataFile(null);
                return;
            }
            const colMap = {};
            requiredColumns.forEach((col, i) => {
                if (headerMap[col]) colMap[requiredColumnsRaw[i]] = headerMap[col];
            });
            const requiredColumnsMandatory = ["Apellido", "Nombre", "DNI", "Email"];
            const dataRows = rows.slice(1)
                .filter(row => row.length > 0 && row.some(cell => cell !== "" && cell !== null && cell !== undefined))
                .map(row => {
                    if (row.length < headerRow.length) {
                        return [...row, ...Array(headerRow.length - row.length).fill("")];
                    }
                    return row;
                });
            const data_ = dataRows.map(row => {
                const obj = {};
                Object.entries(colMap).forEach(([key, realCol]) => {
                    const idx = headerRow.indexOf(realCol);
                    obj[realCol] = row[idx] !== undefined ? row[idx] : "";
                });
                return obj;
            }).filter(row => requiredColumnsMandatory.every(col => {
                const realCol = colMap[col];
                return row[realCol] !== undefined && row[realCol] !== null && row[realCol].toString().trim() !== "";
            }));
            if (data_.length === 0) {
                alert(`El archivo XLSX no contiene filas válidas con todos los campos requeridos.\nFilas leídas: ${dataRows.length}`);
                setParsedData(null);
                setDataFile(null);
            } else {
                setParsedData(data_);
            }
        };
        reader.readAsArrayBuffer(file);
    };

    const handleFileChange = async (file) => {
        if (!file) return;
        if (file.name.endsWith(".xlsx")) {
            parseXLSX(file);
        }
        else {
            alert("Formato no soportado. Solo se permiten .xlsx");
            setDataFile(null);
        }
        setDataFile(file);
    };

    const handlePdfChange = (file) => {
        if (!file || !file.name.endsWith(".pdf")) {
            alert("Solo se permiten archivos en formato PDF");
            return;
        }
        setPdfFile(file);
    };

    const handleConfirmarRegistro = async () => {
        const nombreNormalizado = nuevoEvento.trim();
        if (!nombreNormalizado || !fecha || !dataFile || !pdfFile) {
            alert("Todos los campos son obligatorios.");
            return;
        }
        if (eventos.some(e => e.nombre.toLowerCase() === nombreNormalizado.toLowerCase())) {
            alert("Ya existe un evento con este nombre.");
            return;
        }

        try {
            const fechaISO = new Date(fecha).toISOString();
            const eventoData = { nombre: nombreNormalizado, fecha: fechaISO };

            const nuevoEventoCreado = await createEvento(eventoData);
            if (nuevoEventoCreado && nuevoEventoCreado.id) {
                const eventoId = nuevoEventoCreado.id;
                await uploadXLSX(eventoId, dataFile);
                await uploadPDF(eventoId, pdfFile);

                await cargarEventos();

                setNuevoEvento("");
                setFecha("");
                setDataFile(null);
                setPdfFile(null);
                setParsedData(null);
                setRegistrarEventoVisible(false);
                alert(`Evento "${nombreNormalizado}" registrado con éxito!`);
            } else {
                alert("Hubo un error al crear el evento.");
            }
        } catch (error) {
            console.error("Error en el registro del evento:", error);
            alert(`Hubo un error en el registro: ${error.message}`);
        }
    };

    const descargarPlantilla = async () => {
        try {
            const token = localStorage.getItem("token");
            const response = await fetch("https://nuvix-el0j.onrender.com/v1/participantes/download-template", {
                method: "GET",
                headers: { "Authorization": `Bearer ${token}` }
            });
            if (!response.ok) throw new Error("Error al descargar la plantilla");
            const blob = await response.blob();
            const url = URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.download = "plantilla-participantes.xlsx";
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
        } catch (e) {
            alert("No se pudo descargar la plantilla.");
        }
    }

    return (
        <>
            <Navbar/>
            <motion.div
                className="page-container"
                initial={{opacity: 0}}
                animate={{opacity: 1}}
                exit={{opacity: 0}}
                transition={{duration: 0.5}}
            >
                <div className="card">
                    <h2>Gestión de Eventos</h2>
                    <button
                        onClick={() => setRegistrarEventoVisible(!registrarEventoVisible)}
                        className={"btn" + (registrarEventoVisible ? " cancel" : "")}
                    >
                        {registrarEventoVisible ? "Cancelar" : "Registrar nuevo evento"}
                    </button>

                    {registrarEventoVisible && (
                        <motion.div
                            className="registro-evento-form"
                            initial={{opacity: 0, y: 20}}
                            animate={{opacity: 1, y: 0}}
                            transition={{duration: 0.5}}
                        >
                            <label className="label" htmlFor={"nombre"}>Nombre del evento</label>
                            <input
                                name="nombre"
                                type="text"
                                placeholder="Evento..."
                                value={nuevoEvento}
                                onChange={(e) => setNuevoEvento(e.target.value)}
                                className="input-field"
                            />

                            <label className="label" htmlFor={"fecha"}>Fecha del evento</label>
                            <input type={"date"} className="input-field" style={{marginBottom: "10px"}}
                                   onChange={(e) => setFecha(e.target.value)}
                                   required={true}
                            />

                            <button className="btn" onClick={descargarPlantilla}>Descarga plantilla de participantes</button>

                            <label className="file-input-label">
                                Seleccionar archivo XLSX (participantes)
                                <input
                                    type="file"
                                    accept=".xlsx"
                                    onChange={(e) => handleFileChange(e.target.files[0])}
                                    style={{display: "none"}}
                                />
                            </label>
                            {dataFile && <p className="file-name">{dataFile.name}</p>}

                            <label className="file-input-label">
                                Seleccionar archivo PDF (itinerario)
                                <input
                                    type="file"
                                    accept=".pdf"
                                    onChange={(e) => handlePdfChange(e.target.files[0])}
                                    style={{display: "none"}}
                                />
                            </label>
                            {pdfFile && <p className="file-name">{pdfFile.name}</p>}

                            <button onClick={handleConfirmarRegistro} className="btn">Confirmar registro</button>
                        </motion.div>
                    )}

                    <div className="search-container">
                        <input
                            type="text"
                            placeholder="Buscar evento..."
                            value={filtroEvento}
                            onChange={(e) => setFiltroEvento(e.target.value)}
                            className="input-field"
                        />
                    </div>

                    <h3>Eventos Registrados</h3>
                    <ul className="eventos-list">
                        {eventos.length > 0 ? (
                            eventos.filter(ev => ev.nombre.toLowerCase().includes(filtroEvento.toLowerCase())).map((ev, index) => (
                                <motion.li
                                    key={index}
                                    className="event-item"
                                    initial={{x: -100, opacity: 0}}
                                    animate={{x: 0, opacity: 1}}
                                    transition={{delay: index * 0.1, type: "spring", stiffness: 100}}
                                >
                                    <span className="event-text">
                                        {ev.nombre} ({(() => {
                                            const count = ev.participantes?.length || 0;
                                            if (count === 0) return "Sin participantes";
                                            if (count === 1) return "1 participante";
                                            return `${count} participantes`;
                                        })()})
                                    </span>
                                    <button
                                        onClick={async () => {
                                            const hasParticipants = ev.participantes?.length > 0;
                                            try {
                                                if (hasParticipants) {
                                                    await resendInvitations(ev.id);
                                                    alert("Invitaciones reenviadas con éxito.");
                                                } else {
                                                    if (!ev.listaParticipantes) {
                                                        alert("Error: No se encontró la ruta del archivo de participantes.");
                                                        return;
                                                    }
                                                    await createParticipantsFromList(ev.id, ev.listaParticipantes);
                                                    alert("¡Proceso de envío de invitaciones iniciado con éxito!");
                                                    await cargarEventos();
                                                }
                                            } catch (error) {
                                                alert(`Error: ${error.message}`);
                                            }
                                        }}
                                        className={`btn ${ev.participantes?.length > 0 ? "btn-resend" : "btn-pending"}`}
                                    >
                                        {ev.participantes?.length > 0 ? "Reenviar invitaciones" : "Enviar invitaciones"}
                                    </button>
                                    <button
                                        onClick={async () => {
                                            try {
                                                await finishEvento(ev.id);
                                                setEventos(eventosAnteriores =>
                                                    eventosAnteriores.filter(evento => evento.id !== ev.id)
                                                );
                                                alert("Evento finalizado con éxito.");
                                            } catch (error) {
                                                alert(`Error al finalizar el evento: ${error.message}`);
                                            }
                                        }}
                                        className="btn"
                                    >
                                        Finalizar evento
                                    </button>
                                </motion.li>
                            ))
                        ) : (
                            <p className="no-events-message">No se encontraron eventos.</p>
                        )}
                    </ul>
                </div>
            </motion.div>
            <Footer/>
        </>
    );
}