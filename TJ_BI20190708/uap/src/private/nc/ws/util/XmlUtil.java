package nc.ws.util;

import java.io.IOException;
import java.util.List;

import nc.tool.file.util.DateFormatUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xml.sax.SAXException;




public class XmlUtil {
	private static Logger logger = Logger.getLogger(XmlUtil.class);

    /**
     * 将报文转换为json字符（表头）
     * @param xml
     * @param groupcode 分组标识
     * @return
     * @throws IOException 
     * @throws SAXException 
     */
    public static String headXmlToJson(String xml){
        Document doc;
        String xmlStr = null;
        try {
            doc = DocumentHelper.parseText(xml);
            JSONObject json = new JSONObject();
            dom4j2Json(doc.getRootElement(), json);
            System.out.println("xml2Json:" + json.toString());
            String messageHeader = json.getString("MessageHeader");
            JSONObject jsonObject= JSONObject.fromObject(messageHeader);
            String eventType = jsonObject.getString("EventType");
            return eventType;
        } catch (DocumentException e) {
            logger.warn("数据解析失败");
            return "数据解析失败";
        }
 
    }
    
    /**
     * 将报文转换为json字符(表体)
     * @param xml
     * @param groupcode 分组标识
     * @return
     * @throws IOException 
     * @throws SAXException 
     */
    public static String paramXmlToJson(String xml,String groupcode,String paraMark){
        Document doc;
        String xmlStr = null;
        try {
            doc = DocumentHelper.parseText(xml);
            JSONObject json = new JSONObject();
            dom4j2Json(doc.getRootElement(), json);
            System.out.println("xml2Json:" + json.toString());
            String MessageHeader = json.getString("MessageHeader");
            String MessageBody = json.getString(groupcode);
            JSONObject jsonObject=JSONObject.fromObject(MessageBody);
            if(paraMark.equals("HrpMedicalAmout")){
            	xmlStr = jsonObject.getString("HrpMedicalAmout");
            }
            
            return xmlStr;
        } catch (DocumentException e) {
            logger.warn("数据解析失败");
            xmlStr = "数据解析失败";
        }

        return xmlStr;
 
    }
    
    /**
     * xml转json
     *
     * @param element
     * @param json
     */
    public static void dom4j2Json(Element element, JSONObject json) {
        // 如果是属性
        for (Object o : element.attributes()) {
            Attribute attr = (Attribute) o;
            if (!isEmpty(attr.getValue())) {
                json.put(attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl = element.elements();
        if (chdEl.isEmpty() && !isEmpty(element.getText())) {// 如果没有子元素,只有一个值
            json.put(element.getName(), element.getText());
        }
 
        for (Element e : chdEl) {// 有子元素
            if (!e.elements().isEmpty()) {// 子元素也有子元素
                JSONObject chdjson = new JSONObject();
                dom4j2Json(e, chdjson);
                Object o = json.get(e.getName());
                if (o != null) {
                    JSONArray jsona = null;
                    if (o instanceof JSONObject) {// 如果此元素已存在,则转为jsonArray
                        JSONObject jsono = (JSONObject) o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if (o instanceof JSONArray) {
                        jsona = (JSONArray) o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                } else {
                    if (!chdjson.isEmpty()) {
                        json.put(e.getName(), chdjson);
                    }
                }
 
            } else {// 子元素没有子元素
                for (Object o : element.attributes()) {
                    Attribute attr = (Attribute) o;
                    if (!isEmpty(attr.getValue())) {
                        json.put(attr.getName(), attr.getValue());
                    }
                }
                if (!e.getText().isEmpty()) {
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }
    
    public static boolean isEmpty(String str) {
    	 
        if (str == null || str.trim().isEmpty() || "null".equals(str)) {
            return true;
        }
        return false;
    }

    
    /**
    * JSON 转换为XML
    * 2019-07-10
    * @param json
    * @return
    */

    public static String jsonToXML(String json,String rootName) {

	    String xmlStr = "";
	    int a =1;
		JSONArray arraySupp = new JSONArray();
		JSONObject objectSupp = new JSONObject();
	    XMLSerializer xmlSerializer = new XMLSerializer();
	    // 根节点名称
	    xmlSerializer.setRootName(rootName);	
	    // 不对类型进行设置
	    xmlSerializer.setTypeHintsEnabled(false);
	
	    if (json.contains("[") && json.contains("]")) {
	    	arraySupp = JSONArray.fromObject(json);
	        a = arraySupp.size();
	    } else {
		    // jsonObject
		    objectSupp = JSONObject.fromObject(json);

	    }
	    
		for(int i=0;i<a;i++){
			JSONObject  custObject  =  new JSONObject(); 
			if(arraySupp.size()==0){
				custObject = objectSupp;
			}else{
				custObject = arraySupp.getJSONObject(i); 
			}

			xmlStr += xmlSerializer.write(custObject).replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").replace("<e>", "").replace("</e>", "").trim();
		}
	
	    return xmlStr;

    }
    
    
	/**
	 * 返回信息
	 * @author liaoshuang
	 * @date 2019-07-01
	 * @param code
	 * @param type
	 * @return
	 */
	public static String responBank(String eventType,int count) {
		StringBuilder str=new StringBuilder();
		String miTime = DateFormatUtil.getSendTime();
		String random = String.valueOf((int)((Math.random()*9+1)*10000));
		String msgId = miTime+random;
		
		str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		str.append("<Response>");
		str.append("<MessageHeader>");
		str.append("<Sender>用友-NC</Sender>");
		str.append("<Receiver>同和中控-HRP</Receiver>");
		str.append("<SendTime>"+miTime+"</SendTime>");
		str.append("<EventType>"+eventType+"</EventType>");
		str.append("<MsgId>"+msgId+"</MsgId>");
		str.append("</MessageHeader>");
		str.append("<MessageBody>");
		str.append("<Result>");
		str.append("<Code>CA</Code>");
		str.append("<Desc>业务处理成功，根据当前验证条件，查询到【"+count+"】条数据！</Desc>");
		str.append("</Result>");

		return str.toString();
	}
    
}
