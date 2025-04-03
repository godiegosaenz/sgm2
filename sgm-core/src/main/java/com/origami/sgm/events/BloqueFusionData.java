package com.origami.sgm.events;

import java.io.Serializable;

public class BloqueFusionData implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String codigoOld;
	private Short numOld;
	private Short numNew;
	
	
	
	public String getCodigoOld() {
		return codigoOld;
	}
	public void setCodigoOld(String codigoOld) {
		this.codigoOld = codigoOld;
	}
	public Short getNumOld() {
		return numOld;
	}
	public void setNumOld(Short numOld) {
		this.numOld = numOld;
	}
	public Short getNumNew() {
		return numNew;
	}
	public void setNumNew(Short numNew) {
		this.numNew = numNew;
	}
	
}
