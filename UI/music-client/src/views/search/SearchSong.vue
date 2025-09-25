<template>
  <div class="search-song">
    <song-list :songList="currentSongList"></song-list>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, watch, onMounted, getCurrentInstance } from "vue";
import { useStore } from "vuex";
import SongList from "@/components/SongList.vue";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    SongList,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();

    const currentSongList = ref([]); // 存放的音乐
    //我们当时在YinHeader.vue存储的搜索框内容，现在可以拿出来了
    const searchWord = computed(() => store.getters.searchWord);
    // 监听 searchWord 计算属性的变化 ，当搜索词的值发生改变时，触发回调函数
    //value代表的是 searchWord 计算属性的当前值，也就是从 Vuex store 中获取的最新搜索词。
    watch(searchWord, (value) => {
      searchSong(value);
    });

    // 搜索音乐
    async function searchSong(value) {
      if (!value) {
        currentSongList.value = [];
        return;
      }
      //发送请求
      const result = (await HttpManager.searchSong(value)) as ResponseBody
      currentSongList.value = result.data
    }

    onMounted(() => {
      searchSong(proxy.$route.query.keywords);
    });

    return {
      currentSongList,
    };
  },
});
</script>
