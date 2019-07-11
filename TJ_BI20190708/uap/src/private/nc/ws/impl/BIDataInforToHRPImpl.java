package nc.ws.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import nc.itf.ws.BIDataInforToHRP;
import nc.tool.file.util.WriteToFile;
import nc.ws.util.XmlUtil;

public class BIDataInforToHRPImpl implements BIDataInforToHRP{

	@Override
	public String AcceptMessage(String xmlStr) {
		// TODO 自动生成的方法存根
		String eventType = XmlUtil.headXmlToJson(xmlStr).trim();
		String resResult = "";
		if(eventType.equals("数据解析失败")){
			resResult = "数据解析失败";
		}
		WriteToFile write=new WriteToFile();
		String currtime=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String filedir="e:/hrplog";
		
		if("002".equals(eventType)){//应收医疗款
			MedicalReceivablesInforImpl mrInforImpl = new MedicalReceivablesInforImpl();
			resResult = mrInforImpl.AcceptMessage(xmlStr);
		}
		
		String cont="请求参数=="+xmlStr+"返回值=="+resResult;
		write.writeToFile(filedir,eventType+"_"+currtime, cont);
		
		return resResult;
	}

}
