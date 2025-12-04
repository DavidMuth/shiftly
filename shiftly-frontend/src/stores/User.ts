import { defineStore } from 'pinia';
import type { User} from '@/types/User';
import UserService from '@/services/UserService';

interface UserState {
  user: User | null;
}

export const useUserStore = defineStore('User', {
  state: (): UserState => ({
    user: null,
  }),

  getters: {
    getUser: (state): User | null => state.user,
  },

  actions: {
    async getCurrentUser(): Promise<void> {
      try {
        const response = await UserService.getCurrentUser();
        this.user = response.data;
      } catch (error) {
        console.error("Login failed:", error);
      }
    },

    clearUser() {
      this.user = null;
    }
  }
});
