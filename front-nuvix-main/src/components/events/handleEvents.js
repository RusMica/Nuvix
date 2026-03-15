const API_BASE = "http://localhost:8080"; // unifica host

export const createEvento = async (event) => {
    try {
        const token = localStorage.getItem("token");
        const response = await fetch(`${API_BASE}/v1/eventos`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(event)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error ${response.status}: ${errorText}`);
        }

        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            const data = await response.json();
            console.log(data);
            return data;
        } else {
            const text = await response.text();
            return text ? { message: text } : {};
        }
    } catch(e) {
        console.error(e);
        return null;
    }
}

export const uploadXLSX = async (eventoId, file) => {
    try{
        const token = localStorage.getItem("token");
        const formData = new FormData();
        formData.append("file", file);

        const response = await fetch(`${API_BASE}/v1/eventos/add/lista/${eventoId}`, {
            method: "PATCH",
            headers: { "Authorization": `Bearer ${token}` },
            body: formData
        });

        if (!response.ok) {
            const text = await response.text();
            throw new Error(`Fallo al subir XLSX: ${response.status} ${text}`);
        }
        const contentType = response.headers.get("content-type") || "";
        if (contentType.includes("application/json")) {
            return await response.json();
        }
        return await response.text();
    } catch(e){
        console.error(e);
        throw e;
    }
}

export const uploadPDF = async (eventoId, file) => {
    try{
        const token = localStorage.getItem("token");
        const formData = new FormData();
        formData.append("file", file);

        const response = await fetch(`${API_BASE}/v1/eventos/add/itinerario/${eventoId}`, {
            method: "PATCH",
            headers: { "Authorization": `Bearer ${token}` },
            body: formData
        });

        if (!response.ok) {
            const text = await response.text();
            throw new Error(`Fallo al subir PDF: ${response.status} ${text}`);
        }
        return await response.json();
    } catch(e){
        console.error(e);
        throw e;
    }
}


export const createParticipantsFromList = async (eventoId, filePathLike) => {
    try {
        const token = localStorage.getItem("token");

        const filePath = typeof filePathLike === 'string'
            ? filePathLike
            : (filePathLike?.filePath ?? filePathLike?.path ?? filePathLike?.url ?? '');

        const url = new URL(`${API_BASE}/v1/participantes/create/${encodeURIComponent(eventoId)}`);
        if (filePath) {
            url.searchParams.set("filePath", filePath);
        }

        const response = await fetch(url.toString(), {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({})
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Fallo al crear participantes: ${response.status} - ${errorText}`);
        }

        const contentType = response.headers.get("content-type") || "";
        if (contentType.includes("application/json")) {
            return await response.json();
        }
        return await response.text();

    } catch (e) {
        console.error("Error en createParticipantsFromList:", e);
        throw e;
    }
};

export const resendInvitations = async (eventoId) => {
    try {
        const token = localStorage.getItem("token");
        const response = await fetch(`${API_BASE}/v1/participantes/resend-emails/${eventoId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({})
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Fallo al reenviar invitaciones: ${response.status} - ${errorText}`);
        }

        const contentType = response.headers.get("content-type") || "";
        if (contentType.includes("application/json")) {
            return await response.json();
        }
        return await response.text();

    } catch (e) {
        console.error("Error in resendInvitations:", e);
        throw e;
    }
};

export const finishEvento = async (eventoId) => {
    try{
        const token = localStorage.getItem("token");

        const response = await fetch(`${API_BASE}/v1/eventos/finish/${eventoId}`, {
            method: "PATCH",
            headers: { "Authorization": `Bearer ${token}` },
            body: JSON.stringify({})
        });

        if (!response.ok) {
            await response.text();
        }
        const contentType = response.headers.get("content-type") || "";
        if (contentType.includes("application/json")) {
            return await response.json();
        }
        return await response.text();
    } catch(e){
        console.error(e);
        throw e;
    }
}

export const getParticipantesByEvento = async (eventoId) => {
    try {
        const token = localStorage.getItem("token");
        const response = await fetch(`${API_BASE}/v1/participantes/evento/${eventoId}`, {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error al listar participantes: ${response.status} - ${errorText}`);
        }

        return await response.json();
    } catch (e) {
        console.error(`Error al obtener participantes para el evento ${eventoId}:`, e);
        return [];
    }
};

export const listarEventos = async () => {
    try{
        const token = localStorage.getItem("token");

        if (!token) {
            throw new Error("Usuario no autenticado");
        }

        const response = await fetch(`${API_BASE}/v1/eventos/all/active`, {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error al listar eventos: ${response.status} - ${errorText}`);
        }

        const eventos = await response.json();

        return await Promise.all(
            eventos.map(async (evento) => {
                const participantes = await getParticipantesByEvento(evento.id);
                return {...evento, participantes: participantes || []};
            })
        );
    } catch(e){
        console.error(e);
        throw e;
    }
}