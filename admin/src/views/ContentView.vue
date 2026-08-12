<template>
  <div>
    <div class="page-header">
      <div><h1>内容运营</h1><p>管理当前社区的圈子和查看已发布帖子</p></div>
      <div><ElButton @click="load">刷新</ElButton><ElButton type="primary" @click="openCreate">新增圈子</ElButton></div>
    </div>
    <ElCard class="panel-card" shadow="never">
      <ElTabs v-model="tab">
        <ElTabPane label="圈子管理" name="circles">
          <ElTable v-loading="loading" :data="circles">
            <ElTableColumn label="图片" width="90"><template #default="scope"><ElAvatar shape="square" :size="48" :src="scope.row.iconUrl">{{ scope.row.circleName.slice(0,1) }}</ElAvatar></template></ElTableColumn>
            <ElTableColumn prop="circleName" label="圈子名称" min-width="130"/>
            <ElTableColumn prop="circleCode" label="编码" min-width="120"/>
            <ElTableColumn label="状态" width="90"><template #default="scope"><ElTag :type="scope.row.status==='ACTIVE'?'success':'info'">{{scope.row.status==='ACTIVE'?'启用':'停用'}}</ElTag></template></ElTableColumn>
            <ElTableColumn prop="joinMode" label="加入方式" width="110"/>
            <ElTableColumn prop="sortOrder" label="排序" width="75"/>
            <ElTableColumn prop="memberCount" label="成员" width="75"/>
            <ElTableColumn prop="postCount" label="帖子" width="75"/>
            <ElTableColumn prop="description" label="简介" min-width="200" show-overflow-tooltip/>
            <ElTableColumn label="操作" width="140" fixed="right"><template #default="scope"><ElButton link type="primary" @click="openEdit(scope.row)">编辑</ElButton><ElButton link type="danger" @click="remove(scope.row)">删除</ElButton></template></ElTableColumn>
          </ElTable>
        </ElTabPane>
        <ElTabPane label="帖子浏览" name="posts">
          <ElTable v-loading="loading" :data="posts"><ElTableColumn prop="id" label="ID" width="90"/><ElTableColumn label="标题" min-width="220"><template #default="scope">{{scope.row.title||scope.row.summary||'无标题'}}</template></ElTableColumn><ElTableColumn prop="circleName" label="圈子"/><ElTableColumn prop="username" label="作者"/><ElTableColumn prop="status" label="状态"/><ElTableColumn prop="viewCount" label="浏览"/><ElTableColumn prop="commentCount" label="评论"/><ElTableColumn label="操作" width="90" fixed="right"><template #default="scope"><ElButton link type="danger" @click="removePost(scope.row)">删除</ElButton></template></ElTableColumn></ElTable>
        </ElTabPane>
      </ElTabs>
    </ElCard>

    <ElDialog v-model="dialogVisible" :title="editingId?'编辑圈子':'新增圈子'" width="680px" destroy-on-close>
      <ElForm ref="formRef" :model="form" :rules="rules" label-width="90px">
        <ElRow :gutter="18"><ElCol :span="12"><ElFormItem label="圈子名称" prop="circleName"><ElInput v-model="form.circleName" maxlength="64"/></ElFormItem></ElCol><ElCol :span="12"><ElFormItem label="圈子编码" prop="circleCode"><ElInput v-model="form.circleCode" placeholder="如 strategy" :disabled="!!editingId"/></ElFormItem></ElCol></ElRow>
        <ElRow :gutter="18"><ElCol :span="12"><ElFormItem label="加入方式"><ElSelect v-model="form.joinMode"><ElOption label="开放加入" value="OPEN"/><ElOption label="审核加入" value="APPROVAL"/><ElOption label="私密圈子" value="PRIVATE"/></ElSelect></ElFormItem></ElCol><ElCol :span="12"><ElFormItem label="状态"><ElSelect v-model="form.status"><ElOption label="启用" value="ACTIVE"/><ElOption label="停用" value="INACTIVE"/></ElSelect></ElFormItem></ElCol></ElRow>
        <ElFormItem label="排序"><ElInputNumber v-model="form.sortOrder" :min="0" :max="9999"/></ElFormItem>
        <ElFormItem label="圈子图标"><ImageField v-model="form.iconUrl" label="上传图标" tip="建议正方形，支持 jpg/png/webp，大小按后端 OSS 配置限制"/></ElFormItem>
        <ElFormItem label="圈子封面"><ImageField v-model="form.coverUrl" label="上传封面" tip="建议横图，上传成功后自动填入 OSS 地址" wide/></ElFormItem>
        <ElFormItem label="圈子简介"><ElInput v-model="form.description" type="textarea" :rows="3" maxlength="500" show-word-limit/></ElFormItem>
      </ElForm>
      <template #footer><ElButton @click="dialogVisible=false">取消</ElButton><ElButton type="primary" :loading="saving" @click="save">保存</ElButton></template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { defineComponent, h, onMounted, reactive, ref } from 'vue'
