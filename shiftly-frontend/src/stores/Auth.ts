import { defineStore } from 'pinia'
import type { User, LoginCredentials, LoginResponse } from '@/types/Auth'
import AuthService from '@/services/AuthService'


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
        const response = await AuthService.signin(credentials) as unknown as LoginResponse
        console.log('Login response:', response)
        this.token = response.data.jwtToken

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
  }
})
