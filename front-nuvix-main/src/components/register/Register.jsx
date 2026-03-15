import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";

import {registerUser} from "./registerUser";
import {ErrorModal} from "../errorModal/ErrorModal";
import '../login/Login.css';

export function Register() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const handleRegister = async (e) => {
        e.preventDefault();
        const result = await registerUser(email, password);
        if (result.success) {
            setMessage(" Registro exitoso, ahora puedes iniciar sesión");
            navigate("/login");
        } else {
            setError(result.message);
        }
    };

    const handleCloseErrorModal = () => {
        setError(null);
    };

    return (
        <>
            <ErrorModal message={error} onClose={handleCloseErrorModal} />
            <div className="auth-container">
                <div className="form-card">
                    <h2>Registro</h2>
                    <form onSubmit={handleRegister}>
                        <input
                            type="email"
                            placeholder="Correo"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                        <input
                            type="password"
                            placeholder="Contraseña"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        <button type="submit">Registrarse</button>
                    </form>
                    <div className="links-container">
                        <Link className="link" to={"/login"}>
                            ¿Ya tienes cuenta? Inicia sesión
                        </Link>
                    </div>
                    <p className="message">{message}</p>
                </div>
            </div>
        </>

    );
}