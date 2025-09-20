package com.ly.yin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ly.yin.common.R;
import com.ly.yin.domain.Consumer;
import com.ly.yin.query.BaseQuery;
import com.ly.yin.service.IConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
/*虽然controller层只是与前端进行交互的，不负责具体后端功能的实现，但是mybatis很多已经
* 内部封装好的方法可以直接在controller使用，不必在service层再实现了*/
@RestController
@RequestMapping("/consumer") //对应资源路径的定位，路径映射
public class ConsumerController {
    //注入service
    @Autowired
    private IConsumerService consumerService;


    /*1. 全查询的后端实现*/
    @GetMapping("/list")
//    public List<Consumer> list(){
//        //从controller层调用servicer层需要手动返回
//        return consumerService.list();
//    }
    public R list() {
        List<Consumer> list = consumerService.list();  //调用List内置的list()方法
        return R.success("查询成功", list);
    }

    /* 2. 分页查询的后端实现*/
    @PostMapping("/page")  //提交表单的Mapping,用于实现分页查询
    public R pageQuery(@RequestBody BaseQuery query) {
        IPage<Consumer> page = consumerService.pageQuery(query);  //调用service里面实现的pageQuery方法
        return R.success("分页查询成功", page);
    }

    /*3. 删除用户记录 && 注销用户 */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Integer id) {
        try {
            consumerService.removeById(id);  //removeById是封装好的，直接调用，无返回结果
            return R.success("删除成功");
        }catch (Exception e){
            return R.error("删除用户失败"+e.getMessage());
        }

    }

    /*4. 批量删除用户记录，不好用小闪电测试，不方便直接在控制面板传参*/
    @DeleteMapping("/deleteAll")
    public R deleteAll(@RequestParam List<Integer> ids) {
        System.out.println("用户删除的id:" + ids);
        consumerService.removeBatchByIds(ids);  //调用内置的removeBatchByIds，删除指定行数的用户记录
        return R.success("删除成功");
    }

    /*5. 修改用户头像  不好用小闪电测试，不方便直接在控制面板传参*/
    @PostMapping("/avatar/{id}")  //"/avatar/{id}"这玩意为什么在api接口中找不到？
    public R update(@PathVariable Integer id, MultipartFile file) {
        System.out.println(id);
        System.out.println(file);
        String avatar = consumerService.updateAvatar(id, file);  //调用service中的实现的方法
        return R.success("用户头像更新成功",avatar);
    }

    /*6. 用户注册（新增用户）*/
    @PostMapping("/add")
    public R add(@RequestBody Consumer consumer) {
        //因为用户注册的创建时间需要默认值，所以我们在这里设为当前时间
        consumer.setCreateTime(new Date());
        //用户注册的更新时间也需要默认值。。。
        consumer.setUpdateTime(new Date());
        consumerService.save(consumer);
        return R.success("用户注册成功");
    }

    /* 7.用户密码登录（Admin是管理员登录）*/
    @PostMapping("/login")
    public R login(@RequestBody Consumer consumer) {
        try {
            //仿照管理员登录实现的，与老师的代码可能会有出入的地方
            Consumer dbConsumer = consumerService.login(consumer);
            dbConsumer.setPassword(""); //登录成功后或每次刷新置空密码，以免信息泄露
            return R.success("用户登录成功", dbConsumer); //成功登录显示“登录成功”且返回用户名
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage()); //在前端输出错误信息
        }

    }

    /*8. 用户邮箱登录 */
    @PostMapping("/email")
    public R loginByemail(@RequestBody Consumer consumer){
        try {
            //仿照管理员登录实现的，与老师的代码可能会有出入的地方
            Consumer dbConsumer = consumerService.loginByemail(consumer);
            dbConsumer.setPassword(""); //登录成功后或每次刷新置空密码，以免信息泄露
            return R.success("用户邮箱登录成功", dbConsumer); //成功登录显示“登录成功”且返回用户名
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage()); //在前端输出错误信息
        }
    }

    /* 9.用户通过邮箱发送验证码*/
    @GetMapping("/sendVerificationCode/{email}")
    public R sendVerificationCode(@PathVariable String email){
//        System.out.println(email);
//        return R.success("邮箱发送成功");
        try {
            System.out.println(email);
            //发送验证码
            consumerService.sendVerificationCode(email);
            return R.success("邮箱发送成功");
        }catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /* 10.用户在登录页面重置密码 */
    @PostMapping("/resetPassword")
    //HashMap<String,String> key为 String，value也为String 因为传过来的参数不能用domain中的类接收就只能用map接收了
    public R resetPassword(@RequestBody HashMap<String,String> param){
        try {
            consumerService.resetPassword(param);
            return R.success("密码重置成功");
        }catch (Exception e){
            e.printStackTrace();
            //接收并打印  throw  new RuntimeException("邮箱不存在哦~"); 抛出的异常信息
            return R.error(e.getMessage());
        }

    }

    /*11. 根据指定userid获取用户信息 （用户端个人页面使用）*/
    @GetMapping("/detail/{userId}")
    public R detail(@PathVariable Integer userId){
//错误的使用方法，selectById是mapper层的数据库查询方法，
// 要么在mapper层直接使用，要在其他层使用必须要说明：
/*@Autowired
    public ConsumerMapper consumer; // 暴露Mapper（谨慎使用）验证
      Consumer list = consumer.selectById(userId);
 */

        Consumer consumer = consumerService.getById(userId);
        return R.success("用户个人信息获取成功喵~", consumer);
    }

    /*12. 用户端用户修改个人信息 */
    @PostMapping("/update")
    public R updateInfo(@RequestBody Consumer consumer){
        try {
            consumerService.updateById(consumer);
            return R.success("用户个人信息更改成功喵~");
        }catch (Exception e){
            return R.error("用户个人信息更新失败喵~" + e.getMessage());
        }
    }

    /* 13.用户在个人资料界面更改密码 */
    @PostMapping("/updatePassword")
    //HashMap<String,String> key为 String，value也为String
    public R updatePassword(@RequestBody HashMap<String,String> param){
        try {
            consumerService.updatePassword(param);
            return R.success("密码重置成功");
        }catch (Exception e){
            e.printStackTrace();
            //接收并打印  throw  new RuntimeException("邮箱不存在哦~"); 抛出的异常信息
            return R.error(e.getMessage());
        }

    }

}
