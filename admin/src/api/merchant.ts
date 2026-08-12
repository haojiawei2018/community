import { apiClient, publicClient } from './http'
import type { ApiResponse, PageData, TokenData } from './types'

export interface Dashboard { communityId:number; communityCode:string; communityName:string; logoUrl?:string; memberCount:number; circleCount:number; postCount:number; pendingReviewCount:number }
export interface Member { memberId:number; userId:number; username:string; displayName:string; avatarUrl?:string; status:string; muteUntil?:string; joinedAt:string; roles:string[] }
export interface Circle { id:number; circleCode:string; circleName:string; iconUrl?:string; coverUrl?:string; description?:string; joinMode:string; status:string; sortOrder:number; memberCount:number; postCount:number }
export interface CirclePayload { circleCode:string; circleName:string; iconUrl?:string; coverUrl?:string; description?:string; joinMode:string; status:string; sortOrder:number }
export interface ImageUpload { url:string; objectName:string; originalName:string; size:number }
export interface Activity { id:number; circleId?:number; circleName?:string; topicName:string; description?:string; coverUrl?:string; status:string; startAt?:string; endAt?:string; sortOrder:number }
export interface ActivityPayload { topicName:string; circleId?:number; description?:string; coverUrl?:string; status:string; startAt?:string; endAt?:string; sortOrder:number }
export interface Post { id:number; circleName:string; username:string; title?:string; summary?:string; status:string; viewCount:number; commentCount:number; createTime:string }
export interface Bootstrap { communityId:number; communityCode:string; communityName:string; logoUrl?:string; features:Record<string,boolean> }

export async function login(username:string, password:string) {
  const response = await publicClient.post<ApiResponse<TokenData>>('/api/v1/auth/login', { username, password, clientType:'ADMIN' })
  return response.data.data
}
export async function getDashboard() {
  const [community,members,circles,posts]=await Promise.all([getBootstrap(),getMembers({page:1,pageSize:1}),getCircles(),getPosts({page:1,pageSize:100})])
  return { ...community, memberCount:members.total, circleCount:circles.length, postCount:posts.total, pendingReviewCount:posts.records.filter((post)=>post.status==='PENDING_REVIEW').length }
}
export async function getMembers(params?:Record<string,unknown>) {
  return (await apiClient.get<ApiResponse<PageData<Member>>>('/api/admin/v1/members', { params })).data.data
}
export async function updateMemberStatus(memberId:number, status:string) {
  return (await apiClient.put<ApiResponse<Member>>(`/api/admin/v1/members/${memberId}/status`, { status })).data.data
}
export async function getCircles() { return (await apiClient.get<ApiResponse<Circle[]>>('/api/admin/v1/circles')).data.data }
export async function createCircle(payload:CirclePayload) { return (await apiClient.post<ApiResponse<Circle>>('/api/admin/v1/circles',payload)).data.data }
export async function updateCircle(circleId:number,payload:CirclePayload) { return (await apiClient.put<ApiResponse<Circle>>(`/api/admin/v1/circles/${circleId}`,payload)).data.data }
export async function deleteCircle(circleId:number) { await apiClient.delete(`/api/admin/v1/circles/${circleId}`) }
export async function uploadImage(file:File) { const body=new FormData();body.append('file',file);return (await apiClient.post<ApiResponse<ImageUpload>>('/api/v1/files/images',body)).data.data }
export async function getActivities() { return (await apiClient.get<ApiResponse<Activity[]>>('/api/admin/v1/activities')).data.data }
export async function createActivity(payload:ActivityPayload) { return (await apiClient.post<ApiResponse<Activity>>('/api/admin/v1/activities',payload)).data.data }
export async function deleteActivity(activityId:number) { await apiClient.delete(`/api/admin/v1/activities/${activityId}`) }
export async function getPosts(params?:Record<string,unknown>) { return (await apiClient.get<ApiResponse<PageData<Post>>>('/api/v1/posts', { params })).data.data }
export async function deletePost(postId:number) { await apiClient.delete(`/api/admin/v1/posts/${postId}`) }
export async function getBootstrap() { return (await publicClient.get<ApiResponse<Bootstrap>>('/api/v1/bootstrap')).data.data }
