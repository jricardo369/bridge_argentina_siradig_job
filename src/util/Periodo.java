/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

public class Periodo {

    private String id = "";
    private String mesDesde = "";
    private String mesHasta = "";
    private String montoMensual = "";

    private String nroDoc = "";
    private String tipo = "";

    private String nroPresentacion = "";
    private String cuit = "";
    private String idParent = "";
    private String anio = "";

    public Periodo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the mesDesde
     */
    public String getMesDesde() {
        return mesDesde;
    }

    /**
     * @param mesDesde the mesDesde to set
     */
    public void setMesDesde(String mesDesde) {
        this.mesDesde = mesDesde;
    }

    /**
     * @return the mesHasta
     */
    public String getMesHasta() {
        return mesHasta;
    }

    /**
     * @param mesHasta the mesHasta to set
     */
    public void setMesHasta(String mesHasta) {
        this.mesHasta = mesHasta;
    }

    /**
     * @return the montoMensual
     */
    public String getMontoMensual() {
        return montoMensual;
    }

    /**
     * @param montoMensual the montoMensual to set
     */
    public void setMontoMensual(String montoMensual) {
        this.montoMensual = montoMensual;
    }

    /**
     * @return the nroDoc
     */
    public String getNroDoc() {
        return nroDoc;
    }

    /**
     * @param nroDoc the nroDoc to set
     */
    public void setNroDoc(String nroDoc) {
        this.nroDoc = nroDoc;
    }

    /**
     * @return the nroPresentacion
     */
    public String getNroPresentacion() {
        return nroPresentacion;
    }

    /**
     * @param nroPresentacion the nroPresentacion to set
     */
    public void setNroPresentacion(String nroPresentacion) {
        this.nroPresentacion = nroPresentacion;
    }

    /**
     * @return the cuit
     */
    public String getCuit() {
        return cuit;
    }

    /**
     * @param cuit the cuit to set
     */
    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    /**
     * @return the idParent
     */
    public String getIdParent() {
        return idParent;
    }

    /**
     * @param idParent the idParent to set
     */
    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Periodo{id=").append(id);
        sb.append(", mesDesde=").append(mesDesde);
        sb.append(", mesHasta=").append(mesHasta);
        sb.append(", montoMensual=").append(montoMensual);
        sb.append(", nroDoc=").append(nroDoc);
        sb.append(", tipo=").append(tipo);
        sb.append(", nroPresentacion=").append(nroPresentacion);
        sb.append(", cuit=").append(cuit);
        sb.append(", idParent=").append(idParent);
        sb.append(", anio=").append(anio);
        sb.append('}');
        return sb.toString();
    }

}
