import { defineStore } from 'pinia'
import axios from 'axios'
import type { User, LoginCredentials, LoginResponse } from '@/types/Auth'

interface AuthState {
  token: string | null
  user: User | null
}

export const useAuthStore = defineStore('Auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token') || null,
    user: null
  }),

  getters: {
    isAuthenticated: (state): boolean => !!state.token,
    getUser: (state): User | null => state.user
  },

  actions: {
    async login(credentials: LoginCredentials): Promise<boolean> {
      try {
        const response = await axios.post<LoginResponse>('/api/auth/login', credentials)
        this.token = response.data.token
        this.user = response.data.user || null

        localStorage.setItem('token', this.token)

        return true
      } catch (error) {
        console.error('Login failed:', error)
        return false
      }
    },

    logout(): void {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
    },

    async fetchUser(): Promise<void> {
      try {
        const response = await axios.get<User>('/api/auth/me')
        this.user = response.data
      } catch (error) {
        console.error('Failed to fetch user:', error)
        this.logout()
      }
    }
  }
})
