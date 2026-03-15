import './Payment.css';
import {useEffect, useState} from "react";
import {initMercadoPago, Wallet} from "@mercadopago/sdk-react";
import {Navbar} from "../navbar/Navbar";
import Footer from "../footer/Footer";

const API_BASE_URL = "https://sistemadeverificacion.onrender.com/v1";
const MERCADOPAGO_PUBLIC_KEY = "APP_USR-bc98dcba-e77e-4426-af29-5f486b47fd68";

export function Payment() {
    const [preferenceId, setPreferenceId] = useState(null);
    const [selectedLicense, setSelectedLicense] = useState(null);
    const [selectedValue, setSelectedValue] = useState(null);
    const [cantidad, setCantidad] = useState(1);
    const [showContactForm, setShowContactForm] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [contactForm, setContactForm] = useState({
        name: '',
        email: '',
        message: ''
    });

    // Initialize with hardcoded plans as a fallback or initial structure
    const initialPlans = {
        individual: [
            {id: "LIC-PREPAGA-CHICA", title: "Licencia para eventos pequeños", price: 500, tipoPreferencia: "COMPRA_LICENCIA_PREPAGA_CHICA"},
            {id: "LIC-PREPAGA-MEDIANA", title: "Licencia para eventos medianos", price: 800, tipoPreferencia: "COMPRA_LICENCIA_PREPAGA_MEDIANA"},
            {id: "LIC-PREPAGA-GRANDE", title: "Licencia para eventos grandes", price: 1000, tipoPreferencia: "COMPRA_LICENCIA_PREPAGA_GRANDE"},
            {id: "LIC-PREPAGA-MASIVA", title: "Licencia para eventos masivos", price: 1500, tipoPreferencia: "COMPRA_LICENCIA_PREPAGA_MASIVA"}
        ],
        comun: [
            { id: 'comm_monthly', title: 'Licencia Común Mensual', price: 1500, tipoPreferencia: "SUSCRIPCION_PLAN_COMMON" },
        ],
        profesional: [
            { id: 'pro_monthly', title: 'Plan Profesional Mensual', price: 5000, tipoPreferencia: "SUSCRIPCION_PLAN_PROFESSIONAL" },
        ],
        corporativo: [
            { id: 'corp_contact', title: 'Plan Corporativo Personalizado', price: 'contact', tipoPreferencia: "SUSCRIPCION_PLAN_CORPORATE" },
        ]
    };
    const [licensePlans, setLicensePlans] = useState(initialPlans);

    const formatPlansFromApi = (settings) => {
        if (!Array.isArray(settings)) {
            throw new Error("La respuesta del servidor no es una lista de planes válida.");
        }

        const apiIndividual = settings
            .filter(s => s?.settingKey?.startsWith('PRECIO_LICENCIA_PREPAGA'))
            .map(s => {
                const size = s.settingKey.split('_').pop();
                return {
                    id: s.settingKey,
                    title: `Licencia para eventos ${size.toLowerCase()}`,
                    price: parseFloat(s.settingValue),
                    tipoPreferencia: `COMPRA_LICENCIA_PREPAGA_${size}`
                };
            }).sort((a, b) => a.price - b.price);

        const apiComun = settings
            .filter(s => s?.settingKey === 'PRECIO_SUSCRIPCION_PLAN_COMMON')
            .map(s => ({
                id: 'comm_monthly',
                title: 'Licencia Común Mensual',
                price: parseFloat(s.settingValue),
                tipoPreferencia: "SUSCRIPCION_PLAN_COMMON"
            }));

        const apiProfesional = settings
            .filter(s => s?.settingKey === 'PRECIO_SUSCRIPCION_PLAN_PROFESSIONAL')
            .map(s => ({
                id: 'pro_monthly',
                title: 'Plan Profesional Mensual',
                price: parseFloat(s.settingValue),
                tipoPreferencia: "SUSCRIPCION_PLAN_PROFESSIONAL"
            }));

        setLicensePlans(prevPlans => ({
            individual: apiIndividual.length > 0 ? apiIndividual : prevPlans.individual,
            comun: apiComun.length > 0 ? apiComun : prevPlans.comun,
            profesional: apiProfesional.length > 0 ? apiProfesional : prevPlans.profesional,
            corporativo: prevPlans.corporativo, // Corporate is always static
        }));
    };

    useEffect(() => {
        initMercadoPago(MERCADOPAGO_PUBLIC_KEY, { locale: "es-AR" });

        const fetchLicensePlans = async () => {
            setLoading(true);
            setError(null);
            try {
                const response = await fetch(`${API_BASE_URL}/settings/all`, {
                    headers: {
                        "Authorization": `Bearer ${localStorage.getItem("token")}`
                    }
                });
                if (!response.ok) {
                    throw new Error(`Error al cargar los planes: ${response.status} ${response.statusText}`);
                }
                const settings = await response.json();
                formatPlansFromApi(settings);
            } catch (err) {
                console.error("Error in fetchLicensePlans:", err);
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchLicensePlans();
    }, []);

    const handleSelect = (e) => {
        setSelectedValue(e.target.value);
        setPreferenceId(null);
        setSelectedLicense(null);
        setShowContactForm(false);
    }

    const handleContactFormChange = (e) => {
        setContactForm({ ...contactForm, [e.target.name]: e.target.value });
    };

    const handleContactSubmit = (e) => {
        e.preventDefault();
        console.log("Formulario de contacto enviado:", contactForm);
        alert("Gracias por tu mensaje. Nos pondremos en contacto contigo pronto.");
        setShowContactForm(false);
        setContactForm({ name: '', email: '', message: '' });
    };

    const createPreference = (planSeleccionado) => {
        setPreferenceId(null);
        setSelectedLicense({ title: planSeleccionado.title, price: planSeleccionado.price });

        const datosPreferencia = {
            tipoPreferencia: planSeleccionado.tipoPreferencia,
            cantidad: cantidad
        };

        fetch(`${API_BASE_URL}/payment/buy-license`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                 "Authorization": `Bearer ${localStorage.getItem("token")}`
            },
            body: JSON.stringify(datosPreferencia),
        })
            .then(async (res) => {
                const responseText = await res.text();
                if (!res.ok) {
                    try {
                        const errorJson = JSON.parse(responseText);
                        throw new Error(errorJson.message || responseText);
                    } catch (e) {
                        throw new Error(responseText || `Error del servidor: ${res.status}`);
                    }
                }
                if (!responseText) {
                    throw new Error("La respuesta del servidor está vacía pero se esperaba un ID de preferencia.");
                }
                return JSON.parse(responseText);
            })
            .then((data) => {
                setPreferenceId(data.id || data.preferenceId);
            })
            .catch((err) => {
                console.error("Error creando preferencia:", err);
                alert(`No se pudo crear la preferencia de pago: ${err.message}`);
            });
    };

    if (loading) {
        return <div className="payment-container"><p className="loading-text">Cargando planes...</p></div>;
    }

    if (error) {
        return <div className="payment-container"><p className="error-text">Error al cargar los planes: {error}</p></div>;
    }

    return (
        <>
            <Navbar/>
                <div className="payment-container">
                    <div className="payment-card">
                        <h1 className="payment-title">Adquirir Licencia</h1>
                        <p className="payment-description">
                            Con tu licencia activa podrás gestionar el sistema de control de
                            ingresos y egresos de tus eventos sin limitaciones.
                        </p>
                        <div className="select-container">
                            <select id="plan" className="select-menu" onChange={handleSelect}>
                                <option value="">-- Selecciona una licencia --</option>
                                <option value="individual">Licencia individual</option>
                                <option value="comun">Plan Común</option>
                                <option value="profesional">Plan Profesional</option>
                                <option value="corporativo">Plan Corporativo</option>
                            </select>
                            <input name="cantidad" type="number" className="input-number"
                                   onChange={(e) => setCantidad(e.target.value)}
                                   value={cantidad}
                                   min="1" disabled={selectedValue === "" || selectedValue === "corporativo"}/>
                        </div>
                        <div className="license-options">
                            {selectedValue && licensePlans[selectedValue] && licensePlans[selectedValue].map((plan) => (
                                <div
                                    key={plan.id}
                                    className="license-option"
                                    onClick={() => {
                                        if (plan.price !== 'contact') {
                                            createPreference(plan);
                                            setShowContactForm(false);
                                        } else {
                                            setShowContactForm(true);
                                            setPreferenceId(null);
                                            setSelectedLicense(null);
                                        }
                                    }}
                                >
                                    <h2>{plan.title}</h2>
                                    <p>{plan.price !== 'contact' ? `$${plan.price} ARS` : 'Contáctanos'}</p>
                                </div>
                            ))}
                        </div>

                        {showContactForm && (
                            <div className="contact-form-container">
                                <h2 className="contact-form-title">Contacto Corporativo</h2>
                                <p className="contact-form-description">
                                    Completa el formulario y nuestro equipo de ventas se pondrá en contacto contigo.
                                </p>
                                <form onSubmit={handleContactSubmit} className="contact-form">
                                    <div className="form-group">
                                        <label htmlFor="name">Nombre Completo</label>
                                        <input
                                            type="text"
                                            id="name"
                                            name="name"
                                            className="form-input"
                                            value={contactForm.name}
                                            onChange={handleContactFormChange}
                                            required
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="email">Email de la Empresa</label>
                                        <input
                                            type="email"
                                            id="email"
                                            name="email"
                                            className="form-input"
                                            value={contactForm.email}
                                            onChange={handleContactFormChange}
                                            required
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="message">Mensaje</label>
                                        <textarea
                                            id="message"
                                            name="message"
                                            className="form-textarea"
                                            value={contactForm.message}
                                            onChange={handleContactFormChange}
                                            placeholder="Cuéntanos sobre las necesidades de tu empresa..."
                                            required
                                        />
                                    </div>
                                    <button type="submit" className="submit-btn">Enviar Consulta</button>
                                </form>
                            </div>
                        )}

                        {!showContactForm && (
                            <div className="wallet-container">
                                {preferenceId ? (
                                    <Wallet initialization={{ preferenceId }} />
                                ) : selectedLicense ? (
                                    <p className="loading-text">Cargando opciones de pago para {selectedLicense.title}...</p>
                                ) : (
                                    <p className="loading-text">Selecciona una licencia para continuar.</p>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            <Footer/>
        </>
    );
}
