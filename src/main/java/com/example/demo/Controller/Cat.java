package com.example.demo.Controller;

import com.example.demo.entity.Result;
import com.example.demo.entity.User;
import com.example.demo.entity.cat;
import com.example.demo.entity.pageFun;
import com.example.demo.entity.search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
public class Cat {
//    Result rel = new Result();
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private Result rel;


    @PostMapping("/importExcelbb")
    public Result importExcel( @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        System.out.println("进入导入excel函数-22--"+request.getParameter("owner"));
        int own = Integer.parseInt(request.getParameter("owner"));
        // Result rel = new Result();
        if (file.isEmpty()) {
            rel.setCode(601);
            rel.setMsg("文件为空");
            return rel;
        }
        try {
            //实体的集合，把csv中的列装在list里。
            List<cat> list = new ArrayList<>();
            //获取文件名
            String fileName = file.getOriginalFilename();
            //获取文件后缀
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            System.out.println("suffix:"+suffix);
            if (suffix.equals(".csv")) {
                InputStream inputStream = file.getInputStream();
                //编码格式要是用utf8 | GBK
                InputStreamReader is = new InputStreamReader(inputStream, "GBK");
                BufferedReader reader=new BufferedReader(is);
                reader.readLine();  //第一行信息，为标题信息，不用,如果需要，注释掉
                String line = null;
                while((line=reader.readLine())!=null){
                    //实体类
                    cat entity = new cat();
                    //名称
                    String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                    if (getValue(item, 0)!=null) {
                        //getValue(item, 0)   就是文件中去掉标题行的第一列的数据
//                        entity.setId(Integer.parseInt(getValue(item, 0)));
                    }
                    if (getValue(item, 1)!=null) {
                        entity.setFunctionkindOne(getValue(item, 1));
                    }
                    if (getValue(item, 2)!=null) {
                        entity.setFunctionkindSon(getValue(item, 2));
                    }
                    if (getValue(item, 3)!=null) {
                        entity.setCatGroupNum(getValue(item, 3));
                    }
                    if (getValue(item, 4)!=null) {
                        entity.setCatNum(getValue(item, 4));
                    }
                    if (getValue(item, 5)!=null) {
                        entity.setErrorLevel(getValue(item, 5));
                    }
                    if (getValue(item, 6)!=null) {
                        entity.setErrorMsg(getValue(item, 6));
                    }
                    if (getValue(item, 7)!=null) {
                        entity.setDesc(getValue(item, 7));
                    }
                    if (getValue(item, 8)!=null) {
                        entity.setResult(getValue(item, 8));
                    }

//                    entity.setUnFinish(entity.getTotal() - entity.getFinish());
//                    entity.setFinishRatio((float)Integer.parseInt(getValue(item, 3)) / Integer.parseInt(getValue(item, 2)));
//                    entity.setOwner(own);

                    list.add(entity);
                }
            }

            list.forEach((one) -> {
                System.out.println("one:"+one.toString());
                String sql = "insert into cat(functionkindOne,functionkindSon,catGroupNum,catNum,errorLevel,errorMsg,moreDesc,theResult) values(?,?,?,?,?,?,?,?)";
                int updateRel = jdbcTemplate.update(sql,
                        one.getFunctionkindOne(),one.getFunctionkindSon(),
                        one.getCatGroupNum(),one.getCatNum(),one.getErrorLevel(),one.getErrorMsg(),
                        one.getDesc(),one.getResult());
                System.out.println("update-rel"+updateRel);
            });
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
    public static String getValue(String[] item,int index){
        if(item.length > index){
            String value = item[index];
            return value;
        }
        return "";
    }




    @RequestMapping("/getCat")
    public Result hello(@RequestBody pageFun pageobj){
        System.out.println("list:"+pageobj.getSkipNum()+","+pageobj.getPageSize());
        int lessNum = (pageobj.getSkipNum()-1)*pageobj.getPageSize();
        int lastNum = pageobj.getPageSize();

        String sql = "SELECT * FROM cat LIMIT ?,?";
        String sql2 = "SELECT count(*) FROM cat";
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

    @RequestMapping("/search")
    public Result search(@RequestBody search searchOne){
        System.out.println("list:"+searchOne.getName()+","+searchOne.getValue());
        int lessNum = (searchOne.getSkipNum()-1)*searchOne.getPageSize();
        int lastNum = searchOne.getPageSize();
        String name = searchOne.getName();
        String value = searchOne.getValue();
        List<Map<String, Object>> list;
        int count;
        String sql,sql2;

        sql = "SELECT * FROM cat LIMIT ?,?";
        sql2 = "SELECT count(*) FROM cat";
        list= jdbcTemplate.queryForList(sql,lessNum,lastNum);
        count= jdbcTemplate.queryForObject(sql2, Integer.class);

        if(name !="" && value!=""){
            sql = "SELECT * FROM cat where "+ name +"= ? LIMIT ?,?";
            sql2 = "SELECT count(*) FROM cat where "+ name +"= ?";
            list= jdbcTemplate.queryForList(sql,searchOne.getValue(), lessNum, lastNum);
            count= jdbcTemplate.queryForObject(sql2, Integer.class, value);
        }

        System.out.println("list2:"+lessNum+","+lastNum);
        //写法很多种
        //下面列举两种写法，都可以实现
        //List<User> list= jdbcTemplate.query(sql,new Object[]{userName}, new BeanPropertyRowMapper(User.class));


        System.out.println("list:"+list.size());
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

}
