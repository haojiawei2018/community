<template>
  <div class="shell">
    <header>
      <div class="brand"><div class="logo">G</div><div><strong>Gaming Community</strong><span>商户管理后台</span></div></div>
      <div class="user"><ElTag type="success" effect="light" round>商户端</ElTag><span>{{ session.current.displayName || session.current.username }}</span><ElButton link type="primary" @click="logout">退出</ElButton></div>
    </header>
    <div class="body">
      <aside>
        <div class="caption">社区运营</div>
        <ElMenu :default-active="route.path">
          <ElMenuItem v-for="item in visibleMenus" :key="item.path" :index="item.path" @click="navigate(item.path)"><ElIcon><component :is="item.icon" /></ElIcon><span>{{ item.title }}</span></ElMenuItem>
        </ElMenu>
      </aside>
      <main><div class="breadcrumb">商户管理 <i>/</i> <strong>{{ route.meta.title }}</strong></div><RouterView /></main>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import { DataBoard, Flag, Promotion, Setting, User } from '@element-plus/icons-vue'
import { useSessionStore } from '@/stores/session'
const route=useRoute(), router=useRouter(), session=useSessionStore()
const menus=[
  {path:'/dashboard',title:'商户概览',permission:'tenant.config.read',icon:DataBoard},
  {path:'/members',title:'成员管理',permission:'member.read',icon:User},
  {path:'/content',title:'内容运营',permission:'circle.manage',icon:Flag},
  {path:'/activities',title:'活动管理',permission:'announcement.manage',icon:Promotion},
  {path:'/settings',title:'社区设置',permission:'tenant.config.read',icon:Setting},
]
const visibleMenus=computed(()=>menus.filter(item=>session.hasPermission(item.permission)))
async function navigate(path:string){if(route.path!==path)await router.push(path)}
async function logout(){session.clear();await router.replace('/login')}
</script>
<style scoped lang="scss">
.shell{height:100vh;background:#f2f5fb}header{height:82px;padding:0 28px;display:flex;align-items:center;justify-content:space-between}.brand,.user{display:flex;align-items:center;gap:13px}.logo{width:42px;height:42px;display:grid;place-items:center;color:#fff;font-weight:800;font-size:20px;border-radius:13px;background:linear-gradient(135deg,#2563eb,#14b8a6)}.brand strong,.brand span{display:block}.brand span{margin-top:3px;color:#8791a7;font-size:12px}.body{display:flex;height:calc(100vh - 82px)}aside{width:224px;padding:8px 14px}.caption{padding:10px 14px;color:#8791a7;font-size:12px;letter-spacing:.1em}aside :deep(.el-menu){border:0;background:transparent}aside :deep(.el-menu-item){height:50px;margin:5px 0;border-radius:11px}aside :deep(.el-menu-item.is-active){background:#fff;box-shadow:0 8px 24px rgba(37,99,235,.08)}main{flex:1;min-width:0;padding:24px 30px 36px;overflow:auto;background:#fff;border-radius:24px 0 0}.breadcrumb{margin-bottom:24px;color:#8791a7;font-size:13px}.breadcrumb i{margin:0 8px;font-style:normal}.breadcrumb strong{color:#36415a}
</style>
