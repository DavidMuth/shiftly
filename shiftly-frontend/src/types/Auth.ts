export interface LoginCredentials {
  email: string
  password: string
}

export interface LoginResponse {
  jwtToken: string
  user?: User
}

export interface User {
  id: number
  username: string
  email: string
}
