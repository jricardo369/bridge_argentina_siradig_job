/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author svcbsmmx
 */
public class TableSpaceVO  implements Comparable<TableSpaceVO> {

    public TableSpaceVO() {
    }

    public TableSpaceVO(String nombre, int PUsado, String cadena) {
        this.nombre = nombre;
        this.PUsado = PUsado;
        this.cadena = cadena;
    }

    private String nombre;
    private Integer PUsado;
    private String cadena;

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
     * @return the PUsado
     */
    public Integer getPUsado() {
        return PUsado;
    }

    /**
     * @param PUsado the PUsado to set
     */
    public void setPUsado(Integer PUsado) {
        this.PUsado = PUsado;
    }

    /**
     * @return the cadena
     */
    public String getCadena() {
        return cadena;
    }

    /**
     * @param cadena the cadena to set
     */
    public void setCadena(String cadena) {
        this.cadena = cadena;
    }

    @Override
    public int compareTo(TableSpaceVO o) {
        return o.getPUsado().compareTo(this.getPUsado());
    }

}
