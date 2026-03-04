/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

public class IngresosAporte {

    private String id = "";
    private String mes = "";
    private String obraSoc = "";
    private String segSoc = "";
    private String sind = "";
    private String ganBrut = "";
    private String retGan = "";
    private String retribNoHab = "";
    private String ajuste = "";
    private String exeNoAlc = "";
    private String sac = "";
    private String horasExtGr = "";
    private String horasExtEx = "";
    private String matDid = "";
    private String gastosMovViat = "";
    
    private String cuitG = "";
    
    private String nroPresentacion = "";
    private String cuit = "";
    private String anio = "";
    
    private String segSocCajas = "";
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCuitG() {
        return cuitG;
    }

    public void setCuitG(String cuitG) {
        this.cuitG = cuitG;
    }

    public String getNroPresentacion() {
        return nroPresentacion;
    }

    public void setNroPresentacion(String nroPresentacion) {
        this.nroPresentacion = nroPresentacion;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public IngresosAporte() {
    }

    public String getObraSoc() {
        return obraSoc;
    }

    public void setObraSoc(String obraSoc) {
        this.obraSoc = obraSoc;
    }

    public String getSegSoc() {
        return segSoc;
    }

    public void setSegSoc(String segSoc) {
        this.segSoc = segSoc;
    }

    public String getSind() {
        return sind;
    }

    public void setSind(String sind) {
        this.sind = sind;
    }

    public String getGanBrut() {
        return ganBrut;
    }

    public void setGanBrut(String ganBrut) {
        this.ganBrut = ganBrut;
    }

    public String getRetGan() {
        return retGan;
    }

    public void setRetGan(String retGan) {
        this.retGan = retGan;
    }

    public String getRetribNoHab() {
        return retribNoHab;
    }

    public void setRetribNoHab(String retribNoHab) {
        this.retribNoHab = retribNoHab;
    }

    public String getAjuste() {
        return ajuste;
    }

    public void setAjuste(String ajuste) {
        this.ajuste = ajuste;
    }

    public String getExeNoAlc() {
        return exeNoAlc;
    }

    public void setExeNoAlc(String exeNoAlc) {
        this.exeNoAlc = exeNoAlc;
    }

    public String getSac() {
        return sac;
    }

    public void setSac(String sac) {
        this.sac = sac;
    }

    public String getHorasExtGr() {
        return horasExtGr;
    }

    public void setHorasExtGr(String horasExtGr) {
        this.horasExtGr = horasExtGr;
    }

    public String getHorasExtEx() {
        return horasExtEx;
    }

    public void setHorasExtEx(String horasExtEx) {
        this.horasExtEx = horasExtEx;
    }

    public String getMatDid() {
        return matDid;
    }

    public void setMatDid(String matDid) {
        this.matDid = matDid;
    }

    public String getGastosMovViat() {
        return gastosMovViat;
    }

    public void setGastosMovViat(String gastosMovViat) {
        this.gastosMovViat = gastosMovViat;
    }

    /**
     * @return the mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * @return the anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }
    
    

    public String getSegSocCajas() {
		return segSocCajas;
	}

	public void setSegSocCajas(String segSocCajas) {
		this.segSocCajas = segSocCajas;
	}


	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IngresosAporte{id=").append(id);
        sb.append(", mes=").append(mes);
        sb.append(", obraSoc=").append(obraSoc);
        sb.append(", segSoc=").append(segSoc);
        sb.append(", sind=").append(sind);
        sb.append(", ganBrut=").append(ganBrut);
        sb.append(", retGan=").append(retGan);
        sb.append(", retribNoHab=").append(retribNoHab);
        sb.append(", ajuste=").append(ajuste);
        sb.append(", exeNoAlc=").append(exeNoAlc);
        sb.append(", sac=").append(sac);
        sb.append(", horasExtGr=").append(horasExtGr);
        sb.append(", horasExtEx=").append(horasExtEx);
        sb.append(", matDid=").append(matDid);
        sb.append(", gastosMovViat=").append(gastosMovViat);
        sb.append(", cuitG=").append(cuitG);
        sb.append(", nroPresentacion=").append(nroPresentacion);
        sb.append(", cuit=").append(cuit);
        sb.append(", anio=").append(anio);
        sb.append('}');
        return sb.toString();
    }

}
