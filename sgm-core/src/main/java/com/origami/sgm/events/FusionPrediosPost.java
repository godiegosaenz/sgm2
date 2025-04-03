package com.origami.sgm.events;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Fernando
 */
public class FusionPrediosPost {

    private List<String> codPrediosFusion = new LinkedList<>();
    private String codPredioFinal;
    private List<BloqueFusionData> bloques;

    public FusionPrediosPost() {
    }

    public List<String> getCodPrediosFusion() {
        return codPrediosFusion;
    }

    public void setCodPrediosFusion(List<String> codPrediosFusion) {
        this.codPrediosFusion = codPrediosFusion;
    }

    public String getCodPredioFinal() {
        return codPredioFinal;
    }

    public void setCodPredioFinal(String codPredioFinal) {
        this.codPredioFinal = codPredioFinal;
    }

	public List<BloqueFusionData> getBloques() {
		return bloques;
	}

	public void setBloques(List<BloqueFusionData> bloques) {
		this.bloques = bloques;
	}

}
