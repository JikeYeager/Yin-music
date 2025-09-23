<!--vue文件分层三部分：
1. <template>html代码</template>
2. <script>js/Ts代码</script>
3. <style>css样式代码</style>
-->

<template>
  <div class="login-container">
<!-- 插值语法，作为vue语法中最常见的内容。
  {{ musicName }}双大括号标签会被替换为相应组件实例中 musicName属性的值。
  同时每次 musicName 属性更改时它也会同步更新。
  musicName在JS/TS中去找
-->
    <div class="title">{{ musicName }}
<!-- 用来验证双向绑定，通过v-model把template里面输入的内容传入JS
 <el-input v-model="musicName"></el-input>
 -->
    </div>
    <div class="login">
      <el-form :model="ruleForm" :rules="rules">
        <el-form-item prop="username">
          <el-input v-model="ruleForm.username" placeholder="username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" placeholder="password" v-model="ruleForm.password" @keyup.enter="submitForm"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="login-btn" type="primary" @click="submitForm">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, ref, reactive } from "vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api/index";
import { RouterName, MUSICNAME } from "@/enums";

export default defineComponent({
  setup() {
    //当前vue组件实例，可以通过该实例调用很多我们需要的方法
    const { proxy } = getCurrentInstance();
    const { routerManager } = mixin();
    const musicName = ref(MUSICNAME); //响应式，监控普通数据类型
    // let musicName = ref('MUSIC NAME'); 这里想要验证插值语法的实时修改，要把musicName定为变量
    const ruleForm = reactive({  //响应式，监控对象数据类型
      username: "admin",
      password: "123",
    });
    const rules = reactive({
      username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
      password: [{ required: true, message: "请输入密码", trigger: "blur" }],
    });
    async function submitForm() {
      //0. 获取参数
      let name = ruleForm.username;  //因为后端adminController定义的是name，为了与后端一致才能接收，所以把username改成name
      let password = ruleForm.password;
      //1.请求 登陆接口   await表示等待异步请求执行完毕后执行后续代码，即同步请求
      //结果集返回成ResponseBody的格式
      const result=(await HttpManager.adminLogin({name,password})) as ResponseBody;
      // console.log(result)

      //2.提示信息 ，当点击登录时弹出提示信息
      (proxy as any).$message({
        message:result.message,
        type:result.success?'success':'error' //三元表达式，根据result是success还是error，弹出相应的提示框
      })


      //3. 路由跳转 登录成功则跳转到主页面info
      if(result.success){
        /*3.1 设置info右上角用户名
        为了在info页面更新，因为info的顶部导航栏和侧边栏都是继承Home.vue的，
        而Home.vue的顶部导航栏又是通过layouts组件实现的，所以我们去YinHeader.vue进行修改
        * sessionStorage会话缓存函数，在检查->application中可以查看到存入的值
        * username设置的接收值的属性
        * result.data 因为result.data保存了登录用户的用户名，所以作为接收值传入
        * */
        sessionStorage.setItem("username",result.data)
        routerManager(RouterName.Info,{path:"/info"})
      }

    }
    return {
      musicName,
      ruleForm,
      rules,
      submitForm,
    };
  },
});
</script>

<style scoped>
.login-container {
  position: relative;
  background: url("../assets/images/background.jpg");
  background-attachment: fixed;
  background-position: center;
  background-size: cover;
  width: 100%;
  height: 100%;
}

.title {
  position: absolute;
  top: 50%;
  width: 100%;
  margin-top: -230px;
  text-align: center;
  font-size: 30px;
  font-weight: 600;
  color: #fff;
}

.login {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 300px;
  margin: -150px 0 0 -190px;
  padding: 40px;
  border-radius: 5px;
  background: #fff;
}

.login-btn {
  width: 100%;
}
</style>
