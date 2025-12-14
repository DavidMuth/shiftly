import type { User } from "@/types/User";
import apiClient from "@/utils/ApiAxios";


class UserService {
    private resource = "/api/shiftly/user";

    getCurrentUser(){
      return apiClient.get<User>(this.resource + "/me");
    }

    updateWorkingHours(hours: number) {
      return apiClient.put<User>(this.resource + "/me/updateWorkingHours", {
        workingHours: hours,
      })
    }

    updatePassword(update: {currentPassword: string, newPassword: string, confirmNewPassword: string}) {
      return apiClient.put<User>(this.resource + "/me/updatePassword", {
        currentPassword: update.currentPassword,
        newPassword: update.newPassword,
        confirmNewPassword: update.confirmNewPassword
      })
    }
}

export default new UserService();
