import {useState} from "react";
import {authenticateUser} from "./authenticateUser";
import {Link, useNavigate} from "react-router-dom";
import './Login.css';
import {ErrorModal} from "../errorModal/ErrorModal";

export function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();
    const handleLogin = async (e) => {
        e.preventDefault();
        const result = await authenticateUser(email, password);

        if(!result.success) {
            setMessage("Error al autenticar el usuario");
        } else {
            localStorage.setItem("token", result.token);
            localStorage.setItem("userRole", result.rol);
            navigate("/scanner");
        }
    };

    const closeErrorModal = () => {
        setMessage("");
    };

    return (
        <>
            <div className="auth-container">
                <div className="form-card">
                    <h2>Iniciar Sesión</h2>
                    <form onSubmit={handleLogin}>
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
                        <button type="submit">Entrar</button>
                    </form>
                    <div className="links-container">
                        <Link className="link" to={"/register"}>
                            ¿No tienes cuenta? Regístrate
                        </Link>
                        <Link className="link" to={"/forgot-password"}>
                            ¿Olvidaste tu contraseña?
                        </Link>
                    </div>

                    <ErrorModal message={message} onClose={closeErrorModal} />
                </div>
            </div>
        </>
    );
}