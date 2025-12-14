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
        console.error("Failed to get current user:", error);
      }
    },

    async updateWorkingHours(hours: number): Promise<void> {
      try {
        const response = await UserService.updateWorkingHours(hours);
        this.user = response.data;
      } catch (error) {
        console.error("Failed to update working hours:", error)
        throw error;
      }
    },

    async updateUserPassword(update: {currentPassword: string, newPassword: string, confirmNewPassword: string}): Promise<void> {
      try {
        const response = await UserService.updatePassword(update)
      } catch (error) {
        console.error("Failed to update passsword:", error)
        throw error;
      }
    },

    clearUser() {
      this.user = null;
    }
  }
});
