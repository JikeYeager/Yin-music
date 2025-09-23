<template>
  <div class="container">
    <div class="handle-box">
      <el-button @click="deleteAll">批量删除</el-button>
      <el-input v-model="searchWord" placeholder="筛选关键词"></el-input>
      <el-button type="primary" @click="getData">查询</el-button>
      <el-button type="primary" @click="centerDialogVisible = true">添加歌单</el-button>
      <el-button type="primary" @click="exportPlaylist">导出歌单</el-button>
    </div>
    <el-table height="550px" border size="small" :data="data" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40" align="center"></el-table-column>
      <el-table-column label="ID" prop="id" width="50" align="center"></el-table-column>
      <el-table-column label="歌单图片" width="110" align="center">
        <template v-slot="scope">
          <img :src="attachImageUrl(scope.row.pic)" style="width: 80px"/>
          <el-upload :action="uploadUrl(scope.row.id)" :show-file-list="false" :on-success="handleImgSuccess"
                     :before-upload="beforeImgUpload">
            <el-button>更新图片</el-button>
          </el-upload>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="200"></el-table-column>
      <el-table-column label="简介">
        <template v-slot="scope">
          <p style="height: 100px; overflow: scroll">
            {{ scope.row.introduction }}
          </p>
        </template>
      </el-table-column>
      <el-table-column label="风格" prop="style" width="100"></el-table-column>
      <el-table-column label="内容" width="90" align="center">
        <template v-slot="scope">
          <el-button @click="goContentPage(scope.row.id)">内容</el-button>
        </template>
      </el-table-column>
      <el-table-column label="评论" width="90" align="center">
        <template v-slot="scope">
          <el-button @click="goCommentPage(scope.row.id)">评论</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center">
        <template v-slot="scope">
          <el-button @click="editRow(scope.row)">编辑</el-button>
          <el-button type="danger" @click="deleteRow(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        class="pagination"
        background
        layout="total, prev, pager, next"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalCount"
        @current-change="handleCurrentChange"
    >
    </el-pagination>
  </div>

  <!--添加歌单-->
  <el-dialog title="添加歌单" v-model="centerDialogVisible">
    <el-form label-width="70px" :model="registerForm">
      <el-form-item label="歌单名" prop="title">
        <el-input v-model="registerForm.title"></el-input>
      </el-form-item>
      <el-form-item label="歌单介绍" prop="introduction">
        <el-input v-model="registerForm.introduction"></el-input>
      </el-form-item>
      <el-form-item label="风格" prop="style">
        <el-input v-model="registerForm.style"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addsongList">确 定</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 歌单编辑弹出框 -->
<!--  前端仅显示标题title、简介introduction、风格style供修改-->
  <el-dialog title="编辑" v-model="editVisible">
    <el-form :model="editForm">
      <el-form-item label="标题">
        <el-input v-model="editForm.title"></el-input>
      </el-form-item>
      <el-form-item label="简介">
        <el-input type="textarea" v-model="editForm.introduction"></el-input>
      </el-form-item>
      <el-form-item label="风格">
        <el-input v-model="editForm.style"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="editVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveEdit">确 定</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 删除提示框 -->
  <yin-del-dialog :delVisible="delVisible" @confirm="confirm" @cancelRow="delVisible = $event"></yin-del-dialog>
</template>

