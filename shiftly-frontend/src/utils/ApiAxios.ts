import axios from "axios";

const baseURL = import.meta.env.VITE_API_BASE_URL as string;

console.log(baseURL)

const apiClient = axios.create({
    baseURL,
    timeout: 2000,
    headers: {
        "Content-Type": "application/json"
    }
});

// Optional: add interceptors
apiClient.interceptors.request.use(config => {
    // Example: attach token
    const token = localStorage.getItem("token");
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});

apiClient.interceptors.response.use(
    response => response,
    error => {
        // Global error handling
        console.error("API Error:", error);
        return Promise.reject(error);
    }
);

export default apiClient;
