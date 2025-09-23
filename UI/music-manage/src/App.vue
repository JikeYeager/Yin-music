<!--入口vue文件，但是文件运行的第一个页面是public里面的index.html文件-->
<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script lang="ts" setup>
import { getCurrentInstance } from "vue";

const { proxy } = getCurrentInstance();

if (sessionStorage.getItem("dataStore")) {
  proxy.$store.replaceState(Object.assign({}, proxy.$store.state, JSON.parse(sessionStorage.getItem("dataStore"))));
}

window.addEventListener("beforeunload", () => {
  sessionStorage.setItem("dataStore", JSON.stringify(proxy.$store.state));
});
</script>
