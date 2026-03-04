/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.List;

public class Deduccion {

    private String id = "";
    private String tipoDeduccion = "";
    private String tipoDoc = "";
    private String nroDoc = "";
    private String denominacion = "";
    private String descBasica = "";
    private String montoTotal = "";
    private List<Periodo> periodos = new ArrayList<>();

    private String nroPresentacion = "";
    private String cuit = "";
    private String anio = "";

    public Deduccion() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the tipoDoc
     */
    public String getTipoDoc() {
        return tipoDoc;
    }

    /**
     * @param tipoDoc the tipoDoc to set
     */
    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
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
     * @return the denominacion
     */
    public String getDenominacion() {
        return denominacion;
    }

    /**
     * @param denominacion the denominacion to set
     */
    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    /**
     * @return the descBasica
     */
    public String getDescBasica() {
        return descBasica;
    }

    /**
     * @param descBasica the descBasica to set
     */
    public void setDescBasica(String descBasica) {
        this.descBasica = descBasica;
    }

    /**
     * @return the montoTotal
     */
    public String getMontoTotal() {
        return montoTotal;
    }

    /**
     * @param montoTotal the montoTotal to set
     */
    public void setMontoTotal(String montoTotal) {
        this.montoTotal = montoTotal;
    }

    /**
     * @return the periodos
     */
    public List<Periodo> getPeriodos() {
        return periodos;
    }

    /**
     * @param periodos the periodos to set
     */
    public void setPeriodos(List<Periodo> periodos) {
        this.periodos = periodos;
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
     * @return the tipoDeduccion
     */
    public String getTipoDeduccion() {
        return tipoDeduccion;
    }

    /**
     * @param tipoDeduccion the tipoDeduccion to set
     */
    public void setTipoDeduccion(String tipoDeduccion) {
        this.tipoDeduccion = tipoDeduccion;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Deduccion{id=").append(id);
        sb.append(", tipoDeduccion=").append(tipoDeduccion);
        sb.append(", tipoDoc=").append(tipoDoc);
        sb.append(", nroDoc=").append(nroDoc);
        sb.append(", denominacion=").append(denominacion);
        sb.append(", descBasica=").append(descBasica);
        sb.append(", montoTotal=").append(montoTotal);
        sb.append(", periodos=").append(periodos);
        sb.append(", nroPresentacion=").append(nroPresentacion);
        sb.append(", cuit=").append(cuit);
        sb.append(", anio=").append(anio);
        sb.append('}');
        return sb.toString();
    }

}
