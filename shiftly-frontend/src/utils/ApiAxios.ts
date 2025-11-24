import router from "@/router";
import { useAuthStore } from "@/stores/Auth";
import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";

const baseURL = import.meta.env.VITE_API_BASE_URL as string;

console.log(baseURL)

const apiClient = axios.create({
    baseURL,
    timeout: 2000,
    headers: {
        "Content-Type": "application/json"
    }
});

// Request interceptor
axios.interceptors.request.use(
  (config: InternalAxiosRequestConfig): InternalAxiosRequestConfig => {
    const authStore = useAuthStore()
    if (authStore.token && config.headers) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error: AxiosError) => Promise.reject(error)
)


// Response interceptor
axios.interceptors.response.use(
  response => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

export default apiClient;
