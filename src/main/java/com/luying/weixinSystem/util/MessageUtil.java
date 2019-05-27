package com.luying.weixinSystem.util;

import com.luying.weixinSystem.message.TextMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageUtil {

    /***
     * xml转map
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String,String> xmlToMap(HttpServletRequest request) throws Exception{
        Map<String,String> resultMap = new HashMap<String,String>();
        SAXReader reader = new SAXReader();
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            Document document = reader.read(inputStream);
            Element rootElement = document.getRootElement();
            List<Element> elementList = rootElement.elements();
            for (Element element : elementList) {
                resultMap.put(element.getName(),element.getText());
            }
            return resultMap;
        } finally {
            inputStream.close();
        }
    }

    public static String textMessageToXml(TextMessage textMessage){
        xStream.alias("xml",textMessage.getClass());
        return xStream.toXML(textMessage);
    }

    private static XStream xStream = new XStream(new XppDriver(){
        public HierarchicalStreamWriter createWriter(Writer writer){
            return new PrettyPrintWriter(writer){
                boolean cdata = true;
                public void startNode(String name,Class clazz){
                    super.startNode(name,clazz);
                }

                protected void writeText(QuickWriter quickWriter,String text){
                    if (cdata) {
                        quickWriter.write("<![CDATA[");
                        quickWriter.write(text);
                        quickWriter.write("]]>");
                    } else {
                        quickWriter.write(text);
                    }
                }
            };
        };
    });
}
