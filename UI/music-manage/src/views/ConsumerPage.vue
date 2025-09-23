<template>
  <div class="container">
    <div class="handle-box">
<!--      点击批量删除调用deleteAll方法-->
      <el-button @click="deleteAll">批量删除</el-button>
      <el-input v-model="searchWord" placeholder="筛选用户"></el-input>
     <!--添加查询组件  调用getData()以实现查询功能 注意要在setup中returngetData方法-->
      <el-button type="primary" @click="getData">查询</el-button>
    </div>
<!--点击复选框触发事件：@selection-change   触发方法：handleSelectionChange-->
    <el-table height="550px" border size="small" :data="data" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40" align="center"></el-table-column>
      <el-table-column label="ID" prop="id" width="50" align="center"></el-table-column>
      <el-table-column label="用户头像" width="102" align="center">
        <template v-slot="scope">
          <img :src="attachImageUrl(scope.row.avator)" style="width: 80px" />
        </template>
      </el-table-column>
      <el-table-column label="用户名" prop="username" width="80" align="center"></el-table-column>
      <el-table-column label="性别" width="50" align="center">
        <template v-slot="scope">
          <div>{{ changeSex(scope.row.sex) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="手机号码" prop="phoneNum" width="120" align="center"></el-table-column>
      <el-table-column label="邮箱" prop="email" width="120" align="center"></el-table-column>
      <el-table-column label="生日" width="120" align="center">
        <template v-slot="scope">
          <div>{{ getBirth(scope.row.birth) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="签名" prop="introduction"></el-table-column>
      <el-table-column label="地区" prop="location" width="70" align="center"></el-table-column>
      <el-table-column label="收藏" width="90" align="center">
        <template v-slot="scope">
          <el-button @click="goCollectPage(scope.row.id)">收藏</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="90" align="center">
        <template v-slot="scope">
          <el-button type="danger" @click="deleteRow(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
      <el-table-column label="修改" width="90" align="center">
        
    <template v-slot="scope">
<!--      upload为上传组件，
:action="'http://localhost:9999/consumer/avatar/' + scope.row.id  点击时触发后端服务，同时传回本次点击的行id
:show-file-list="false" 不显示已上传文件列表
:on-success="handleAvatarSuccess" 在上传文件后触发handleAvatarSuccess方法
:before-upload="beforeAvatarUpload"  在上传文件前触发限制上传文件大小方法（在其他地方实现 ）
-->
      <el-upload
        :action="'http://localhost:9999/consumer/avatar/' + scope.row.id"
        :show-file-list="false"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload">
        <el-button>更新头像</el-button>
      </el-upload>
   </template>

        
      </el-table-column>
    </el-table>
<!-- 前端加载的分页查询变量
total我们修改成自己的总记录数，因为我们不是一次性的假查询的假分页，
所以total！=pageSize-->
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

  <!-- 删除提示框  @confirm确认  @cancelRow取消-->
  <yin-del-dialog :delVisible="delVisible" @confirm="confirm" @cancelRow="delVisible = $event"></yin-del-dialog>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, watch, ref, reactive, computed } from "vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { RouterName } from "@/enums";
import YinDelDialog from "@/components/dialog/YinDelDialog.vue";
import { getBirth } from "@/utils";

export default defineComponent({
  components: {
    YinDelDialog,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { changeSex, routerManager } = mixin();

    const tableData = ref([]); // 记录用户，用于显示
    const tempDate = ref([]); // 记录用户，用于搜索时能临时记录一份用户列表
    const pageSize = ref(10); // 每页显示的用户记录数
    const currentPage = ref(1); // 当前页
    const totalCount = ref(0);  // 定义总记录数


    // 计算当前表格中的数据 我们使用真分页和真搜索
    const data = computed(() => {
      /*假分页:从数据库中取出所有的数据，然后分页在界面上显示。访问一次数据库，
      但由于选择的数据量比较大，所以第一次花费时间比较长，但之后每一页的显示都是直接、快速的，避免对数据库的多次访问。
       */
      return tableData.value

      /* */
    });
    // 记录输入框输入的内容,搜索框
    const searchWord = ref("");
    watch(searchWord, () => {
      //假搜索
    });

    getData();


    // 获取用户信息 && 刷新表格
    async function getData() {
      /*  假分页的实现
      // //1.1 定义数组以接收后端数据库传过来的用户数据
      tableData.value = [];
      tempDate.value = []; //为了做假分页写的变量
      // //1.2 异步请求接收数据
      // HttpManager.getAllUser().then(res => {
      //   const body = res as ResponseBody
      //   const userlist = body.data
      //   //1.3 新数组覆盖原来的值 ,以显示用户数据到表格中
      //   tableData.value = userlist
      //  })
      */

      /* 6/23 1. 实现真分页（实际上是在查询的基础+分页的有关参数实现的） */
      tableData.value = [];
      tempDate.value = []; //为了做假分页写的变量
      // 1.1 分页查询参数
      const param = {
        "currentPage":currentPage.value,
        "pageSize":pageSize.value,
        "keyword":searchWord.value  //搜索框定义的是searchWord
      }
      //1.2 发送异步请求  const res =(await HttpManager.getPageUser(param)) as ResponseBody
      HttpManager.getPageUser(param).then(res =>{
        //1.3 通过分页查询，请求所有用户接口
        const body = res as ResponseBody
        // console.log(body)  //分页查询成功的所有信息
        console.log(body.data)  //data还包含了分页查询的相关参数
        console.log(body.data.records)  //这个才是返回的用户数据

        //表格数据
        tableData.value = body.data.records
        //更新记录总数
        totalCount.value = body.data.total  //21条

      })



    }
    //换页方法：获取当前页，通过改变currentPage的值,然后根据此参数重新调用一次获取用户的方法，以实现换页功能
    function handleCurrentChange(val) {
      currentPage.value = val;
      //刷新表格数据
      getData()
    }

    /**
     * 路由
     */
    function goCollectPage(id) {
      console.log(id)
      const breadcrumbList = reactive([
        {
          path: RouterName.Consumer,
          name: "用户管理",
        },
        {
          path: "",
          name: "收藏信息",
        },
      ]);
      proxy.$store.commit("setBreadcrumbList", breadcrumbList);
      routerManager(RouterName.Collect, { path: RouterName.Collect, query: { id } });
    }

    /**
     * 删除
     */
    const idx = ref(-1); // 记录当前要删除的行
    const multipleSelection = ref([]); // 记录当前要删除的列表
    const delVisible = ref(false); // 显示删除框

    // 具体用户删除方法
    async function confirm() {
      // 1. 把提示框隐藏掉
      delVisible.value = false; //这里压根就没实现删除功能，只是把提示框隐藏起来而已

      //2.发起删除的请求  和登录功能一样需要同步请求
      const result=(await HttpManager.deleteUser(idx.value)) as ResponseBody

      //3. 提示信息 ，当点击删除后弹出提示信息，显示删除功还是删除失败
      (proxy as any).$message({
        message:result.message,
        type:result.success?'sudccess':'error' //三元表达式，根据result是success还是error，弹出相应的提示框
      })
      console.log(idx.value)
      //4.刷新表格（重新向后端发起一次查询请求，得到更新后的表数据）
      getData()
    }
    // 点击“删除”按钮时绑定的方法，跳出提示框
    function deleteRow(id) {
      idx.value = id;            //记录要删除的行数
      delVisible.value = true;  //跳出一个删除时的弹窗
    }
    // 批量管理
    function handleSelectionChange(val) {
      //注意：点击复选框时，这里的val传的不是行id，而是整行的数据。我们要在后面修改一下，只需要当前选中的行数即可
      multipleSelection.value = val; //multipleSelection是列表
    }

    //批量删除（原先的做法是在前端写个循环，多次调用单次删除的后端以实现批量删除）
   async function deleteAll() { //async定义异同步请求
     //1、多选的数组传给后端
     //multipleSelection.value.map(row =>row.id)  把整行数据转出id
     //join(",") 把数组元素用逗号拼接返回字符串1,2,3
     const ids = multipleSelection.value.map(row => row.id).join(",")  //map函数把行id赋值给value再把这些值以字符串的格式拼接起来
     console.log(ids)
     //1.1 发起批量删除请求
     const result = (await HttpManager.deleteAllUser(ids)) as ResponseBody
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

   //更改头像成功后的方法  只是刷新一下表格
   function handleAvatarSuccess(){
     location.reload();
   }




    return {
      searchWord,
      data,
      tableData,
      delVisible,
      pageSize,
      currentPage,
      totalCount,  //这里我们自己设置的totalCount，要记得返回结果
      deleteAll,
      handleSelectionChange,
      handleCurrentChange,
      changeSex,
      getBirth,
      getData,  //返回getData()方法，如果在<script>标签中写setup方法就不用return了
      deleteRow,
      handleAvatarSuccess,
      confirm,
      goCollectPage,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style scoped></style>
