package com.daliu.classtime.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daliu.classtime.dao.RoomDao;
import com.daliu.classtime.dao.RoomPeopleDao;
import com.daliu.classtime.domain.RoomDoMain;
import com.daliu.classtime.domain.RoomPeopleDoMain;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

@Service
public class GenerateXls {
	/**
	 * 生成xls文件
	 */
	
	
	@Autowired
	private RoomPeopleDao roomPeopleDao;
	
	@Autowired
	private RoomDao roomDao;
	
	public String CreateXls(Integer roomId) throws Exception{
		//生成xls文件，返回文件地址
		String path="C:/xls/"+roomId+".xls";
		try {
			
			List<RoomPeopleDoMain> list=roomPeopleDao.findByRoomId(roomId);
			//System.out.println(list);
			RoomDoMain roomDoMain=roomDao.findByRoomId(roomId);
			
			File file=new File(path);
			if(file.exists()) file.delete();
			
			WritableWorkbook book = Workbook.createWorkbook(file);
			 // 生成名为“sheet1”的工作表，参数0表示这是第一页
            WritableSheet sheet = book.createSheet("您的记录", 0);
            for (int i = 0; i < 10; i++) {
            	sheet.setColumnView(i, 15);
			}
            sheet.setColumnView(6, 25);
			
            //new Label(0, 0,"姓名");代表第0列第0行
            //添加列头信息
			sheet.addCell(new Label(0, 0,"姓名"));
			sheet.addCell(new Label(1, 0,"学号"));
			sheet.addCell(new Label(2, 0,"计时总数"));
			//空一列
			sheet.addCell(new Label(4, 0,"房间号"));
			sheet.addCell(new Label(4, 1,String.valueOf(roomDoMain.getRoomNumber())));
			
			sheet.addCell(new Label(5, 0,"备注信息"));
			sheet.addCell(new Label(5, 1,roomDoMain.getRemark()));
			
			sheet.addCell(new Label(6, 0,"创建时间"));
			sheet.addCell(new Label(6, 1,roomDoMain.getCreateTime()));
			
			//中间空一行，从第三行开始添加记录信息
            int i=2;
			for (RoomPeopleDoMain room : list) {
				sheet.addCell(new Label(0, i, room.getName()));
				
				if(room.getSchoolId()==null || "".equals(room.getSchoolId()))
					room.setSchoolId("未绑定学号");
				sheet.addCell(new Label(1, i, room.getSchoolId()));
				
				String time="";
				if(room.getTimes()<60) time="小于一分钟";
				else time=room.getTimes()/60+"分钟";
				sheet.addCell(new Label(2, i,time));
				
				i++;
			}
			
			// 写入数据并关闭文件
            book.write();
            book.close();
            //System.out.println("邮件生成完毕");
		} catch (Exception e) {
			throw e;
		}
		return path;
	}

}
