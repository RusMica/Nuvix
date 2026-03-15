import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {forgotPassword} from './forgotPassword.js';
import '../login/Login.css';

export function ForgotPassword() {
    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();
    const handleForgot = async (e) => {
        e.preventDefault();
        const result = await forgotPassword(email);
        if (!result.success){
            setMessage("Error al enviar email");
        } else {
            setMessage("Su código fue enviado a su casilla de email");
            navigate("/verify-code", {state: {email}})
        }
    };

    return (
        <>
            <div className="auth-container">
                <div className="form-card">
                    <h2>Olvidaste tu contraseña</h2>
                    <form onSubmit={handleForgot}>
                        <input
                            type="email"
                            placeholder="Ingresa tu correo"
                            name="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                        <button type="submit">Enviar código</button>
                    </form>
                    <div className="links-container">
                        <Link className="link" to={"/login"}>
                            Volver al login
                        </Link>
                    </div>

                    <p className="message">{message}</p>
                </div>
            </div>
        </>

    );
}