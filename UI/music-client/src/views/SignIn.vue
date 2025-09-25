<template>
  <yin-login-logo></yin-login-logo>
  <div class="sign">
    <div class="sign-head">
      <span>帐号登录</span>
    </div>
    <el-form ref="signInForm" status-icon :model="loginForm" :rules="SignInRules">
      <el-form-item prop="username">
        <el-input placeholder="用户名" v-model="loginForm.username"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" placeholder="密码" v-model="loginForm.password" @keyup.enter="handleLoginIn"></el-input>
      </el-form-item>
      <el-form-item class="sign-btn">
        <el-button @click="handleSignUp">注册</el-button>
        <el-button type="primary" @click="handleLoginIn">登录</el-button>
        <el-button @click="handleForgotPassword">忘记密码</el-button>
        <el-button @click="handleEmail">邮箱登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, getCurrentInstance } from "vue";
import mixin from "@/mixins/mixin";
import YinLoginLogo from "@/components/layouts/YinLoginLogo.vue";
import { HttpManager } from "@/api";
import { NavName, RouterName, SignInRules } from "@/enums";

export default defineComponent({
  components: {
    YinLoginLogo,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager, changeIndex } = mixin();

    // 登录表单，有用户名、密码属性
    const loginForm = reactive({
      username: "",
      password: "",
    });

    //处理登录请求
    async function  handleLoginIn() {
      // 1.登录验证
      let canRun = true;
      (proxy.$refs["signInForm"] as any).validate((valid) => {
        if (!valid) return (canRun = false);
      });
      if (!canRun) return;


      try {
        // 2.获取参数
        let username = loginForm.username
        let password = loginForm.password
        // 3.请求结构
        const result = (await HttpManager.signIn({username,password})) as ResponseBody
        // 4.提示信息
            (proxy as any).$message({
              message:result.message,
              type:result.success?'success':'error' //三元表达式，根据result是success还是error，弹出相应的提示框
            })
        console.log(result)
        console.log(result.data)
        // 5.保存信息 ???
        if (result.success) {
          //  检查数据有效性
          // proxy.$store.commit("setUserId", result.data[0].id);   下面的都把“data[0]”改为data
          proxy.$store.commit("setUserId", result.data.id);
          proxy.$store.commit("setUsername", result.data.username);
          proxy.$store.commit("setUserPic", result.data.avator);
          proxy.$store.commit("setToken", true);

          // //  修改commit方式 - 添加模块名前缀  7.24     是错误的。。。
          // proxy.$store.commit("store/user/setUserId", result.data.id);
          // proxy.$store.commit("/user/setUsername", result.data.username);
          // proxy.$store.commit("/store/user/setUserPic", result.data.avator);
          // // configure模块的token设置
          // proxy.$store.commit("configure/setToken", true);

          console.log('登录成功，设置userId:', result.data.id);
          console.log('登录成功，设置username:', result.data.username);
          console.log('登录成功，设置avator:', result.data.avator);
          console.log('当前store中的userId:', proxy.$store.getters['user/userId']);






          //6. 记录当前用户选中的导航项（Home），用于在页面中同步显示导航的激活状态（如高亮当前选中的菜单）
          changeIndex(NavName.Home); //NavName.Home是我们自行定义的常量，NavName.Home="首页"
          //7. 路由跳转  跳转到Home页面
          routerManager(RouterName.Home, { path: RouterName.Home });
        }
      } catch (error) {
        console.error(error);
      }
    }

    //登录页面有“注册”按钮，点击跳转到注册页面
    function handleSignUp() {
      routerManager(RouterName.SignUp, { path: RouterName.SignUp });
    }

    //忘记密码
     function handleForgotPassword() {
      routerManager(RouterName.ForgotPassword, { path: RouterName.ForgotPassword });
    }
    //邮箱登录
    function handleEmail() {
      routerManager(RouterName.loginByemail, { path: RouterName.loginByemail });
    }


    return {
      loginForm,
      SignInRules,
      handleLoginIn,
      handleForgotPassword,
      handleEmail,
      handleSignUp,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/sign.scss";
</style>
