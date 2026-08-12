export interface ApiResponse<T> { code: number; message: string; data: T; traceId?: string }
export interface UserSession { username:string; nickname:string; displayName:string; permissions:string[]; roles:string[] }
export interface TokenData { accessToken:string; refreshToken:string; expiresIn:number; user:UserSession }
export interface PageData<T> { records:T[]; total:number; page:number; pageSize:number }
