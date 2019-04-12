package com.luying.weixinSystem.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luying.weixinSystem.util.WeixinClient;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Service
public class WeixinService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeixinService.class);
    //通讯录与EXCEL匹配的对应的列（EXCEL）
    private static final int CELLNUM = 1;
    //通讯录与EXCEL匹配的对应的key(通讯录)
    private static final String MAPKEY = "mobile";
    @Value("${weixin.corpid}")
    public String corpid;
    @Value("${weixin.address.corpsecret}")
    public String addressCorpsecret;
    @Value("${weixin.myapp.corpsecret}")
    public String myAppCorpsecret;
    @Value("${weixin.myapp.agentid}")
    public int myappAgentid;
    @Autowired
    WeixinClient weixinClient;

    public String sendMessage(MultipartFile file,String[] checkedInfos){
        String result = "0";
        int errCode = 0;
        Map<String,Object> resultMap = new HashMap<String,Object>();

        try {
            String fileName = file.getOriginalFilename();
            Workbook workbook = null;
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            if(".xlsx".equals(suffix)){
                workbook = new XSSFWorkbook(file.getInputStream());
            }
            if(".xls".equals(suffix)){
                workbook = new HSSFWorkbook(file.getInputStream());
            }

            //File newFile = new File(System.currentTimeMillis()+fileName);
            //file.transferTo(newFile);

            //获取通讯录token
            String addressToken = weixinClient.getToken(corpid,addressCorpsecret);
            //获取通讯录列表
            List<Map<String,String>> addressList = new ArrayList<Map<String, String>>();
            if (checkedInfos == null) {
                addressList = weixinClient.getAddressList(addressToken);
            } else {

                for (String oneInfo : checkedInfos) {
                    Map<String,String> oneMap = new HashMap<String, String>();
                    String[] oneInfoArray = oneInfo.split("\\|");
                    oneMap.put("mobile",oneInfoArray[0]);
                    oneMap.put("name",oneInfoArray[1]);
                    oneMap.put("userid",oneInfoArray[2]);
                    addressList.add(oneMap);
                }
            }

            if ( addressList == null ) {
                errCode =  -1;
                resultMap.put("errCode",errCode);
                return JSON.toJSONString(resultMap);
            }
            LOGGER.info("通讯录总数：" + addressList.size());
            //获取发送消息token
            String sendMessageToken = weixinClient.getToken(corpid,myAppCorpsecret);

            Map<String,String> errMap = new HashMap<String,String>();
            for(Map user : addressList){
                LOGGER.info("userInfo：" + user.toString());
                LOGGER.info("员工姓名：" + user.get("name") + " 员工ID：" + user.get("userid"));
                //获取每个员工的Excel
                File sendFile = this.creadExcelFile(workbook,user);
                //上传每个员工的Excel获取media_id
                JSONObject resultUp = weixinClient.uploadTempFile(sendMessageToken,sendFile);
                if("0".equals(resultUp.get("errcode").toString())){
                    Map<String,Object> contentMap = new HashMap<String, Object>();
                    contentMap.put("touser", user.get("userid"));
                    contentMap.put("agentid", myappAgentid);
                    //contentMap.put("msgtype", "text");
                    contentMap.put("msgtype", "file");
                    Map<String,String> cont = new HashMap<String, String>();
                    cont.put("media_id",resultUp.get("media_id").toString());
                    contentMap.put("file", cont);
                    //发送员工工资条Excel
                    //result = weixinClient.sendMessage(sendMessageToken,contentMap);
                    if(!"0".equals(result)){
                        //异常数量
                        errCode++;
                        //异常信息
                        errMap.put(user.get("name").toString(),result);
                        LOGGER.info("发送失败信息：【姓名：" + user.get("name").toString() + " 错误码：" + result + "】");
                    }
                    //清理文件
                    sendFile.delete();
                }
            }
            resultMap.put("errCode",Integer.toString(errCode));
            resultMap.put("totalCount",addressList.size());
            resultMap.put("successCount",addressList.size() - errCode);
            resultMap.put("message",errMap);
            LOGGER.info("发送工资条成功！");
        } catch (Exception e) {
            LOGGER.info("发送工资条异常："+ e);
            resultMap.put("errCode","-999");
        }
        return JSON.toJSONString(resultMap);
    }

    /***
     * 获取员工信息（手机号）与内容的行号关系MAP
     * @param workbook
     * @return
     */
    public Map<String,List<Integer>> getRelationMap(Workbook workbook){
        Sheet sheet = workbook.getSheetAt(0);

        Map<String,List<Integer>> relationMap = new HashMap<String,List<Integer>>();
        for (Row row : sheet){
            String rowValue = row.getCell(CELLNUM).getStringCellValue();
            List<Integer> rowList = new ArrayList<>();
            if(relationMap.get(rowValue) != null){
                rowList = relationMap.get(rowValue);
            }
            rowList.add(row.getRowNum());
            relationMap.put(rowValue,rowList);
        }
        return relationMap;
    }

    /***
     * 创建每个员工的工资条Excel
     * @param workbook
     * @param user
     * @return
     * @throws IOException
     */
    public File creadExcelFile(Workbook workbook,Map user) throws IOException {
        //获取员工姓名与Excel中的位置关系
        Map<String,List<Integer>> relationMap = this.getRelationMap(workbook);
        Sheet sheet = workbook.getSheetAt(0);
        Row headRow = sheet.getRow(0);
        List<Integer> rowList = relationMap.get(user.get(MAPKEY));
        Workbook perWorkbook = new XSSFWorkbook();
        Sheet perSheet = perWorkbook.createSheet();

        //身份证号	银行卡号	银行	开户行 宽度设置
        if(headRow.getLastCellNum() >= 30){
            for(int w = 27;w <=30 ;w++){
                perSheet.setColumnWidth(w,6000);
            }
        }
        Row newHeadRow = perSheet.createRow(0);
        for (int i =0;i<headRow.getLastCellNum();i++){
            newHeadRow.createCell(i).setCellValue(headRow.getCell(i).getStringCellValue());
        }
        if(rowList != null){
            for(int i=1;i<=rowList.size();i++){
                int tmpNum = i - 1;
                Row contentRow = sheet.getRow(rowList.get(tmpNum));
                Row newRow = perSheet.createRow(i);
                for(int j=0;j<contentRow.getLastCellNum();j++){
                    if (contentRow.getCell(j) != null) {
                        if (1 == contentRow.getCell(j).getCellType()) {
                            newRow.createCell(j).setCellValue(contentRow.getCell(j).getStringCellValue());
                        } else {
                            newRow.createCell(j).setCellValue(contentRow.getCell(j).getNumericCellValue());
                        }
                    }
                }
            }
        }

        Calendar calendar = Calendar.getInstance();
        StringBuilder perFileName = new StringBuilder();
       // perFileName.append(user.get("name"))
        perFileName.append(calendar.get(Calendar.YEAR))
                .append(calendar.get(Calendar.MONTH)+1)
                .append(calendar.get(Calendar.DATE))
                .append("_工资条_【测试数据请忽略】.xlsx");
        File perFile = new File(perFileName.toString());
        OutputStream outputStream = new FileOutputStream(perFile);
        perWorkbook.write(outputStream);
        return perFile;
    }

    /***
     * 获取通讯录列表
     * @return
     */
    public String getAddressList(){
        try {
            String addressToken = weixinClient.getToken(corpid,addressCorpsecret);
            return JSON.toJSONString(weixinClient.getAddressList(addressToken));
        }catch (Exception e){
            LOGGER.info("Weixin_getAddressList_exception:" + e);
            return "";
        }

    }
}