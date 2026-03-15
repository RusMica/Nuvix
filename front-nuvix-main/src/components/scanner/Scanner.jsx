import './Scanner.css';
import {useEffect, useRef, useState} from "react";
import {Html5Qrcode} from "html5-qrcode";
import { motion } from "framer-motion";
import {Navbar} from "../navbar/Navbar";
import Footer from "../footer/Footer";
import {verifyEntrada, verifySalida} from "./handleScanner";
import {listarEventos} from "../events/handleEvents";
import {ParticipantModal} from "./ParticipantModal";
import {ErrorModal} from "../errorModal/ErrorModal";

export const Scanner = ({eventos = [], setEventos}) => {
    const [started, setStarted] = useState(false);
    const [eventoSeleccionado, setEventoSeleccionado] = useState("");
    const [verifiedParticipant, setVerifiedParticipant] = useState(null);
    const [errorModalMessage, setErrorModalMessage] = useState("");
    const qrCodeRegionRef = useRef(null);
    const html5QrCodeRef = useRef(null);
    const scannerActiveRef = useRef(false);
    const [flagQR, setFlagQR] = useState("ENTRADA");

    useEffect(() => {
        const cargarEventos = async () => {
            try {
                const eventosObtenidos = await listarEventos();
                setEventos(eventosObtenidos || []);
            } catch (error) {
                console.error("Error al cargar eventos en el scanner:", error);
                setErrorModalMessage("No se pudieron cargar los eventos.");
            }
        };
        cargarEventos();
    }, [setEventos]);

    const resetScanResult = () => {
        setVerifiedParticipant(null);
        setErrorModalMessage("");
    };

    const stopScanner = async () => {
        if (!scannerActiveRef.current || !html5QrCodeRef.current) return;
        try {
            await html5QrCodeRef.current.stop();
            await html5QrCodeRef.current.clear();
            html5QrCodeRef.current = null;
            scannerActiveRef.current = false;
            setStarted(false);
            console.log("Scanner detenido correctamente");
        } catch (err) {
            console.log("Error al detener scanner:", err);
        }
    };

    useEffect(() => {
        window.stopScannerGlobal = stopScanner;
        return () => {
            if (scannerActiveRef.current && html5QrCodeRef.current) {
                html5QrCodeRef.current.stop().catch(console.error);
                html5QrCodeRef.current.clear().catch(console.error);
            }
            scannerActiveRef.current = false;
            html5QrCodeRef.current = null;
            delete window.stopScannerGlobal;
        };
    }, []);

    const startScanner = () => {
        if (!eventoSeleccionado) {
            setErrorModalMessage("Debes seleccionar un evento antes de escanear.");
            return;
        }

        resetScanResult();

        stopScanner().finally(() => {
            const config = { fps: 10, qrbox: 250 };
            const html5QrCode = new Html5Qrcode(qrCodeRegionRef.current.id);
            html5QrCodeRef.current = html5QrCode;

            const qrCodeSuccessCallback = async (decodedText) => {
                stopScanner();
                try {
                    const participantData = flagQR === "ENTRADA"
                        ? await verifyEntrada(decodedText)
                        : await verifySalida(decodedText);

                    if (participantData && participantData.participante) {
                        setVerifiedParticipant({
                            participante: participantData.participante,
                            estadoEntrada: participantData.estado
                        });
                    } else {
                        throw new Error('Respuesta de API inválida o datos del participante no encontrados.');
                    }
                } catch (error) {
                    setErrorModalMessage(error.message || 'Código QR no válido o expirado.');
                    setVerifiedParticipant(null);
                }
            };

            html5QrCode.start({ facingMode: "environment" }, config, qrCodeSuccessCallback)
                .then(() => {
                    scannerActiveRef.current = true;
                    setStarted(true);
                })
                .catch((err) => {
                    console.error("No se pudo iniciar el escáner", err);
                    setErrorModalMessage("No se pudo iniciar el escáner. Asegúrate de dar permisos para usar la cámara.");
                });
        });
    };

    const closeErrorModal = () => {
        setErrorModalMessage("");
    };

    return (
        <>
            <Navbar/>
            <motion.div
                className="page-container"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                transition={{ duration: 0.5 }}
            >
                <div className="card">
                    <h2>{flagQR === "ENTRADA" ? "Escanear Entrada" : "Escanear Salida"}</h2>
                    <div className="btn-container">
                        <button className="btn" onClick={() => setFlagQR("ENTRADA")}>Entrada</button>
                        <button className="btn" onClick={() => setFlagQR("SALIDA")}>Salida</button>
                    </div>
                    <select
                        id="select-evento"
                        value={eventoSeleccionado}
                        onChange={(e) => setEventoSeleccionado(e.target.value)}
                        className="input-field"
                    >
                        <option value="">-- Selecciona un evento --</option>
                        {eventos.map((ev, index) => (
                            <option key={index} value={ev.nombre}>
                                {ev.nombre}
                            </option>
                        ))}
                    </select>

                    <div ref={qrCodeRegionRef} id="reader" className="qr-scan-window"></div>

                    {!started ? (
                        <button
                            onClick={startScanner}
                            className="btn"
                            disabled={!eventoSeleccionado}
                        >
                            Iniciar escaneo
                        </button>
                    ) : (
                        <button onClick={stopScanner} className="btn stop">
                            Detener escaneo
                        </button>
                    )}

                    <ErrorModal message={errorModalMessage} onClose={closeErrorModal} />

                    {verifiedParticipant && (
                        <ParticipantModal
                            participant={verifiedParticipant.participante}
                            estadoEntrada={verifiedParticipant.estadoEntrada}
                            onClose={resetScanResult}
                        />
                    )}
                </div>
            </motion.div>
            <Footer/>
        </>
    );
}