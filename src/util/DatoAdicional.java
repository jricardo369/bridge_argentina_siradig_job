/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

public class DatoAdicional {

    private String id = "";
    private String nombre = "";
    private String mesDesde = "";
    private String mesHasta = "";
    private String valor = "";

    private String nroPresentacion = "";
    private String cuit = "";
    private String anio = "";

    public DatoAdicional() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the valor
     */
    public String getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(String valor) {
        this.valor = valor;
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
        sb.append("DatoAdicional{id=").append(id);
        sb.append(", nombre=").append(nombre);
        sb.append(", mesDesde=").append(mesDesde);
        sb.append(", mesHasta=").append(mesHasta);
        sb.append(", valor=").append(valor);
        sb.append(", nroPresentacion=").append(nroPresentacion);
        sb.append(", cuit=").append(cuit);
        sb.append(", anio=").append(anio);
        sb.append('}');
        return sb.toString();
    }

}
