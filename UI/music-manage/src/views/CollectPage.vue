<template>
  <el-breadcrumb class="crumbs" separator="/">
    <el-breadcrumb-item v-for="item in breadcrumbList" :key="item.name" :to="{ path: item.path, query: item.query }">
      {{ item.name }}
    </el-breadcrumb-item>
  </el-breadcrumb>

  <div class="container">
    <div class="handle-box">
      <el-button @click="deleteAll">批量删除</el-button>
      <el-input placeholder="筛选歌曲" v-model="searchWord"></el-input>
      <!--添加查询组件  调用getData()以实现查询功能 注意要在setup方法中return getData方法-->
      <el-button type="primary" @click="getData">查询</el-button>
    </div>
    <el-table height="550px" border size="small" :data="tableData" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40" align="center"></el-table-column>
      <el-table-column prop="name" label="歌手-歌曲"></el-table-column>
      <el-table-column label="操作" width="90" align="center">
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
import { defineComponent, getCurrentInstance, watch, ref, computed } from "vue";
import { useStore } from "vuex";
import { HttpManager } from "@/api";
import YinDelDialog from "@/components/dialog/YinDelDialog.vue";

// 占用ResponseBody关键字了
// interface ResponseBody {
//   code: string;
//   success: boolean;
//   message: string;
//   type: string;
// }

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

    // 通过用户 ID 获取用户收藏的歌曲 ID
    async function getData() {
      tableData.value = [];
      // console.log("id:",proxy.$route.query.id)  //看一下点击用户时显示的id是多少

     // 1.定义用户收藏功能需要的参数，根据userId和songName进行相应查询
      const param ={
        "userId" :proxy.$route.query.id,
        "songName" :searchWord.value  //搜索框传入的值
      }

      //2.发送异步请求
      HttpManager.getCollectionOfUserAndSongName(param).then(res =>{
        //2.1 、请求所有用户接口
        const body = res as ResponseBody
        console.log(body.data)

        //2.2 表格数据
        tableData.value = body.data

      })

    }

    /**
     * 删除
     */
    const idx = ref(-1); // 记录当前要删除的行
    const multipleSelection = ref([]); // 记录当前要删除的列表
    const delVisible = ref(false); // 显示删除框

    //单个删除
    async function confirm() {
      // 1. 把提示框隐藏掉
      delVisible.value = false;
      //调用删除接口
      const result = (await HttpManager.deleteCollection(idx.value)) as ResponseBody
      //提示信息
      (proxy as any).$message({
        message:result.message,
        type:result.success?'success':'error' //三元表达式，根据result是success还是error，弹出相应的提示框
      })
      //刷新表格
      getData()

    }
    function deleteRow(id) {
      idx.value = id;
      delVisible.value = true;
    }
    function handleSelectionChange(val) {
      multipleSelection.value = val;
    }
    async function deleteAll() { //async定义异同步请求
      //1、多选的数组传给后端
      //multipleSelection.value.map(row =>row.id)  把整行数据转出id
      //join(",") 把数组元素用逗号拼接返回字符串1,2,3
      const ids = multipleSelection.value.map(row => row.id).join(",")  //map函数把行id赋值给value再把这些值以字符串的格式拼接起来
      console.log(ids)
      //1.1 发起批量删除请求
      const result = (await HttpManager.deleteAllCollection(ids)) as ResponseBody
      //1.2 提示信息
      (proxy as any).$message({
        message:result.message,
        type:result.success?'success':'error' //三元表达式，根据result是success还是error，弹出相应的提示框
      })

      //2.刷新表格
      getData()
      //3.清空勾选项
      multipleSelection.value = [];
    }

    return {
      searchWord,
      tableData,
      delVisible,
      breadcrumbList,
      getData,  //返回getData()方法，如果在<script>标签中写setup方法就不用return了
      deleteAll,
      handleSelectionChange,
      deleteRow,
      confirm,
    };
  },
});
</script>

<style scoped></style>
