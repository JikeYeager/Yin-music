package com.ly.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.yin.common.CacheUtils;
import com.ly.yin.common.Constants;
import com.ly.yin.common.VerifyCodeUtils;
import com.ly.yin.domain.Consumer;
import com.ly.yin.mapper.ConsumerMapper;
import com.ly.yin.query.BaseQuery;
import com.ly.yin.service.IConsumerService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Service
public class ConsumerServiceImpl
        extends ServiceImpl<ConsumerMapper, Consumer>
        implements IConsumerService
{

    @Override
    //实现分页查询
    public IPage<Consumer> pageQuery(BaseQuery query) //传入参数类型BaseQuery的query
    {
        //1. 构建分页查询   Mybatis支持分页有关操作，所以直接初始化一个Page对象
        IPage<Consumer> page = new Page<>();
        //设置当前页码
        page.setCurrent(query.getCurrentPage());
        //设置每页记录数 不是setPage（）方法
        page.setSize(query.getPageSize());

        //2.构建查询参数
        LambdaQueryWrapper<Consumer> wrapper = new LambdaQueryWrapper<>();
        //2.1 判断keyword(查询关键字)是否为空
        String keyword = query.getKeyword();
        boolean flag = keyword !=null && keyword.length() > 0;
        //2.2、如果 keyword 不为空 加上 username like '%keyword% (模糊查询)
        //like()
        wrapper.like(flag,Consumer::getUsername,keyword);

        //3.执行分页查询
        IPage<Consumer> result = page(page,wrapper);
        return result;
    }

//    实现用户头像更新
    @Override
    public String updateAvatar(Integer id, MultipartFile file) {
        //1.删除原来的图片
        //2.新增图片 (copy)   java 内存 --> img磁盘
        //2.1、获取原始的文件名称
        String originalFilename = file.getOriginalFilename();
        int i = originalFilename.lastIndexOf("."); //通过lastIndexOf(".")找到扩展名的起始后缀索引

        //2.2 获取原始文件名后缀，如：“.jpg ”
        String h = originalFilename.substring(i); //substring(i)截取字符串子串,截取从i开始到字符串末的子串

        //2.3、生成新的文件名   "xxx.jpg"
        String uuid = UUID.randomUUID().toString() +h;

        //2.4 拼接copy 文件路径       "D:/2025-06-code/YINYUE-20250616/img/avatorImages/xxx.jpg"
        String fileName = Constants.AVATOR_IMAGES_PATH + uuid;

        //2.5 拼接数据库字段 avator    "img/avatorImages/xxx.jpg"
        String avator = Constants.AVATOR_PATH + uuid;

        //2.6 copy   java 内存上传图片 --> img 磁盘
        try(InputStream in = file.getInputStream(); FileOutputStream out = new FileOutputStream(fileName);)
        {
//      允许用户以一行代码的形式，将输入流（InputStream）的内容复制到输出流（OutputStream）中，从而避免了传统的多行代码实现，大大简化了流的复制操作。
            IOUtils.copy(in,out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //3、更新数据库
        Consumer consumer = new Consumer();
//        创建Consumer对象，并根据拼接数据库字段 avator和传入的行id，更新 ID 和头像路径
        consumer.setId(id);
        consumer.setAvator(avator);
        updateById(consumer); //MyBatis-Plus 提供的通用方法，根据 ID 更新记录
        return avator;   //7.31 更新 为了实现用户端更换头像做出的修改，返回图片的数据库存储路径
    }

    /* 用户登录 （不是管理员登录）*/
    @Override
    public Consumer login(Consumer consumer) {
        //1.0使用mybatis提供的方法查询数据库，匹配用户名和密码
        /*1.1调用LambdaQueryWrapper实现条件查询
         * select * from consumer where username=? and password =?;
         * */

        //1.2 mybatis提供的基于Lambda的条件查询构造器，,<Consumer>表示查询Consumer这张表
        LambdaQueryWrapper<Consumer> wrapper=new LambdaQueryWrapper<>();

       /*1.3 eq表示equals，判断是否相等；
        Consumer::getUsername表示查找Consumer表的Username字段的值；(select username from Consumer)
        consumer.getUsername()表示前端consumer接收到的UserName的值，这里也可以写常量 ( where Username=?)
        等价于select Username from consumer where Username=?
        如果此条件查询语句能够运行则匹配结果不为空，说明匹配成功;
        * */
        System.out.println(consumer.getUsername());
        wrapper.eq(Consumer::getUsername,consumer.getUsername());

        /*1.4 和匹配用户名username一样的步骤，匹配密码password*/
        System.out.println(consumer.getPassword());
        wrapper.eq(Consumer::getPassword,consumer.getPassword());

        /*1.5
        * 使用getOne(),前端传回一次登录请求肯定只有一个匹配成功的用户，要是有多个匹配结果说明你数据库存的数据有问题
        dbConsumer 为根据用户名和密码从数据库查询的数据*/
        Consumer dbConsumer = getOne(wrapper);


        /*1.6 判断匹配结果是否为空
         * 前端传入的数据若和数据库中的数据无法配对上，dbConsumer为空，登录失败,抛出异常信息
         * 前端传入的数据若和数据库中的数据配对上，dbConsumer非空，登录成功*/
        if (dbConsumer == null){
            throw new RuntimeException("用户名账号或密码错误");
        }else {
            System.out.println("用户登录成功");
            return dbConsumer;  //wcnmd,这里写成consumer了
        }
    }

    /* 邮箱登录 */
    @Override
    public Consumer loginByemail(Consumer consumer) {
        //1.2 mybatis提供的基于Lambda的条件查询构造器，,<Consumer>表示查询Consumer这张表
        LambdaQueryWrapper<Consumer> wrapper=new LambdaQueryWrapper<>();
        //1.3 判断邮箱是否相等
        wrapper.eq(Consumer::getEmail,consumer.getEmail());
        //1.4 判断密码是否相等
        wrapper.eq(Consumer::getPassword,consumer.getPassword());
        /*1.5使用getOne(),前端传回一次登录请求肯定只有一个匹配成功的用户，要是有多个匹配结果说明你数据库存的数据有问题
        //  dbConsumer 为根据邮箱和密码从数据库查询的数据*/
        Consumer dbConsumer = getOne(wrapper);
        /*1.6 判断匹配结果是否为空
         * 前端传入的数据若和数据库中的数据无法配对上，dbConsumer为空，登录失败,抛出异常信息
         * 前端传入的数据若和数据库中的数据配对上，dbConsumer非空，登录成功*/
        if (dbConsumer == null){
            throw new RuntimeException("用户名邮箱或密码错误");
        }else {
            System.out.println("邮箱登录成功");
            return consumer;
        }
    }

    //获取在application.yml配置的邮件发送地址
    @Value("${mail.address}")
    private String sendAddress;

    //为了让邮箱发送邮件新增的注入类
    @Autowired
    private JavaMailSender javaMailSender;

    /* 邮箱发送验证码 */
    @Override
    public void sendVerificationCode(String email) {
        //1. 验证邮箱是否存在（如果consumer表中都没有这个邮箱，那么肯定也无法发送验证码）或者说进行邮箱匹配
        //select * from consumer where email = ?
        LambdaQueryWrapper<Consumer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Consumer::getEmail,email);
        Consumer consumer = getOne(wrapper);
        //若邮箱不存在则直接报错
        if(consumer == null){
            throw  new RuntimeException("邮箱不存在哦~");
        }

        //2. 发送验证码  发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        //2.1 验证账号 username和发件人sendAddress验证必须一致
        message.setFrom(sendAddress);  //这里没有写死，sendAddress可以自己去application.yml改
        //2.2 邮件标题
        message.setSubject("YIN-邮件验证码");
        //2.3 邮件内容
//        message.setText("123456"); //正常来说应该生成随机数，这里定死123456了
        // 7.19 这里我们使用自己写的验证码类，实现生成合法的六位随机验证码
        String code = VerifyCodeUtils.generateVerifyCode(6);
        message.setText("验证码：" + code);

        //2.4 收件人
        message.setTo(email);
        //2.5 发送
        javaMailSender.send(message);
        //2.6 缓存验证码  通过我们自己写的CacheUtils.java实现
//        CacheUtils.setKey(email,"123456");
        CacheUtils.setKey(email,code);
    }

    /*重置密码实现*/
    @Override
    public void resetPassword(HashMap<String, String> param) {
        //0. 从param中提取参数
        String email = param.get("email");
        String code = param.get("code");  //验证码
        String password = param.get("password"); //新密码
        String confirmPassword = param.get("confirmPassword");//再次确认的密码

        //1. 验证码是否正确
        //1.1 从缓存中取验证码
        String value = CacheUtils.getKey(email);
        //1.2 若验证码为空
        if (value == null) {
            throw new RuntimeException("验证码已过期，请重新获取~");
        }
        //1.3 若验证码错误（输入的验证码与缓存的验证码匹配错误）
        if(!value.equals(code)) {
            throw new RuntimeException("验证码不正确~");
        }

        //2. 两次密码是否一致
        if(!password.equals(confirmPassword)) {
            throw new RuntimeException("两次密码不一致~");
        }

        //3. 新密码和旧密码是否一致（自主完成）
//        Consumer consumer = new Consumer();
//        System.out.println(consumer.getPassword());
        //创建个jb空对象有个毛用
        //3.1  创建查询对象
        LambdaQueryWrapper<Consumer> queryWrapper = new LambdaQueryWrapper<>();
        //3.2 执行select * from Consumer where email = ?
        queryWrapper.eq(Consumer::getEmail,email);
        //3.3 从数据库加载用户
        Consumer dbconsumer = getOne(queryWrapper);
        //3.4 校验新旧密码
        if(password.equals(dbconsumer.getPassword())){
            throw new RuntimeException("新密码不能与旧密码相同~");
        }

        //4. 更新密码
        //执行此SQL语句：update consumer set password = ？where email = ？
        //4.1 创建Consumer数据库的更新对象
        LambdaUpdateWrapper<Consumer> wrapper = new LambdaUpdateWrapper<>();
        //4.2  set password = ？
        wrapper.set(Consumer::getPassword,password);
        //4.3  where email = ?
        wrapper.eq(Consumer::getEmail,email);
        //4.4 最后再更新一下数据库的更新时间
        wrapper.set(Consumer::getUpdateTime,new Date());

        //5. 将修改保存到数据库
        update(wrapper);

    }

    /* 用户在个人资料界面更改密码 */
    @Override
    public void updatePassword(HashMap<String, String> param) {
        //0.从param中提取参数
        String id =param.get("id");
        String oldPassword =param.get("oldPassword");
        String password =param.get("password");
        String confirmPassword = param.get("confirmPassword");
//        System.out.println(password);
//        System.out.println(confirmPassword);
        //1. 判断新密码与确认密码是否一致 (要求一致)
        //这里实际上不用写，因为我们在前端写的验证规则已经规定好了，它会在你输入的时候就进行判断，而不是点击提交后再判断
        if (!password.equals(confirmPassword)) {
            throw new RuntimeException("两次输入的密码不相同哦~");
        }

        //2. 执行SQL语句：update consumer set password = ? where user_id =? and password = ${oldPassword}
        LambdaQueryWrapper<Consumer> wrapper = new LambdaQueryWrapper<>();
         // where user_id =?
        wrapper.eq(Consumer::getId,id);
        //判断输入的旧密码与数据库中的密码是否一致 and password = ${oldPassword}
        wrapper.eq(Consumer::getPassword,oldPassword);

       // 3. 获取条件查询后的数据库记录，准备对此记录进行修改
        Consumer dbconsumer = getOne(wrapper);

        //4. 相当于select * from consumer where user_id =? and password = ${oldPassword}
        //看是否能够得到返回结果，若得不到就说明条件查询的密码判断那里出错了
        if (dbconsumer == null){
            throw new RuntimeException("旧密码出错了哦~");
        }
        //4. update consumer set password = ?
        dbconsumer.setPassword(password);
        //5.最后将修改保存到数据库
        updateById(dbconsumer);
    }
}

