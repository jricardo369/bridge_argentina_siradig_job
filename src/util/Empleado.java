/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.List;

public class Empleado {

    private String id = "";
    private String periodo = "";
    private String nroPresentacion = "";
    private String codEmpresa = "SIN VALOR";
    private String descEmpresa = "SIN VALOR";
    private String fechaPresentacion = "";
    private String directorio = "";
    private String nombreArchivo = "";
    private String cuit = "";
    private String tipoDoc = "";
    private String apellido = "";
    private String nombre = "";
    private String provincia = "";
    private String cp = "";
    private String localidad = "";
    private String calle = "";
    private String nro = "";
    private String dpto = "";
    private String claseNomina = "";
    private List<Carga> cargas = new ArrayList<>();
    private List<Ganancia> ganancias = new ArrayList<>();
    private List<Deduccion> deducciones = new ArrayList<>();
    private List<Retencion> retenciones = new ArrayList<>();
    private List<DatoAdicional> datosAdicionales = new ArrayList<>();
    private List<OtrosEmp> otrosEmps = new ArrayList<>();

    public Empleado() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo the periodo to set
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
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
     * @return the fechaPresentacion
     */
    public String getFechaPresentacion() {
        return fechaPresentacion;
    }

    /**
     * @param fechaPresentacion the fechaPresentacion to set
     */
    public void setFechaPresentacion(String fechaPresentacion) {
        this.fechaPresentacion = fechaPresentacion;
    }

    /**
     * @return the directorio
     */
    public String getDirectorio() {
        return directorio;
    }

    /**
     * @param directorio the directorio to set
     */
    public void setDirectorio(String directorio) {
        this.directorio = directorio;
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
     * @return the provincia
     */
    public String getProvincia() {
        return provincia;
    }

    /**
     * @param provincia the provincia to set
     */
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    /**
     * @return the cp
     */
    public String getCp() {
        return cp;
    }

    /**
     * @param cp the cp to set
     */
    public void setCp(String cp) {
        this.cp = cp;
    }

    /**
     * @return the localidad
     */
    public String getLocalidad() {
        return localidad;
    }

    /**
     * @param localidad the localidad to set
     */
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    /**
     * @return the calle
     */
    public String getCalle() {
        return calle;
    }

    /**
     * @param calle the calle to set
     */
    public void setCalle(String calle) {
        this.calle = calle;
    }

    /**
     * @return the nro
     */
    public String getNro() {
        return nro;
    }

    /**
     * @param nro the nro to set
     */
    public void setNro(String nro) {
        this.nro = nro;
    }

    /**
     * @return the dpto
     */
    public String getDpto() {
        return dpto;
    }

    /**
     * @param dpto the dpto to set
     */
    public void setDpto(String dpto) {
        this.dpto = dpto;
    }

    /**
     * @return the cargas
     */
    public List<Carga> getCargas() {
        return cargas;
    }

    /**
     * @param cargas the cargas to set
     */
    public void setCargas(List<Carga> cargas) {
        this.cargas = cargas;
    }

    /**
     * @return the ganancias
     */
    public List<Ganancia> getGanancias() {
        return ganancias;
    }

    /**
     * @param ganancias the ganancias to set
     */
    public void setGanancias(List<Ganancia> ganancias) {
        this.ganancias = ganancias;
    }

    /**
     * @return the deducciones
     */
    public List<Deduccion> getDeducciones() {
        return deducciones;
    }

    /**
     * @param deducciones the deducciones to set
     */
    public void setDeducciones(List<Deduccion> deducciones) {
        this.deducciones = deducciones;
    }

    /**
     * @return the retenciones
     */
    public List<Retencion> getRetenciones() {
        return retenciones;
    }

    /**
     * @param retenciones the retenciones to set
     */
    public void setRetenciones(List<Retencion> retenciones) {
        this.retenciones = retenciones;
    }

    /**
     * @return the datosAdicionales
     */
    public List<DatoAdicional> getDatosAdicionales() {
        return datosAdicionales;
    }

    /**
     * @param datosAdicionales the datosAdicionales to set
     */
    public void setDatosAdicionales(List<DatoAdicional> datosAdicionales) {
        this.datosAdicionales = datosAdicionales;
    }

    /**
     * @return the nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * @param nombreArchivo the nombreArchivo to set
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    @Override
    public String toString() {
        return "Empleado{" + "periodo=" + periodo + ", nroPresentacion=" + nroPresentacion + ", fechaPresentacion=" + fechaPresentacion + ", directorio=" + directorio + ", nombreArchivo=" + nombreArchivo + ", cuit=" + cuit + ", tipoDoc=" + tipoDoc + ", apellido=" + apellido + ", nombre=" + nombre + ", provincia=" + provincia + ", cp=" + cp + ", localidad=" + localidad + ", calle=" + calle + ", nro=" + nro + ", dpto=" + dpto + ", cargas=" + cargas + ", ganancias=" + ganancias + ", deducciones=" + deducciones + ", retenciones=" + retenciones + ", datosAdicionales=" + datosAdicionales + '}';
    }

    /**
     * @return the codEmpresa
     */
    public String getCodEmpresa() {
        return codEmpresa;
    }

    /**
     * @param codEmpresa the codEmpresa to set
     */
    public void setCodEmpresa(String codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    /**
     * @return the descEmpresa
     */
    public String getDescEmpresa() {
        return descEmpresa;
    }

    /**
     * @param descEmpresa the descEmpresa to set
     */
    public void setDescEmpresa(String descEmpresa) {
        this.descEmpresa = descEmpresa;
    }
    
    /**
     * @return the claseNomina
     */
    public String getClaseNomina() {
        return claseNomina;
    }

    /**
     * @param descEmpresa the claseNomina to set
     */
    public void setClaseNomina(String claseNomina) {
        this.claseNomina = claseNomina;
    }

	public List<OtrosEmp> getOtrosEmps() {
		return otrosEmps;
	}

	public void setOtrosEmps(List<OtrosEmp> otrosEmps) {
		this.otrosEmps = otrosEmps;
	}

}
