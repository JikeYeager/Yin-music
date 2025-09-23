<!--只包含主页面Info，不包含顶部导航栏和侧边栏，顶部导航栏和侧边栏在Home.vue中实现-->
<template>
  <el-row :gutter="20">
    <el-col :span="6">
      <el-card shadow="hover" :body-style="{ padding: '0px' }">
        <div class="card-content">
          <div class="card-left">
            <el-icon><user /></el-icon>
          </div>
          <div class="card-right">
            <div class="card-num">{{ userCount }}</div>
            <div>用户总数</div>
          </div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover" :body-style="{ padding: '0px' }">
        <div class="card-content">
          <div class="card-left">
            <el-icon><headset /></el-icon>
          </div>
          <div class="card-right">
            <div class="card-num">{{ songCount }}</div>
            <div>歌曲总数</div>
          </div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover" :body-style="{ padding: '0px' }">
        <div class="card-content">
          <div class="card-left">
            <el-icon><mic /></el-icon>
          </div>
          <div class="card-right">
            <div class="card-num">{{ singerCount }}</div>
            <div>歌手数量</div>
          </div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover" :body-style="{ padding: '0px' }">
        <div class="card-content">
          <div class="card-left">
            <el-icon><document /></el-icon>
          </div>
          <div class="card-right">
            <div class="card-num">{{ songListCount }}</div>
            <div>歌单数量</div>
          </div>
        </div>
      </el-card>
    </el-col>
  </el-row>
  <el-row :gutter="20">
    <el-col :span="12">
      <h3>用户性别比例</h3>
      <el-card class="cav-info" shadow="hover" :body-style="{ padding: '0px' }" id="userSex"></el-card>
    </el-col>
    <el-col :span="12">
      <h3>歌曲类型</h3>
      <el-card class="cav-info" shadow="hover" :body-style="{ padding: '0px' }" id="songStyle"></el-card>
    </el-col>
  </el-row>
  <el-row :gutter="20">
    <el-col :span="12">
      <h3>歌手性别比例</h3>
      <el-card class="cav-info" shadow="hover" :body-style="{ padding: '0px' }" id="singerSex"></el-card>
    </el-col>
    <el-col :span="12">
      <h3>歌手国籍</h3>
      <el-card class="cav-info" shadow="hover" :body-style="{ padding: '0px' }" id="country"></el-card>
    </el-col>
  </el-row>
</template>
<script lang="ts" setup>
// import { ref, reactive, getCurrentInstance } from "vue";
import { ref, reactive } from "vue";
import * as echarts from "echarts";
import { Mic, Document, User, Headset } from "@element-plus/icons-vue";
import { HttpManager } from "@/api/index";

// const { proxy } = getCurrentInstance();
//修改Info主页面上的用户总数、歌曲总数、歌手数量、歌单数量,一开始初始化均为0
const userCount = ref(0);
const songCount = ref(0);
const singerCount = ref(0);
const songListCount = ref(0);

//定义用户性别比例的饼状图
const userSex = reactive({
  series: [
    {
      type: "pie",
      data: [
        {
          value: 0,
          name: "男",
        },
        {
          value: 0,
          name: "女",
        },
      ],
    },
  ],
});
//定义歌曲类型的柱状图
const songStyle = reactive({
  xAxis: {
    type: "category",
    /*   2025/6/21  因为数据实际上不止有7类歌单类型，我搜了一下有13种，故更新一下（13类太长了，排版有问题，就写10类）
    * 6/22 使用模糊匹配，"轻音乐-乐器", "日韩-轻音乐"不用单独统计了，如"轻音乐-乐器"，既算到轻音乐，又算到乐器里面
    // data: ["华语", "粤语", "欧美", "日韩", "BGM", "轻音乐", "乐器", "欧美-轻音乐" ,"轻音乐-乐器", "日韩-轻音乐"],
    */
    data: ["华语", "粤语", "欧美", "日韩", "BGM", "轻音乐", "乐器"]
  },
  yAxis: {
    type: "value",
  },
  series: [
    {
      data: [0, 0, 0, 0, 0, 0, 0], //同理，这里也要更新
      type: "bar",
      barWidth: "20%",
    },
  ],
});
//歌手性别
const singerSex = reactive({
  series: [
    {
      type: "pie",
      data: [
        {
          value: 0,
          name: "男",
        },
        {
          value: 0,
          name: "女",
        },
      ],
    },
  ],
});
//歌手国籍柱状图
const country = reactive({
  xAxis: {
    type: "category",
    data: [
      "中国",
      "韩国",
      "意大利",
      "新加坡",
      "美国",
      // "马来西亚",
      "西班牙",
      "日本",
    ],
  },
  yAxis: {
    type: "value",
  },
  series: [
    {
      data: [0, 0, 0, 0, 0, 0, 0, 0],
      type: "bar",
      barWidth: "15%",
    },
  ],
});

// 统计性别人数方法
//在数据库中为了节省空间，0表示男，1表示女
function setSex(sex, arr) { //arr这里是传入的用户表的data列表
  let value = 0;
  const name = sex === 0 ? "男" : "女"; //解映射，0为男，1为女 name也是集合？{'男'|'女'}
  for (let item of arr) {
    if (sex === item.sex) {
      value++;
    }
  }
  return { value, name };
}

