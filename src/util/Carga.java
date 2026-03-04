/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

public class Carga {

    private String id = "";
    private String tipoDoc = "";
    private String nroDoc = "";
    private String apellido = "";
    private String nombre = "";
    private String fechaNac = "";
    private String mesDesde = "";
    private String mesHasta = "";
    private String parentesco = "";
    private String vigenteProximosPeriodos = "";
    private String porcentajeDeduccion = "";

    private String nroPresentacion = "";
    private String cuit = "";
    private String anio = "";

    public Carga() {
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
     * @return the apellido
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * @param apellido the apellido to set
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
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
     * @return the fechaNac
     */
    public String getFechaNac() {
        return fechaNac;
    }

    /**
     * @param fechaNac the fechaNac to set
     */
    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
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
     * @return the parentesco
     */
    public String getParentesco() {
        return parentesco;
    }

    /**
     * @param parentesco the parentesco to set
     */
    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    /**
     * @return the vigenteProximosPeriodos
     */
    public String getVigenteProximosPeriodos() {
        return vigenteProximosPeriodos;
    }

    /**
     * @param vigenteProximosPeriodos the vigenteProximosPeriodos to set
     */
    public void setVigenteProximosPeriodos(String vigenteProximosPeriodos) {
        this.vigenteProximosPeriodos = vigenteProximosPeriodos;
    }

    /**
     * @return the porcentajeDeduccion
     */
    public String getPorcentajeDeduccion() {
        return porcentajeDeduccion;
    }

    /**
     * @param porcentajeDeduccion the porcentajeDeduccion to set
     */
    public void setPorcentajeDeduccion(String porcentajeDeduccion) {
        this.porcentajeDeduccion = porcentajeDeduccion;
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
        sb.append("Carga{id=").append(id);
        sb.append(", tipoDoc=").append(tipoDoc);
        sb.append(", nroDoc=").append(nroDoc);
        sb.append(", apellido=").append(apellido);
        sb.append(", nombre=").append(nombre);
        sb.append(", fechaNac=").append(fechaNac);
        sb.append(", mesDesde=").append(mesDesde);
        sb.append(", mesHasta=").append(mesHasta);
        sb.append(", parentesco=").append(parentesco);
        sb.append(", vigenteProximosPeriodos=").append(vigenteProximosPeriodos);
        sb.append(", porcentajeDeduccion=").append(porcentajeDeduccion);
        sb.append(", nroPresentacion=").append(nroPresentacion);
        sb.append(", cuit=").append(cuit);
        sb.append(", anio=").append(anio);
        sb.append('}');
        return sb.toString();
    }

}
