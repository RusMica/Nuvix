const API_BASE = "http://localhost:8080";

export const registerUser = async (email, password) => {
    try {
        const response = await fetch(`${API_BASE}/v1/auth/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        if (response.ok) {
            const data = await response.json();
            return { success: true, token: data.token, message: "Registro exitoso" };
        }

        if (response.status === 409) {
            return { success: false, message: "El correo electrónico ya está en uso." };
        }

        // For other non-ok responses, try to parse json, but have a fallback.
        try {
            const errorData = await response.json();
            return { success: false, message: errorData.message || "Error al registrar el usuario" };
        } catch (e) {
            // The response body was not valid JSON.
            return { success: false, message: "Ocurrió un error inesperado del servidor." };
        }

    } catch (e) {
        return { success: false, message: "Error de red. Verifique su conexión e intente de nuevo." };
    }
};