<template>
  <yin-login-logo></yin-login-logo>
  <div class="sign">
    <div class="sign-head">
      <span>邮箱登录</span>
    </div>
    <el-form ref="signInForm" status-icon :model="registerForm" :rules="SignInRules">
      <el-form-item prop="email">
        <el-input placeholder="邮箱" v-model="registerForm.email"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" placeholder="密码" v-model="registerForm.password" @keyup.enter="handleLoginIn"></el-input>
      </el-form-item>
      <el-form-item class="sign-btn">
        <el-button type="primary" @click="handleLoginIn">登录</el-button>
         <el-button type="primary" @click="handleLoginCancel">取消</el-button>
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

    // 邮箱登录表单
    const registerForm = reactive({
      email: "",
      password: "",
    });

    //点击“取消”，返回用户密码登录页面
    async function handleLoginCancel() {
       routerManager(RouterName.SignIn, { path: RouterName.SignIn });
    }

    //处理登录请求
    async function handleLoginIn() {
      // 1.登录验证
      let canRun = true;
      (proxy.$refs["signInForm"] as any).validate((valid) => {
        if (!valid) return (canRun = false);
      });
      if (!canRun) return;


      try {
        // 2.获取参数
        let email = registerForm.email
        let password = registerForm.password
        // 3.发送请求
        const result = (await HttpManager.signInByemail({email,password})) as ResponseBody
        // 4.提示信息
        (proxy as any).$message({
          message:result.message,
          type:result.success?'success':'error' //三元表达式，根据result是success还是error，弹出相应的提示框
        })
        // 5.保存信息
        if (result.success) {
          proxy.$store.commit("setUserId", result.data.id);
          proxy.$store.commit("setUsername", result.data.username);
          proxy.$store.commit("setUserPic", result.data.avator);
          proxy.$store.commit("setToken", true);
          //6. 记录当前用户选中的导航项（Home），用于在页面中同步显示导航的激活状态（如高亮当前选中的菜单）
          changeIndex(NavName.Home); //NavName.Home是我们自行定义的常量，NavName.Home="首页"
          //7. 路由跳转  跳转到Home页面
          routerManager(RouterName.Home, { path: RouterName.Home });
        }
      } catch (error) {
        console.error(error);
      }
    }

    return {
      registerForm,
      SignInRules,
      handleLoginIn,
      handleLoginCancel,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/sign.scss";
</style>
