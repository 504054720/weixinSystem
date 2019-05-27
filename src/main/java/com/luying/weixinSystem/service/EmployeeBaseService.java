package com.luying.weixinSystem.service;

import com.luying.weixinSystem.mapper.EmployeeBaseMapper;
import com.luying.weixinSystem.mapper.WxOpenMapper;
import com.luying.weixinSystem.util.IdGernerator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeBaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeBaseService.class);
    @Autowired
    private EmployeeBaseMapper employeeBaseMapper;
    @Autowired
    private WxOpenMapper wxOpenMapper;

    public List<Map<String,Object>> queryAll() throws Exception{
        Map<String,String> userMap = (Map<String, String>) SecurityUtils.getSubject().getSession().getAttribute("USER");
        Map<String,String> paraMap = new HashMap<String, String>();
        paraMap.put("department",userMap.get("departmentId").toString());
        return employeeBaseMapper.queryAll(paraMap) ;
    }

    public Map<String,String> query(Map map) throws Exception{
        return employeeBaseMapper.query(map) ;
    }
    public String insert(MultipartFile file){
        String result = "0";
        try {
            Workbook workbook = null;
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            if (".xlsx".equals(suffix)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            }
            if (".xls".equals(suffix)) {
                workbook = new HSSFWorkbook(file.getInputStream());
            }
            Map<String,String> userMap = (Map<String, String>) SecurityUtils.getSubject().getSession().getAttribute("USER");
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1;i<=sheet.getLastRowNum();i++) {
                Row row = sheet.getRow(i);
                Map<String,String> map = new HashMap<>();
                map.put("id", IdGernerator.next());
                map.put("name",row.getCell(0).getStringCellValue());
                map.put("idCard",row.getCell(1).getStringCellValue());
                map.put("mobile",row.getCell(2).getStringCellValue());
                map.put("remark",row.getCell(3).getStringCellValue());
                map.put("isMainCard","1");
                map.put("department", userMap.get("departmentId").toString());
                map.put("role","0");

                if (employeeBaseMapper.queryForValidate(map) == 0) {
                    employeeBaseMapper.insert(map);
                }
            }

        } catch (Exception e) {
            LOGGER.info("employeeBaseService_insert_exception:" + e);
            result = "-1";
        }
        return result;
    }

    @Transactional
    public void midifyMobile(Map map) throws Exception{
        wxOpenMapper.update(map);
        employeeBaseMapper.updateByMobile(map);
    }

    public Map<String,String> queryEmployeBaseInfo(Map map) throws Exception{
        return employeeBaseMapper.queryBaseInfo(map);
    }

    public String addOne(Map map) throws Exception{
        if (employeeBaseMapper.queryForValidate(map) == 0) {
            Map<String,String> userMap = (Map<String, String>) SecurityUtils.getSubject().getSession().getAttribute("USER");
            map.put("department",userMap.get("departmentId").toString());
            map.put("id",IdGernerator.next());
            map.put("bindingState","0");
            employeeBaseMapper.insert(map);
            return "0";
        }else {
            return "1";
        }
    }

    public Map<String,String> queryOne(Map map) throws Exception{
        return employeeBaseMapper.queryOne(map);
    }
    public void updateOne(Map map) throws Exception{
        employeeBaseMapper.updateOne(map);
    }
    public void del(Map map) throws Exception{
        if (map.get("checkedIds") == null) {
            return;
        }
        List<String> ids = (List<String>) map.get("checkedIds");
        for (String id : ids) {
            employeeBaseMapper.del(id);
        }
    }

    public List<Map<String,String>> queryForList(Map map) throws Exception{
        Map<String,String> userMap = (Map<String, String>) SecurityUtils.getSubject().getSession().getAttribute("USER");
        map.put("isMainCard","0");
        map.put("departmentId",userMap.get("departmentId").toString());
        return employeeBaseMapper.queryForList(map);
    }

}
