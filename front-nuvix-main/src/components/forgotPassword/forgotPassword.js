export const forgotPassword = async (email) => {
    try {
        const response =
            await fetch("https://sistemadeverificacion.onrender.com/v1/auth/send-code", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email:email
            })
        })

        const data = await response.json();

        if (!response.ok) return { success: false, message: data.message || "Error al enviar email" };

        return { success: true, token: data.token, message: "Email enviado exitosamente" };
    }catch (e){
        return { success: false, message: e.message || "Error de red" };
    }
}