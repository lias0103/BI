package nc.ws.pojo;

public class MedicalRecInforPOJO {
	
	private String ShoundMedType;//应收医疗款类别
	
	private String StartAmount;//月初余额
	
	private String DebitAmount;//本月增加
	
	private String CreditAmount;//本月减少
	
	private String EndAmount;//月末余额



	public String getShoundMedType() {
		return ShoundMedType;
	}

	public void setShoundMedType(String shoundMedType) {
		ShoundMedType = shoundMedType;
	}

	public String getStartAmount() {
		return StartAmount;
	}

	public void setStartAmount(String startAmount) {
		StartAmount = startAmount;
	}

	public String getDebitAmount() {
		return DebitAmount;
	}

	public void setDebitAmount(String debitAmount) {
		DebitAmount = debitAmount;
	}

	public String getCreditAmount() {
		return CreditAmount;
	}

	public void setCreditAmount(String creditAmount) {
		CreditAmount = creditAmount;
	}

	public String getEndAmount() {
		return EndAmount;
	}

	public void setEndAmount(String endAmount) {
		EndAmount = endAmount;
	}
	

}
