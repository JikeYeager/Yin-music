<template>
  <div class="play-list-container">
    <yin-nav :styleList="singerStyle" :activeName="activeName" @click="handleChangeView"></yin-nav>
    <play-list :playList="data" path="singer-detail"></play-list>
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
</template>

<script lang="ts" setup>
import { ref, computed ,onMounted } from "vue";
import YinNav from "@/components/layouts/YinNav.vue";
import PlayList from "@/components/PlayList.vue";
import { singerStyle } from "@/enums";
import { HttpManager } from "@/api";

// data
const activeName = ref("全部歌手");
const pageSize = ref(10); // 页数
const currentPage = ref(1); // 当前页
const totalCount = ref(0); // 总记录       8.5
const allPlayList = ref([]);     //存放查询结果
const currentSex = ref(-1);
// computed   这里是想在前端分页，但其实这是假分页，我们已经在后端实现了真分页的话这里就要修改一下（模仿歌单的分类分页查询）
// const data = computed(() => {
//   return allPlayList.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
// });
//这里像SongSheet.vue一样改为allPlayList就可以了
const data = computed(() => allPlayList.value);

// // 获取所有歌手
// async function getAllSinger() {
//   //
// }

// getAllSinger();

// 切换页码
function handleCurrentChange(val) {
  currentPage.value = val;
  getSingerSex(currentSex.value)
}

// 点击不同sex的歌单时立即触发此方法
function handleChangeView(item) {
  console.log(item);
  //0. 根据点击的sex改变名字，实现导航栏的切换效果 同时清空歌单列表
  activeName.value = item.name;
  allPlayList.value = [];
  currentPage.value = 1;   // 页码重置为第一页
//1. 根据sex和分页参数发送请求
  if (item.name === "全部歌手") {
    getSingerSex(-1);   //-1表all
  } else {
    getSingerSex(item.type);
  }
}

// 通过性别对歌手分类  0女 1男 2组合  那么这里就用-1来代指all
async function getSingerSex(sex) {
  //1、保存当前sex   后面换页的时候要必须要传入sex，所以在这里要记录一下
  currentSex.value =sex;
  //构建参数
  const param ={
    sex,
    "pageSize":pageSize.value,
    "currentPage":currentPage.value,
  }

  //发送请求
  const result = (await HttpManager.getSingerOfSex(param)) as ResponseBody;
  if(result.success) {
    //4.获取总记录数
    totalCount.value = result.data.total
    //5.获取当前页数据
    allPlayList.value = result.data.records
    //6.获取当前页码
    currentPage.value = result.data.currentPage
  }
  // currentPage.value = 1;
  // allPlayList.value = result.data;
}

// 初始化时获取全部歌手（第一页）
onMounted(() => {
  getSingerSex(-1);
});
</script>
