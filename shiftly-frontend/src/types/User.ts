export interface User {
    id: number;
    name: string;
    email: string;
    workingHours: number;
}

export interface CreateUserDTO {
    name: string;
    email: string;
    password: string;
    passwordConfirm: string;
}

export interface UpdateUserDTO {
    name: string;
    email: string;
    currentPassword: string;
    password: string;
    passwordConfirm: string;
}