import { ElButton, ElImage, ElMessage, ElMessageBox, ElUpload, type FormInstance, type FormRules, type UploadRequestOptions } from 'element-plus'
import type { AxiosError } from 'axios'
import { createCircle, deleteCircle, deletePost, getCircles, getPosts, updateCircle, uploadImage, type Circle, type CirclePayload, type Post } from '@/api/merchant'
import type { ApiResponse } from '@/api/types'

const ImageField=defineComponent({props:{modelValue:{type:String,default:''},label:{type:String,required:true},tip:{type:String,default:''},wide:Boolean},emits:['update:modelValue'],setup(props,{emit}){const uploading=ref(false);async function request(options:UploadRequestOptions){uploading.value=true;try{const result=await uploadImage(options.file);emit('update:modelValue',result.url);ElMessage.success('图片上传成功');options.onSuccess(result)}catch(error){options.onError(error as Error);ElMessage.error(apiMessage(error,'图片上传失败，请检查 OSS 配置'))}finally{uploading.value=false}}return()=>h('div',{class:'image-field'},[props.modelValue?h(ElImage,{src:props.modelValue,fit:'cover',class:props.wide?'preview preview--wide':'preview'}):null,h('div',{class:'image-actions'},[h(ElUpload,{showFileList:false,httpRequest:request,accept:'image/jpeg,image/png,image/gif,image/webp,image/bmp'},{default:()=>h(ElButton,{loading:uploading.value,type:'primary',plain:true},()=>props.label)}),props.modelValue?h(ElButton,{link:true,type:'danger',onClick:()=>emit('update:modelValue','')},()=> '移除'):null,h('small',props.tip)])])}})

const tab=ref('circles'),loading=ref(false),saving=ref(false),dialogVisible=ref(false),editingId=ref<number>(),formRef=ref<FormInstance>(),circles=ref<Circle[]>([]),posts=ref<Post[]>([])
const emptyForm=():CirclePayload=>({circleCode:'',circleName:'',iconUrl:'',coverUrl:'',description:'',joinMode:'OPEN',status:'ACTIVE',sortOrder:0})
const form=reactive<CirclePayload>(emptyForm())
const rules:FormRules<CirclePayload>={circleName:[{required:true,message:'请输入圈子名称',trigger:'blur'}],circleCode:[{required:true,message:'请输入圈子编码',trigger:'blur'},{pattern:/^[a-z0-9][a-z0-9_-]{1,63}$/,message:'请输入 2-64 位小写字母、数字、横线或下划线',trigger:'blur'}]}
function apiMessage(error:unknown,fallback:string){return (error as AxiosError<ApiResponse<unknown>>).response?.data?.message||fallback}
async function load(){loading.value=true;try{const[c,p]=await Promise.all([getCircles(),getPosts({page:1,pageSize:50})]);circles.value=c;posts.value=p.records}catch(error){ElMessage.error(apiMessage(error,'内容数据加载失败'))}finally{loading.value=false}}
function resetForm(){Object.assign(form,emptyForm())}
function openCreate(){editingId.value=undefined;resetForm();dialogVisible.value=true}
function openEdit(circle:Circle){editingId.value=circle.id;Object.assign(form,{circleCode:circle.circleCode,circleName:circle.circleName,iconUrl:circle.iconUrl||'',coverUrl:circle.coverUrl||'',description:circle.description||'',joinMode:circle.joinMode,status:circle.status,sortOrder:circle.sortOrder||0});dialogVisible.value=true}
async function save(){if(!await formRef.value?.validate())return;saving.value=true;try{if(editingId.value)await updateCircle(editingId.value,{...form});else await createCircle({...form});ElMessage.success(editingId.value?'圈子已更新':'圈子已创建');dialogVisible.value=false;await load()}catch(error){ElMessage.error(apiMessage(error,'保存圈子失败'))}finally{saving.value=false}}
async function remove(circle:Circle){try{await ElMessageBox.confirm(`确认删除圈子“${circle.circleName}”？仅空圈子可以删除。`,'删除圈子',{type:'warning',confirmButtonText:'删除',confirmButtonClass:'el-button--danger'});await deleteCircle(circle.id);ElMessage.success('圈子已删除');await load()}catch(error){if(error==='cancel'||error==='close')return;ElMessage.error(apiMessage(error,'删除圈子失败'))}}
async function removePost(post:Post){const title=post.title||post.summary||`帖子 #${post.id}`;try{await ElMessageBox.confirm(`确认删除帖子“${title}”？删除后用户端将无法访问。`,'删除帖子',{type:'warning',confirmButtonText:'删除',confirmButtonClass:'el-button--danger'});await deletePost(post.id);ElMessage.success('帖子已删除');await load()}catch(error){if(error==='cancel'||error==='close')return;ElMessage.error(apiMessage(error,'删除帖子失败'))}}
onMounted(load)
</script>

<style scoped lang="scss">
:deep(.image-field){display:flex;align-items:center;gap:14px;width:100%}:deep(.preview){width:72px;height:72px;border-radius:10px}:deep(.preview--wide){width:150px;height:84px}:deep(.image-actions){display:flex;align-items:flex-start;flex-direction:column;gap:5px}:deep(.image-actions small){color:#9099aa;line-height:1.5}.el-select{width:100%}
</style>
