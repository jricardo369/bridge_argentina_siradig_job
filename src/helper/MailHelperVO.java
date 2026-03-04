/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import util.Constantes;
import vo.AppMailVO;

public class MailHelperVO
        extends AppMailVO
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final long releaseVersionID = 0L;
    private static String[] colorNivel = {"grn", "yel", "red"};
    private String bodyMail;
    private boolean criticalFlag;
    HtmlEmail emailObj;
    private List<String> toMail;
    private List<String> ccMail;
    private List<String> ccoMail;
    private List<String> toMailC;
    private List<String> ccMailC;
    private List<String> ccoMailC;
    private List<ParamHelperVO> paramLst;

    String dir = "E:\\BSM\\SendMail\\";

    public MailHelperVO() {
        this.toMail = new ArrayList();
        this.ccMail = new ArrayList();
        this.ccoMail = new ArrayList();
        this.toMailC = new ArrayList();
        this.ccMailC = new ArrayList();
        this.ccoMailC = new ArrayList();
        this.emailObj = new HtmlEmail();
        this.paramLst = new ArrayList();
        this.criticalFlag = false;
    }

    public List<String> getCcMail() {
        return this.ccMail;
    }

    public List<String> getCcMailC() {
        return this.ccMailC;
    }

    public List<String> getCcoMail() {
        return this.ccoMail;
    }

    public List<String> getCcoMailC() {
        return this.ccoMailC;
    }

    public Integer getNivelColor(String type, double min, double max, double val) {
        Integer nivel;
        if (type.contentEquals("A")) {
            nivel = Integer.valueOf(2);
            if (val < min) {
                nivel = Integer.valueOf(0);
            } else if (val < max) {
                nivel = Integer.valueOf(1);
            }
        } else {
            nivel = Integer.valueOf(0);
            if (val < min) {
                nivel = Integer.valueOf(2);
            } else if (val < max) {
                nivel = Integer.valueOf(1);
            }
        }
        return nivel;
    }

    public List<String> getToMail() {
        return this.toMail;
    }

    public List<String> getToMailC() {
        return this.toMailC;
    }

    public String getBodyMail() {
        return this.bodyMail;
    }

    public List<ParamHelperVO> getParamLst() {
        return this.paramLst;
    }

    public void setParamLst(List<ParamHelperVO> paramLst) {
        this.paramLst = paramLst;
    }

    public void insFrames() {
        if (this.bodyMail.contains("{Frame")) {
            if (!getFrames().contains("|")) {
                this.bodyMail = this.bodyMail.replace("{Frame}", getFrames());
            } else {
                String[] frameLst = getFrames().split("\\|");
                for (int i = 0; i < frameLst.length; i++) {
                    this.bodyMail = this.bodyMail.replace("{Frame." + (i + 1) + "}", frameLst[i]);
                }
            }
        }
    }

    public void insLogo(File logoImg)
            throws EmailException {
        this.bodyMail = this.bodyMail.replace("{LogoPepsiCo}", this.emailObj.embed(logoImg));
    }

    public void insParam(String prmId, String prmName, String prmValue)
            throws EmailException {
        for (ParamHelperVO param : this.paramLst) {
            if (param.getId().contentEquals(prmId)) {
                param.setValue(prmValue);
                int j;
                if (!param.getType().startsWith("T")) {
                    if (param.getTitle() != null) {
                        setBodyMail(getBodyMail().replace("{Title." + prmId + "}", param.getTitle()));
                    }
                    if (param.getUnit() != null) {
                        setBodyMail(getBodyMail().replace("{Unit." + prmId + "}", param.getUnit()));
                    }
                    if ((param.getImage() != null) && (param.getImage().indexOf("${}") < 0)) {
                        setBodyMail(getBodyMail().replace("{Image." + prmId + "}", this.emailObj.embed(new File(dir + "Images\\" + param.getImage()))));
                    }
                    if (param.getType().startsWith("A")) {
                        String separator = param.getType().substring(2, 3);
                        int i = Integer.parseInt(param.getType().substring(3, 4));
                        if (prmValue.contains(separator)) {
                            prmValue = prmValue.split(separator)[i];
                        }
                        param.setType(param.getType().substring(1, 2));
                    } else if (param.getType().startsWith("L")) {
                        String separator = param.getType().substring(2, 3);
                        String variable = param.getType().substring(4);
                        String valor = "";
                        if (prmValue.contains(separator)) {
                            String[] arrayOfString1;
                            j = (arrayOfString1 = prmValue.split(separator)).length;
                            for (int i = 0; i < j; i++) {
                                String v = arrayOfString1[i];
                                if (v.contains(variable)) {
                                    valor = v;
                                    break;
                                }
                            }
                            separator = param.getType().substring(3, 4);
                            if ((valor != "") && (valor.contains(separator))) {
                                prmValue = valor.split(separator)[1];
                            }
                        }
                        param.setType(param.getType().substring(1, 2));
                    } else if (param.getType().startsWith("I")) {
                        String separator = param.getType().substring(2, 3);
                        if (prmValue.contains(separator)) {
                            for (int i = 1; i < prmValue.split(separator).length; i++) {
                                param.setImage(param.getImage().replace("${" + i + "}", prmValue.split(separator)[i]));
                            }
                            prmValue = prmValue.split(separator)[0];
                        }
                        param.setType(param.getType().substring(1, 2));
                    }
                    if (param.getType().startsWith("N")) {
                        if (param.getFormat() != null) {
                            if (prmValue != null) {
                                prmValue = String.format(param.getFormat(), new Object[]{Double.valueOf(Double.parseDouble(prmValue))});
                            } else {
                                prmValue = "";
                            }
                        }
                    } else if (param.getType().startsWith("D")) {
                        prmValue = prmValue.toString();
                    } else if (param.getType().startsWith("H")) {
                        Integer hourInt = Integer.valueOf(Integer.parseInt(prmValue));
                        prmValue = param.getFormat();
                        if (param.getFormat().contains("S")) {
                            prmValue = prmValue.replace("S", String.format("%02d", new Object[]{Integer.valueOf(hourInt.intValue() % 60)}));
                        }
                        hourInt = Integer.valueOf(hourInt.intValue() / 60);
                        if (param.getFormat().contains("M")) {
                            prmValue = prmValue.replace("M", String.format("%02d", new Object[]{Integer.valueOf(hourInt.intValue() % 60)}));
                        }
                        hourInt = Integer.valueOf(hourInt.intValue() / 60);
                        prmValue = prmValue.replace("H", hourInt.toString());
                    }
                } else {
                    String[] style = param.getType().substring(1).split("\\|");
                    String[] fmts = param.getFormat().split("\\|");
                    String[] units = param.getUnit().split("\\|");
                    String tabStr = "";
                    if (Constantes.DEBUG_MODE.booleanValue()) {
                        System.out.println("Styles[" + param.getType().substring(1).split("\\|").length + "]: " + param.getType().substring(1));
                        System.out.println("Format[" + param.getFormat().split("\\|").length + "]: " + param.getFormat());
                        System.out.println("Units[" + param.getUnit().split("\\|").length + "]: " + param.getUnit());
                    }
                    String[] arrayOfString2;
                    int k = (arrayOfString2 = param.getValue().split("\\$\\$")).length;
                    for (j = 0; j < k; j++) {
                        String row = arrayOfString2[j];
                        tabStr = tabStr + "<tr" + (!style[1].isEmpty() ? " style=\"" + style[1] + "\"" : "") + ">";
                        for (int ind = 0; ind < Integer.parseInt(style[0]); ind++) {
                            String typeCol = fmts[ind].split(";")[0];
                            String fmtoCol = fmts[ind].split(";")[1];
                            String algnCol = fmts[ind].split(";")[2];
                            String nivCCol = fmts[ind].split(";")[3];
                            boolean boldCol = fmts[ind].split(";")[4].equalsIgnoreCase("B");

                            tabStr = tabStr + "<td align=\"" + (algnCol.equalsIgnoreCase("R") ? "right" : algnCol.equalsIgnoreCase("C") ? "center" : "left") + "\" ";
                            if (ind == 0) {
                                tabStr = tabStr + (!style[2].isEmpty() ? " style=\"" + style[2] + "\"" : "");
                            } else if (ind < Integer.parseInt(style[0]) - 1) {
                                tabStr = tabStr + (!style[3].isEmpty() ? " style=\"" + style[3] + "\"" : "");
                            } else {
                                tabStr = tabStr + (!style[4].isEmpty() ? " style=\"" + style[4] + "\"" : "");
                            }
                            if (!typeCol.equalsIgnoreCase("I")) {
                                String valor;
                                if (ind < row.split("\\|").length) {
                                    valor = row.split("\\|")[ind].replace("{", "").replace("}", "");
                                } else {
                                    valor = "";
                                }
                                tabStr = tabStr + "><p class=\"MsoNormal\"><span";
                                if ((nivCCol.isEmpty()) || (valor.isEmpty())) {
                                    tabStr = tabStr + (!style[5].isEmpty() ? " style=\"" + style[5] + "\"" : "");
                                } else {
                                    tabStr
                                            = tabStr + (!style[(6 + getNivelColor(nivCCol.split(",")[0], Double.parseDouble(nivCCol.split(",")[1]), Double.parseDouble(nivCCol.split(",")[2]), Double.parseDouble(valor)).intValue())].isEmpty() ? " style=\"" + style[(6 + getNivelColor(nivCCol.split(",")[0], Double.parseDouble(nivCCol.split(",")[1]), Double.parseDouble(nivCCol.split(",")[2]), Double.parseDouble(valor)).intValue())] + "\"" : "");
                                }
                                tabStr = tabStr + ">";
                                if (typeCol.equalsIgnoreCase("N")) {
                                    if (!fmtoCol.isEmpty()) {
                                        if ((valor != null) && (!valor.isEmpty())) {
                                            valor = String.format(fmtoCol, new Object[]{Double.valueOf(Double.parseDouble(valor))}) + (units[ind] != null ? " " + units[ind] : "");
                                        } else {
                                            valor = "";
                                        }
                                    }
                                } else if (typeCol.equalsIgnoreCase("D")) {
                                    valor = valor.toString();
                                } else if (typeCol.equalsIgnoreCase("H")) {
                                    Integer hourInt = Integer.valueOf(Integer.parseInt(valor));
                                    valor = fmtoCol;
                                    if (valor.contains("S")) {
                                        valor = valor.replace("S", String.format("%02d", new Object[]{Integer.valueOf(hourInt.intValue() % 60)}));
                                    }
                                    hourInt = Integer.valueOf(hourInt.intValue() / 60);
                                    if (valor.contains("M")) {
                                        valor = valor.replace("M", String.format("%02d", new Object[]{Integer.valueOf(hourInt.intValue() % 60)}));
                                    }
                                    hourInt = Integer.valueOf(hourInt.intValue() / 60);
                                    valor = valor.replace("H", hourInt.toString());
                                }
                                tabStr = tabStr + (boldCol ? "<b>" : "") + valor + (boldCol ? "<b>" : "") + "</span></p>";
                            } else {
                                String color = "";
                                tabStr = tabStr + ">";
                                if (!nivCCol.isEmpty()) {
                                    color = colorNivel[getNivelColor(nivCCol.split(",")[0], Double.parseDouble(nivCCol.split(",")[1]), Double.parseDouble(nivCCol.split(",")[2]), Double.parseDouble(row.split("\\|")[Integer.parseInt(fmtoCol)].replace("{", "").replace("}", ""))).intValue()];
                                }
                                String valor = row.split("\\|")[ind].replace("{Img}", this.emailObj.embed(new File(dir + "Images\\" + param.getImage().replace("${}", color))));
                                tabStr = tabStr + valor;
                            }
                            tabStr = tabStr + "</td>";
                        }
                        tabStr = tabStr + "</tr>";
                    }
                    this.bodyMail = this.bodyMail.replace("<tr><td>{Value." + prmId + "}</td></tr>", "{Value." + prmId + "}");
                    prmValue = tabStr;
                }
                this.bodyMail = this.bodyMail.replace("{Value." + prmId + "}", prmValue);
                if ((param.getType().startsWith("T")) || (param.getImage() == null)) {
                    break;
                }
                if ((param.getMin() != null) && (param.getMax() != null) && (prmValue != "")) {
                    param.setNivel(getNivelColor(param.getCmpType(), Double.parseDouble(param.getMin()), Double.parseDouble(param.getMax()), Double.parseDouble(prmValue)));
                    param.setNivelName(colorNivel[param.getNivel().intValue()]);
                }
                if ((param.getReport().contentEquals("Y")) && ((param.getNivel() == null) || (param.getNivel().intValue() == 2))) {
                    this.setCriticalFlag(true);
                    if (Constantes.DEBUG_MODE.booleanValue()) {
                        System.out.println("ParamRep[" + prmId + "]: " + param.getReport());
                    }
                }
                this.bodyMail = this.bodyMail.replace("{Led." + prmId + "}", this.emailObj.embed(new File(dir + "Images\\" + param.getImage().replace("${}", param.getNivelName()))));
                if (!Constantes.DEBUG_MODE.booleanValue()) {
                    break;
                }
                System.out.println("Led." + prmId + ": " + param.getImage().replace("${}", param.getNivelName()));

                break;
            }
        }
    }

    public void insUsers()
            throws EmailException {
        for (ParamHelperVO param : this.paramLst) {
            if (param.getReport().contentEquals("O")) {
                this.bodyMail = this.bodyMail.replace("{Value." + param.getId() + "}", "");
                this.bodyMail = this.bodyMail.replace("{Title." + param.getId() + "}", "");
                if (Constantes.DEBUG_MODE.booleanValue()) {
                    System.err.println("\tParametro tipo Omision[" + param.getId() + "]: " + param.getName());
                }
            }
        }
        if ((this.bodyMail.indexOf("{Value") < 0) && (this.bodyMail.indexOf("{Title") < 0)) {
            for (String user : this.toMail) {
//                if (user.equals("baldo.garcia@pepsico.com")) {
//                    if (this.criticalFlag) {
//                        this.emailObj.addTo(user);
//                    }
//                } else if (user.equals("luis.garciaislas.contractor@pepsico.com")) {
//                    if (this.criticalFlag) {
//                        this.emailObj.addTo(user);
//                    }
//                }
//                else{
                    this.emailObj.addTo(user);
//                }
            }
            for (String user : this.ccMail) {
                this.emailObj.addCc(user);
            }
            for (String user : this.ccoMail) {
                this.emailObj.addBcc(user);
            }
            if (this.isCriticalFlag()) {
                for (String user : this.toMailC) {
                    this.emailObj.addTo(user);
                }
                for (String user : this.ccMailC) {
                    this.emailObj.addCc(user);
                }
                for (String user : this.ccoMailC) {
                    this.emailObj.addBcc(user);
                }
                this.emailObj.addHeader("X-Priority", "1");
            }
        } else {
            if (getAdminMail().indexOf(";") > 0) {
                String[] arrayOfString;
                int j = (arrayOfString = getAdminMail().split(";")).length;
                for (int i = 0; i < j; i++) {
                    String vAdm = arrayOfString[i];
                    this.emailObj.addTo(vAdm);
                }
            } else {
                this.emailObj.addTo(getAdminMail());
            }
            this.emailObj.addHeader("X-Priority", "1");
            this.emailObj.setSubject("(Error: Invalid Parameters) - " + this.emailObj.getSubject());
            System.err.println("\tError de parametros");
        }
    }

    public void sendMail()
            throws EmailException {
        if (Constantes.DEBUG_MODE.booleanValue()) {
            System.out.println(getBodyMail());
        }
        this.emailObj.setHtmlMsg(this.bodyMail);
        this.emailObj.setTextMsg("Su cliente de emails no soporta mensajes HTML");
        this.emailObj.send();
        System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " Enviando Correo " + getId() + " ......");
    }

    public void setBodyMail(String bodyMail) {
        this.bodyMail = bodyMail;
    }

    public void setHdrMail(String hour)
            throws EmailException {
        this.emailObj.setHostName(getServer());
        this.emailObj.setFrom(getFrom());
        this.emailObj.setSubject(getSubject() + " " + hour + " CST");
    }

    public void setUsersList(String usermail, String typeList) {
        if (typeList.substring(0, 3).contentEquals("TO ")) {
            this.toMail.add(usermail);
        } else if (typeList.substring(0, 3).contentEquals("CC ")) {
            this.ccMail.add(usermail);
        } else if (typeList.substring(0, 3).contentEquals("CCO")) {
            this.ccoMail.add(usermail);
        }
        if (typeList.substring(3).contentEquals("TO ")) {
            this.toMailC.add(usermail);
        } else if (typeList.substring(3).contentEquals("CC ")) {
            this.ccMailC.add(usermail);
        } else if (typeList.substring(3).contentEquals("CCO")) {
            this.ccoMailC.add(usermail);
        }
    }

    public String getVOInfo() {
        return "Clase MailHelperVO. Versi�n 1.0";
    }

    /**
     * @return the criticalFlag
     */
    public boolean isCriticalFlag() {
        return criticalFlag;
    }

    /**
     * @param criticalFlag the criticalFlag to set
     */
    public void setCriticalFlag(boolean criticalFlag) {
        this.criticalFlag = criticalFlag;
    }
}
