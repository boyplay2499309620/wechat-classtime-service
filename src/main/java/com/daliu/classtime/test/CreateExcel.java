package com.daliu.classtime.test;

import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.Number;
import jxl.write.DateTime;

public class CreateExcel {
	
	@Value("${com.classtime.path}")
	String path;
	
	
	public void create() {
        try {
            // 打开文件
        	File file=new File("C:/xls/test.xls"); 
        	if(file.exists())System.out.println(true);
        	else {
				System.out.println(false); 
			}
        	
        	WritableWorkbook book = Workbook.createWorkbook(file); 
            // 生成名为“sheet1”的工作表，参数0表示这是第一页
             WritableSheet sheet = book.createSheet("sheet1", 0);
            // 在Label对象的构造子中指名单元格位置是第一列第一行(0,0),单元格内容为string
            Label label = new Label(0, 0, "str");
            // 将定义好的单元格添加到工作表中
            sheet.addCell(label);
            // 生成一个保存数字的单元格,单元格位置是第二列，第一行，单元格的内容为1234.5
            Number number = new Number(1, 0, 1234.5);
            sheet.addCell(number);
            // 生成一个保存日期的单元格，单元格位置是第三列，第一行，单元格的内容为当前日期
            DateTime dtime = new DateTime(2, 0, new Date());
            sheet.addCell(dtime);
            // 写入数据并关闭文件
            book.write();
            book.close();
            System.out.println("邮件生成完毕！");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
