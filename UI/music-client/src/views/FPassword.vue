<template>
  <el-container id="appSend">
    <el-header>
      <h1>重置密码</h1>
    </el-header>
    <el-main>
      <el-form @submit.prevent="handleSubmit">
        <el-form-item label="邮箱：" prop="email">
          <el-input id="email" v-model="email" type="email" required />
          <el-button @click="sendVerificationCode">发送验证码</el-button>
        </el-form-item>
        <el-form-item label="验证码：" prop="code">
          <el-input id="code" v-model="code" type="text" required />
        </el-form-item>
        <el-form-item label="新密码：" prop="password">
          <el-input id="password" v-model="password" type="password" required />
        </el-form-item>
        <el-form-item label="确认密码：" prop="confirmPassword">
          <el-input id="confirmPassword" v-model="confirmPassword" type="password" required />
        </el-form-item>
        <el-form-item>
          
          <el-button @click="handleSubmit" type="submit">提交</el-button>
          
        </el-form-item>
      </el-form>
    </el-main>
  </el-container>
</template>


<style>
#appSend {
  max-width: 400px;
  margin: 0 auto;
}
</style>

<script>
import axios from 'axios';
import { defineComponent } from 'vue';
import {routerManager} from '@/mixins/mixin'; // 导入路由管理函数
import {RouterName} from "@/enums";


export default {

  data() {
  
    return {
      email: "",
      code: "",
      password: "",
      confirmPassword: ""
    };
  },
  methods: {
    //点击“发送验证码”，通过邮箱发送验证码
    async sendVerificationCode() {
      try {
        //1. 定义请求参数
         const email =document.getElementById('email').value;
        /*其实可以直接使用v-model绑定的email，不需要document.getElementById
        const email = this.email;*/

         //2. 发起同步请求  端口号改为9999
  //        const response = await axios.get('http://localhost:9999/consumer/sendVerificationCode',({params: {
  //        email: email
  // }}));          这就一个参数还搞这么复杂，没必要，使用下面的即可
         const response = await axios.get(`http://localhost:9999/consumer/sendVerificationCode/${email}`)
         console.log(response.data);

         //3. 提示信息  显示出请求返回的数据
        this.$message({
          // message: response.data,
          message: response.data.message,  //修改为message信息
          type: response.data.success?'success':'error'
        });
      } catch (error) {
        //7.20 新增错误信息修改
        let message = '发送验证码失败';
        if (error.response && error.response.data && error.response.data.message) {
          message = error.response.data.message;
        } else {
          message = error.message || message;
        }
        console.error('Error submitting email:');

       this.$message({
        // message: 'response.data',  //邮箱为空时才会显示此错误提示信息
         //不等价于 message:'发送验证码失败' 因为倘若出现“邮箱不存在”的错误进行全局异常处理的覆盖时，
         // 样式会使用success的样式而不是error样式，所以这里我们要使用定义的变量message
         message:message,
        type: 'error'
      });
 } 
   },

    //重置密码
    async handleSubmit() {
  try {
    //1. 接收重置密码表单参数
    const email =document.getElementById('email').value;
    const code=document.getElementById('code').value
    const password=document.getElementById('password').value
    const confirmPassword=document.getElementById('confirmPassword').value

    //2.构建请求参数
    const data = {
      email: email,
      code: code,
      password: password,
      confirmPassword: confirmPassword
    };

    //3.发起同步请求
    const response = await axios.post('http://localhost:9999/consumer/resetPassword', data);
    console.log(response.data);

    //4.提示信息
    this.$message({
      // message: response.data,
      message: response.data.message,  //返回的response（R类）的message为提示信息
      type: response.data.success?'success':'error',
      duration: 2000
    });

    //5. 路由跳转  重置密码成功则跳转到登录页面
    //5.1 立即跳转
    // if(response.data.success) {
    //   this.$router.push(RouterName.SignIn);
    // }
    //5.2 延迟跳转    替换原有的 routerManager 调用
    if (response.data.success) {
      setTimeout(() => {
        this.$router.push({ path: RouterName.SignIn });
      }, 1000); // 延迟1秒跳转
    }
  } catch (error) {
    //7.21 新增错误信息修改
    let message = '发送验证码失败';
    if (error.response && error.response.data && error.response.data.message) {
      message = error.response.data.message;
    } else {
      message = error.message || message;
    }
    console.error('Error submitting email:');

    this.$message({
      // message: 'response.data', //这里提示信息写的不好
      message:message,
      type: 'error',
      duration: 2000
    });
  }


}

},
};
</script>