/*统计不同歌曲类型数量
根据外循环遍历歌曲风格列表data: ["华语", "粤语", "欧美", "日韩", "BGM", "轻音乐", "乐器"],改变style
加上传入的歌单列表arr，这样就能把每个类型的歌曲数统计出来
* */
function setStyle(style,arr){
  let value = 0;
  for (let item of arr) {  //遍历歌单列表
    // if (style === item.style){ //若某一歌单style与当前传入的style一致
    // @Data 6/21 这里使用模糊匹配 ，因为"轻音乐-乐器", "日韩-轻音乐"不用单独统计了
    if (item.style.includes(style)){
      value++;
    }
  }
  return value;
}

function setCountry(location,arr){
  let value = 0;
  for (let item of arr) {
    if(item.location.includes(location)){  //国籍应该不存在模糊匹配吧
      value++;
    }
  }
  return value;
}


//1.获取所有用户信息插入echarts
// const result=(await HttpManager.getAllUser()) as ResponseBody  这里使用同步请求await会报错，所以用异步请求
//异步是请求响应后立马显示，同步是代码执行完毕后显示
HttpManager.getAllUser().then(res =>{
   //1.1请求所有用户接口
   const body = res as ResponseBody //getAllUser()返回结果
   const userList = body.data  //从返回结果中拿出用户数据信息
   console.log(body)  //打印在控制台方便看
  /*userCount是一开始定义的用户总数变量，用value进行赋值
  * 用户总数的逻辑就是用户数据的长度，data返回数据类型为列表，所以data.length表示用户总数*/
  userCount.value = body.data.length

  //1.2 统计用户性别 调用上面已经写好的setSex方法
  const man = setSex(0,userList)
  const woman = setSex(1,userList)

  /*1.3 替换userSex数组里面value的值 ,通过更改value的值才能绘制饼状图
  * 这里可以把1.2的步骤给合并掉
  * 注意更新userSex数组不可以使用map方法，map方法返回更新后的一个新数组，不影响用来的数组,所以要重新定义userSex.series[0].data
  *
  * */
  userSex.series[0].data=userSex.series[0].data.map(data =>{
    const key=data.name=='男' ? 0 : 1  //这里的data是user.Sexseries[0]内的data，不是body.data
    console.log(setSex(key,userList))  //1.2的步骤放到这里
    return setSex(key,userList)
  })
  // console.log(userSex)
  //1.4、echarts 渲染出饼状图
  const userSexChart = echarts.init(document.getElementById("userSex"));
  userSexChart.setOption(userSex);
    }
)

//2 获取所有歌单
HttpManager.getAllSongList().then(res =>{
  //2.1 请求歌单接口
  const body = res as ResponseBody //getAllUser()返回结果
  const list = body.data
  console.log(body)

  //2.2 设置歌单总数
  songListCount.value=body.data.length


  //2.3 统计每个类型歌单数量
  let ydata =[]  //用来存放每种歌曲类别的数量,不可以用new Array()
  /* 获取每种歌曲类型的数量，可以使用双重嵌套循环，也可以使用第一循环+调用方法实现*/
  for (let style of songStyle.xAxis.data) {  //先遍历X轴，依次统计华语、粤语.....等类型的歌曲数量
    ydata.push(setStyle(style,list))
  }
  console.log(ydata)  //测试用的

  //2.4、新数组覆盖原来的值
  songStyle.series[0].data = ydata

  //2.5 echarts 渲染
  const songStyleDom = echarts.init(document.getElementById("songStyle"));
  songStyleDom.setOption(songStyle);
})

//3. 获取所有歌手
HttpManager.getAllSinger().then(res =>{
  //3.1
  const body = res as  ResponseBody
  const singerlist = body.data
  console.log(body)
  //3.2
  singerCount.value = singerlist.length
  //3.3
  singerSex.series[0].data=singerSex.series[0].data.map(data =>{
    //把前端存放歌手的singerSex的data.name的男映射为0，女映射为1。
    /*tip：有点疑惑，setsex方法为什么不能把sex参数直接设置为字符类型，这样不就能直接判断了吗？
    这样又要在setsex方法把0/1映射回男/女，是为了数据安全吗？
    答：因为sex是集合类型（可迭代对象），而字符串无法同时存放男/女的值*/
    const key=data.name==='男' ? 0 : 1  //这个key是集合？ {0|1}
    console.log(setSex(key,singerlist))

    return setSex(key,singerlist)  //最后再返回歌手性别
  })
  //3.4、echarts 渲染出饼状图
  const singerSexChart = echarts.init(document.getElementById("singerSex"));
  singerSexChart.setOption(singerSex);

  //3.5 统计歌手国籍
  let ydata=[]
  for (let con of country.xAxis.data) {
    ydata.push(setCountry(con,singerlist))
  }
  console.log(ydata)

  //3.6新数组覆盖原来的值
  country.series[0].data = ydata

  //3.7 echarts 渲染
  const countryDom = echarts.init(document.getElementById("country"));
  countryDom.setOption(country);


})
//4. 获取所有歌曲
HttpManager.getAllSong().then(res =>{
  //4.1 请求歌单接口
  const body = res as ResponseBody //getAllUser()返回结果
  const list = body.data
  console.log(body)

  //4.2 设置歌曲总数
  songCount.value=body.data.length  //116条

})

</script>

<style scoped>
.card-content {
  display: flex;
  align-items: center;
  justify-content: space-around;
  height: 100px;
  padding-left: 20%;
  text-align: center;
}

.card-left {
  display: flex;
  font-size: 3rem;
}

.card-right {
  flex: 1;
  font-size: 14px;
}

.card-num {
  font-size: 30px;
  font-weight: bold;
}

h3 {
  margin: 10px 0;
  text-align: center;
}
.cav-info {
  border-radius: 6px;
  overflow: hidden;
  height: 250px;
  background-color: white;
}
</style>
