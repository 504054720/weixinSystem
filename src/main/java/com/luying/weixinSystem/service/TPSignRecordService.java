package com.luying.weixinSystem.service;

import com.luying.weixinSystem.mapper.TPSignRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2019/4/23.
 */
@Service
public class TPSignRecordService {
    @Autowired
    private TPSignRecordMapper tPSignRecordMapper;

  public   List<Map<String,Object>> querySign(Map p){
      return tPSignRecordMapper.querySign(p);
  };

    public int insertSign(Map p){
        return tPSignRecordMapper.insertSign(p);
    };

   public  int updateSign(Map p){
       return tPSignRecordMapper.updateSign(p);
   };

        public  Map<String,Object> querySignById(Map p){
            return tPSignRecordMapper.querySignById(p);
        }

}
