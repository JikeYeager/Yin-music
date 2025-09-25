<template>
  <yin-login-logo></yin-login-logo>
  <div class="sign">
    <div class="sign-head">
      <span>用户注册</span>
    </div>
<!--   :model="registerForm"和v-model一样都是为了进行双向数据绑定-->
<!--   :rules="SignUpRules" 调用了存放在enums的，注册验证的规则SignUpRules -->
    <el-form ref="signUpForm" label-width="70px" status-icon :model="registerForm" :rules="SignUpRules">
      <el-form-item prop="username" label="用户名">
        <el-input v-model="registerForm.username" placeholder="用户名"></el-input>
      </el-form-item>
      <el-form-item prop="password" label="密码">
        <el-input type="password" placeholder="密码" v-model="registerForm.password"></el-input>
      </el-form-item>
      <el-form-item prop="sex" label="性别">
        <el-radio-group v-model="registerForm.sex">
          <el-radio :label="0">女</el-radio>
          <el-radio :label="1">男</el-radio>
          <el-radio :label="2">保密</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item prop="phoneNum" label="手机">
        <el-input placeholder="手机" v-model="registerForm.phoneNum"></el-input>
      </el-form-item>
      <el-form-item prop="email" label="邮箱">
        <el-input v-model="registerForm.email" placeholder="邮箱"></el-input>
      </el-form-item>
      <el-form-item prop="birth" label="生日">
        <el-date-picker type="date" placeholder="选择日期" v-model="registerForm.birth" style="width: 100%"></el-date-picker>
      </el-form-item>
      <el-form-item prop="introduction" label="签名">
        <el-input type="textarea" placeholder="签名" v-model="registerForm.introduction"></el-input>
      </el-form-item>
      <el-form-item prop="location" label="地区">
        <el-select v-model="registerForm.location" placeholder="地区" style="width: 100%">
          <el-option v-for="item in AREA" :key="item.value" :label="item.label" :value="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item class="sign-btn">
        <el-button @click="goBackRegist()">取消</el-button>
        <el-button type="primary" @click="handleSignUp()">确定</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, getCurrentInstance } from "vue";
import mixin from "@/mixins/mixin";
import YinLoginLogo from "@/components/layouts/YinLoginLogo.vue";
import { HttpManager } from "@/api";
import { getBirth } from "@/utils";
import { AREA, RouterName, NavName, SignUpRules } from "@/enums";

export default defineComponent({
  components: {
    YinLoginLogo,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager, changeIndex } = mixin();

    // 注册表单
    const registerForm = reactive({
      username: "",
      password: "",
      sex: "",
      phoneNum: "",
      email: "",
      birth: new Date(),
      introduction: "",
      location: "",
    });

    async function goBackRegist() {
       routerManager(RouterName.SignIn, { path: RouterName.SignIn });
    }

    // 处理注册请求
    async function handleSignUp() {
      // 1.注册验证
      let canRun = true;
      //从signUpForm获取信息后调用validate规则进行判断
      (proxy.$refs["signUpForm"] as any).validate((valid) => {
        if (!valid) return (canRun = false);
      });
      if (!canRun) return;


      try {
        // 2.获取参数
        let username = registerForm.username
        let password = registerForm.password
        let sex = registerForm.sex
        let phoneNum = registerForm.phoneNum
        let email = registerForm.email
        let birth = registerForm.birth
        let introduction = registerForm.introduction
        let location = registerForm.location
        const param = {username,password,sex,phoneNum,email,birth,introduction,location}
        // 3.发送同步请求
        const result=(await HttpManager.signUp(param)) as ResponseBody;
        // 4.提示信息
        (proxy as any).$message({
          message:result.message,
          type:result.success?'success':'error' //三元表达式，根据result是success还是error，弹出相应的提示框
        })
        // 5.如注册成功则跳转到登陆
        if(result.success) {
          routerManager(RouterName.SignIn, {
            path: RouterName.SignIn,
          });
        }

      } catch (error) {
        console.error(error);
      }
    }

    return {
      SignUpRules,
      AREA,
      registerForm,
      handleSignUp,
      goBackRegist,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/sign.scss";
</style>
