const API_BASE = "http://localhost:8080";

export const resetPassword = async (email, newPassword) => {

    try {
        const response =
            await fetch(`${API_BASE}/v1/auth/change-password`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email:email,
                    newPassword:newPassword
                })
            })

        const data = await response.json();

        if (!response.ok) return { success: false, message: data.message || "Error al cambiar contraseña" };

        return { success: true, token: data.token, message: "Contraseña cambiada con exito" };
    }catch (e){
        return { success: false, message: e.message || "Error de red" };
    }
}