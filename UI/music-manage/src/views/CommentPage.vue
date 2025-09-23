<template>
  <el-breadcrumb class="crumbs" separator="/">
    <el-breadcrumb-item v-for="item in breadcrumbList" :key="item.name" :to="{ path: item.path, query: item.query }">
      {{ item.name }}
    </el-breadcrumb-item>
  </el-breadcrumb>

  <div class="container">
    <div class="handle-box">
      <el-button @click="deleteAll">批量删除</el-button>
      <el-input v-model="searchWord" placeholder="筛选关键词"></el-input>
    </div>
    <el-table height="550px" border size="small" :data="tableData" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40" align="center"></el-table-column>
      <el-table-column prop="id" label="ID" width="50"></el-table-column>
      <el-table-column prop="username" label="用户" width="80"></el-table-column>
      <el-table-column prop="content" label="评论内容"></el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template v-slot="scope">
          <el-button type="danger" @click="deleteRow(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <!-- 删除提示框 -->
  <yin-del-dialog :delVisible="delVisible" @confirm="confirm" @cancelRow="delVisible = $event"></yin-del-dialog>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, watch, ref, computed, onMounted } from "vue";
import { useStore } from "vuex";
import { HttpManager } from "@/api";
import YinDelDialog from "@/components/dialog/YinDelDialog.vue";

export default defineComponent({
  components: {
    YinDelDialog,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();

    const tableData = ref([]); // 记录歌曲，用于显示
    const tempDate = ref([]); // 记录歌曲，用于搜索时能临时记录一份歌曲列表
    const breadcrumbList = computed(() => store.getters.breadcrumbList);

    const searchWord = ref(""); // 记录输入框输入的内容
    watch(searchWord, () => {
      //假搜索
    });

    getData();
//0. 从songPage跳转的当前路由的query中获取id和type，并赋值给resourceId 和type。

    const resourceId  = ref(proxy.$route.query.id as string);
    const type = ref(proxy.$route.query.type as string); // 这里应该是'0'(字符串类型)

    // 获取评论
     async function getData() {
      tableData.value = [];
      tempDate.value = [];
      let promise = null;
      //1. 构建查询参数  这里和收藏页面一样，不使用分页查询,只传id和type
      const param = {
        "resourceId": resourceId.value,
        "type": type.value,
        "keyword":searchWord.value
      }
       //2. 根据不同的type区分歌曲评论和歌单评论，发送不同的异步请求
      if(Number(type.value) === 0){
        // const result = (await HttpManager.getCommentOfSongId(param)) as ResponseBody;
        HttpManager.getCommentOfSongId(param).then(res =>{
              //2.1 、请求所有用户接口
              const body0 = res as ResponseBody
              console.log(body0.data)
            }
        )
      }else {
        HttpManager.getCommentOfSongListId(param).then(res =>{
              //2.1 、请求所有用户接口
              const body1 = res as ResponseBody
              console.log(body1.data)
            }
        )
      }


      //根据type判断  0：歌曲评论  1：歌单评论
      // 根据type和resourceId 获取评论
      //根据响应结果获取userId，获取用户信息  getUsers
    }
    async function getUsers(id, item) {
      //获取用户信息
      const result = (await HttpManager.getUserOfId(id)) as ResponseBody;
      //更新表格信息

    }

    /**
     * 删除
     */
    const idx = ref(-1); // 记录当前要删除的行
    const multipleSelection = ref([]); // 记录当前要删除的列表
    const delVisible = ref(false); // 显示删除框

    async function confirm() {

      delVisible.value = false;
    }
    function deleteRow(id) {
      idx.value = id;
      delVisible.value = true;
    }
    function handleSelectionChange(val) {
      multipleSelection.value = val;
    }
    function deleteAll() {
      for (let item of multipleSelection.value) {
        deleteRow(item.id);
        confirm();
      }
      multipleSelection.value = [];
    }

    return {
      searchWord,
      tableData,
      delVisible,
      breadcrumbList,
      deleteAll,
      handleSelectionChange,
      deleteRow,
      confirm,
    };
  },
});
</script>

<style scoped></style>
