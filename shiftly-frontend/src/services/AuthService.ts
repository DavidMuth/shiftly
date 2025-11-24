import type { LoginCredentials } from "@/types/Auth";
import BaseApi from "@/utils/BaseApi";


class AuthService extends BaseApi {
    constructor() {
        super("/api/signin");
    }

    signin(credentials: LoginCredentials) {
        return this.create( credentials)
    }
}

export default new AuthService();
