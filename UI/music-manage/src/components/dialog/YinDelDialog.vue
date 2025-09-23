<template>
  <div>
    <!-- 删除提示框 -->
    <el-dialog title="提示" v-model="centerDialogVisible" width="300px" center>
      <div class="del-dialog-cnt" align="center">删除不可恢复，是否确定删除？</div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelRow">取 消</el-button>
          <el-button type="primary" @click="confirm">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, toRefs, watch, ref } from "vue";

// 删除提示框的事件触发逻辑，点击取消时，cancelRow引发  confirm
export default defineComponent({
  props: {
    delVisible: Boolean,  //定义delVisible类型
  },
  emits: ["cancelRow", "confirm"],   //两种触发情况
  setup(props) {
    const { proxy } = getCurrentInstance();

    const { delVisible } = toRefs(props);
    const centerDialogVisible = ref(delVisible.value);

    // 事件监听（触发器）
    watch(delVisible, (value) => {
      centerDialogVisible.value = value;
    });

    // 点击取消时centerDialogVisible=false
    function cancelRow() {
      proxy.$emit("cancelRow", false);
    }

    // 点击确认时centerDialogVisible=true
    function confirm() {
      proxy.$emit("confirm", null);
    }
    return {
      centerDialogVisible,
      cancelRow,
      confirm,
    };
  },
});
</script>
