import { createRouter, createWebHistory } from 'vue-router'
import { useSessionStore } from '@/stores/session'
import AdminLayout from '@/layouts/AdminLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path:'/login', component:() => import('@/views/LoginView.vue'), meta:{ title:'商户登录', public:true } },
    { path:'/', component:AdminLayout, redirect:'/dashboard', children:[
      { path:'dashboard', component:() => import('@/views/DashboardView.vue'), meta:{ title:'商户概览', permission:'tenant.config.read' } },
      { path:'members', component:() => import('@/views/MembersView.vue'), meta:{ title:'成员管理', permission:'member.read' } },
      { path:'content', component:() => import('@/views/ContentView.vue'), meta:{ title:'内容运营', permission:'circle.manage' } },
      { path:'activities', component:() => import('@/views/ActivitiesView.vue'), meta:{ title:'活动管理', permission:'announcement.manage' } },
      { path:'settings', component:() => import('@/views/SettingsView.vue'), meta:{ title:'社区设置', permission:'tenant.config.read' } },
    ] },
    { path:'/:pathMatch(.*)*', redirect:'/dashboard' },
  ],
})

router.beforeEach((to) => {
  document.title = `${to.meta.title || '管理后台'} - Gaming Community`
  if (to.meta.public) return true
  const session = useSessionStore()
  if (!session.current.token) return { path:'/login', query:{ redirect:to.fullPath } }
  if (!session.hasPermission(to.meta.permission)) return { path:'/dashboard' }
  return true
})

export default router
