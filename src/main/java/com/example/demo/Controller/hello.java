package com.example.demo.Controller;

import com.example.demo.entity.Result;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.pageFun;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.apache.catalina.connector.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// excel导入功能依赖
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;

@ResponseBody
@Controller
public class hello {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/aaa")
    public String aaa(){
        return "aaa";
    }

    @PostMapping("/addUser")
    public Result addUser(@RequestBody User user){
        String sql = "insert into users(username,password,myRoleId) values(?,?,?)";
        int rel = jdbcTemplate.update(sql,user.getUsername(),user.getPassword(),user.getRole());
        System.out.println("update-rel"+rel);
        Result result = new Result();
        if(rel==1){
            String sqla = "select * from users";
            List<Map<String, Object>> list =jdbcTemplate.queryForList(sqla);
            result.setCode(200);
            result.setData(list);
            result.setMsg("更新成功");
        }else{
            result.setCode(500);
            result.setMsg("更新失败");
        }
        return result;
    }

    @PostMapping("/deleteUser")
    public Result deleteUser(@RequestBody User user){
        String sql = "delete from users where id=?";
        int rel = jdbcTemplate.update(sql,user.getId());
        System.out.println("update-rel"+rel);
        Result result = new Result();
        if(rel==1){
            result.setCode(200);
            result.setMsg("更新成功");
        }else{
            result.setCode(500);
            result.setMsg("更新失败");
        }
        return result;
    }

    @PostMapping("/updateUser")
    public Result updateUser(@RequestBody User user){
        System.out.println(user.print());
        String sql = "update users set username=?, password=?,myRoleId=? where id=?";
        int rel = jdbcTemplate.update(sql,user.getUsername(),user.getPassword(),user.getRole(),user.getId());
        System.out.println("update-rel"+rel);
        Result result = new Result();
        if(rel==1){
            result.setCode(200);
            result.setMsg("更新成功");
        }else{
            result.setCode(500);
            result.setMsg("更新失败");
        }
        return result;
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user){
//        String sql = "SELECT * FROM students ? ";
        Result rel = new Result();
        try {
            System.out.println("one user:"+user.print());
            String sql = "SELECT * FROM users WHERE username=? and password=? and myRoleId=?";
            List<Map<String, Object>> list= jdbcTemplate.queryForList(sql,user.getUsername(), user.getPassword(), user.getRole());
            System.out.println("one person:"+list);
            if(list.size()==0){
                rel.setCode(500);
                rel.setMsg("登录失败");
            }else {
                rel.setCode(200);
                rel.setMsg("登录成功");
                rel.setData(list);
            }
            return rel;
        }catch ( Exception err){
            System.out.println("err:"+err);
            rel.setCode(500);
            rel.setMsg("登录失败");
            return rel;
        }

    }

    @RequestMapping("/hello")
    public Result hello(@RequestBody pageFun pageobj){
        System.out.println("list:"+pageobj.getSkipNum()+","+pageobj.getPageSize());
        int lessNum = (pageobj.getSkipNum()-1)*pageobj.getPageSize();
        int lastNum = pageobj.getSkipNum() * pageobj.getPageSize();

        String sql = "SELECT * FROM users LIMIT ?,?";
        String sql2 = "SELECT count(*) FROM users;";
        System.out.println("list2:"+lessNum+","+lastNum);
        //写法很多种
        //下面列举两种写法，都可以实现
        //List<User> list= jdbcTemplate.query(sql,new Object[]{userName}, new BeanPropertyRowMapper(User.class));

        List<Map<String, Object>> list= jdbcTemplate.queryForList(sql,lessNum,lastNum);
        int count= jdbcTemplate.queryForObject(sql2, Integer.class);
        System.out.println("list:"+list.size()+","+count);
        // 返回结果集
        Result rel = new Result();
        rel.setMsg("查询成功");
        rel.setCode(200);
        rel.setTotal(count);
        rel.setData(list);
        // mysql分页查询： https://www.iteye.com/blog/qimo601-1634748
        // MySQL中count函数查总条数：https://www.cnblogs.com/rocky-fang/p/5660890.html
        // 慕课jdbcTemplate 链接：https://www.imooc.com/article/46879
        return rel;
    }



