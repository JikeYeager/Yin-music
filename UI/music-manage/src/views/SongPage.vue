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
      <!--添加查询组件  调用getData()以实现查询功能 注意要在setup中returngetData方法-->
      <el-button type="primary" @click="getData">查询</el-button>
      <el-button type="primary" @click="centerDialogVisible = true">添加歌曲</el-button>
    </div>
    <el-table height="550px" border size="small" :data="data" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40"></el-table-column>
      <el-table-column label="ID" prop="id" width="50" align="center"></el-table-column>
      <el-table-column label="歌曲图片" width="110" align="center">
        <template v-slot="scope">
          <div style="width: 80px; height: 80px; overflow: hidden">
            <img :src="attachImageUrl(scope.row.pic)" style="width: 100%" />
          </div>
<!--          歌曲图片上还搞了播放按钮，不知道能不能实现-->
          <div class="play" @click="setSongUrl(scope.row)">
            <svg class="icon" aria-hidden="true">
              <use :xlink:href="toggle === scope.row.name ? playIcon : BOFANG"></use>
            </svg>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="歌名" prop="name" width="150"></el-table-column>
      <el-table-column label="专辑" prop="introduction" width="150"></el-table-column>
      <el-table-column label="歌词" align="center">
        <template v-slot="scope">
          <ul style="height: 100px; overflow: scroll">
            <li v-for="(item, index) in parseLyric(scope.row.lyric)" :key="index">
              {{ item }}
            </li>
          </ul>
        </template>
      </el-table-column>
<!--      资源更新下面还分了更新图片、更新歌曲、更新歌词-->
      <el-table-column label="资源更新" width="120" align="center">
        <template v-slot="scope">
          <el-upload :action="updateSongImg(scope.row.id)" :show-file-list="false" :on-success="handleImgSuccess" :before-upload="beforeImgUpload">
            <el-button>更新图片</el-button>
          </el-upload>
          <br />
          <el-upload :action="updateSongUrl(scope.row.id)" :show-file-list="false" :on-success="handleSongSuccess" :before-upload="beforeSongUpload">
            <el-button>更新歌曲</el-button>
          </el-upload>
            <br />
           <el-upload :action="updateSongLrc(scope.row.id)" :show-file-list="false" :on-success="handleSongSuccess" :before-upload="beforeSongUpload">
            <el-button>更新歌词</el-button>
          </el-upload>
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

  <!--添加歌曲-->
  <el-dialog title="添加歌曲" v-model="centerDialogVisible">
    <el-form id="add-song" label-width="120px" :model="registerForm">
<!--  registerForm表单组件里面的name指的是歌曲名字    -->
      <el-form-item label="歌曲名">
        <el-input type="text" name="name" v-model="registerForm.name"></el-input>
      </el-form-item>
      <el-form-item label="专辑">
        <el-input type="text" name="introduction" v-model="registerForm.introduction"></el-input>
      </el-form-item>
      <el-form-item label="歌词（有歌词lrc可以直接上传）">
        <el-input type="textarea" name="lyric" v-model="registerForm.lyric"></el-input>
      </el-form-item>
      <el-form-item label="歌词lrc上传">
        <input type="file" name="lrcfile"/>
      </el-form-item>
      <el-form-item label="歌曲上传">
        <input type="file" name="file" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addSong">确 定</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 编辑弹出框 -->
<!--  仅显示了歌曲名name ， 歌曲专辑introduction ，歌词lyric，和歌手信息修改不一样，想要显示更多可以自己添加代码-->
  <el-dialog title="编辑" v-model="editVisible">
    <el-form :model="editForm">
      <el-form-item label="歌曲">
        <el-input v-model="editForm.name"></el-input>
      </el-form-item>
      <el-form-item label="专辑">
        <el-input v-model="editForm.introduction"></el-input>
      </el-form-item>
      <el-form-item label="歌词">
        <el-input type="textarea" v-model="editForm.lyric"></el-input>
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
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import { Icon, RouterName } from "@/enums";
import { HttpManager } from "@/api";
import { parseLyric } from "@/utils";
import YinDelDialog from "@/components/dialog/YinDelDialog.vue";

