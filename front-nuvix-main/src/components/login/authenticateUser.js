const API_BASE = "http://localhost:8080";

export const authenticateUser = async (email, password) => {
    try {
        const response = await fetch(`${API_BASE}/v1/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });
        const data = await response.json();
        if (!response.ok) return { success: false, message: data.message || "Error al autenticar el usuario" };

        return { 
  success: true, 
  token: data.token, 
  rol: data.rol,
  message: "Login exitoso" 
};
    } catch (e) {
        return { success: false, message: e.message || "Error de red" };
    }
}