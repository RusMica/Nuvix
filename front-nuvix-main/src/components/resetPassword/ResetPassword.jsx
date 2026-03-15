import {useState} from "react";
import {Link, useLocation, useNavigate} from "react-router-dom";
import {resetPassword} from "./resetPassword";
import '../login/Login.css';

export function ResetPassword() {
    const {state} = useLocation();
    const [email] = useState(state?.email ?? "")
    const [newPassword, setNewPassword] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();
    const handleReset = async (e) => {
        e.preventDefault();
        const result = await resetPassword(email, newPassword);

        if(!result.success) setMessage("Error al cambiar contraseña");
        else{
            setMessage("Contraseña cambiada con éxito");
            navigate("/login");
        }
    };

    return (
        <div className="auth-container">
            <div className="form-card">
                <h2>Restablecer contraseña</h2>
                <form onSubmit={handleReset}>
                    <input
                        type="password"
                        placeholder="Nueva contraseña"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                    />
                    <button type="submit">Restablecer</button>
                </form>
                <Link className="link" to={"/login"}>
                    Volver al login
                </Link>
                <p className="message">{message}</p>
            </div>
        </div>
    );
}