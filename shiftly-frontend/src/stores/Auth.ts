import { defineStore } from 'pinia';
import type { User, LoginCredentials, Signup, SignupResponse } from '@/types/Auth';
import AuthService from '@/services/AuthService';

// Cookie synchron auslesen
function getCookie(name: string): string | null {
  const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
  if (match && match[2]) return decodeURIComponent(match[2]);
  return null;
}

interface AuthState {
  token: string | null;
  user: User | null;
}

export const useAuthStore = defineStore('Auth', {
  state: (): AuthState => ({
    token: getCookie('token'),
    user: null,
  }),

  getters: {
    isAuthenticated: (state): boolean => !!state.token,
    getUser: (state): User | null => state.user,
  },

  actions: {
    async login(credentials: LoginCredentials): Promise<boolean> {
      try {
        const response = await AuthService.signin(credentials);
        const token = response.data.jwtToken;
        this.token = token;

        // Cookie setzen
        document.cookie = `token=${token}; path=/;`;

        return true;
      } catch (error) {
        console.error('Login failed:', error);
        return false;
      }
    },


    async signUp(req: Signup): Promise<SignupResponse> {
        const response = await AuthService.signup(req);
        console.log("response:", response)
        return response
    },

    logout(): void {
      this.token = null;
      this.user = null;

      // Cookie löschen
      document.cookie = 'token=; Max-Age=0; path=/;';
    },
  }
});
