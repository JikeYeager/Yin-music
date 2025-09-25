// 登录规则

/* 判断用户名是否为空 */
const validateName = (rule, value, callback) => {
  if (!value) {
    return callback(new Error("用户名不能为空"));
  } else {
    callback();
  }
};

/* 判断密码是否为空 */
export const validatePassword = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("密码不能为空"));
  } else {
    callback();
  }
};
//
export const SignInRules = {
  //用户名（username）：使用 validateName 验证器，在失焦（blur）时触发，且最小长度为 3
  username: [{ validator: validateName, trigger: "blur", min: 3 }],
  password: [{ validator: validatePassword, trigger: "blur", min: 3 }],
};

// 注册规则 ；导出 SignUpRules 对象，包含注册表单的验证规则
export const SignUpRules = {
  username: [{ required: true, trigger: "blur", min: 3 }],
  password: [{ required: true, trigger: "blur", min: 3 }],
  // 性别（sex）：必填，选择变化时触发验证，提示 “请选择性别”
  sex: [{ required: true, message: "请选择性别", trigger: "change" }],
  phoneNum: [{ message: "请输入手机号码", trigger: "blur" }],
  //email： 失焦时提示 “请输入邮箱地址”；失焦和内容变化时验证格式，提示 “请输入正确的邮箱地址”
  email: [
    { message: "请输入邮箱地址", trigger: "blur" },
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: ["blur", "change"],
    },
  ],
  birth: [{ required: true, type: "date", message: "请选择日期", trigger: "change" }],
  introduction: [{ message: "请输入介绍", trigger: "blur" }],
  location: [{ message: "请输入地区", trigger: "change" }],
};
