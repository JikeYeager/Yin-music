// vue的全局配置文件
const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  chainWebpack: config => {
    config.plugin('define').tap(definitions => {
      Object.assign(definitions[0]['process.env'], {
        NODE_HOST: '"http://localhost:9999"', //这里是指前端8080调用后端的9999端口
      });
      return definitions;
    });
  }
})
