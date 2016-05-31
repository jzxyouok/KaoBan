package kongjian.pojo;

import java.io.Serializable;

public class City implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1995234536303443198L;
	private String ci_id;
	private String ci_province;
	private String ci_city;

	public String getCi_id() {
		return ci_id;
	}

	public void setCi_id(String ci_id) {
		this.ci_id = ci_id;
	}

	public String getCi_province() {
		return ci_province;
	}

	public void setCi_province(String ci_province) {
		this.ci_province = ci_province;
	}

	public String getCi_city() {
		return ci_city;
	}

	public void setCi_city(String ci_city) {
		this.ci_city = ci_city;
	}

	public City(String ci_id, String ci_province, String ci_city) {
		super();
		this.ci_id = ci_id;
		this.ci_province = ci_province;
		this.ci_city = ci_city;
	}

}
