package com.luying.weixinSystem.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luying.weixinSystem.mapper.EmployeeBaseMapper;
import com.luying.weixinSystem.mapper.PayInfoMapper;
import com.luying.weixinSystem.mapper.SignRuleMapper;
import com.luying.weixinSystem.mapper.TPSignRecordMapper;
import com.luying.weixinSystem.util.EncryptionUtil;
import com.luying.weixinSystem.util.TimeUtils;
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

import java.io.*;
import java.util.*;

@Service
public class WeixinService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeixinService.class);
    //通讯录与EXCEL匹配的对应的列（EXCEL）
    private static final int CELLNUM = 0;
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

    @Value("${weixin.sign.corpsecret}")
    public String signCorpsecret;
    @Value("${weixin.sign.agentid}")
    public int signAgentid;
    @Autowired
    WeixinClient weixinClient;
    @Autowired
    private EmployeeBaseMapper employeeBaseMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private TPSignRecordMapper recordMapper;

    /**
     * 获取 工作台 打卡应用 access_token
     *@retrun
     * */
    public String getSignAppAccessToken(){
        try {
            return  weixinClient.getToken(corpid,signCorpsecret);
        }catch (Exception e){
            LOGGER.info("Weixin_getSignAppAccessToken_exception:" + e);
            return "";
        }

    }

    /**
     * 企业    应用 ticket
     * */
    public String getJsapiTicket(String token){
        try {
            return  weixinClient.getJsapiTicket(token);
        }catch (Exception e){
            LOGGER.info("Weixin_getJsapiTicket_exception:" + e);
            return "";
        }

    }
    /**
     * 企业 ticket
     * */
    public String getQYJsapiTicket(String token){
        try {
            return  weixinClient.getQYJsapiTicket(token);
        }catch (Exception e){
            LOGGER.info("Weixin_getQYJsapiTicket_exception:" + e);
            return "";
        }

    }

    /**
     * sha1的加密算法：
     * */
    public  String SHA1(String str){
        try {
            return  weixinClient.SHA1(str);
        }catch (Exception e){
            LOGGER.info("Weixin_SHA1_exception:" + e);
            return "";
        }
    }
    /**
     * 记录打卡
     *  uid   电话
     *  daytime  记录日期
     *
     *  amtime  上班打卡时间
     *  amstate 上班打卡状态
     *
     *  pmtime  下班打卡时间
     *  pmstate 下班打卡状态
     *
     *   xpoint  打卡地点 经度
     *   ypoint          维度
     *
     * **/
    public  int recoredSign(String deptid ,String ruleid, String dist ,String userid,
        String phone,String xpoint,String ypoint,String scope,String amt,String pmt ){
        Map<String ,Object> map=new HashMap<>();

//        System.out.println("deptid"+deptid+"ruleid"+ruleid+"dist"+dist+"userid="+userid
//        +"phone="+phone+"xpoint="+xpoint+"ypoint"+ypoint);

        String signtime=TimeUtils.date2Stringymd(new Date());
        map.put("id",userid+"_"+signtime );
        Map<String ,Object>  re=recordMapper.querySignById(map);
        int flag= Integer.parseInt(re.get("num").toString());
        Date dt=new Date();

        if(flag>0){
            int lg= Integer.parseInt(re.get("lg").toString());
            if(lg>=3){ // 早退,下班 重复打卡
                return 4;
            }
            map.put("off_work_time",TimeUtils.date2String(dt));
            if(Integer.parseInt(dist)>Integer.parseInt(scope)   || !TimeUtils.beforeDefinedTime(pmt,dt) ){
                if(TimeUtils.beforeDefinedTime(amt,dt)&& !TimeUtils.beforeDefinedTime(pmt,dt)){
                    return 2; // 第1次 打卡之后  且 在 amt 点 到 pmt 点 之间  n 次打卡
                }else if(!TimeUtils.beforeDefinedTime(amt,dt)){ // 第1次 打卡之后  且 在amt点之前
                    return 3;
                }
                map.put("state",",c");//早退
            }else{
                map.put("state",",a");//下班
            }
            return   recordMapper.updateSign(map);
        }
        map.put("userid",userid  );
        map.put("sign_time",signtime);
        map.put("ruleid",ruleid  );
        map.put("phone",phone);
        map.put("department",deptid);
        map.put("xpoint",xpoint);
        map.put("ypoint",ypoint);
        map.put("work_time",TimeUtils.date2String(dt));
        if(Integer.parseInt(dist)>Integer.parseInt(scope)   || TimeUtils.beforeDefinedTime(amt,dt) ){
            map.put("state","b");// 迟到
        }else{
            map.put("state","a");
        }
        return   recordMapper.insertSign(map);

    }

    public String sendMessage(MultipartFile file,String[] checkedInfos,String salaryMonth){
        String result = "0";
        int errCode = 0;
        int sendCount = 0;
        int errCount = 0;
        int importCount = 0;
        Map<String,Object> resultMap = new HashMap<String,Object>();
        Map<String,Object> importResultMap = new HashMap<String,Object>();

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

            //初始化每个身份证在sheet中的行号的对应关系
            Map<String,Integer> relationMap = this.getRelationMap(workbook);

            Map<String,String> errMap = new HashMap<String,String>();
            for(Map user : addressList){
                //LOGGER.info("userInfo：" + user.toString());
                //LOGGER.info("员工姓名：" + user.get("name") + " 员工ID：" + user.get("userid") + "手机号：" + user.get("mobile"));

                if (user.get("mobile") == null || "".equals(user.get("mobile"))) {
                    continue;
                }
                //获取每个员工的Excel
                File sendFile = this.creadExcelFile(workbook,user,relationMap,salaryMonth);
                if (sendFile == null) {
                    continue;
                }
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
                    result = weixinClient.sendMessage(sendMessageToken,contentMap);
                    if(!"0".equals(result)){
                        //异常数量
                        errCount++;
                        //异常信息
                        errMap.put(user.get("name").toString(),result);
                        LOGGER.info("发送失败信息：【姓名：" + user.get("name").toString() + " 错误码：" + result + "】");
                    } else {
                        try {
                            this.importPayInfo(sendFile,salaryMonth);
                            importCount++;
                        } catch (Exception e) {
                            LOGGER.info("import_salary_exception:" + e);
                        }

                    }
                    //清理文件
                    sendFile.delete();
                }
                sendCount++;
            }
            resultMap.put("errCode",Integer.toString(errCode));
            resultMap.put("errCount",Integer.toString(errCount));
            resultMap.put("totalCount",addressList.size());
            resultMap.put("sendCount",sendCount);
            resultMap.put("successCount",sendCount - errCount);
            resultMap.put("message",errMap);

            resultMap.put("importTotalCount",sendCount - errCount);
            resultMap.put("importSucCount",importCount);
            resultMap.put("importExcCount",sendCount - errCount - importCount);
            LOGGER.info("发送工资条成功！");

        } catch (Exception e) {
            e.printStackTrace();
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
    public Map<String,Integer> getRelationMap(Workbook workbook){
        Sheet sheet = workbook.getSheetAt(0);

        //Map<String,List<Integer>> relationMap = new HashMap<String,List<Integer>>();
//        for (Row row : sheet){
//            String rowValue = row.getCell(CELLNUM).getStringCellValue();
//            List<Integer> rowList = new ArrayList<>();
//            if(relationMap.get(rowValue) != null){
//                rowList = relationMap.get(rowValue);
//            }
//            rowList.add(row.getRowNum());
//            relationMap.put(rowValue,rowList);
//        }
        Map<String,Integer> relationMap = new HashMap<String,Integer>();
        for (Row row : sheet ) {
            String rowValue = row.getCell(CELLNUM).getStringCellValue();
            relationMap.put(rowValue,row.getRowNum());
        }
        return relationMap;
    }

    /***
     * 创建每个员工的工资条Excel
     * @param workbook
     * @param user
     * @return
     * @throws java.io.IOException
     */
    public File creadExcelFile(Workbook workbook,Map user,Map<String,Integer> relationMap,String salaryYearMonth) throws Exception {
        //获取员工姓名与Excel中的位置关系
        //Map<String,List<Integer>> relationMap = this.getRelationMap(workbook);
        //每个身份证在sheet中的行号
        //Map<String,Integer> relationMap = this.getRelationMap(workbook);
        Map<String,String> mobileCardMap = employeeBaseMapper.query(user);
        if (mobileCardMap == null) {
            return null;
        }

        List<Integer> rowList = new ArrayList<>();
        String[] idCards = mobileCardMap.get("idCards").split(",");
        for (String idCard :idCards) {
            if (relationMap.get(idCard) == null) {
                continue;
            }
            rowList.add(relationMap.get(idCard));
        }

        // List<Integer> rowList = relationMap.get(user.get(MAPKEY));
        if (rowList.size() == 0) {
            return null;
        }
        Sheet sheet = workbook.getSheetAt(0);
        Row headRow = sheet.getRow(0);

        Workbook perWorkbook = new XSSFWorkbook();
        Sheet perSheet = perWorkbook.createSheet();

        //身份证号	银行卡号	银行	开户行 宽度设置
//        if(headRow.getLastCellNum() >= 30){
//            for(int w = 27;w <=30 ;w++){
//                perSheet.setColumnWidth(w,6000);
//            }
//        }
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

        StringBuilder perFileName = new StringBuilder(salaryYearMonth);
        //perFileName.append(user.get("name"))
        perFileName.append("_工资条.xlsx");
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
            List<Map<String,String>> result = weixinClient.getAddressList(addressToken);
            LOGGER.info("getAddressList_通讯录总数：" + result.size());
            return JSON.toJSONString(result);
        }catch (Exception e){
            LOGGER.info("Weixin_getAddressList_exception:" + e);
            return "";
        }
    }
    /**
     * 获取 access_token
     *@retrun
     * */
    public String getAccessToken(){
        try {
            return  weixinClient.getToken(corpid,myAppCorpsecret);
        }catch (Exception e){
            LOGGER.info("Weixin_getAccessToken_exception:" + e);
            return "";
        }

    }
    /**
     *根据code获取成员信息
     * @param a
     * @param b
     * */
    public  String   getUserIdByCode(String a,String b){
        try {
            return  weixinClient.getUIdByCode(a, b);
        }catch (Exception e){
            LOGGER.info("Weixin_getUserIdByCode_exception:" + e);
            return "";
        }
    }

    /***
     * 根据微信端唯一code获取对应的用户信息
     * @param code
     * @return
     * @throws Exception
     */
    public String getUserInfoByCode(String code) throws Exception{
        String addressToken = weixinClient.getToken(corpid,signCorpsecret);
        if (addressToken == null) {
            return "";
        }
        String userId = (String) JSON.parseObject(weixinClient.getUIdByCode(addressToken,code)).get("UserId");
        if (userId == null || "".equals(userId)) {
            return  "";
        }
        return weixinClient.getUserInfo(addressToken,userId);
    }

    /**
     * 导入工资条信息到数据表
     * @param file
     * @param salaryMonth
     * @return
     */
    public void importPayInfo(File file,String salaryMonth) throws Exception{
        Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
        Map<String,Object> resultMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1;i<=sheet.getLastRowNum();i++) {
                Row row = sheet.getRow(i);
                Map<String,String> map = new HashMap<>();
                map.put("idCard", row.getCell(0).getStringCellValue());
                map.put("name",row.getCell(1).getStringCellValue());
                map.put("department",row.getCell(2).getStringCellValue());
                map.put("level",EncryptionUtil.encode(row.getCell(3).getStringCellValue()));
                map.put("basePay", EncryptionUtil.encode(row.getCell(4).getStringCellValue()));
                map.put("benefitPay",EncryptionUtil.encode(row.getCell(5).getStringCellValue()));
                map.put("subsidyPay",EncryptionUtil.encode(row.getCell(6).getStringCellValue()));
                map.put("bonusPay",EncryptionUtil.encode(row.getCell(7).getStringCellValue()));
                map.put("upPay",EncryptionUtil.encode(row.getCell(8).getStringCellValue()));
                map.put("dayWorkOver",EncryptionUtil.encode(row.getCell(9).getStringCellValue()));
                map.put("weekdayWorkOver",EncryptionUtil.encode(row.getCell(10).getStringCellValue()));
                map.put("itemBonusPay",EncryptionUtil.encode(row.getCell(11).getStringCellValue()));
                map.put("kpiBonusPay",EncryptionUtil.encode(row.getCell(12).getStringCellValue()));
                map.put("telephonePay",EncryptionUtil.encode(row.getCell(13).getStringCellValue()));
                map.put("attendanceDeductions",EncryptionUtil.encode(row.getCell(14).getStringCellValue()));
                map.put("disciplineDeductions",EncryptionUtil.encode(row.getCell(15).getStringCellValue()));
                map.put("shouldSendPay",EncryptionUtil.encode(row.getCell(16).getStringCellValue()));
                map.put("insteadDeductions",EncryptionUtil.encode(row.getCell(17).getStringCellValue()));
                map.put("pensionPay",EncryptionUtil.encode(row.getCell(18).getStringCellValue()));
                map.put("unemploymentInsurance",EncryptionUtil.encode(row.getCell(19).getStringCellValue()));
                map.put("medicalInsurance",EncryptionUtil.encode(row.getCell(20).getStringCellValue()));
                map.put("accumulationFund",EncryptionUtil.encode(row.getCell(21).getStringCellValue()));
                map.put("personalTaxes",EncryptionUtil.encode(row.getCell(22).getStringCellValue()));
                map.put("actualSendPay",EncryptionUtil.encode(row.getCell(23).getStringCellValue()));
                //map.put("yearsMonth",calendar.get(Calendar.YEAR) + "" + (calendar.get(Calendar.MONTH) + 1));
                map.put("yearsMonth",salaryMonth);

                if (payInfoMapper.queryForValidate(map) == 0) {
                   // payInfoMapper.insert(map);
                } else {
                    throw new Exception();
                }

            }
    }
}