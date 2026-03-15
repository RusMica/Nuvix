import React, { useState, useCallback, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import NuvixLogo from "../../img/logo2-Photoroom.png";
import { getUserData } from "../../services/user";
import './Navbar.css';

export const Navbar = React.memo(() => {
    const navigate = useNavigate();
    const [isMobileMenuOpen, setMobileMenuOpen] = useState(false);
    const [userRole, setUserRole] = useState(null);

    useEffect(() => {
        const fetchUserData = async () => {
            const userData = await getUserData();
            if (userData) {
                setUserRole(userData.rol);
                localStorage.setItem("userRole", userData.rol); // Keep localStorage up-to-date
            }
        };

        fetchUserData();
    }, []);

    const handleLogout = useCallback(async () => {
        if (window.stopScannerGlobal && typeof window.stopScannerGlobal === "function") {
            try {
                await window.stopScannerGlobal();
            } catch (err) {
                console.warn("Scanner ya estaba detenido:", err);
            }
        }
        alert("Sesión cerrada. Redirigiendo a la página de inicio.");
        localStorage.removeItem("token");
        localStorage.removeItem("userRole");
        navigate("/");
    }, [navigate]);

    const toggleMobileMenu = useCallback(() => {
        setMobileMenuOpen(prev => !prev);
    }, []);

    const isTrialUser = userRole === 'USER_TRIAL';

    return (
        <motion.nav
            className="navbar"
            initial={{ y: -100 }}
            animate={{ y: 0 }}
            transition={{ type: "spring", stiffness: 120 }}
        >
            <div className="navbar-container">
                <motion.div
                    className="navbar-brand"
                    whileHover={{ scale: 1.1 }}
                    whileTap={{ scale: 0.9 }}
                >
                    <Link to="/" className="no-underline">
                        <div className="logo-container">
                            <img src={NuvixLogo} alt="Nuvix Logo" className="logo-icon-img" />
                        </div>
                    </Link>
                </motion.div>

                <div className="mobile-menu-icon" onClick={toggleMobileMenu}>
                    &#9776;
                </div>

                <ul className={`navbar-links ${isMobileMenuOpen ? "active" : ""}`}>
                    <li>
                        <Link to="/scanner" className="nav-link">Escáner</Link>
                    </li>
                    <li>
                        {isTrialUser ? (
                            <span className="nav-link disabled-link" title="Actualiza tu plan para acceder a esta función">Eventos</span>
                        ) : (
                            <Link to="/events" className="nav-link">Eventos</Link>
                        )}
                    </li>
                    <li>
                        <Link to="/records" className="nav-link">Registros</Link>
                    </li>
                    <li>
                        <button onClick={handleLogout} className="nav-link logout-btn">
                            Cerrar Sesión
                        </button>
                    </li>
                </ul>
            </div>
        </motion.nav>
    );
});