package com.daliu.classtime.test;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daliu.classtime.service.UserServiceImp;
import com.daliu.classtime.domain.UserDoMain;

@RestController
@RequestMapping("/mydb")
public class DbTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private UserServiceImp userServiceImp;
    
    @Autowired 
    private testEntity testEntity;
    
    @Autowired
    private testReposity testReposity;
    
    @RequestMapping("/test")
    public void testEntity(@RequestParam String id,@RequestParam String name){
    	testEntity.setIds(id);
    	testEntity.setName(name);
    	testReposity.save(testEntity);
    
    }
    
    //  http://localhost:8080/mydb/test?id=111&name=111
    
    @RequestMapping("/getAllUesr")
    public List<UserDoMain> getAllUser(){
    	//通过springdata查询数据库
    	List<UserDoMain> list=(List<UserDoMain>) userServiceImp.queryAllUser();
    	System.out.println("jinrufangfa");
    	System.out.println(list);
    	return (List<UserDoMain>) userServiceImp.queryAllUser();
    }
  
    @RequestMapping("/getUsers")
    public List<Map<String, Object>> getDbType(){
    	//通过springboot的jdbcTemplate查询数据库
        String sql = "select * from user";
        List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql);
        for (Map<String, Object> map : list) {
            Set<Entry<String, Object>> entries = map.entrySet( );
                if(entries != null) {
                    Iterator<Entry<String, Object>> iterator = entries.iterator( );
                    while(iterator.hasNext( )) {
                    Entry<String, Object> entry =(Entry<String, Object>) iterator.next( );
                    Object key = entry.getKey( );
                    Object value = entry.getValue();
                    System.out.println(key+":"+value);
                }
            }
        }
        return list;
    }
    
    @RequestMapping("/user/{id}")
    public Map<String,Object> getUser(@PathVariable String id){
        Map<String,Object> map = null;
        
        List<Map<String, Object>> list = getDbType();
        
        for (Map<String, Object> dbmap : list) {
            
            Set<String> set = dbmap.keySet();
            
            for (String key : set) {
                if(key.equals("id")){    
                    if(dbmap.get(key).equals(id)){
                        map = dbmap;
                    }
                }
            }
        }
        
        if(map==null)
            map = list.get(0);
        return map;
    }
    
}
