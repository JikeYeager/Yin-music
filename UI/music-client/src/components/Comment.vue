<template>
  <div class="comment">
    <h2 class="comment-title">
      <span>评论</span>
      <span class="comment-desc">共 {{ commentList.length }} 条评论</span>
    </h2>
    <el-input class="comment-input" type="textarea" placeholder="期待您的精彩评论..." :rows="2" v-model="textarea" />
    <el-button class="sub-btn" type="primary" @click="submitComment()">发表评论</el-button>
  </div>
<!--  显示从数据库中查询到的歌单评论-->
  <ul class="popular">
    <li v-for="(item, index) in commentList" :key="index">
      <el-image class="popular-img" fit="contain" :src="attachImageUrl(item.avator)" />
      <div class="popular-msg">
        <ul>
          <li class="name">{{ item.username }}</li>
          <li class="time">{{ formatDate(item.createTime) }}</li>
          <li class="content">{{ item.content }}</li>
        </ul>
      </div>

      <!--这特么是直接拿到了评论的id-->
      <div ref="up" class="comment-ctr" @click="setSupport(item.id, item.up, userId)">
        <div><yin-icon :icon="iconList.Support"></yin-icon> {{ item.up }}</div>
        <el-icon v-if="item.userId === userId" @click="deleteComment(item.id, index)"><delete /></el-icon>
      </div>
    </li>
  </ul>
</template>

<script lang="ts" setup>

import { defineProps, getCurrentInstance, ref, toRefs, computed, watch, reactive, onMounted } from "vue";
import { useStore } from "vuex";
import { Delete } from "@element-plus/icons-vue";

import YinIcon from "@/components/layouts/YinIcon.vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { Icon } from "@/enums";
import { formatDate } from "@/utils";

const { proxy } = getCurrentInstance();
const store = useStore();
const { checkStatus } = mixin();



const props = defineProps({
  playId: Number || String, // 歌曲ID 或 歌单ID
  type: Number, // 歌单 1 / 歌曲 0
});

const { playId, type } = toRefs(props);
const textarea = ref(""); // 存放输入内容
const commentList = ref([]); // 存放评论内容
const iconList = reactive({
  Support: Icon.Support,
});

const userId = computed(() => store.getters.userId);
const songId = computed(() => store.getters.songId);

watch(songId, () => {
  getComment(songId.value);
});

onMounted(() => {
  getComment(playId.value);
});

// 获取所有评论   x      获取指定歌单id/歌曲id的全部评论  所以步骤3省略
async function getComment(id) {
  try {
    //1. 发送请求  类型判断在index.ts那里实现
    const result = (await HttpManager.getAllComment(type.value, id)) as ResponseBody;
    console.log(result)
    //2. 将数据库多表查询返回的值赋给commentList
    commentList.value = result.data;
    // //3.遍历commentList依次取出各字段值
    // for (let index = 0; index < commentList.value.length; index++) {
    //   // 获取评论用户的昵称和头像
    //   const resultUser = (await HttpManager.getUserOfId(commentList.value[index].userId)) as ResponseBody;
    //   commentList.value[index].avator = resultUser.data[0].avator;
    //   commentList.value[index].username = resultUser.data[0].username;
    // }
  } catch (error) {
    console.error('[获取所有评论失败]===>', error);
  }
}

// 提交评论
async function submitComment() {
  //1. 判断登录状态
  if (!checkStatus()) return;

   //2. 构建请求参数
  let songListId = null;
  let songId = null;
  let nowType = null;
  // 0 代表歌曲， 1 代表歌单
  if (type.value === 1) {
    nowType = 1;
    songListId = `${playId.value}`;
  } else if (type.value === 0) {
    nowType = 0;
    songId = `${playId.value}`;
  }
  const content = textarea.value;  //评论详情

  //3. 发送请求
  const result = (await HttpManager.setComment({ userId: userId.value, content, songId, songListId, type:nowType })) as ResponseBody;
  console.log(result);
  //4.提示信息
  (proxy as any).$message({
    message: result.message,
    type: result.type,
  });

//5. 提交评论成功后置空输入框，同时重新加载当前歌单的评论
  if (result.success) {
    textarea.value = "";
    await getComment(playId.value);
  }
}

// 删除评论
async function deleteComment(id, index) {
  const result = (await HttpManager.deleteComment(id)) as ResponseBody;
  // console.log(result);
  //提示信息
  (proxy as any).$message({
    message: result.message,
    type: result.type,
  });

  if (result.success) commentList.value.splice(index, 1);
}

// 点赞  还得再查一下
async function setSupport(id, up, userId) {
  //0. 检查登录状态
  if (!checkStatus()) return;

  let result = null;
  let operatorR = null;
  const commentId = id;
  // 在点赞功能中，我们需要检查用户是否已经点赞过
  // 1. 检查用户是否已经点赞（`testAlreadySupport`）
  const r = (await HttpManager.testAlreadySupport({ commentId, userId })) as ResponseBody;
  (proxy as any).$message({
    message: r.message,
    type: r.type,
    date: r.data,
  });

  // 2. 根据检查结果，进行取消点赞（`deleteUserSupport`）或新增点赞（`insertUserSupport`），同时更新评论的点赞数（`setSupport`）
  //已点赞 → 取消点赞
  if (r.data) {
    up = up - 1;
    operatorR = (await HttpManager.deleteUserSupport({ commentId, userId })) as ResponseBody;
    result = (await HttpManager.setSupport({ id, up })) as ResponseBody;
  } else {
    // 未点赞 → 新增点赞
    up = up + 1;
    operatorR = (await HttpManager.insertUserSupport({ commentId, userId })) as ResponseBody;
    result = (await HttpManager.setSupport({ id, up })) as ResponseBody;
  }
  // 3. 刷新评论列表
  if (result.success && operatorR.success) {
    // proxy.$refs.up[index].children[0].style.color = "#2796dd";
    await getComment(playId.value);
  }
}

const attachImageUrl = HttpManager.attachImageUrl;
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

/*评论*/
.comment {
  position: relative;
  margin-bottom: 60px;

  .comment-title {
    height: 50px;
    line-height: 50px;

    .comment-desc {
      font-size: 14px;
      font-weight: 400;
      color: $color-grey;
      margin-left: 10px;
    }
  }

  .comment-input {
    display: flex;
    margin-bottom: 20px;
  }

  .sub-btn {
    position: absolute;
    right: 0;
  }
}

/*热门评论*/
.popular {
  width: 100%;
  > li {
    border-bottom: solid 1px rgba(0, 0, 0, 0.1);
    padding: 15px 0;
    display: flex;
    .popular-img {
      width: 50px;
    }

    .popular-msg {
      padding: 0 20px;
      flex: 1;
      li {
        width: 100%;
      }
      .time {
        font-size: 0.6rem;
        color: rgba(0, 0, 0, 0.5);
      }
      .name {
        color: rgba(0, 0, 0, 0.5);
      }
      .content {
        font-size: 1rem;
      }
    }

    .comment-ctr {
      display: flex;
      align-items: center;
      width: 80px;
      font-size: 1rem;
      cursor: pointer;

      .el-icon {
        margin: 0 10px;
      }

      &:hover,
      :deep(.icon):hover {
        color: $color-grey;
      }
    }
  }
}

.icon {
  @include icon(1em);
}
</style>
