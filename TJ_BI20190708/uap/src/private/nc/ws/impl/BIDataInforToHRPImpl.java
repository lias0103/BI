package nc.ws.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import nc.itf.ws.BIDataInforToHRP;
import nc.tool.file.util.WriteToFile;
import nc.ws.util.XmlUtil;

public class BIDataInforToHRPImpl implements BIDataInforToHRP{

	@Override
	public String AcceptMessage(String xmlStr) {
		// TODO �Զ����ɵķ������
		String eventType = XmlUtil.headXmlToJson(xmlStr).trim();
		String resResult = "";
		if(eventType.equals("���ݽ���ʧ��")){
			resResult = "���ݽ���ʧ��";
		}
		WriteToFile write=new WriteToFile();
		String currtime=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String filedir="e:/hrplog";
		
		if("002".equals(eventType)){//Ӧ��ҽ�ƿ�
			MedicalReceivablesInforImpl mrInforImpl = new MedicalReceivablesInforImpl();
			resResult = mrInforImpl.AcceptMessage(xmlStr);
		}
		
		String cont="�������=="+xmlStr+"����ֵ=="+resResult;
		write.writeToFile(filedir,eventType+"_"+currtime, cont);
		
		return resResult;
	}

}
