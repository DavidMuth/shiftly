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

export interface Signup {
  name: string
  email: string
  password: string
  confirmPassword: string
}

export interface SignupResponse {
  success: boolean
  error?: string
}