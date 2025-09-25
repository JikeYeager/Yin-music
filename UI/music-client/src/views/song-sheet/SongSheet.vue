<template>
  <div class="play-list-container">
    <yin-nav :styleList="songStyle" :activeName="activeName" @click="handleChangeView"></yin-nav>
    <play-list :playList="data" path="song-sheet-detail"></play-list>
    <el-pagination
      class="pagination"
      background
      layout="total, prev, pager, next"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="totalCount"
      @current-change="handleCurrentChange">
    </el-pagination>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from "vue";
import YinNav from "@/components/layouts/YinNav.vue";
import PlayList from "@/components/PlayList.vue";
import { SONGSTYLE } from "@/enums";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    YinNav,
    PlayList,
  },
  setup() {
    const activeName = ref("全部歌单");
    const pageSize = ref(10); // 页数
    const currentPage = ref(1); // 当前页
    const totalCount = ref(0); // 总记录       8.4
    const currentStyle = ref('') //当前style   8.4
    const songStyle = ref(SONGSTYLE); // 歌单导航栏类别
    const allPlayList = ref([]); // 歌单
    const data = computed(() => allPlayList.value);


    // // 获取全部歌单   我们不搞两个方法实现了，统一使用一个方法来实现
    // async function getSongList() {
    //   //歌单分页查询
    // }

    //根据style获取歌单分页查询
    async function getSongListOfStyle(style) {
      //1、保存当前style   后面换页的时候要必须要传入style，所以在这里要记录一下
      currentStyle.value = style

      // 2. 构建查询参数 除了style不同以外，分页查询的参数一致
      const param = {
        style,   //用于接收不同点击下的不同歌单类型
        "pageSize":pageSize.value,
        "currentPage":currentPage.value,
      }

      //3. 发送查询请求
      const result = ((await HttpManager.getSongListOfStyle(param)) as ResponseBody)
      //4.获取总记录数
      totalCount.value = result.data.total
      //5.获取当前页数据
      allPlayList.value = result.data.records
      //6.获取当前页码
      currentPage.value = result.data.currentPage

    }

    try {
      //进入页面默认搜索所有歌单
      getSongListOfStyle("all");
    } catch (error) {
      console.error(error);
    }

    // 获取歌单
    // 点击不同style的歌单时立即触发此方法
    async function handleChangeView(item) {
      console.log(item);
      //0. 根据点击的style改变名字，实现导航栏的切换效果 同时清空歌单列表
      activeName.value = item.name;
      allPlayList.value = [];

      //1. 根据style的name和分页参数发送请求
      try {
        //刚进入页面或点击“全部歌单”时触发
        if (item.name === "全部歌单") {
          await getSongListOfStyle("all");
        } else {
          //否则就根据对应的style发送请求
          await getSongListOfStyle(item.name);
        }
      } catch (error) {
        console.error(error);
      }
    }
    // 获取当前页
    function handleCurrentChange(val) {
      currentPage.value = val;
      getSongListOfStyle(currentStyle.value)
    }
    return {
      activeName,
      songStyle,
      pageSize,
      totalCount,   //8.4 totalCount创建了要记得返回
      currentPage,
      currentStyle,  //8.4 currentStyle创建了要记得返回
      allPlayList,
      data,
      handleChangeView,
      handleCurrentChange,
    };
  },
});
</script>
