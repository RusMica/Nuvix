import React, {useEffect, useState} from 'react';
import {Routes, Route, useNavigate, useLocation} from 'react-router-dom';
import './App.css';
import {Home} from "./components/home/Home";
import {Scanner} from "./components/scanner/Scanner";
import {Records} from "./components/records/Records";
import {Payment} from "./components/payment/Payment";
import {Events} from "./components/events/Events";
import {Login} from "./components/login/Login";
import {Register} from "./components/register/Register";
import {forgotPassword} from "./components/forgotPassword/forgotPassword";
import {verifyCode} from "./components/resetPassword/verifyCode";
import {resetPassword} from "./components/resetPassword/resetPassword";

function App() {
    const [eventos, setEventos] = useState([]);
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const token = localStorage.getItem("token");
        const userRole = localStorage.getItem("userRole");
        const path = location.pathname;

        const protectedPaths = ["/scanner", "/events", "/records", "/payment"];
        const publicPaths = ["/", "/login", "/register", "/forgot-password", "/reset-password"];

        // Redirect to login if not authenticated and trying to access a protected route
        if (!token && protectedPaths.includes(path)) {
            if (path !== "/login") navigate("/login", { replace: true });
            return;
        }

        // Redirect to scanner if authenticated and trying to access a public route
        if (token && publicPaths.includes(path)) {
            if (path !== "/scanner") {
                navigate("/scanner", { replace: true });
            }
            return;
        }

        // Redirect trial users away from the events page
        if (userRole === 'USER_TRIAL' && path === '/events') {
            alert("La sección de eventos no está disponible en la versión de prueba. Actualiza tu plan para acceder.");
            navigate("/scanner", { replace: true });
            return;
        }

    }, [location, navigate]);

    return (
        <div className="App-container">
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />}/>
                <Route path="/register" element={<Register />}/>
                <Route path="/forgot-password" element={<forgotPassword />}/>
                <Route path="/reset-password" element={<resetPassword />}/>
                <Route path="/verify-code" element={<verifyCode />}/>
                <Route
                    path="/scanner"
                    element={<Scanner eventos={eventos} setEventos={setEventos} />}
                />
                <Route
                    path="/events"
                    element={<Events eventos={eventos} setEventos={setEventos} />}
                />
                <Route
                    path="/records"
                    element={<Records eventos={eventos} />}
                />
                <Route
                    path="/payment"
                    element={<Payment />}
                />
            </Routes>
        </div>
    );
}

export default App;
