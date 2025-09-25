<template>
  <el-container>
    <el-aside class="album-slide">
      <el-image class="album-img" fit="contain" :src="attachImageUrl(songDetails.pic)" />
      <h3 class="album-info">{{ songDetails.title }}</h3>
    </el-aside>
    <el-main class="album-main">
      <h1>简介</h1>
      <p>{{ songDetails.introduction }}</p>
      <!--评分-->
      <div class="album-score">
        <div>
          <h3>歌单评分</h3>
          <el-rate v-model="rank" allow-half disabled></el-rate>
        </div>
<!--  歌单评分      -->
        <span>{{ rank *2 }}</span>
        <div>
<!-- 我的评分 score * 2   5星*2=10分         10-->
          <h3>{{ assistText }} {{ score * 2 }}</h3>
          <el-rate allow-half v-model="score" :disabled="disabledRank" @click="pushValue()"></el-rate>
        </div>
      </div>
      <!--歌曲-->
      <song-list class="album-body" :songList="currentSongList"></song-list>
      <comment :playId="songListId" :type="1"></comment>
    </el-main>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, ref, computed, getCurrentInstance } from "vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import SongList from "@/components/SongList.vue";
import Comment from "@/components/Comment.vue";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    SongList,
    Comment,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const { checkStatus } = mixin();

    const currentSongList = ref([]); // 存放的音乐
    const nowSongListId = ref(""); // 歌单 ID
    const nowScore = ref(0);    //用户现在评分
    const nowRank = ref(0);    //歌单评分
    const disabledRank = ref(false);
    const assistText = ref("评价");
    // const evaluateList = ref(["很差", "较差", "还行", "推荐", "力推"]);
    //在PlayList.vue中我们存入了了歌单信息和用户id到setSongDetails,我们这里取出来使用，从而显示歌单图片和简介
    const songDetails = computed(() => store.getters.songDetails); // 单个歌单信息
    const nowUserId = computed(() => store.getters.userId);

    nowSongListId.value = songDetails.value.id; // 给歌单ID赋值
    console.log('nowUserId:', nowUserId.value); // 在获取评分之前
    // 收集歌单里面的歌曲
    async function getSongId(id) {
     //获取歌单里的歌曲信息
      const result = (await HttpManager.getSongOfSongListId(id)) as ResponseBody;
      console.log(result)
      //把从前端获取到的歌曲信息放到currentSongList
      currentSongList.value = result.data
    }

    //获取歌单评分 这里的id就是songlistId
    async function getRank(songListId) {
      // const result = (await HttpManager.getUserRank(nowUserId.value,id)) as ResponseBody;
      const result = (await HttpManager.getRankOfSongListId(songListId)) as ResponseBody;
      console.log(result)
      if (result.data) {
        nowRank.value = result.data/ 2
      }
    }

    //获取用户评分
    async function getUserRank(userId, songListId) {
      // console.log("==============================")
      const result = (await HttpManager.getUserRank(userId,songListId)) as ResponseBody;
      console.log(result)
      if (result.data) {
        nowScore.value = result.data.score / 2;  //5
        disabledRank.value = true;  //不允许再评分
        assistText.value = "已评价";
      }
    }
    // 提交评分
    async function pushValue() {
      if (disabledRank.value || !checkStatus()) return;
      //1. 获取请求参数
      const songListId = nowSongListId.value;
      var consumerId = nowUserId.value;
      const score = nowScore.value*2;  //10
      try {
        const result = (await HttpManager.setRank({songListId,consumerId,score})) as ResponseBody;
        //提示信息
        (proxy as any).$message({
          message: result.message,
          type: result.type,
        });

        if (result.success) {
          //更新歌单评分
          getRank(nowSongListId.value);
          disabledRank.value = true;
          assistText.value = "已评价";
        }
      } catch (error) {
        console.error(error);
      }
    }

    getUserRank(nowUserId.value, nowSongListId.value);
    getRank(nowSongListId.value); // 获取评分
    getSongId(nowSongListId.value); // 获取歌单里面的歌曲ID

    return {
      songDetails,
      rank: nowRank,
      score: nowScore,   //5
      disabledRank,
      assistText,
      currentSongList,
      songListId: nowSongListId,
      userId: nowUserId.value,
      attachImageUrl: HttpManager.attachImageUrl,
      pushValue,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.album-slide {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;

  .album-img {
    height: 250px;
    width: 250px;
    border-radius: 10%;
  }

  .album-info {
    width: 70%;
    padding-top: 2rem;
  }
}

.album-main {
  h1 {
    font-size: 22px;
  }

  p {
    color: rgba(0, 0, 0, 0.5);
    margin: 10px 0 20px 0px;
  }
  /*歌单打分*/
  .album-score {
    display: flex;
    align-items: center;
    margin: 1vw;

    h3 {
      margin: 10px 0;
    }
    span {
      font-size: 60px;
    }
    & > div:last-child {
      margin-left: 10%;
    }
  }

  .album-body {
    margin: 20px 0 20px 0px;
  }
}

@media screen and (min-width: $sm) {
  .album-slide {
    position: fixed;
    width: 400px;
  }
  .album-main {
    min-width: 600px;
    padding-right: 10vw;
    margin-left: 400px;
  }
}

@media screen and (max-width: $sm) {
  .album-slide {
    display: none;
  }
}
</style>
