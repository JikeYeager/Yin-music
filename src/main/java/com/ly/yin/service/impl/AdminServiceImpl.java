package com.ly.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.yin.domain.Admin;
import com.ly.yin.mapper.AdminMapper;
import com.ly.yin.service.IAdminService;
import org.springframework.stereotype.Service;

//实现继承的ServiceImpl接口
@Service  //只有打了@Service才能被Spring管理，才能用@Autowired注入
public class AdminServiceImpl
        extends ServiceImpl<AdminMapper, Admin>
        implements IAdminService
{
    //1.实现登录逻辑   具体的后端服务都在service层实现
    @Override
    public Admin login(Admin admin)
    {
        //1.0使用mybatis提供的方法查询数据库，匹配用户名和密码
        /*1.1调用LambdaQueryWrapper实现条件查询
        * select * from admin where name=? and password =?;
        * */

        //1.2 mybatis提供的基于Lambda的条件查询构造器，,<admin>表示查询admin这张表
        LambdaQueryWrapper<Admin> wrapper=new LambdaQueryWrapper<>();

        /*1.3 eq表示equals，判断是否相等；
        Admin::getName表示查找Admin表的Name字段的值；(select name from admin)
        admin.getName()表示前端admin接收到的Name的值，这里也可以写常量 ( where name=?)
        等价于select name from admin where name=?
        如果此条件查询语句能够运行则匹配结果不为空，说明匹配成功;
        * */
        wrapper.eq(Admin::getName,admin.getName());

        /*1.4
        * Admin::getPassword  查找Admin的getPassword字段 (from admin where Password)
        * admin.getPassword() 前端接收到的Password的值 ( ?)
        * 等价于select * from admin where Password=? ;
        *  如果此条件查询语句能够运行则匹配结果不为空，说明匹配成功;
        * */
        wrapper.eq(Admin::getPassword,admin.getPassword());

        /*1.5  获取数据库查询数据
        使用getOne(),前端传回一次登录请求肯定只有一个匹配成功的用户，要是有多个匹配结果说明你数据库存的数据有问题
        dbadmin 为根据用户名和密码从数据库查询的数据
        * */
        Admin dbAdmin = getOne(wrapper);

        /*1.6 判断匹配结果是否为空
        * 前端传入的数据若和数据库中的数据无法配对上，dbadmin为空，登录失败,抛出异常信息
        * 前端传入的数据若和数据库中的数据配对上，dbadmin非空，登录成功*/
        if (dbAdmin == null){
//            System.out.println("用户名账号或密码错误");
            throw new RuntimeException("管理员名账号或密码错误");
        }else {
//            System.out.println("登录成功");
            return dbAdmin;
        }



    }


}
