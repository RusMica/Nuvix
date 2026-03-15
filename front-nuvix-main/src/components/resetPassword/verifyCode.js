const API_BASE = "http://localhost:8080";

export const verifyCode = async (email, code) => {
    try {
        const response =
            await fetch(`${API_BASE}/v1/auth/verify-code`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email:email,
                    code:code
                })
            })

        const data = await response.json();

        if (!response.ok) return { success: false, message: data.message || "Error verificar codigo" };

        return { success: true, token: data.token, message: "Codigo verificado con exito" };
    }catch (e){
        return { success: false, message: e.message || "Error de red" };
    }
}