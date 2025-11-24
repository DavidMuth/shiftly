// src/services/AuthService.ts
import type { LoginCredentials } from "@/types/Auth";
import apiClient from "@/utils/ApiAxios";

class AuthService {
  private resource = "/api/signin";

  signin(credentials: LoginCredentials) {
    return apiClient.post(this.resource, credentials);
  }
}

export default new AuthService();
