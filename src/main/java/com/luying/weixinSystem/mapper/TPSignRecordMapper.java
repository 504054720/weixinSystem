package com.luying.weixinSystem.mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2019/4/23.
 */
public interface TPSignRecordMapper {

    List<Map<String,Object>> querySign(Map p);

    int insertSign(Map p);

    int updateSign(Map p);

    Map<String,Object> querySignById(Map p);

}