    @Controller
    public class ExcelImportController {
        @GetMapping("/toHtml")
        String test(HttpServletRequest request) {
            return "excelImport";
        }

        //处理文件上传
        @ResponseBody//返回json数据
        @RequestMapping(value = "/excelImport", method = RequestMethod.POST)
        public Result uploadImg(@RequestParam("file") MultipartFile file,
                                HttpServletRequest request) {
            System.out.println("进入导入excel函数---");
            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();
            Result rel = new Result();
            if (file.isEmpty()) {
                rel.setCode(601);
                rel.setMsg("文件为空");
            }
            try {

//               关键问题的原因还是excel2003和excel2007版本的问题
//                3、解决办法
//                （1）判断文件后缀名是xls，还是xlsx
//                （2）如果是xls，使用HSSFWorkbook；如果是xlsx，使用XSSFWorkbook
                //根据路径获取这个操作excel的实例 XSSFWorkbook
                HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
//                XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
                //根据页面index 获取sheet页
                HSSFSheet sheet = wb.getSheetAt(0);

                //实体类集合
                List<User> importDatas = new ArrayList<>();
                HSSFRow row = null;
                //循环sesheet页中数据从第二行开始，第一行是标题

                for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    //获取每一行数据
                    row = sheet.getRow(i);
                    User data = new User();
//                    data.setId(Integer.valueOf((int) row.getCell(0).getNumericCellValue()));
                    data.setId((int) Float.parseFloat(String.valueOf(row.getCell(0).getNumericCellValue())));
                    data.setUsername(String.valueOf(row.getCell(1).getStringCellValue()));
                    data.setPassword(row.getCell(2).getStringCellValue());
                    data.setSex(row.getCell(3).getStringCellValue());
                    data.setRole(Integer.parseInt(row.getCell(4).getStringCellValue()));
                    importDatas.add(data);

//                  data.setAge(Integer.valueOf((int) row.getCell(3).getNumericCellValue()));
//                  SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
//                  data.setCreateDate(df.parse(df.format(HSSFDateUtil.getJavaDate(row.getCell(2).getNumericCellValue()))));
                }

                //循环展示导入的数据，实际应用中应该校验并存入数据库
                for (User imdata : importDatas) {
//                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                    System.out.println("ID:"+imdata.getId()+" name:"+imdata.getUsername()
                            +" password:"+imdata.getPassword()+" sex:"+imdata.getSex()
                            +" role:"+imdata.getRole());
                }
            } catch (Exception e) {
                rel.setCode(501);
                rel.setMsg("服务器发生错误");
                e.printStackTrace();
                return rel;
            }
            rel.setCode(200);
            rel.setMsg("导入成功");
            return rel;
        }
    }


    //    保存角色
    @PostMapping("/saveRole")
    public Result saveRole(@RequestBody Role role){
        //        String sql = "SELECT * FROM students ? ";
        Result rel = new Result();
        try {
            System.out.println("one role:"+role.toString());
            String sql = "insert into role(name,roleRight) values(?,?)";
            int updateRel = jdbcTemplate.update(sql,role.getName(),role.getRoleRight());
            System.out.println("update-rel"+updateRel);
            if(updateRel==0){
                rel.setCode(500);
                rel.setMsg("保存失败");
            }else {
                rel.setCode(200);
                rel.setMsg("保存成功");
            }
            return rel;
        }catch ( Exception err){
            System.out.println("err:"+err);
            rel.setCode(500);
            rel.setMsg("保存失败");
            return rel;
        }

    }

    //    保存角色
    @PostMapping("updateRole")
    public Result updateRole(@RequestBody Role role){
        //        String sql = "SELECT * FROM students ? ";
        Result rel = new Result();
        try {
            System.out.println("one role:"+role.toString());
            String sql = "update role set name=?, roleRight=? where id=?";
            int count = jdbcTemplate.update(sql,role.getName(),role.getRoleRight(),role.getId());
            System.out.println("update-rel"+count);
            if(count==0){
                rel.setCode(500);
                rel.setMsg("保存失败");
            }else {
                rel.setCode(200);
                rel.setMsg("保存成功");
            }
            return rel;
        }catch ( Exception err){
            System.out.println("err:"+err);
            rel.setCode(500);
            rel.setMsg("保存失败");
            return rel;
        }

    }

    //    删除角色
    @PostMapping("/delRoleById")
    public Result delRoleById(@RequestBody Role role){
        //        String sql = "SELECT * FROM students ? ";
//        int id = Integer.parseInt(request.getParameter("id"));
        Result rel = new Result();
        try {
            System.out.println("one role id:"+ role.getId());
            String sql = "delete from role where id=?";
            int updateRel = jdbcTemplate.update(sql,role.getId());
            System.out.println("del-rel"+updateRel);
            if(updateRel==0){
                rel.setCode(500);
                rel.setMsg("删除失败");
            }else {
                rel.setCode(200);
                rel.setMsg("删除成功");
            }
            return rel;
        }catch ( Exception err){
            System.out.println("err:"+err);
            rel.setCode(500);
            rel.setMsg("保存失败");
            return rel;
        }

    }

    // 获取所有角色
    @RequestMapping("/getAllRole")
    public Result getAllRole(){
//        @RequestBody pageFun pageobj
//        System.out.println("list:"+pageobj.getSkipNum()+","+pageobj.getPageSize());
//        int lessNum = (pageobj.getSkipNum()-1)*pageobj.getPageSize();
//        int lastNum = pageobj.getPageSize();

//        String sql = "SELECT * FROM cat LIMIT ?,?";
//        String sql2 = "SELECT count(*) FROM cat";
        String sql = "SELECT * FROM role";
        List<Map<String, Object>> list= jdbcTemplate.queryForList(sql);
//        System.out.println("list2:"+lessNum+","+lastNum);
        //写法很多种
        //下面列举两种写法，都可以实现
        //List<User> list= jdbcTemplate.query(sql,new Object[]{userName}, new BeanPropertyRowMapper(User.class));

//        List<Map<String, Object>> list= jdbcTemplate.queryForList(sql,lessNum,lastNum);
//        int count= jdbcTemplate.queryForObject(sql2, Integer.class);
//        System.out.println("list:"+list.size()+","+count);
        // 返回结果集
        Result rel = new Result();
        rel.setMsg("查询成功");
        rel.setCode(200);
//        rel.setTotal(count);
        rel.setData(list);
        // mysql分页查询： https://www.iteye.com/blog/qimo601-1634748
        // MySQL中count函数查总条数：https://www.cnblogs.com/rocky-fang/p/5660890.html
        // 慕课jdbcTemplate 链接：https://www.imooc.com/article/46879
        return rel;
    }

    // 获取所有角色id和name
    @RequestMapping("/getAllRoleName")
    public Result getAllRoleName(){
        String sql = "SELECT id,name FROM role";
        List<Map<String, Object>> list= jdbcTemplate.queryForList(sql);
        // 返回结果集
        Result rel = new Result();
        rel.setMsg("查询成功");
        rel.setCode(200);
        rel.setData(list);
        return rel;
    }

    // 获取所有权限，根绝角色id
    @PostMapping("/getRightByRoleId")
    public Result getRightByRoleId(@RequestBody User user){
        String sql = "SELECT * FROM role where id=?";
        System.out.println("user.getRole():"+user.getRole());
        List<Map<String, Object>> list= jdbcTemplate.queryForList(sql, user.getRole());
        // 返回结果集
        Result rel = new Result();
        rel.setMsg("查询成功");
        rel.setCode(200);
        rel.setData(list);
        return rel;
    }
}

