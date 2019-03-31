/**
 * 
 */
package com.daliu.classtime.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**  
* @Title: ImageControl.java
* @Package:com.daliu.classtime.control
* @Description:(所有图片相关的所有请求)
* @author:刘严岩 
* @date:2019年3月30日
*/
@RestController
@RequestMapping("classtime/image")
@Api("所有图片相关的所有请求")
public class ImageControl {
	
	@Value("${com.classtime.imagePath}")
	String imagePath;
	
	@RequestMapping(value="/downloadImage/{number}",method=RequestMethod.GET,produces = MediaType.IMAGE_JPEG_VALUE)
	@ApiOperation("下载图片")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="number",value="图片信息",required=true )
	})
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable("number") String number)
            throws IOException {
		//通过修过tcp的http协议头，来达到下载图片的目的
        String filePath = imagePath +number+ ".jpg";
        FileSystemResource file = new FileSystemResource(filePath);
        if(!file.exists()){
        	System.out.println("unKnow file path: "+filePath);
        	return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getFilename()));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
 
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file.getInputStream()));
    }
	
	@RequestMapping(value="/getImage/{number}",produces = MediaType.IMAGE_JPEG_VALUE,method=RequestMethod.GET)
	@ApiOperation("返回图片")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="number",value="图片信息",required=true )
	})
    @ResponseBody
    public byte[] getImage(@PathVariable("number") String number) throws IOException {
		//把图片以字节流的形式返回
		try {
			String filePath=imagePath+number+".jpg";
			File file = new File(filePath);
			if(!file.exists()){
				System.out.println("unKnow file path: "+filePath);
				return null;
			}else{
				//System.out.println("ok");
				FileInputStream inputStream = new FileInputStream(file);
		        byte[] bytes = new byte[inputStream.available()];
		        inputStream.read(bytes, 0, inputStream.available());
		        inputStream.close();
		        //System.out.println("return");
		        return bytes;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		
        

	}

}