export default defineComponent({
  components: {
    YinDelDialog,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager, beforeImgUpload, beforeSongUpload } = mixin();
    const store = useStore();

    const tableData = ref([]); // 记录歌曲，用于显示
    const tempDate = ref([]); // 记录歌曲，用于搜索时能临时记录一份歌曲列表
    const pageSize = ref(5); // 页数
    const currentPage = ref(1); // 当前页
    const totalCount = ref(0);  // 定义总记录数
    const singerId = ref("");
    const singerName = ref("");
    const toggle = ref(false); // 控制播放图标状态
    const BOFANG = ref(Icon.BOFANG);
    const ZANTING = ref(Icon.ZANTING);
    const breadcrumbList = computed(() => store.getters.breadcrumbList);

    const isPlay = computed(() => store.getters.isPlay); // 播放状态
    const playIcon = computed(() => (isPlay.value ? ZANTING.value : BOFANG.value)); // 播放状态
    // 计算当前表格中的数据
    const data = computed(() => {
      //假分页
      return tableData.value;
    });

    const searchWord = ref(""); // 记录输入框输入的内容
    watch(searchWord, () => {
      //假搜索
    });

//从singerPage跳转的当前路由的query中获取id和name，并赋值给singerId和singerName。
    singerId.value = proxy.$route.query.id as string;
    singerName.value = proxy.$route.query.name as string;
    proxy.$store.commit("setIsPlay", false);
    getData();

    // 获取歌曲
    async function getData() {
      // 定义分页查询和搜索的有关参数
      tableData.value = [];
      const param = {
        //然后，在SongPage.vue的getData方法中，使用了singerId（歌手ID）来查询该歌手的歌曲列表：
        "singerId": proxy.$route.query.id,
        "currentPage":currentPage.value,
        "pageSize":pageSize.value,
        "keyword":searchWord.value
      }
        HttpManager.getSongPage(param).then(res=> {
        //1、请求所有用户接口
        const body = res as ResponseBody
        console.log(body.data)
        //表格数据
        tableData.value = body.data.records
        //更新总记录数
        totalCount.value = body.data.total
      })

    }
    // 获取当前页，通过不断请求新的页并且刷新表格以实现换页效果
    function handleCurrentChange(val) {
      currentPage.value = val;
      //刷新表格数据
      getData()
    }

    function setSongUrl(row) {
      proxy.$store.commit("setUrl", row.url);
      toggle.value = row.name;
      if (isPlay.value) {
        proxy.$store.commit("setIsPlay", false);
      } else {
        proxy.$store.commit("setIsPlay", true);
      }
    }
    // 更新歌曲图片
    function updateSongImg(id) {
      return HttpManager.updateSongImg(id);
    }

    //更新歌曲
    function updateSongUrl(id) {
      return HttpManager.updateSongUrl(id);
    }

    //更新歌词
     function updateSongLrc(id) {
      return HttpManager.updateSongLrc(id);
    }

    // 更新歌曲成功后触发的提示信息
    function handleSongSuccess(res) {
      (proxy as any).$message({
        message: res.message,
        type: res.type,
      });
      if (res.success) getData();
    }
    // 更新歌词成功后触发的提示信息
     function handleLyricsSuccess(res) {
      (proxy as any).$message({
        message: res.message,
        type: res.type,
      });
      if (res.success) getData();
    }

    // 更新图片
    function handleImgSuccess(res, file) {
      (proxy as any).$message({
        message: res.message,
        type: res.type,
      });
      if (res.success) getData();
    }

    /**
     * 路由 从歌曲页面跳转到评论页面
     */
    function goCommentPage(id) {
      const breadcrumbList = reactive([
        {
          path: RouterName.Singer,
          name: "歌手管理",
        },
        {
          path: RouterName.Song,
          query: {
            id: singerId.value,
            name: singerName.value,
          },
          name: "歌曲信息",
        },
        {
          path: "",
          name: "评论详情",
        },
      ]);
      proxy.$store.commit("setBreadcrumbList", breadcrumbList);
      routerManager(RouterName.Comment,
          {
            path: RouterName.Comment,
            query: { id, type: 0}
          }
          );
    }

    /**
     * 添加歌曲时的相关变量
     */
    const centerDialogVisible = ref(false);
    const registerForm = reactive({
      name: "",  //歌曲名字
      singerName: "", //歌手名字
      introduction: "", //专辑名字
      lyric: "",  //歌词
    });

    // 添加歌曲方法
    function addSong() {
      const addSongForm = new FormData(document.getElementById("add-song") as HTMLFormElement);
      addSongForm.append("singerId", singerId.value); //将歌手ID添加到表单中，这样后端在添加歌曲时就知道这首歌属于哪个歌手
      //构建“歌手 - 歌曲名”的格式作为最终的歌曲名字
      addSongForm.set("name", singerName.value + "-" + addSongForm.get("name"));

      if (!addSongForm.get("lyric")) addSongForm.set("lyric", "[00:00:00]暂无歌词");

      //原生异步请求
      const req = new XMLHttpRequest();
      req.onreadystatechange = () => {
        if (req.readyState === 4 && req.status === 200) {
          let res = JSON.parse(req.response);
          (proxy as any).$message({
            message: res.message,
            type: res.type,
          });
          if (res.success) {
            getData();
            registerForm.name = "";
            registerForm.singerName = "";
            registerForm.introduction = "";
            registerForm.lyric = "";
           
          }
        }
      };
      console.log(registerForm.name)
      console.log(registerForm.singerName)
      console.log(registerForm.introduction)
      console.log(registerForm.lyric)

      //向后端发送添加歌手的异步请求
      req.open("post", HttpManager.attachImageUrl(`song/add`), false);
      req.send(addSongForm);
      centerDialogVisible.value = false;  //关闭表单信息框
    }

    /**
     * 编辑
     */
    // 编辑歌曲消息框
    const editVisible = ref(false);
    const editForm = reactive({
      id: "",
      singerId: "",
      name: "",
      introduction: "",
      createTime: "",
      updateTime: "",
      pic: "",
      lyric: "",
      url: "",
    });

    // 点击歌曲编辑，获取对应信息到表单中
    function editRow(row) {
      //这里面字段都是数据库song表中的，但是实际上在表单中不需要显示那么多，
      //在前端中，仅显示了歌曲名name ， 歌曲专辑introduction ，歌词lyric
      idx.value = row.id;
      editForm.id = row.id;
      editForm.singerId = row.singerId;
      editForm.name = row.name;
      editForm.introduction = row.introduction;
      editForm.createTime = row.createTime;
      editForm.updateTime = row.updateTime;
      editForm.pic = row.pic;
      editForm.lyric = row.lyric;
      editForm.url = row.url;
      editVisible.value = true;
    }
    async function saveEdit() {
      try {
        //1. 构建param集合表单属性作为请求参数
        let id = editForm.id;  //所选中的行id（歌曲id）
        let singerId = editForm.singerId;  //歌手id
        let name = editForm.name;
        let introduction = editForm.introduction;
        let lyric = editForm.lyric;
        const param = {id, singerId, name, introduction, lyric}
        //2. 请求修改接口
        const result = (await HttpManager.updateSongMsg(param)) as ResponseBody
        //3. 提示信息
        (proxy as any).$message({
          message: result.message,
          type: result.success ?
              'success' : 'error'
        })
        //4. 刷新表数据
        getData()

        //5. 隐藏修改表单
        editVisible.value = false
      } catch (error) {
        console.log(error)
      }
    }

    /**
     * 删除
     */
    const idx = ref(-1); // 记录当前要删除的行
    const multipleSelection = ref([]); // 记录当前要删除的列表
    const delVisible = ref(false); // 显示删除框

    // 删除歌曲方法
    async function confirm() {
      //1. 把提示框隐藏掉
      delVisible.value = false;
      //2.发起删除请求  同步请求
      const result = (await HttpManager.deleteSong(idx.value)) as ResponseBody

      //3. 提示信息 ，当点击删除后弹出提示信息，显示删除功还是删除失败
      (proxy as any).$message({
        message:result.message,
        type:result.success?'sudccess':'error' //三元表达式，根据result是success还是error，弹出相应的提示框
      })
      console.log(idx.value)

      //4. 刷新表格（重新向后端发起一次查询请求，得到更新后的表数据）
      getData()

    }


    // 点击删除按钮时触发的方法
    function deleteRow(id) {
      idx.value = id;  //记录当前要删除的行id
      delVisible.value = true; //弹出删除的提示框
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
      playIcon,
      toggle,
      searchWord,
      data,
      editForm,
      registerForm,
      tableData,
      centerDialogVisible,
      editVisible,
      delVisible,
      pageSize,
      currentPage,
      totalCount,  //自定义的总量要返回
      ZANTING,
      BOFANG,
      breadcrumbList,
      getData,   //返回getData()方法，如果在<script>标签中写setup方法就不用return了
      deleteAll,
      handleSelectionChange,
      handleCurrentChange,
      handleImgSuccess,
      beforeImgUpload,
      parseLyric,
      saveEdit,
      updateSongImg,
      updateSongUrl,
      updateSongLrc,
      deleteRow,
      confirm,
      attachImageUrl: HttpManager.attachImageUrl,
      addSong,
      editRow,
      handleSongSuccess,
      setSongUrl,
      handleLyricsSuccess,
      goCommentPage,
    };
  },
});
</script>

<style scoped>
.play {
  position: absolute;
  z-index: 100;
  width: 80px;
  height: 80px;
  top: 18px;
  left: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.icon {
  width: 2em;
  height: 2em;
  color: white;
  fill: currentColor;
  overflow: hidden;
}
</style>
