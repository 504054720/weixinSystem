package com.luying.weixinSystem.util.wxGongZhongHao;

import com.luying.weixinSystem.service.FuwuhaoService;
import com.luying.weixinSystem.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/volidateWx")
public class VolidateWx {
    private static final Logger LOGGER = LoggerFactory.getLogger(VolidateWx.class);

    @Autowired
    private FuwuhaoService fuwuhaoService;


    @GetMapping
    public void voladate(@RequestParam String signature, @RequestParam String timestamp, @RequestParam String nonce, @RequestParam String echostr, HttpServletResponse response){
        String token = "myToken";
        String hashStr = "";

        try {
            hashStr = getSHA1(token,timestamp,nonce,"");
            if (signature.equals(hashStr)) {
                response.getWriter().write(echostr);
            }
        } catch (Exception e) {
            LOGGER.info("volidateWx_exception:" + e);
        }

    }
    @PostMapping
    public void index(HttpServletRequest request,HttpServletResponse response){

        String resMessage = "";

        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            Map<String,String> reqMap = MessageUtil.xmlToMap(request);
            //消息类型
            String msgType = reqMap.get("MsgType");
            if ("text".equals(msgType)) {
                resMessage = fuwuhaoService.handleTextMessage(reqMap);
                response.getWriter().write(resMessage);
            }
            if ("event".equals(msgType)) {
                fuwuhaoService.handleEventMessage(reqMap);
            }

        } catch (Exception e) {
            LOGGER.info("wx_post_exception:" + e);
        }
    }


    /**
     * 用SHA1算法生成安全签名
     * @param token 票据
     * @param timestamp 时间戳
     * @param nonce 随机字符串
     * @param encrypt 密文
     * @return 安全签名
     */
    public static String getSHA1(String token, String timestamp, String nonce, String encrypt) throws Exception {
        try {
            String[] array = new String[]{token, timestamp, nonce, encrypt};
            StringBuffer sb = new StringBuffer();
            // 字符串排序
            Arrays.sort(array);
            for (int i = 0; i < 4; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            // SHA1签名生成
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            StringBuffer hexstr = new StringBuffer();
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            return hexstr.toString();
        } catch (Exception e) {
            e.printStackTrace();
            //throw new AesException(AesException.ComputeSignatureError);
        }
        return "";
    }
}
