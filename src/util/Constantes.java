/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Constantes {

    public static final String PATH_MAIN = "E:\\BSM\\SendMail\\";
    public static final String PATH_INPUT = "E:\\BSM\\SendMail\\Input\\";
    public static final String PATH_OUTPUT = "E:\\BSM\\SendMail\\Output\\";
    public static final String PATH_BACKUP_INPUT = "E:\\BSM\\SendMail\\RespaldoInput\\";
    public static final String PATH_BACKUP_OUTPUT = "E:\\BSM\\SendMail\\RespaldoOutput\\";
    public static final String RUTA_DIRECTORIO_LOG = "E:\\BSM\\SendMail\\Log";
    public static final String PATH_CONFIG = "E:\\BSM\\SendMail\\Conf\\";
    public static final String PATH_IMAGES = "E:\\BSM\\SendMail\\Images\\";
    public static final String TAB_PREFIX = "BSM_CONFIG_";
    public static final String TAB_APP = "APPMAIL";
    public static final String TAB_HTML = "HTMLMAIL";
    public static final String TAB_LOGS = "LOGS";
    public static final String TAB_PRM = "APPPARAM";
    public static final String TAB_QRY = "PARQUERY";
    public static final String TAB_USRS = "USERMAIL";
    public static final String APP_NAME = "bsmgridped";
    public static final String DB_NAME = "bsmmonitor";
    public static final Boolean DEBUG_MODE = Boolean.valueOf(true);

    public static Date obtenerFecha(String valor) {
//        //System.err.println(valor);
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
            return sdf.parse(valor);
        } catch (Exception e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                return sdf.parse(valor);
            } catch (Exception ex) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    return sdf.parse(valor);
                } catch (Exception exi) {
//                    exi.printStackTrace();
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        return sdf.parse(valor);
                    } catch (Exception ex2) {
//                    ex2.printStackTrace();
                        return null;
                    }
                }
            }
        }

    }

    public static Integer obtenerMesNum(String mes) {
        switch (mes) {
            case "Enero":
                return 1;
            case "Febrero":
                return 2;
            case "Marzo":
                return 3;
            case "Abril":
                return 4;
            case "Mayo":
                return 5;
            case "Junio":
                return 6;
            case "Julio":
                return 7;
            case "Agosto":
                return 8;
            case "Septiembre":
                return 9;
            case "Octubre":
                return 10;
            case "Noviembre":
                return 11;
            case "Diciembre":
                return 12;
            default:
                return 1;
        }
    }

    public static String obtenerMesName(String mes) {
        switch (mes) {
            case "1":
                return "Enero";
            case "2":
                return "Febrero";
            case "3":
                return "Marzo";
            case "4":
                return "Abril";
            case "5":
                return "Mayo";
            case "6":
                return "Junio";
            case "7":
                return "Julio";
            case "8":
                return "Agosto";
            case "9":
                return "Septiembre";
            case "10":
                return "Octubre";
            case "11":
                return "Noviembre";
            case "12":
                return "Diciembre";
            default:
                return "";
        }
    }

}
