<template>
  <div class="search-song-list">
    <play-list :playList="playList" path="song-sheet-detail"></play-list>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, watch, onMounted, getCurrentInstance } from "vue";
import { useStore } from "vuex";
import PlayList from "@/components/PlayList.vue";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    PlayList,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();

    const playList = ref([]);   //存放歌单搜索结果的列表
    const searchWord = computed(() => store.getters.searchWord);
    watch(searchWord, (value) => {
      getSearchList(value);
    });

    // 歌单搜索
    async function getSearchList(value) {
      if (!value) {
        playList.value = [];
        return;
      }
      //发送请求
      const result = (await HttpManager.SearchSongList(value)) as ResponseBody
      playList.value = result.data

    }

    onMounted(() => {
      getSearchList(proxy.$route.query.keywords);
    });

    return {
      playList,
    };
  },
});
</script>
