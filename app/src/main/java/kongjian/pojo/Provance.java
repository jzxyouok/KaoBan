package kongjian.pojo;

import java.io.Serializable;

public class Provance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6562027229997646111L;
	private String pr_id;
	private String pr_province;

	public Provance(String pr_id, String pr_province) {
		super();
		this.setPr_id(pr_id);
		this.setPr_province(pr_province);
	}

	public String getPr_id() {
		return pr_id;
	}

	public void setPr_id(String pr_id) {
		this.pr_id = pr_id;
	}

	public String getPr_province() {
		return pr_province;
	}

	public void setPr_province(String pr_province) {
		this.pr_province = pr_province;
	}

}
