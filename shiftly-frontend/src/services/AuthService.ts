import type { LoginCredentials, LoginResponse, Signup, SignupResponse } from "@/types/Auth";
import apiClient from "@/utils/ApiAxios";

class AuthService {
  private resource = "/api/signin";

  signin(credentials: LoginCredentials) {
    return apiClient.post<LoginResponse>(this.resource, credentials);
  }

  signup(req: Signup) {
    return apiClient.post<SignupResponse>(this.resource, req)
  }
}

export default new AuthService();
