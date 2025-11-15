import type { User, CreateUserDTO, UpdateUserDTO } from "@/types/User";
import BaseApi from "@/utils/BaseApi";


class UserService extends BaseApi<
    User[],         // list() returns User[]
    User,           // get() returns User
    CreateUserDTO,  // create() expects CreateUserDTO
    UpdateUserDTO   // update() expects UpdateUserDTO
> {
    constructor() {
        super("/api/user");
    }

    getProfile() {
        return this.get("profile");
    }
}

export default new UserService();
