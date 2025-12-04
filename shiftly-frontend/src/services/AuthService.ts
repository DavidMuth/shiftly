import type { LoginCredentials, LoginResponse } from "@/types/Auth";
import apiClient from "@/utils/ApiAxios";

class AuthService {
  private resource = "/api/signin";

  signin(credentials: LoginCredentials) {

    return apiClient.post<LoginResponse>(this.resource, credentials);
  }
}

export default new AuthService();
