<template>
  <el-form ref="passwordForm" label-width="70px" :model="form" :rules="rules">
    <el-form-item label="旧密码" prop="oldPassword">
      <el-input type="password" v-model="form.oldPassword" />
    </el-form-item>
    <el-form-item label="新密码" prop="newPassword">
      <el-input type="password" v-model="form.newPassword" />
    </el-form-item>
    <el-form-item label="密码确认" prop="confirmPassword">
      <el-input type="password" v-model="form.confirmPassword" />
    </el-form-item>
    <el-form-item>
      <el-button @click="clearData()">重置</el-button>
      <el-button type="primary" @click="confirm()">确认修改</el-button>
    </el-form-item>
  </el-form>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, computed, reactive } from "vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
//从validate.ts引用了validatePassword来判断密码是否为空
import { validatePassword } from "@/enums";

export default defineComponent({
  setup() {
    const store = useStore();
    const { proxy } = getCurrentInstance();
    const { goBack } = mixin();

    const form = reactive({
      oldPassword: "",
      newPassword: "",
      confirmPassword: "",
    });

    //在SignIn登录页面保存的登录信息，我们这里取出来用
    const userId = computed(() => store.getters.userId);
    const userName = computed(() => store.getters.username);

    /* 输入合法性验证 */
    const validateCheck = (rule: any, value: any, callback: any) => {
      if (value === "") {
        callback(new Error("密码不能为空"));
      } else if (value !== form.newPassword) {
        callback(new Error("请输入正确密码"));
      } else {
        callback();
      }
    };

    //验证规则（为什么不直接导入 :rules="SignInRules/SignUpRules"呢？）
    //因为在validate.ts中定义的登录规则和注册规则并不是我们想要的，所以我们在这里自己写一个。
    //但其实从还是从validate.ts引用了validatePassword
    //当然，我觉得更好的办法应该是在validate.ts里补充并且再调用重置密码规则比较符合规范一点
    const rules = reactive({
      oldPassword: [{ validator: validatePassword, trigger: "blur", min: 3 }],
      newPassword: [{ validator: validatePassword, trigger: "blur", min: 3 }],
      //validateCheck则是我们自己在上面写的合法性验证，作为我们重置密码的规则中的一条
      confirmPassword: [{ validator: validateCheck, trigger: "blur", min: 3 }],
    });

    //点击“重置”按钮后清空表单
    async function clearData() {
      form.oldPassword = "";
      form.newPassword = "";
      form.confirmPassword = "";
    }

    // 重置密码
    // 点击“确认修改”按钮后执行重置密码方法
    async function confirm() {
      // 1.重置密码输入合法性验证
      let canRun = true;
      (proxy.$refs["passwordForm"] as any).validate((valid) => {
        if (!valid) return (canRun = false);
      });
      if (!canRun) return;

      //2. 构建请求参数
      const id = userId.value;
      const username = userName.value;
      const oldPassword = form.oldPassword;
      const password = form.newPassword;
      const confirmPassword = form.confirmPassword
      const param = {id,username,oldPassword,password,confirmPassword}

      //3. 发送请求
      const result = (await HttpManager.updateUserPassword(param)) as ResponseBody

      //4. 提示信息
      (proxy as any).$message({
        message:result.message,
        type:result.success?'success':'error' //三元表达式，根据result是success还是error，弹出相应的提示框
      })

      //5. goBack() 在密码修改成功后，让用户返回之前的页面
      if (result.success)
        goBack();
    }

    return {
      form,
      clearData,
      confirm,
      rules,
    };
  },
});
</script>

<style></style>
