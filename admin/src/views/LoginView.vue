<template>
  <div class="login-page">
    <section class="intro"><span class="eyebrow">OPEN SOURCE COMMUNITY FORUM</span><h1>让每一次交流，都沉淀为社区价值</h1><p>面向社区的开源论坛管理后台，统一管理成员、圈子、帖子与社区内容，让运营更简单，让兴趣连接更紧密。</p><ul><li>圈子与帖子内容管理</li><li>社区成员与权限治理</li><li>开源部署，自由扩展</li></ul></section>
    <section class="panel"><ElCard class="login-card" shadow="never"><div class="title"><span>论坛管理端</span><h2>登录论坛后台</h2><p>使用社区管理员账号进入管理中心</p></div><ElForm label-position="top" @submit.prevent="submit"><ElFormItem label="用户名"><ElInput v-model="form.username" size="large" autocomplete="username" /></ElFormItem><ElFormItem label="密码"><ElInput v-model="form.password" size="large" type="password" show-password autocomplete="current-password" @keyup.enter="submit" /></ElFormItem><ElButton class="submit" type="primary" size="large" native-type="submit" :loading="loading">登录论坛后台</ElButton></ElForm><div class="demo">开发账号：merchant_admin / Merchant@123456</div></ElCard><div class="opensource-note">小程序小程 开源代码</div></section>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { AxiosError } from 'axios'
import { login } from '@/api/merchant'
import type { ApiResponse } from '@/api/types'
import { useSessionStore } from '@/stores/session'
const route=useRoute(),router=useRouter(),session=useSessionStore(),loading=ref(false)
const form=reactive({username:'merchant_admin',password:'Merchant@123456'})
async function submit(){if(!form.username.trim()||!form.password){ElMessage.warning('请输入用户名和密码');return}loading.value=true;try{const data=await login(form.username,form.password);session.save({token:data.accessToken,username:data.user.username,displayName:data.user.displayName||data.user.nickname||data.user.username,permissions:data.user.permissions||[]});const redirect=typeof route.query.redirect==='string'?route.query.redirect:'/dashboard';await router.replace(redirect)}catch(error){const e=error as AxiosError<ApiResponse<unknown>>;ElMessage.error(e.response?.data?.message||'账号或密码不正确')}finally{loading.value=false}}
</script>
<style scoped lang="scss">
.login-page{display:grid;grid-template-columns:minmax(0,1.15fr) minmax(440px,.85fr);min-height:100vh;background:#f2f5fb}.intro,.panel{display:flex;justify-content:center;flex-direction:column}.intro{padding:8vw;color:#fff;background:radial-gradient(circle at 15% 18%,rgba(45,212,191,.35),transparent 30%),linear-gradient(145deg,#13213b,#2563eb 58%,#14b8a6)}.eyebrow{font-size:12px;font-weight:700;letter-spacing:.14em}.intro h1{max-width:720px;margin:28px 0 20px;font-size:clamp(40px,4.6vw,68px);line-height:1.08}.intro p{max-width:650px;margin:0;color:rgba(255,255,255,.8);font-size:17px;line-height:1.8}.intro ul{margin:32px 0 0;padding:0;list-style:none}.intro li{margin:13px 0}.intro li:before{margin-right:10px;content:'✓';color:#86efac}.panel{position:relative;align-items:center;padding:48px}.login-card{width:430px;padding:18px;border:0;border-radius:20px}.title span{color:#0f9f8f;font-size:13px;font-weight:700}.title h2{margin:8px 0;font-size:28px}.title p{margin:0 0 26px;color:#8791a7}.submit{width:100%;margin-top:8px}.demo{margin-top:18px;padding:12px;color:#64748b;font-size:12px;text-align:center;background:#f5f7fb;border-radius:10px}.opensource-note{position:absolute;bottom:24px;color:#a0a8b8;font-size:12px;letter-spacing:.08em}
</style>
