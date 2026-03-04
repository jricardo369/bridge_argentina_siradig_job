/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.List;

public class Ganancia {

    private String id = "";
    private String cuitG = "";
    private String denominacion = "";
    private List<IngresosAporte> ingresos = new ArrayList<>();

    private String nroPresentacion = "";
    private String cuit = "";
    private String anio = "";

    public Ganancia() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
     * @return the ingresos
     */
    public List<IngresosAporte> getIngresos() {
        return ingresos;
    }

    /**
     * @param ingresos the ingresos to set
     */
    public void setIngresos(List<IngresosAporte> ingresos) {
        this.ingresos = ingresos;
    }

    /**
     * @return the cuitG
     */
    public String getCuitG() {
        return cuitG;
    }

    /**
     * @param cuitG the cuitG to set
     */
    public void setCuitG(String cuitG) {
        this.cuitG = cuitG;
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
        sb.append("Ganancia{id=").append(id);
        sb.append(", cuitG=").append(cuitG);
        sb.append(", denominacion=").append(denominacion);
        sb.append(", ingresos=").append(ingresos);
        sb.append(", nroPresentacion=").append(nroPresentacion);
        sb.append(", cuit=").append(cuit);
        sb.append(", anio=").append(anio);
        sb.append('}');
        return sb.toString();
    }

}
