package nc.ws.impl;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.alibaba.fastjson.util.TypeUtils;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ws.pojo.MedicalRecInforPOJO;
import nc.ws.util.XmlUtil;
import net.sf.json.JSONObject;


/**
 * 应收医疗款构成情况
 * @author liaoshuang
 * @date 2019-07-09
 */
public class MedicalReceivablesInforImpl {
	IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);

	@SuppressWarnings({ "unchecked"})
	public String AcceptMessage(String xmlStr) {
		String xmlInfor = null;
		//首字母转成大写
		TypeUtils.compatibleWithJavaBean=true;
		try {
		String jsonstr = XmlUtil.paramXmlToJson(xmlStr,"MessageBody","HrpMedicalAmout");
		if(!jsonstr.equals("数据解析失败")){
			JSONObject jsonObject= JSONObject.fromObject(jsonstr);
			//年
			String Year = jsonObject.getString("Year").trim();	
			//月
			String Month = jsonObject.getString("Month").trim();
			String lastMonth = "";
			if(Integer.parseInt(Month)<10){
				lastMonth = "0"+(Integer.parseInt(Month)-1);
			}else{
				lastMonth = ""+(Integer.parseInt(Month)-1);
			}
			

			StringBuilder sql = new StringBuilder();
			
			sql.append("select  shoundmedtype,\n");
			sql.append("sum(startamount) as startamount,\n");
			sql.append("sum(debitamount) as debitamount,\n");
			sql.append("sum(creditamount) as creditamount,\n");
			sql.append("sum(endamount) as endamount\n");
			sql.append("from \n");
			sql.append("(select t.name   as shoundmedtype,\n");
			sql.append("       t.start_localmny    as startamount,\n");
			sql.append("       t.localdebitamount  as debitamount,\n");
			sql.append("       t.localcreditamount as creditamount,\n");
			sql.append("       t.end_localmny      as endamount\n"); 
			//应收医保、应收公医、应收门诊病人欠费、应收出院病人欠费
			sql.append("  from (select \n");
			sql.append("               case\n"); 
			sql.append("                 when a.accountcode = '12120201' then\n");
			sql.append("                  '应收医保'\n");
			sql.append("                 when a.accountcode = '12120203' then\n" );
			sql.append("                  '应收公医'\n");
			sql.append("                 when a.accountcode = '12120205' then\n");
			sql.append("                  '应收门诊病人欠费'\n");
			sql.append("                 when a.accountcode = '12120206' then\n");
			sql.append("                  '应收出院病人欠费'\n" ); 
			sql.append("               end as name,\n");
			sql.append("               round(c.start_localmny / 10000, 2) as start_localmny,\n");
			sql.append("               round(b.localdebitamount / 10000, 2) as localdebitamount,\n"); 
			sql.append("               round(b.localcreditamount / 10000, 2) as localcreditamount,\n");
			sql.append("               round(a.end_localmny / 10000, 2) as end_localmny,\n");
			sql.append("               case\n"); 
			sql.append("                 when a.accountcode = '12120201' then\n");
			sql.append("                  1\n");
			sql.append("                 when a.accountcode = '12120203' then\n" );
			sql.append("                  2\n");
			sql.append("                 when a.accountcode = '12120205' then\n");
			sql.append("                  3\n");
			sql.append("                 when a.accountcode = '12120206' then\n");
			sql.append("                  4\n" ); 
			sql.append("               end as num\n");
			//期末
			sql.append("          from (select gldt.accountcode,\n");
			sql.append("                       bdas.name,\n");
			sql.append("                       nvl(sum(gldt.localdebitamount - gldt.localcreditamount),\n"); 
			sql.append("                           0) end_localmny\n");
			sql.append("                  from gl_detail gldt\n");
			sql.append("                 inner join bd_accasoa bdas\n");
			sql.append("                    on gldt.pk_accasoa = bdas.pk_accasoa\n");
			sql.append("                 where gldt.pk_accountingbook = '1001A2100000000026JW'\n");
			sql.append("                   and gldt.yearv = '"+Year+"'\n");
			sql.append("                   and gldt.periodv <= '"+Month+"'\n");
			sql.append("                   and gldt.voucherkindv not in (4, 5)\n");
			sql.append("                   and gldt.accountcode in\n");
			sql.append("                       ('12120201', '12120203', '12120205', '12120206')\n");
			sql.append("                 group by gldt.accountcode, bdas.name) a\n");
			sql.append("          left join (select gldt.accountcode,\n"); 
			sql.append("                           bdas.name,\n");
			sql.append("                           nvl(sum(gldt.localdebitamount), 0) as localdebitamount,\n");
			sql.append("                           nvl(sum(gldt.localcreditamount), 0) as localcreditamount\n");
			sql.append("                      from gl_detail gldt\n");
			sql.append("                     inner join bd_accasoa bdas\n"); 
			sql.append("                        on gldt.pk_accasoa = bdas.pk_accasoa\n"); 
			sql.append("                     where gldt.pk_accountingbook = '1001A2100000000026JW'\n");
			sql.append("                       and gldt.yearv = '"+Year+"'\n");
			sql.append("                       and gldt.periodv = '"+Month+"'\n");
			sql.append("                       and gldt.voucherkindv not in (4, 5)\n");
			sql.append("                       and gldt.accountcode in\n");
			sql.append("                           ('12120201', '12120203', '12120205', '12120206')\n");
			sql.append("                     group by gldt.accountcode, bdas.name) b\n");
			sql.append("            on a.accountcode = b.accountcode\n");
			//期初
			sql.append("          left join (select gldt.accountcode,\n");
			sql.append("                           bdas.name,\n"); 
			sql.append("                           nvl(sum(gldt.localdebitamount -\n");
			sql.append("                                   gldt.localcreditamount),\n");
			sql.append("                               0) start_localmny\n");
			sql.append("                      from gl_detail gldt\n"); 
			sql.append("                     inner join bd_accasoa bdas\n");
			sql.append("                        on gldt.pk_accasoa = bdas.pk_accasoa\n");
			sql.append("                     where gldt.pk_accountingbook = '1001A2100000000026JW'\n");
			sql.append("                       and gldt.yearv = '"+Year+"'\n");
			sql.append("                       and gldt.periodv <= '"+lastMonth+"'\n");
			sql.append("                       and gldt.voucherkindv not in (4, 5)\n");
			sql.append("                       and gldt.accountcode in\n");
			sql.append("                           ('12120201', '12120203', '12120205', '12120206')\n");
			sql.append("                     group by gldt.accountcode, bdas.name) c\n");
			sql.append("            on b.accountcode = c.accountcode\n");
			sql.append("        union\n");
			//其他
			sql.append("        select a.name,\n");
			sql.append("               round(c.start_localmny / 10000, 2) as start_localmny,\n");
			sql.append("               round(b.localdebitamount / 10000, 2) as localdebitamount,\n");
			sql.append("               round(b.localcreditamount / 10000, 2) as localcreditamount,\n");
			sql.append("               round(a.end_localmny / 10000, 2) as end_localmny,\n");
			sql.append("               5 as num\n");
			sql.append("          from (select '其他' as name,\n");
			sql.append("                       nvl(sum(gldt.localdebitamount - gldt.localcreditamount),\n");
			sql.append("                           0) end_localmny\n" );
			sql.append("                  from gl_detail gldt\n" );
			sql.append("                 inner join bd_accasoa bdas\n" );
			sql.append("                    on gldt.pk_accasoa = bdas.pk_accasoa\n");
			sql.append("                 where gldt.pk_accountingbook = '1001A2100000000026JW'\n" );
			sql.append("                   and gldt.yearv = '"+Year+"'\n");
			sql.append("                   and gldt.periodv <= '"+Month+"'\n" );
			sql.append("                   and gldt.voucherkindv not in (4, 5)\n" ); 
			sql.append("                   and gldt.accountcode in ('12120202',\n" ); 
			sql.append("                                            '12120204',\n" );
			sql.append("                                            '12120207',\n" );
			sql.append("                                            '12120208',\n" ); 
			sql.append("                                            '12120209')) a\n" );
			sql.append("          left join (select '其他' as name,\n" );
			sql.append("                           nvl(sum(gldt.localdebitamount), 0) as localdebitamount,\n" );
			sql.append("                           nvl(sum(gldt.localcreditamount), 0) as localcreditamount\n" );
			sql.append("                      from gl_detail gldt\n" );
			sql.append("                     inner join bd_accasoa bdas\n" );
			sql.append("                        on gldt.pk_accasoa = bdas.pk_accasoa\n" );
			sql.append("                     where gldt.pk_accountingbook = '1001A2100000000026JW'\n" );
			sql.append("                       and gldt.yearv = '"+Year+"'\n" );
			sql.append("                       and gldt.periodv = '"+Month+"'\n" );
			sql.append("                       and gldt.voucherkindv not in (4, 5)\n" );
			sql.append("                       and gldt.accountcode in\n" );
			sql.append("                           ('12120202',\n" );
			sql.append("                            '12120204',\n" ); 
			sql.append("                            '12120207',\n" ); 
			sql.append("                            '12120208',\n" );
			sql.append("                            '12120209')) b\n" );
			sql.append("            on a.name = b.name\n" );
			sql.append("          left join (select '其他' as name,\n" );
			sql.append("                           nvl(sum(gldt.localdebitamount -\n" );
			sql.append("                                   gldt.localcreditamount),\n" );
			sql.append("                               0) start_localmny\n" );
			sql.append("                      from gl_detail gldt\n" ); 
			sql.append("                     inner join bd_accasoa bdas\n" ); 
			sql.append("                        on gldt.pk_accasoa = bdas.pk_accasoa\n" );
			sql.append("                     where gldt.pk_accountingbook = '1001A2100000000026JW'\n" );
			sql.append("                       and gldt.yearv = '"+Year+"'\n" );
			sql.append("                       and gldt.periodv <= '"+lastMonth+"'\n" ); 
			sql.append("                       and gldt.voucherkindv not in (4, 5)\n" ); 
			sql.append("                       and gldt.accountcode in\n");
			sql.append("                           ('12120202',\n");
			sql.append("                            '12120204',\n"); 
			sql.append("                            '12120207',\n");
			sql.append("                            '12120208',\n"); 
			sql.append("                            '12120209')) c\n");
			sql.append("            on b.name = c.name\n");
			sql.append("        union\n"); 
			//合计
			sql.append("        select a.name,\n");
			sql.append("               round(c.start_localmny / 10000, 2) as start_localmny,\n");
			sql.append("               round(b.localdebitamount / 10000, 2) as localdebitamount,\n");
			sql.append("               round(b.localcreditamount / 10000, 2) as localcreditamount,\n");
			sql.append("               round(a.end_localmny / 10000, 2) as end_localmny,\n");
			sql.append("               6 as num\n");
			sql.append("          from (select '合计' as name,\n");
			sql.append("                       nvl(sum(gldt.localdebitamount - gldt.localcreditamount),\n"); 
			sql.append("                           0) end_localmny\n");
			sql.append("                  from gl_detail gldt\n");
			sql.append("                 inner join bd_accasoa bdas\n");
			sql.append("                    on gldt.pk_accasoa = bdas.pk_accasoa\n");
			sql.append("                 where gldt.pk_accountingbook = '1001A2100000000026JW'\n");
			sql.append("                   and gldt.yearv = '"+Year+"'\n");
			sql.append("                   and gldt.periodv <= '"+Month+"'\n");
			sql.append("                   and gldt.voucherkindv not in (4, 5)\n");
			sql.append("                   and gldt.accountcode like '121202%') a\n");
			sql.append("          left join (select '合计' as name,\n"); 
			sql.append("                           nvl(sum(gldt.localdebitamount), 0) as localdebitamount,\n"); 
			sql.append("                           nvl(sum(gldt.localcreditamount), 0) as localcreditamount\n"); 
			sql.append("                      from gl_detail gldt\n");
			sql.append("                     inner join bd_accasoa bdas\n");
			sql.append("                        on gldt.pk_accasoa = bdas.pk_accasoa\n");
			sql.append("                     where gldt.pk_accountingbook = '1001A2100000000026JW'\n");
			sql.append("                       and gldt.yearv = '"+Year+"'\n");
			sql.append("                       and gldt.periodv = '"+Month+"'\n");
			sql.append("                       and gldt.voucherkindv not in (4, 5)\n");
			sql.append("                       and gldt.accountcode like '121202%') b\n");
			sql.append("            on a.name = b.name\n" );
			sql.append("          left join (select '合计' as name,\n" );
			sql.append("                           nvl(sum(gldt.localdebitamount -\n");
			sql.append("                                   gldt.localcreditamount),\n");
			sql.append("                               0) start_localmny\n" ); 
			sql.append("                      from gl_detail gldt\n"); 
			sql.append("                     inner join bd_accasoa bdas\n" );
			sql.append("                        on gldt.pk_accasoa = bdas.pk_accasoa\n" );
			sql.append("                     where gldt.pk_accountingbook = '1001A2100000000026JW'\n" );
			sql.append("                       and gldt.yearv = '"+Year+"'\n" );
			sql.append("                       and gldt.periodv <= '"+lastMonth+"'\n");
			sql.append("                       and gldt.voucherkindv not in (4, 5)\n" );
			sql.append("                       and gldt.accountcode like '121202%') c\n");
			sql.append("            on b.name = c.name\n");
			sql.append("         order by num) t");
			sql.append(" union\n");
			sql.append(" select\n"); 
			sql.append(" case\n");
			sql.append(" when rownum = 1 then\n");
			sql.append("  '应收医保'\n");
			sql.append(" when rownum = 2 then\n");
			sql.append("  '应收公医'\n");
			sql.append(" when rownum = 3 then\n");
			sql.append("  '应收门诊病人欠费'\n");
			sql.append(" when rownum = 4 then\n");
			sql.append("  '应收出院病人欠费'\n");
			sql.append("  when rownum = 5 then\n");
			sql.append("  '其他'\n");
			sql.append("  when rownum = 6 then\n");
			sql.append("  '合计'\n"); 
			sql.append(" end as shoundmedtype,\n");
			sql.append(" 0.00 as startamount,\n");
			sql.append(" 0.00 as debitamount,\n");
			sql.append(" 0.00 as creditamount,\n");
			sql.append(" 0.00 as endamount\n");
			sql.append(" from dual\n");
			sql.append("connect by 0 + level <= 6) group by shoundmedtype");


			List<MedicalRecInforPOJO> medicalRecList = (List<MedicalRecInforPOJO>) bs.executeQuery(sql.toString(), new BeanListProcessor(MedicalRecInforPOJO.class));
			xmlInfor = XmlUtil.responBank("002",medicalRecList.size());
			xmlInfor +="<HrpMedicalListAmout>";
			xmlInfor += XmlUtil.jsonToXML(JSON.toJSONString(medicalRecList,new PascalNameFilter()),"HrpMedicalAmout");
			xmlInfor +="</HrpMedicalListAmout>";
			xmlInfor +="</MessageBody></Response>";

		}else{

		}
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return xmlInfor;
	}
	
}
