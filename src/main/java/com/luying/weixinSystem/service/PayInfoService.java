package com.luying.weixinSystem.service;

import com.luying.weixinSystem.mapper.PayInfoMapper;
import com.luying.weixinSystem.util.EncryptionUtil;
import com.luying.weixinSystem.util.IdGernerator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PayInfoService {

    @Autowired
    private PayInfoMapper payInfoMapper;

    public List<Map<String,String>> queryYearMonth(Map map){
        return payInfoMapper.queryYearMonth(map);
    }

    public List<Map<String,String>> querySalaryInfoList(Map map) throws Exception{
        List<Map<String,String>> queryMapList = payInfoMapper.querySalaryInfo(map);

        if (queryMapList != null) {
            for (Map<String,String> reMap : queryMapList) {
                for (String key : reMap.keySet()) {
                    if ("id_card".equals(key) || "department".equals(key) || "name".equals(key)) {
                        continue;
                    }
                    reMap.put(key, EncryptionUtil.decode(reMap.get(key)));
                }
            }
        }
        return queryMapList;
    }

    public void importSalaryInfo(MultipartFile file, String salaryYearMonth) throws Exception{
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        Workbook workbook = null;
        if (".xlsx".equals(suffix)) {
            workbook = new XSSFWorkbook(file.getInputStream());
        }
        if (".xls".equals(suffix)) {
            workbook = new HSSFWorkbook(file.getInputStream());
        }
        this.importPayInfo(workbook,salaryYearMonth);
    }


    /**
     * 导入工资条信息到数据表
     * @param workbook
     * @param salaryMonth
     * @return
     */
    private void importPayInfo(Workbook workbook, String salaryMonth) throws Exception{
        //Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
        Map<String,Object> resultMap = new HashMap<>();
        //Calendar calendar = Calendar.getInstance();
        Sheet sheet = workbook.getSheetAt(0);

        List<Map<String,String>> salaryInfoList = new ArrayList<Map<String,String>>();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 1;i<=sheet.getLastRowNum();i++) {
            Row row = sheet.getRow(i);
            Map<String,String> map = new HashMap<>();
            map.put("id", IdGernerator.next());
            map.put("idCard", row.getCell(0).getStringCellValue());
            map.put("name",row.getCell(1).getStringCellValue());
            map.put("department",row.getCell(2).getStringCellValue());
            map.put("level", EncryptionUtil.encode(row.getCell(3).getStringCellValue()));
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
            map.put("createTime",dateFormat.format(date));

            if (payInfoMapper.queryForValidate(map) == 0) {
                salaryInfoList.add(map);
            }
        }
        if (salaryInfoList.size() >=1) {
            payInfoMapper.insert(salaryInfoList);
        }
    }
}
