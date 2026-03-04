/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.List;

public class Retencion {

    private String id = "";
    private String tipoRetencion = "";
    private String descBasica = "";
    private String montoTotal = "";
    private String nroDoc = "";

    private List<Periodo> periodos = new ArrayList<>();
    private List<Detalle> detalles = new ArrayList<>();

    private String cod = "";

    private String nroPresentacion = "";
    private String cuit = "";
    private String anio = "";

    public Retencion() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
     * @return the detalles
     */
    public List<Detalle> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(List<Detalle> detalles) {
        this.detalles = detalles;
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
     * @return the cod
     */
    public String getCod() {
        return cod;
    }

    /**
     * @param cod the cod to set
     */
    public void setCod(String cod) {
        this.cod = cod;
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
     * @return the tipoRetencion
     */
    public String getTipoRetencion() {
        return tipoRetencion;
    }

    /**
     * @param tipoRetencion the tipoRetencion to set
     */
    public void setTipoRetencion(String tipoRetencion) {
        this.tipoRetencion = tipoRetencion;
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
        sb.append("Retencion{id=").append(id);
        sb.append(", tipoRetencion=").append(tipoRetencion);
        sb.append(", descBasica=").append(descBasica);
        sb.append(", montoTotal=").append(montoTotal);
        sb.append(", nroDoc=").append(nroDoc);
        sb.append(", periodos=").append(periodos);
        sb.append(", detalles=").append(detalles);
        sb.append(", cod=").append(cod);
        sb.append(", nroPresentacion=").append(nroPresentacion);
        sb.append(", cuit=").append(cuit);
        sb.append(", anio=").append(anio);
        sb.append('}');
        return sb.toString();
    }

}
