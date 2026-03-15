const API_BASE = "http://localhost:8080";

export const getUserData = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
        return null;
    }

    try {
        const response = await fetch(`${API_BASE}/v1/users/email`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            console.error("Failed to fetch user data. Status:", response.status);
            return null;
        }

        return await response.json();

    } catch (error) {
        console.error("Error fetching user data:", error);
        return null;
    }
};