<script lang="ts">
import {computed, defineComponent, getCurrentInstance, reactive, ref, watch} from "vue";
import mixin from "@/mixins/mixin";
import {HttpManager} from "@/api/index";
import {RouterName} from "@/enums";
import YinDelDialog from "@/components/dialog/YinDelDialog.vue";
import axios from 'axios';
export default defineComponent({
  components: {
    YinDelDialog,
  },
  setup() {
    const {proxy} = getCurrentInstance();
    const {routerManager, beforeImgUpload} = mixin();

    const tableData = ref([]); // 记录歌曲，用于显示
    const tempDate = ref([]); // 记录歌曲，用于搜索时能临时记录一份歌曲列表
    const pageSize = ref(5); // 页数
    const currentPage = ref(1); // 当前页
    const totalCount = ref(0);  // 定义总记录数

    // 计算当前表格中的数据
    const data = computed(() => {
      //假分页
      return tableData.value
    });

    const searchWord = ref(""); // 记录输入框输入的内容
    watch(searchWord, () => {
      //假搜索
    });

    getData();

    // 分页查询获取歌单信息
    async function getData() {
      tableData.value = [];
      // 1.1 分页查询参数
      const param = {
        "currentPage":currentPage.value,
        "pageSize":pageSize.value,   //单页面显示的记录数
        "keyword":searchWord.value  //搜索框定义的是searchWord
      }
      //1.2 发送异步请求
      HttpManager.getPageSongList(param).then(res =>{
        //1.3通过分页查询，请求所有用户接口
        const body = res as ResponseBody
        // console.log(body)  //分页查询成功的所有信息
        console.log(body.data)  //data还包含了分页查询的相关参数
        console.log(body.data.records)  //这个才是返回的用户数据

        //表格数据
        tableData.value = body.data.records
        //更新记录总数
        totalCount.value = body.data.total
      })

    }

    //导出歌单方法
    function exportPlaylist() {
      axios({
        method: 'get',
        url: 'http://localhost:8888/excle',
        responseType: 'blob', // 设置响应类型为blob
      })
        .then((response) => {
          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', 'SongList.xlsx'); // 设置下载的文件名
          document.body.appendChild(link);
          link.click();
          link.remove();
        })
        .catch((error) => {
          console.error('导出歌单失败：', error);
        });
  }

    // 分页切换，通过获取当前页再刷新表格实现
    function handleCurrentChange(val) {
      currentPage.value = val;
      getData()
    }


    // 更新用户图片
    function uploadUrl(id) {
      return HttpManager.attachImageUrl(`songList/avatar/${id}`); // songList前不用加“/”，在路径跳转那里我们已经加了
    }

    // 更新图片
    function handleImgSuccess(response, file) {
      (proxy as any).$message({
        message: response.message,
        type: response.type,
      });
      if (response.success) getData();
    }

    /**
     * 路由
     */
    function goContentPage(id) {
      const breadcrumbList = reactive([
        {
          path: RouterName.SongList,
          name: "歌单管理",
        },
        {
          path: "",
          name: "歌单内容",
        },
      ]);
      proxy.$store.commit("setBreadcrumbList", breadcrumbList);
      routerManager(RouterName.ListSong, {path: RouterName.ListSong, query: {id}});
    }

    //跳转评论页面方法
    function goCommentPage(id) {
      const breadcrumbList = reactive([
        {
          path: RouterName.SongList,
          name: "歌单管理",
        },
        {
          path: "",
          name: "评论详情",
        },
      ]);
      proxy.$store.commit("setBreadcrumbList", breadcrumbList);
      routerManager(RouterName.Comment, {path: RouterName.Comment, query: {id, type: 1}});
    }

    /**
     * 添加歌单的表单信息
     */
    const centerDialogVisible = ref(false);
    /*其实添加表单的字段和修改表单的字段只差了个id，而我们实际上在singer的新增和修改
    那里就试过把两个功能合在一起实现。不过既然这里已经分了两个表单，那我们就分开写。
     */
    const registerForm = reactive({
      title: "",   //标题
      introduction: "", //歌单简介
      style: "",  //歌单风格
    });

    //添加表单方法
    async function addsongList() {
      //1. 获取输入表单参数
      let title = registerForm.title;
      let introduction = registerForm.introduction;
      let style = registerForm.style;
      const param = {title, introduction, style}
      //2. 调用添加接口
      const result = (await HttpManager.setSongList(param)) as ResponseBody
      //提示信息
      (proxy as any).$message({
        message: result.message,
        type: result.success ? 'success' :
            'error'
      })
      //清空表单
      registerForm.title = ""
      registerForm.introduction = ""
      registerForm.style = ""
      //隐藏添加表单
      centerDialogVisible.value = false;
      //刷新表格
      getData()
    }

    /**
     * 编辑表单
     */
    const editVisible = ref(false);
    //基本上表单里面定义的属性都是对应数据库的字段
    const editForm = reactive({
      id: "",
      title: "", //标题
      pic: "",   //歌单图片URL
      introduction: "", //歌单简介
      style: "",  //歌单风格
    });

    //根据选中的行id把数据库信息显示在表单中
    function editRow(row) {
      idx.value = row.id;
      editForm.id = row.id;
      editForm.title = row.title;
      editForm.pic = row.pic;
      editForm.introduction = row.introduction;
      editForm.style = row.style;
      editVisible.value = true;
    }

    // 歌单信息编辑方法
    async function saveEdit() {
      // 1. 传入编辑框中的参数
      let id = editForm.id;
      let title = editForm.title;
      let introduction = editForm.introduction;
      let style = editForm.style;
      //  7/12 创建集合对象param作为传入参数
      const param = {id,title,introduction,style}
      //2. 调用编辑接口
      const result = (await HttpManager.updateSongListMsg(param)) as ResponseBody
      //提示信息
      (proxy as any).$message({
        message: result.message,
        type: result.success ?
            'success' : 'error'
      })
      //3、关闭修改层
      editVisible.value = false
      //4、刷新表格
      getData()

      //应酬编辑表单

    }

    /**
     * 删除
     */
    const idx = ref(-1); // 记录当前要删除的行
    const multipleSelection = ref([]); // 记录当前要删除的列表,用于批量删除
    const delVisible = ref(false); // 显示删除框

    // 删除歌单方法
    async function confirm() {
      // 1. 把提示框隐藏掉
      delVisible.value = false;
      //调用删除接口
      const result = (await HttpManager.deleteSongList(idx.value)) as ResponseBody
      //提示信息
      (proxy as any).$message({
        message:result.message,
        type:result.success?'success':'error' //三元表达式，根据result是success还是error，弹出相应的提示框
      })
      //刷新表格
      getData()
      //隐藏删除层
    }

    //点击“删除”时获取该行id同时显示出删除框
    function deleteRow(id) {
      idx.value = id;
      delVisible.value = true;
    }

    //把选中的行id传入multipleSelection数组中
    function handleSelectionChange(val) {
      multipleSelection.value = val;
    }

    //批量删除方法
    function deleteAll() {
      // 1. 从multipleSelection取出复选框接收的需要批量删除的行id
      for (let item of multipleSelection.value) {
        deleteRow(item.id); //记录当前需要删除的行id
        confirm(); //单次删除
      }
      multipleSelection.value = [];//批量删除完后重置为空
    }

    return {
      searchWord,
      data,
      tableData,
      centerDialogVisible,
      editVisible,
      delVisible,
      pageSize,
      currentPage,
      totalCount,   //这里我们自己设置的totalCount，要记得返回结果
      registerForm,
      editForm,
      getData,
      addsongList,
      deleteAll,
      handleSelectionChange,
      handleImgSuccess,
      beforeImgUpload,
      saveEdit,
      attachImageUrl: HttpManager.attachImageUrl,
      uploadUrl,
      editRow,
      handleCurrentChange,
      exportPlaylist,
      confirm,
      deleteRow,
      goContentPage,
      goCommentPage,
    };
  },
});
</script>

<style scoped></style>
