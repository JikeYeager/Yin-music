<template>
  <div class="container">
    <div class="handle-box">
      <el-button @click="deleteAll">批量删除</el-button>
      <el-input placeholder="筛选歌手" v-model="searchWord"></el-input>
<!--      在setup（）方法里面return getData方法才能在这里使用-->
      <el-button type="primary" @click="getData">查询</el-button>
      <el-button type="primary" @click="centerDialogVisible = true">添加歌手</el-button>
    </div>
<!--    最左侧的复选框，点击触发handleSelectionChange方法-->
    <el-table height="550px" border size="small" :data="data" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40" align="center"></el-table-column>
      <el-table-column label="ID" prop="id" width="50" align="center"></el-table-column>
      <el-table-column label="歌手图片" prop="pic" width="110" align="center">
        <template v-slot="scope">
          <div class="singer-img">
            <img :src="attachImageUrl(scope.row.pic)" style="width: 100%" />
          </div>
          <el-upload :action="uploadUrl(scope.row.id)" :show-file-list="false" :on-success="handleImgSuccess" :before-upload="beforeImgUpload">
            <el-button>更新图片</el-button>
          </el-upload>
        </template>
      </el-table-column>
      <el-table-column label="歌手" prop="name" width="120" align="center"></el-table-column>
      <el-table-column label="性别" prop="sex" width="60" align="center">
        <template v-slot="scope">
          <div>{{ changeSex(scope.row.sex) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="出生" prop="birth" width="120" align="center">
        <template v-slot="scope">
          <div>{{ getBirth(scope.row.birth) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="地区" prop="location" width="100" align="center"></el-table-column>
      <el-table-column label="简介" prop="introduction">
        <template v-slot="scope">
          <p style="height: 100px; overflow: scroll">
            {{ scope.row.introduction }}
          </p>
        </template>
      </el-table-column>
      <el-table-column label="歌曲管理" width="120" align="center">
        <template v-slot="scope">
          <el-button @click="goSongPage(scope.row)">歌曲管理</el-button>
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

  <!-- 添加歌手时弹出提示框，v-model="centerDialogVisible"-->
  <el-dialog title="添加歌手" v-model="centerDialogVisible">
<!--    提示框中是一个收集信息的表单registerForm-->
    <el-form label-width="80px" :model="registerForm" :rules="singerRule">
<!-- 1.歌手名字-->
      <el-form-item label="歌手名" prop="name">
        <el-input v-model="registerForm.name"></el-input>
      </el-form-item>
<!--2. 歌手性别-->
      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="registerForm.sex">
          <el-radio :label="0">女</el-radio>
          <el-radio :label="1">男</el-radio>
          <el-radio :label="2">保密</el-radio>
          <el-radio :label="2">组合</el-radio>
          <el-radio :label="3">不明</el-radio>
        </el-radio-group>
      </el-form-item>
<!--3. 歌手故乡-->
      <el-form-item label="故乡" prop="location">
        <el-input v-model="registerForm.location"></el-input>
      </el-form-item>
<!--4. 歌手出生日期-->
      <el-form-item label="出生日期" prop="birth">
        <el-date-picker type="date" v-model="registerForm.birth"></el-date-picker>
      </el-form-item>
<!--5. 歌手详细介绍 -->
      <el-form-item label="歌手介绍" prop="introduction">
        <el-input type="textarea" v-model="registerForm.introduction"></el-input>
      </el-form-item>

    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addsinger">确 定</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 编辑弹出框 -->
  <el-dialog title="编辑" v-model="editVisible">
    <el-form label-width="60px" :model="editForm" :rules="singerRule">
      <el-form-item label="歌手" prop="name">
        <el-input v-model="editForm.name"></el-input>
      </el-form-item>
      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="editForm.sex">
          <el-radio :label="0">女</el-radio>
          <el-radio :label="1">男</el-radio>
          <el-radio :label="2">保密</el-radio>
          <el-radio :label="2">组合</el-radio>
          <el-radio :label="3">不明</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="出生" prop="birth">
        <el-date-picker type="date" v-model="editForm.birth"></el-date-picker>
      </el-form-item>
      <el-form-item label="地区" prop="location">
        <el-input v-model="editForm.location"></el-input>
      </el-form-item>
      <el-form-item label="简介" prop="introduction">
        <el-input type="textarea" v-model="editForm.introduction"></el-input>
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
import { defineComponent, getCurrentInstance, watch, ref, reactive, computed } from "vue";
import mixin from "@/mixins/mixin";
import YinDelDialog from "@/components/dialog/YinDelDialog.vue";
import { HttpManager } from "@/api/index";
import { RouterName } from "@/enums";
import { getBirth } from "@/utils";

export default defineComponent({
  components: {
    YinDelDialog,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { changeSex, routerManager, beforeImgUpload } = mixin();

    const tableData = ref([]); // 记录歌曲，用于显示
    const tempDate = ref([]); // 记录歌曲，用于搜索时能临时记录一份歌曲列表
    const pageSize = ref(5); // 页数
    const currentPage = ref(1); // 当前页
    //  6/28 新添加totalCount总记录数
    const totalCount = ref(0);  // 定义总记录数

    // 计算当前表格中的数据
    const data = computed(() => {
      // 假分页
      return tableData.value;
    });

    const searchWord = ref(""); // 记录输入框输入的内容
    watch(searchWord, () => {
      //假搜索
    });

    getData();

    // 分页查询基本信息
    async function getData() {
      tableData.value = [];
      // tempDate.value = [];
      // 1.1 分页查询参数
      const param = {
        "currentPage":currentPage.value,
        "pageSize":pageSize.value,   //单页面显示的记录数
        "keyword":searchWord.value  //搜索框定义的是searchWord
      }
      //1.2 发送异步请求
      HttpManager.getPageSinger(param).then(res =>{
        //1.3 通过分页查询，请求所有用户接口
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
    // 获取当前页，实现换页的方法
    function handleCurrentChange(val) {
      currentPage.value = val;  //获取不同的当前页以实现切页
      getData()  //刷新表格数据
    }
    // 更新歌手照片（本质上和更新用户头像一个意思）
    function uploadUrl(id) {
      // return HttpManager.attachImageUrl(`singer/avatar/id=${id}`);为什么不行呢？
      return HttpManager.attachImageUrl(`singer/avatar/${id}`);
    }

    /**
     * 路由
     */
    // 点击“歌曲管理”跳转到song页面  跳转 歌曲管路页面
    function goSongPage(row) {

      console.log(row)
      //1. 构建一个面包屑导航列表（breadcrumbList），用于在跳转后显示导航路径
      const breadcrumbList = reactive([
        //   1.1 第一级：歌手管理，路径为RouterName.Singer（即歌手管理页面的路由）
        {
          path: RouterName.Singer,
          name: "歌手管理",
        },
          //1.2 第二级：歌曲信息，这里路径为空，因为当前就在歌曲信息页面。
        // 为了在面包屑中显示“歌曲信息”而设置的
        {
          path: "",
          name: "歌曲信息",
        },
      ]);
      // 1.3 通过Vuex的commit方法，将面包屑列表存入store，这样在歌曲页面就可以从store中获取并显示面包屑。
      proxy.$store.commit("setBreadcrumbList", breadcrumbList);
      // 1.4 调用`routerManager`方法进行路由跳转。这个方法来自mixin，我们在mixin中定义过。
      routerManager(RouterName.Song,  //路由名称，这里是RouterName.Song
          {
        //path实际跳转的路径，这里也是RouterName.Song（即歌曲页面的路由路径）
        path: RouterName.Song,
        //传递的查询参数，包括歌手的id和name。
        query: {
          id: row.id,
          name: row.name },
      }
      );
    }

    /**
     * 添加
     */
    const centerDialogVisible = ref(false);
    //新增歌手的信息表单
    const registerForm = reactive({
      name: "",
      sex: "",
      birth: new Date(),
      location: "",
      introduction: "",
    });
    const singerRule = reactive({
      name: [{ required: true, trigger: "change" }],
      sex: [{ required: true, trigger: "change" }],
    });

    // 添加歌手方法
    async function addsinger() {
      //1. 获取表单数据
      let datetime = getBirth(registerForm.birth);
      //  6/28：新增的id，作用在后端执行saveOrUpdate方法时要根据id来分别执行新增功能还是修改功能
      let id = null;
      let name = registerForm.name;
      let sex = registerForm.sex;
      let birth = datetime;
      let location = registerForm.location;
      let introduction = registerForm.introduction;
      //这里和consumer.vue构建param是一样的，只不过换了种方法
      const param =
          {id,name,sex,birth,location,introduction}
      //2. 调用添加接口
      const result = (await HttpManager.saveSinger(param)) as ResponseBody
      //提示信息
      (proxy as any).$message({
        message: result.message,
        type: result.success ? 'success' :
            'error'
      })
      //3. 清空新增表单，刷新表格
      registerForm.name = ""
      registerForm.sex = ""
      registerForm.birth = new Date()
      registerForm.location = ""
      registerForm.introduction = ""
      //4. 关闭添加层
      centerDialogVisible.value = false
      //5、刷新表格
      getData()
    }

    /**
     * 编辑表单信息
     */
    const editVisible = ref(false);
    const editForm = reactive({
      id: "",
      name: "",
      sex: "",
      pic: "",
      birth: new Date(),
      location: "",
      introduction: "",
    });

    // 点击歌手编辑，获取对应信息到表单中
    function editRow(row) {
      editVisible.value = true;
      editForm.id = row.id;
      editForm.name = row.name;
      editForm.sex = row.sex;
      editForm.pic = row.pic;
      editForm.birth = row.birth;
      editForm.location = row.location;
      editForm.introduction = row.introduction;
    }

    // 修改歌手信息方法
    async function saveEdit() {
      try {
        let datetime = getBirth(new Date(editForm.birth));
        let id = editForm.id; //与新增歌手相比就多传了个id
        let name = editForm.name;
        let sex = editForm.sex;
        let birth = datetime;
        let location = editForm.location;
        let introduction = editForm.introduction;
        //  7/2 创建集合对象param作为传入参数
        const param = {id,name,sex,birth,location,introduction}
        //2、调用添加接口
        const result = (await HttpManager.saveSinger(param)) as ResponseBody
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

      } catch (error) {
        console.error(error);
      }
    }
    // 在上传图片成功后触发handleAvatarSuccess方法
    function handleImgSuccess(response, file) {
      // 提示信息
      (proxy as any).$message({
        message: response.message,
        type: response.type,
      });
      // 如果成功的话则刷新表格
      if (response.success) getData();
    }

    /**
     * 删除
     */
    const idx = ref(-1); // 记录当前要删除的行
    const multipleSelection = ref([]); // 记录当前要删除的列表
    const delVisible = ref(false); // 显示删除框

    // 删除歌手方法
    async function confirm() {
      // 1. 把提示框隐藏掉
      delVisible.value = false;
      //调用删除接口
      const result = (await HttpManager.deleteSinger(idx.value)) as ResponseBody
      //提示信息
      (proxy as any).$message({
        message:result.message,
        type:result.success?'success':'error' //三元表达式，根据result是success还是error，弹出相应的提示框
      })
      //刷新表格
      getData()
    }
    //点击“删除”时获取该行id同时显示出删除框
    function deleteRow(id) {
      idx.value = id;
      delVisible.value = true;
    }
    function handleSelectionChange(val) {
      multipleSelection.value = val;  //接受批量删除的行id
    }
    //批量删除方法（这里就是按照循环+单次删除方法实现批量删除的）
    function deleteAll() {
      // 1. 从multipleSelection取出复选框接收的需要批量删除的行id
      for (let item of multipleSelection.value) {
        deleteRow(item.id); //记录当前需要删除的行id
        confirm(); //删除
      }
      multipleSelection.value = []; //批量删除完后重置为空
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
      totalCount,  //这里我们自己设置的totalCount，要记得返回结果
      registerForm,
      editForm,
      singerRule,
      getData,  //返回getData()方法，如果在<script>标签中写setup方法就不用return了
      deleteAll,
      handleSelectionChange,
      handleImgSuccess,
      beforeImgUpload,
      saveEdit,
      attachImageUrl: HttpManager.attachImageUrl,
      changeSex,
      getBirth,
      uploadUrl,
      goSongPage,
      editRow,
      handleCurrentChange,
      addsinger,
      confirm,
      deleteRow,
    };
  },
});
</script>

<style scoped>
.singer-img {
  width: 100%;
  height: 80px;
  border-radius: 5px;
  margin-bottom: 5px;
  overflow: hidden;
}
</style>
