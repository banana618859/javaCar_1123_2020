package com.example.demo.Controller;

import com.example.demo.entity.Result;
import com.example.demo.entity.Things;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Controller
@ResponseBody
public class thing {
    Result rel = new Result();
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/importExcel")
    public Result importExcel( @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        System.out.println("进入导入excel函数-22--"+request.getParameter("owner"));
        int own = Integer.parseInt(request.getParameter("owner"));
        Result rel = new Result();
        if (file.isEmpty()) {
            rel.setCode(601);
            rel.setMsg("文件为空");
        }
        try {
            //实体的集合，把csv中的列装在list里。
            List<Things> list = new ArrayList<>();
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
                    Things entity = new Things();
                    //名称
                    String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                    if (getValue(item, 0)!=null) {
                        //getValue(item, 0)   就是文件中去掉标题行的第一列的数据
//                        entity.setId(Integer.parseInt(getValue(item, 0)));
                    }
                    if (getValue(item, 1)!=null) {
                        entity.setName(getValue(item, 1));
                    }
                    if (getValue(item, 2)!=null) {
                        entity.setTotal(Integer.parseInt(getValue(item, 2)));
                    }
                    if (getValue(item, 3)!=null) {
                        entity.setFinish(Integer.parseInt(getValue(item, 3)));
                    }
                    entity.setUnFinish(entity.getTotal() - entity.getFinish());
                    entity.setFinishRatio((float)Integer.parseInt(getValue(item, 3)) / Integer.parseInt(getValue(item, 2)));
                    entity.setOwner(own);
                    list.add(entity);
                }
            }

            list.forEach((one) -> {
                System.out.println("one:"+one.toString());
                String sql = "insert into things(name,total,finish,unFinish,finishRatio,owner) values(?,?,?,?,?,?)";
                int updateRel = jdbcTemplate.update(sql,one.getName(),one.getTotal(),
                        one.getFinish(),one.getUnFinish(),one.getFinishRatio(),one.getOwner());
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
}
