import type { User } from "@/types/User";
import apiClient from "@/utils/ApiAxios";


class UserService {
    private resource = "/api/shiftly/user";

    getCurrentUser(){
      return apiClient.get<User>(this.resource + "/me");
    }

}

export default new UserService();
