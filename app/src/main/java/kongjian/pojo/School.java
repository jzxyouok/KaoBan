package kongjian.pojo;

import java.io.Serializable;

public class School implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8766611917105842300L;
	private String sh_id;
	private String sh_city;
	private String sh_shool;

	public School(String sh_id, String sh_city, String sh_shool) {
		super();
		this.sh_id = sh_id;
		this.sh_city = sh_city;
		this.sh_shool = sh_shool;
	}

	public String getSh_id() {
		return sh_id;
	}

	public void setSh_id(String sh_id) {
		this.sh_id = sh_id;
	}

	public String getSh_city() {
		return sh_city;
	}

	public void setSh_city(String sh_city) {
		this.sh_city = sh_city;
	}

	public String getSh_shool() {
		return sh_shool;
	}

	public void setSh_shool(String sh_shool) {
		this.sh_shool = sh_shool;
	}

}
