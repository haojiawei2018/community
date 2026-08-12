<template>
  <div>
    <div class="page-header"><div><h1>活动管理</h1><p>管理小程序展示的社区话题活动</p></div><div><ElButton @click="load">刷新</ElButton><ElButton type="primary" @click="openCreate">新增活动</ElButton></div></div>
    <ElCard class="panel-card" shadow="never">
      <ElTable v-loading="loading" :data="activities">
        <ElTableColumn label="封面" width="140"><template #default="scope"><ElImage v-if="scope.row.coverUrl" :src="scope.row.coverUrl" fit="cover" class="cover"/><span v-else class="no-cover">暂无封面</span></template></ElTableColumn>
        <ElTableColumn prop="topicName" label="活动名称" min-width="170"/>
        <ElTableColumn prop="circleName" label="所属圈子" width="130"><template #default="scope">{{scope.row.circleName||'全社区'}}</template></ElTableColumn>
        <ElTableColumn label="状态" width="90"><template #default="scope"><ElTag :type="scope.row.status==='ACTIVE'?'success':'info'">{{scope.row.status==='ACTIVE'?'启用':'停用'}}</ElTag></template></ElTableColumn>
        <ElTableColumn label="活动时间" min-width="220"><template #default="scope"><div>{{formatDate(scope.row.startAt)}}<br/>至 {{formatDate(scope.row.endAt)}}</div></template></ElTableColumn>
        <ElTableColumn prop="sortOrder" label="排序" width="70"/>
        <ElTableColumn prop="description" label="简介" min-width="180" show-overflow-tooltip/>
        <ElTableColumn label="操作" width="90" fixed="right"><template #default="scope"><ElButton link type="danger" @click="remove(scope.row)">删除</ElButton></template></ElTableColumn>
      </ElTable>
    </ElCard>

    <ElDialog v-model="dialogVisible" title="新增活动" width="680px" destroy-on-close>
      <ElForm ref="formRef" :model="form" :rules="rules" label-width="90px">
        <ElFormItem label="活动名称" prop="topicName"><ElInput v-model="form.topicName" maxlength="128"/></ElFormItem>
        <ElRow :gutter="18"><ElCol :span="12"><ElFormItem label="所属圈子"><ElSelect v-model="form.circleId" clearable placeholder="全社区"><ElOption v-for="circle in circles" :key="circle.id" :label="circle.circleName" :value="circle.id"/></ElSelect></ElFormItem></ElCol><ElCol :span="12"><ElFormItem label="状态"><ElSelect v-model="form.status"><ElOption label="启用" value="ACTIVE"/><ElOption label="停用" value="INACTIVE"/></ElSelect></ElFormItem></ElCol></ElRow>
        <ElRow :gutter="18"><ElCol :span="12"><ElFormItem label="开始时间"><ElDatePicker v-model="form.startAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="不限制"/></ElFormItem></ElCol><ElCol :span="12"><ElFormItem label="结束时间"><ElDatePicker v-model="form.endAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="不限制"/></ElFormItem></ElCol></ElRow>
        <ElFormItem label="排序"><ElInputNumber v-model="form.sortOrder" :min="0" :max="9999"/></ElFormItem>
        <ElFormItem label="活动封面"><div class="upload-field"><ElImage v-if="form.coverUrl" :src="form.coverUrl" fit="cover" class="preview"/><div><ElUpload :show-file-list="false" :http-request="uploadCover" accept="image/jpeg,image/png,image/gif,image/webp,image/bmp"><ElButton type="primary" plain :loading="uploading">上传封面</ElButton></ElUpload><ElButton v-if="form.coverUrl" link type="danger" @click="form.coverUrl=''">移除</ElButton><small>上传成功后自动保存 OSS 图片地址</small></div></div></ElFormItem>
        <ElFormItem label="活动简介"><ElInput v-model="form.description" type="textarea" :rows="4" maxlength="512" show-word-limit/></ElFormItem>
      </ElForm>
      <template #footer><ElButton @click="dialogVisible=false">取消</ElButton><ElButton type="primary" :loading="saving" @click="save">创建活动</ElButton></template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadRequestOptions } from 'element-plus'
import type { AxiosError } from 'axios'
import { createActivity, deleteActivity, getActivities, getCircles, uploadImage, type Activity, type ActivityPayload, type Circle } from '@/api/merchant'
import type { ApiResponse } from '@/api/types'
const loading=ref(false),saving=ref(false),uploading=ref(false),dialogVisible=ref(false),formRef=ref<FormInstance>(),activities=ref<Activity[]>([]),circles=ref<Circle[]>([])
const emptyForm=():ActivityPayload=>({topicName:'',circleId:undefined,description:'',coverUrl:'',status:'ACTIVE',startAt:undefined,endAt:undefined,sortOrder:0})
const form=reactive<ActivityPayload>(emptyForm()),rules:FormRules<ActivityPayload>={topicName:[{required:true,message:'请输入活动名称',trigger:'blur'}]}
const formatDate=(value?:string)=>value?new Date(value).toLocaleString('zh-CN'):'不限'
function apiMessage(error:unknown,fallback:string){return (error as AxiosError<ApiResponse<unknown>>).response?.data?.message||fallback}
async function load(){loading.value=true;try{[activities.value,circles.value]=await Promise.all([getActivities(),getCircles()])}catch(error){ElMessage.error(apiMessage(error,'活动列表加载失败'))}finally{loading.value=false}}
function openCreate(){Object.assign(form,emptyForm());dialogVisible.value=true}
async function uploadCover(options:UploadRequestOptions){uploading.value=true;try{const result=await uploadImage(options.file);form.coverUrl=result.url;options.onSuccess(result);ElMessage.success('封面上传成功')}catch(error){options.onError(error as Error);ElMessage.error(apiMessage(error,'封面上传失败，请检查 OSS 配置'))}finally{uploading.value=false}}
async function save(){if(!await formRef.value?.validate())return;if(form.startAt&&form.endAt&&new Date(form.endAt)<=new Date(form.startAt)){ElMessage.warning('结束时间必须晚于开始时间');return}saving.value=true;try{await createActivity({...form});ElMessage.success('活动已创建');dialogVisible.value=false;await load()}catch(error){ElMessage.error(apiMessage(error,'活动创建失败'))}finally{saving.value=false}}
async function remove(activity:Activity){try{await ElMessageBox.confirm(`确认删除活动“${activity.topicName}”？删除后小程序将不再展示。`,'删除活动',{type:'warning',confirmButtonText:'删除',confirmButtonClass:'el-button--danger'});await deleteActivity(activity.id);ElMessage.success('活动已删除');await load()}catch(error){if(error==='cancel'||error==='close')return;ElMessage.error(apiMessage(error,'活动删除失败'))}}
onMounted(load)
</script>

<style scoped lang="scss">
.cover{width:108px;height:62px;border-radius:8px}.no-cover{color:#a0a8b8;font-size:12px}.el-select,.el-date-editor{width:100%!important}.upload-field{display:flex;align-items:center;gap:14px}.preview{width:150px;height:84px;border-radius:10px}.upload-field>div{display:flex;align-items:flex-start;flex-direction:column;gap:5px}.upload-field small{color:#9099aa}
</style>
