

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import helper.MailHelperVO;
import helper.ParamHelperVO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.mail.EmailException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.XML;
import org.xml.sax.SAXException;
import util.Carga;
import util.Constantes;
import util.DatoAdicional;
import util.Deduccion;
import util.Detalle;
import util.Empleado;
import util.Ganancia;
import util.IngresosAporte;
import util.OtrosEmp;
import util.Periodo;
import util.Retencion;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import vo.BarHC;

public class BSMSIRADIGARG {

    String dir = "E:\\BSM\\SendMail\\";
    DecimalFormat formato1 = new DecimalFormat("#,###.##");

    private static String rutaPrd = "\\\\corp.pep.pvt\\appscorp\\SCUS\\Operationsbridge\\Finanzas\\Payroll\\ArchivosSIRADIGARG";
    private static String rutaTest = "E:\\TEST\\FILES_SIRADIG";

    public static void main(String[] args)
            throws EmailException, SQLException, Exception {
    	
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date procDate = new Date();

        boolean test = true;

        String isTest = args[0];

        if (isTest != null) {

            if (isTest.equals("0")) {
                test = false;
            } else {
                test = true;
            }

        }

        if (test) {

            System.out.println("Modo Prueba");
            BSMSIRADIGARG exe = new BSMSIRADIGARG();

            List<Empleado> empleados = new ArrayList<>();
            empleados = exe.obtenerDatosXMLDesdeRuta(rutaTest);
            exe.insertarDatos(procDate, "BSMSIRADIGARG", empleados);

        } else {

            System.out.println(dateFormat.format(new Date()) + " Inicio proceso de envio de correos");
            List<Empleado> empleados = new ArrayList<>();
            BSMSIRADIGARG exe = new BSMSIRADIGARG();
            empleados = exe.obtenerDatosXMLDesdeRuta(rutaPrd);
            exe.insertarDatos(procDate, "BSMSIRADIGARG", empleados);

            exe.envioCorreosAdmins(procDate, "BSMSIRADIGARG");

            System.out.println(dateFormat.format(new Date()) + " Fin proceso de envio de correos");

            /*System.out.println(dateFormat.format(new Date()) + " Inicio proceso de envio de correos");
            BSMSIRADIGARG exe = new BSMSIRADIGARG();
            //for (int i = 0; i < args.length; i++) {
            //    exe.enviaCorreos(procDate, args[i]);
            //}
            exe.enviaCorreos(procDate, "BSMSIRADIGARG");
            System.out.println(dateFormat.format(new Date()) + " Fin proceso de envio de correos");*/
        }

    }

    private void envioCorreosAdmins(Date procDate, String mailId) {

        System.out.println("Procesando mailId: " + mailId);

        try {

            File configFile = new File("E:\\BSM\\SendMail\\Conf\\", "myconfigRisServer.properties");

            String tablaInfo = "";
            String files = "";

            Properties props = new Properties();
            MailHelperVO emailStruct = new MailHelperVO();

            emailStruct.setId(mailId);
            try {
                InputStream stream = new FileInputStream(configFile);
                props.load(stream);
            } catch (FileNotFoundException e) {
                System.err.println("FAILED: failed to open config file. " + e);
            } catch (IOException e) {
                System.err.println("FAILED: failed to load propierties. " + e);
            }

            String driver = props.getProperty("driver");
            Connection conn = null;
            Connection connData = null;
            Connection connData2 = null;
            ResultSet rs = null;
            PreparedStatement selReads = null;
            PreparedStatement insLogs = null;

            try {

                String url = props.getProperty("bsmgridped.url");
                String username = props.getProperty("bsmgridped.username");
                String password = props.getProperty("bsmgridped.password");
                if (Constantes.DEBUG_MODE.booleanValue()) {
                    System.err.println("\tURL: " + url + " User: " + username + " Passwd: " + password);
                }
                Class.forName(driver);
                conn = DriverManager.getConnection(url, username, password);

            } catch (Exception e) {
                System.out.println("FAILED: failed to load Oracle JDBC driver. " + e);
            }

            if (conn != null) {
                String paramQry = "";
                String logQuery = "INSERT INTO BSM_CONFIG_LOGS (appid, logdate, paramid, logvalue, loglabel) VALUES (?, TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS'), ?, ?, ?)";

                String hora = new SimpleDateFormat("HH:mm:ss").format(procDate);
                String reportDate = new SimpleDateFormat("EEEEE MMM dd", Locale.ENGLISH).format(procDate) + " @ "
                        + hora;
                if (Constantes.DEBUG_MODE.booleanValue()) {
                    System.out.println(
                            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " Procesando: " + mailId);
                }
                String query = "UPDATE bsm_config_appmail SET applastexec = TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS') WHERE appid = ?";

                insLogs = conn.prepareStatement(query);
                insLogs.setString(1, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(procDate));
                insLogs.setString(2, mailId);
                insLogs.executeUpdate();
                insLogs.close();

                query = "SELECT mailserver, frommail, subjectmail, appname, appframes, applogged, applogexp, appadmin, appinstance, u.usermail, u.usersend "
                        + "FROM BSM_CONFIG_APPMAIL a, BSM_CONFIG_USERMAIL u WHERE a.appId = ? AND a.appId = u.appId AND appActive = 'Y' AND u.ACTIVO = 1 AND u.MONITORNAME = 's'";
                if (Constantes.DEBUG_MODE.booleanValue()) {
                    System.out.println(
                            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " Mail Application: "
                                    + query);
                }
                selReads = conn.prepareStatement(query);
                selReads.setString(1, mailId);
                rs = selReads.executeQuery();
                while (rs.next()) {
                    if (emailStruct.getServer() == null) {
                        emailStruct.setServer(rs.getString("mailserver"));
                        emailStruct.setFrom(rs.getString("frommail"));
                        emailStruct.setSubject(rs.getString("subjectmail"));
                        emailStruct.setName(rs.getString("appname"));
                        emailStruct.setFrames(rs.getString("appframes"));
                        emailStruct.setAdminMail(rs.getString("appadmin"));
                        emailStruct.setLogged(rs.getString("applogged"), rs.getInt("applogexp"));
                        emailStruct.setInstance(
                                rs.getString("appinstance") != null ? rs.getString("appinstance") : "bsmgridped");
                    }
                    System.out.println("Email: " + rs.getString("usermail"));
                    //emailStruct.setUsersList(rs.getString("usermail"), rs.getString("usersend"));
                }
                rs.close();
                selReads.close();

                List<String> correos = new ArrayList<>();

                File logoImg = new File(dir + "Images\\" + props.getProperty("logo"));

                String tmpString = readFileAsString("E:\\BSM\\SendMail\\hmtlFiles\\templateTableSpace.html");

                String strText = tmpString;
                if (strText.contains("{ReportName}")) {
                    strText = strText.replace("{ReportName}", emailStruct.getName());
                }
                if (strText.contains("{ReportDate}")) {
                    strText = strText.replace("{ReportDate}", reportDate);
                }

                emailStruct.setBodyMail(strText);
                if (strText.contains("{LogoPepsiCo}")) {
                    emailStruct.insLogo(logoImg);
                    emailStruct.insFrames();
                }

                String url = props.getProperty("bsmgridbmcone.url");
                String user = props.getProperty("bsmgridbmcone.username");
                String password = props.getProperty("bsmgridbmcone.password");

                if (Constantes.DEBUG_MODE) {
                    System.err.println(
                            "new Connect[" + emailStruct.getInstance() + "]: " + url + ", " + user + "/" + password);
                }

                 files += "DDH<br>";

            tablaInfo += files;

            tablaInfo += "<br>";
            emailStruct.setBodyMail(emailStruct.getBodyMail().replace("{contenido}", tablaInfo));
            emailStruct.setHdrMail(hora);
                
                emailStruct.insUsers();
                emailStruct.sendMail();
       
            }

        } catch (Exception e) {
            System.err.println("Error en envioCorreosAdmins: " + e);
        }

    }

    private void enviaCorreos(Date procDate, String mailId)
            throws EmailException, SQLException, Exception {

        System.out.println("Procesando mailId: " + mailId);

        File configFile = new File("E:\\BSM\\SendMail\\Conf\\", "myconfigRisServer.properties");

        Properties props = new Properties();
        MailHelperVO emailStruct = new MailHelperVO();

        emailStruct.setId(mailId);
        try {
            InputStream stream = new FileInputStream(configFile);
            props.load(stream);
        } catch (FileNotFoundException e) {
            System.err.println("FAILED: failed to open config file. " + e);
        } catch (IOException e) {
            System.err.println("FAILED: failed to load propierties. " + e);
        }

        String driver = props.getProperty("driver");
        Connection conn = null;
        Connection connData = null;
        Connection connData2 = null;
        ResultSet rs = null;
        PreparedStatement selReads = null;
        PreparedStatement insLogs = null;

        try {

            String url = props.getProperty("bsmgridped.url");
            String username = props.getProperty("bsmgridped.username");
            String password = props.getProperty("bsmgridped.password");
            if (Constantes.DEBUG_MODE.booleanValue()) {
                System.err.println("\tURL: " + url + " User: " + username + " Passwd: " + password);
            }
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            System.out.println("FAILED: failed to load Oracle JDBC driver. " + e);
        }

        if (conn != null) {
            String paramQry = "";
            String logQuery = "INSERT INTO BSM_CONFIG_LOGS (appid, logdate, paramid, logvalue, loglabel) VALUES (?, TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS'), ?, ?, ?)";

            String hora = new SimpleDateFormat("HH:mm:ss").format(procDate);
            String reportDate = new SimpleDateFormat("EEEEE MMM dd", Locale.ENGLISH).format(procDate) + " @ " + hora;
            if (Constantes.DEBUG_MODE.booleanValue()) {
                System.out.println(
                        new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " Procesando: " + mailId);
            }
            String query = "UPDATE bsm_config_appmail SET applastexec = TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS') WHERE appid = ?";

            insLogs = conn.prepareStatement(query);
            insLogs.setString(1, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(procDate));
            insLogs.setString(2, mailId);
            insLogs.executeUpdate();
            insLogs.close();

            query = "SELECT mailserver, frommail, subjectmail, appname, appframes, applogged, applogexp, appadmin, appinstance, u.usermail, u.usersend "
                    + "FROM BSM_CONFIG_APPMAIL a, BSM_CONFIG_USERMAIL u WHERE a.appId = ? AND a.appId = u.appId AND appActive = 'Y' AND u.ACTIVO = 1 AND u.MONITORNAME = 's'";
            if (Constantes.DEBUG_MODE.booleanValue()) {
                System.out.println(
                        new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " Mail Application: " + query);
            }
            selReads = conn.prepareStatement(query);
            selReads.setString(1, mailId);
            rs = selReads.executeQuery();
            while (rs.next()) {
                if (emailStruct.getServer() == null) {
                    emailStruct.setServer(rs.getString("mailserver"));
                    emailStruct.setFrom(rs.getString("frommail"));
                    emailStruct.setSubject(rs.getString("subjectmail"));
                    emailStruct.setName(rs.getString("appname"));
                    emailStruct.setFrames(rs.getString("appframes"));
                    emailStruct.setAdminMail(rs.getString("appadmin"));
                    emailStruct.setLogged(rs.getString("applogged"), rs.getInt("applogexp"));
                    emailStruct.setInstance(
                            rs.getString("appinstance") != null ? rs.getString("appinstance") : "bsmgridped");
                }
                System.out.println("Email: " + rs.getString("usermail"));
                emailStruct.setUsersList(rs.getString("usermail"), rs.getString("usersend"));
            }
            rs.close();
            selReads.close();

            List<String> correos = new ArrayList<>();

            File logoImg = new File(dir + "Images\\" + props.getProperty("logo"));

            String tmpString = readFileAsString("E:\\BSM\\SendMail\\hmtlFiles\\templateTableSpace.html");

            String strText = tmpString;
            if (strText.contains("{ReportName}")) {
                strText = strText.replace("{ReportName}", emailStruct.getName());
            }
            if (strText.contains("{ReportDate}")) {
                strText = strText.replace("{ReportDate}", reportDate);
            }

            emailStruct.setBodyMail(strText);
            if (strText.contains("{LogoPepsiCo}")) {
                emailStruct.insLogo(logoImg);
                emailStruct.insFrames();
            }

            String url = props.getProperty("bsmgridbmcone.url");
            String user = props.getProperty("bsmgridbmcone.username");
            String password = props.getProperty("bsmgridbmcone.password");

            if (Constantes.DEBUG_MODE) {
                System.err.println(
                        "new Connect[" + emailStruct.getInstance() + "]: " + url + ", " + user + "/" + password);
            }
            connData = DriverManager.getConnection(url, user, password);

            String url2 = props.getProperty("bsmopert.url");
            String user2 = props.getProperty("bsmopert.username");
            String password2 = props.getProperty("bsmopert.password");
            connData2 = DriverManager.getConnection(url2, user2, password2);

            boolean isFiles = false;
            String tablaInfo = "";
            String files = "";
            String file = "";

            tablaInfo += "<br>";
            System.out.println(props.getProperty("comedores.port"));

            String ruta = "\\\\corp.pep.pvt\\appscorp\\SCUS\\Operationsbridge\\Finanzas\\Payroll\\ArchivosSIRADIGARG";

            File directory = new File(ruta);
            System.out.println("Archivos: " + directory.list().length);

            List<String> listaDir = new ArrayList<>();
            List<Empleado> empleados = new ArrayList<>();

            Date fechaArchivo = null;
            long fecha = 0;
            String filename = "";
            File fileLast = null;
            String[] directoryList = directory.list();
            int count = 1;
            if (directoryList == null) {
                System.out.println("  No files in directory");
            } else {
                for (int i = 0; i < directoryList.length; i++) {
                    String fileName = directoryList[i];
                    File fileToCheck = new File(ruta + "\\" + fileName);
                    long fechaActual = Long.parseLong(
                            new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(fileToCheck.lastModified())));
                    if (fileToCheck.isDirectory()) {
                        listaDir.add(fileName);
                    } else {
                        if (!fileName.contains("$") && fileName.contains(".xml")) {
                            System.out.println(fileName + ", Date: " + fechaActual + ", Dir: " + listaDir.get(i)
                                    + ", esDir: " + fileToCheck.isDirectory());
                            empleados.add(
                                    obtenerXMLJSON(ruta + "\\" + fileName, listaDir.get(i), fileName, count, false));

                            count++;
                        }
                    }
                }
            }

            for (int i = 0; i < listaDir.size(); i++) {
                File directoryInsd = new File(ruta + "\\" + listaDir.get(i));
                String[] directoryListInsd = directoryInsd.list();
                if (directoryListInsd == null) {
                    System.out.println("  No files in directory");
                } else {
                    for (int j = 0; j < directoryListInsd.length; j++) {
                        String fileName = directoryListInsd[j];
                        File fileToCheck = new File(ruta + "\\" + listaDir.get(i) + "\\" + fileName);
                        long fechaActual = Long.parseLong(
                                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(fileToCheck.lastModified())));
                        if (!fileName.contains("$") && fileName.contains(".xml")) {
                            empleados.add(obtenerXMLJSON(ruta + "\\" + listaDir.get(i) + "\\" + fileName,
                                    listaDir.get(i), fileName, count, false));
                            count++;

                        }
                    }
                }
            }

            List<Carga> cargas = new ArrayList<>();
            List<Ganancia> ganancias = new ArrayList<>();
            List<IngresosAporte> ingresos = new ArrayList<>();
            List<Deduccion> deducciones = new ArrayList<>();
            List<Periodo> periodosDe = new ArrayList<>();
            List<Retencion> retenciones = new ArrayList<>();
            List<Periodo> periodosRe = new ArrayList<>();
            List<Detalle> detallesRe = new ArrayList<>();
            List<DatoAdicional> datosAdiconales = new ArrayList<>();

            int countX = 1;

            connData.setAutoCommit(false);

            String cadena = "DELETE FROM BSM_SIRADIG_ARG_EMPLEADOS ";
            selReads = connData.prepareStatement(cadena);
            int resuldel = selReads.executeUpdate(cadena);
            System.out.println("Delete resultado: " + resuldel);

            cadena = "DELETE FROM BSM_SIRADIG_ARG_CARGA";
            selReads = connData.prepareStatement(cadena);
            resuldel = selReads.executeUpdate(cadena);
            System.out.println("Delete BSM_SIRADIG_ARG_CARGA resultado: " + resuldel);

            cadena = "DELETE FROM BSM_SIRADIG_ARG_GANANCIA";
            selReads = connData.prepareStatement(cadena);
            resuldel = selReads.executeUpdate(cadena);
            System.out.println("Delete BSM_SIRADIG_ARG_GANANCIA resultado: " + resuldel);

            cadena = "DELETE FROM BSM_SIRADIG_ARG_INGRESOS";
            selReads = connData.prepareStatement(cadena);
            resuldel = selReads.executeUpdate(cadena);
            System.out.println("Delete BSM_SIRADIG_ARG_INGRESOS resultado: " + resuldel);

            cadena = "DELETE FROM BSM_SIRADIG_ARG_DEDUCCIONES";
            selReads = connData.prepareStatement(cadena);
            resuldel = selReads.executeUpdate(cadena);
            System.out.println("Delete BSM_SIRADIG_ARG_DEDUCCIONES resultado: " + resuldel);

            cadena = "DELETE FROM BSM_SIRADIG_ARG_PERIODOS";
            selReads = connData.prepareStatement(cadena);
            resuldel = selReads.executeUpdate(cadena);
            System.out.println("Delete BSM_SIRADIG_ARG_PERIODOS resultado: " + resuldel);

            cadena = "DELETE FROM BSM_SIRADIG_ARG_RETENCIONES";
            selReads = connData.prepareStatement(cadena);
            resuldel = selReads.executeUpdate(cadena);
            System.out.println("Delete BSM_SIRADIG_ARG_RETENCIONES resultado: " + resuldel);

            cadena = "DELETE FROM BSM_SIRADIG_ARG_DETALLES";
            selReads = connData.prepareStatement(cadena);
            resuldel = selReads.executeUpdate(cadena);
            System.out.println("Delete BSM_SIRADIG_ARG_DETALLES resultado: " + resuldel);

            cadena = "DELETE FROM BSM_SIRADIG_ARG_DATOS_ADICIONALES";
            selReads = connData.prepareStatement(cadena);
            resuldel = selReads.executeUpdate(cadena);
            System.out.println("Delete BSM_SIRADIG_ARG_DATOS_ADICIONALES resultado: " + resuldel);

            Statement select = null;
            ResultSet result = null;

            if (Constantes.DEBUG_MODE.booleanValue()) {
                System.out.println(
                        new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " Comenzar a insertar");
            }

            select = connData2.createStatement();
            result = select.executeQuery(
                    "SELECT E.ID,E.GPID,E.NOMBRE,E.REGISTRO_FISCAL,E.ESTADO_HR,C.DESCRIPCION,N.CLASE_NOMINA "
                            + "FROM BAR_HC E "
                            + "JOIN BAR_COMPANIA C ON E.ID_COMPANIA = C.ID "
                            + "JOIN BAR_TIPO_NOMINA N ON E.ID_TIPO_NOMINA = N.ID ");

            List<BarHC> lHc = new ArrayList<>();
            while (result.next()) {
                BarHC e = new BarHC();
                e.setId(result.getString("ID"));
                e.setGpid(result.getString("GPID"));
                e.setEstadoHr(result.getString("ESTADO_HR"));
                e.setEmpCod(result.getString("DESCRIPCION"));
                e.setTipoNomina(result.getString("CLASE_NOMINA"));
                e.setNombre(result.getString("NOMBRE"));
                e.setRegistroFiscal(
                        result.getString("REGISTRO_FISCAL") != null ? result.getString("REGISTRO_FISCAL") : "");
                lHc.add(e);
            }

            if (select != null) {
                select.close();
                select = null;
            }
            if (result != null) {
                result.close();
                result = null;
            }
            if (connData2 != null) {
                connData2.close();
            }

            System.out.println("Empleados obtenidos:" + lHc.size());

            try (PreparedStatement ps = connData.prepareStatement("INSERT INTO BSM_SIRADIG_ARG_EMPLEADOS "
                    + "(PERIODO,NROPRESENTACION,FECHAPRESENTACION,DIRECTORIO,NOMBREARCHIVO,CUIT,TIPODOC,APELLIDO,NOMBRE,PROVINCIA,"
                    + "CP,LOCALIDAD,CALLE,NRO,DPTO"
                    + ", CARGAS, GANANCIAS, DEDUCCIONES, RETENCIONES, ADICIONALES,CODEMPLEADOR, DESC_EMPLEADOR, CLASE_NOMINA,ROWDATE) "
                    + "VALUES (?, ?, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, SYSDATE)")) {
                int i = 0;
                for (Empleado reg : empleados) {

                    if (Constantes.DEBUG_MODE.booleanValue()) {
                    }
                    List<BarHC> lHcF = lHc.stream().filter(p -> p.getRegistroFiscal().equals(reg.getCuit()))
                            .collect(Collectors.toList());
                    BarHC hc = null;
                    if (!lHcF.isEmpty()) {
                        hc = lHcF.get(0);
                    }

                    if (hc != null) {
                        if (reg.getCodEmpresa().equals("SIN VALOR")) {
                            if (hc.getEmpCod().equals("PEPSICO DE ARGENTINA SRL")) {
                                reg.setCodEmpresa("30537647716");
                                reg.setDescEmpresa("PEPSICO DE ARGENTINA SOCIEDAD DE RESPONSABILIDAD LIMITADA");
                            } else {
                                reg.setCodEmpresa("30504141124");
                                reg.setDescEmpresa(hc.getEmpCod());
                            }
                        }

                        reg.setClaseNomina(hc.getTipoNomina());
                        if (Constantes.DEBUG_MODE.booleanValue()) {
                        }
                    }

                    if (i % 30000 == 0) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (empleados.get(i).getCargas().size() > 0) {
                            cargas.addAll(empleados.get(i).getCargas());
                        }

                        if (empleados.get(i).getGanancias().size() > 0) {
                            ganancias.addAll(empleados.get(i).getGanancias());
                        }

                        if (empleados.get(i).getDeducciones().size() > 0) {
                            deducciones.addAll(empleados.get(i).getDeducciones());
                        }

                        if (empleados.get(i).getRetenciones().size() > 0) {
                            retenciones.addAll(empleados.get(i).getRetenciones());
                        }

                        if (empleados.get(i).getDatosAdicionales().size() > 0) {
                            datosAdiconales.addAll(empleados.get(i).getDatosAdicionales());
                        }

                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else if (i == (empleados.size() - 1)) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (empleados.get(i).getCargas().size() > 0) {
                            cargas.addAll(empleados.get(i).getCargas());
                        }

                        if (empleados.get(i).getGanancias().size() > 0) {
                            ganancias.addAll(empleados.get(i).getGanancias());
                        }
                        if (empleados.get(i).getDeducciones().size() > 0) {
                            deducciones.addAll(empleados.get(i).getDeducciones());
                        }

                        if (empleados.get(i).getRetenciones().size() > 0) {
                            retenciones.addAll(empleados.get(i).getRetenciones());
                        }
                        if (empleados.get(i).getDatosAdicionales().size() > 0) {
                            datosAdiconales.addAll(empleados.get(i).getDatosAdicionales());
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else {
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (empleados.get(i).getCargas().size() > 0) {
                            cargas.addAll(empleados.get(i).getCargas());
                        }
                        if (empleados.get(i).getGanancias().size() > 0) {
                            ganancias.addAll(empleados.get(i).getGanancias());
                        }
                        if (empleados.get(i).getDeducciones().size() > 0) {
                            deducciones.addAll(empleados.get(i).getDeducciones());
                        }
                        if (empleados.get(i).getRetenciones().size() > 0) {
                            retenciones.addAll(empleados.get(i).getRetenciones());
                        }
                        if (empleados.get(i).getDatosAdicionales().size() > 0) {
                            datosAdiconales.addAll(empleados.get(i).getDatosAdicionales());
                        }
                        countX++;
                    }
                    i++;
                }

                ps.executeBatch();
                ps.clearBatch();
            }

            connData.setAutoCommit(false);
            countX = 0;
            try (PreparedStatement ps = connData.prepareStatement("INSERT INTO BSM_SIRADIG_ARG_CARGA "
                    + "(TIPODOC,NRODOC,APELLIDO,NOMBRE,FECHANAC,MESDESDE,MESHASTA,PARENTESCO,VIGENTEPROXIMOSPERIODOS,PORCENTAJEDEDUCCION,NROPRESENTACION,CUIT, ANIO) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                int i = 0;
                for (Carga reg : cargas) {
                    if (i % 30000 == 0) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else if (i == (cargas.size() - 1)) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else {
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        countX++;
                    }
                    i++;
                }

                ps.executeBatch();
                ps.clearBatch();
            }

            connData.setAutoCommit(false);
            countX = 0;
            try (PreparedStatement ps = connData.prepareStatement(
                    "INSERT INTO BSM_SIRADIG_ARG_GANANCIA (CUITG,DENOMINACION,NROPRESENTACION,CUIT, ANIO) "
                            + "VALUES (?, ?, ?, ?, ?)")) {
                int i = 0;
                for (Ganancia reg : ganancias) {
                    if (i % 30000 == 0) {
                        System.out.println("#" + countX + " i: " + i);

                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ganancias.get(i).getIngresos().size() > 0) {
                            ingresos.addAll(ganancias.get(i).getIngresos());
                        }

                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else if (i == (ganancias.size() - 1)) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ganancias.get(i).getIngresos().size() > 0) {
                            ingresos.addAll(ganancias.get(i).getIngresos());
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else {
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ganancias.get(i).getIngresos().size() > 0) {
                            ingresos.addAll(ganancias.get(i).getIngresos());
                        }
                        countX++;
                    }
                    i++;
                }

                ps.executeBatch();
                ps.clearBatch();
            }

            connData.setAutoCommit(false);
            countX = 0;
            try (PreparedStatement ps = connData.prepareStatement(
                    "INSERT INTO BSM_SIRADIG_ARG_INGRESOS (MES,OBRASOC,SEGSOC,SIND,GANBRUT,RETGAN,RETRIBNOHAB,AJUSTE,EXENOALC,SAC,HORASEXTGR,HORASEXTEX,MATDID,GASTOSMOVVIAT,CUITG,NROPRESENTACION,CUIT, ANIO) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                int i = 0;
                for (IngresosAporte reg : ingresos) {

                    if (i % 30000 == 0) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else if (i == (ingresos.size() - 1)) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else {
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        countX++;
                    }
                    i++;
                }

                ps.executeBatch();
                ps.clearBatch();
            }

            connData.setAutoCommit(false);
            countX = 0;
            try (PreparedStatement ps = connData.prepareStatement("INSERT INTO BSM_SIRADIG_ARG_DEDUCCIONES "
                    + "(ID,TIPODOC,NRODOC,DENOMINACION,DESCBASICA,MONTOTOTAL,NROPRESENTACION,CUIT,TIPO, ANIO, ROWDATE) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)")) {
                System.out.println("Num Deducciones: " + deducciones.size());
                int i = 0;
                for (Deduccion reg : deducciones) {

                    if (i % 30000 == 0) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (reg.getPeriodos().size() > 0) {
                            periodosDe.addAll(reg.getPeriodos());
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else if (i == (deducciones.size() - 1)) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (reg.getPeriodos().size() > 0) {
                            periodosDe.addAll(reg.getPeriodos());
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else {
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (reg.getPeriodos().size() > 0) {
                            periodosDe.addAll(reg.getPeriodos());
                        }
                        countX++;
                    }
                    i++;
                }

            }

            connData.setAutoCommit(false);
            try (PreparedStatement ps = connData.prepareStatement("INSERT INTO BSM_SIRADIG_ARG_PERIODOS "
                    + "(ID_PARENT,MESDESDE,MESHASTA,MONTOMENSUAL,NRODOC,TIPODOC,NROPRESENTACION,CUIT, ANIO) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                int i = 0;
                for (Periodo reg : periodosDe) {

                    if (i % 30000 == 0) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else if (i == (periodosDe.size() - 1)) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else {
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        countX++;
                    }
                    i++;
                }

                ps.executeBatch();
                ps.clearBatch();
            }

            connData.setAutoCommit(false);
            countX = 0;
            try (PreparedStatement ps = connData.prepareStatement("INSERT INTO BSM_SIRADIG_ARG_RETENCIONES "
                    + "(ID,DESCBASICA,MONTOTOTAL,COD,NROPRESENTACION,CUIT,TIPO, ANIO) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                int i = 0;
                for (Retencion reg : retenciones) {

                    if (i % 30000 == 0) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (retenciones.get(i).getPeriodos().size() > 0) {
                            periodosRe.addAll(retenciones.get(i).getPeriodos());
                        }

                        if (retenciones.get(i).getDetalles().size() > 0) {
                            detallesRe.addAll(retenciones.get(i).getDetalles());
                        }

                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else if (i == (retenciones.size() - 1)) {
                        System.out.println("#" + countX + " i: " + i);

                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (retenciones.get(i).getPeriodos().size() > 0) {
                            periodosRe.addAll(retenciones.get(i).getPeriodos());
                        }
                        if (retenciones.get(i).getDetalles().size() > 0) {
                            detallesRe.addAll(retenciones.get(i).getDetalles());
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else {
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (reg.getPeriodos().size() > 0) {
                            periodosRe.addAll(retenciones.get(i).getPeriodos());
                        }
                        if (reg.getDetalles().size() > 0) {
                            detallesRe.addAll(retenciones.get(i).getDetalles());
                        }
                        countX++;
                    }
                    i++;
                }

                ps.executeBatch();
                ps.clearBatch();
            }

            connData.setAutoCommit(false);
            countX = 0;
            try (PreparedStatement ps = connData.prepareStatement("INSERT INTO BSM_SIRADIG_ARG_PERIODOS "
                    + "(ID_PARENT, MESDESDE,MESHASTA,MONTOMENSUAL,NRODOC,TIPODOC,NROPRESENTACION,CUIT, ANIO) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                int i = 0;
                for (Periodo reg : periodosRe) {

                    if (i % 30000 == 0) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else if (i == (periodosRe.size() - 1)) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else {
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        countX++;
                    }
                    i++;
                }

                ps.executeBatch();
                ps.clearBatch();
            }

            connData.setAutoCommit(false);
            countX = 0;
            try (PreparedStatement ps = connData.prepareStatement(
                    "INSERT INTO BSM_SIRADIG_ARG_DETALLES (NOMBRE,VALOR,COD,NROPRESENTACION,CUIT, ANIO) "
                            + "VALUES (?, ?, ?, ?, ?, ?)")) {
                int i = 0;
                for (Detalle reg : detallesRe) {

                    if (i % 30000 == 0) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else if (i == (detallesRe.size() - 1)) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else {
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        countX++;
                    }
                    i++;
                }

                ps.executeBatch();
                ps.clearBatch();

            }

            connData.setAutoCommit(false);
            countX = 0;
            try (PreparedStatement ps = connData.prepareStatement(
                    "INSERT INTO BSM_SIRADIG_ARG_DATOS_ADICIONALES (NOMBRE,MESDESDE,MESHASTA,VALOR,NROPRESENTACION,CUIT, ANIO) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                int i = 0;
                for (DatoAdicional reg : datosAdiconales) {

                    if (i % 30000 == 0) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else if (i == (datosAdiconales.size() - 1)) {
                        System.out.println("#" + countX + " i: " + i);
                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ps.executeBatch();
                        ps.clearBatch();
                        countX++;
                    } else {

                        try {
                            procesarStatement(ps, reg).addBatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ps.addBatch();
                        countX++;
                    }
                    i++;
                }

                ps.executeBatch();
                ps.clearBatch();
            }

            connData.commit();

            files += "DDH<br>";

            tablaInfo += files;

            tablaInfo += "<br>";
            emailStruct.setBodyMail(emailStruct.getBodyMail().replace("{contenido}", tablaInfo));
            emailStruct.setHdrMail(hora);

            query = "SELECT mailserver, frommail, subjectmail, appname, appframes, applogged, applogexp, appadmin, appinstance, u.usermail, u.usersend "
                    + "FROM BSM_CONFIG_APPMAIL a, BSM_CONFIG_USERMAIL u "
                    + "WHERE a.appId = ? AND a.appId = u.appId AND appActive = 'Y' AND u.ACTIVO=1 AND u.MONITORNAME = 'user' AND u.PRIORITY='s'";
            if (Constantes.DEBUG_MODE.booleanValue()) {
                System.out.println(
                        new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " Mail Application: " + query);
            }
            selReads = conn.prepareStatement(query);
            selReads.setString(1, mailId);
            rs = selReads.executeQuery();
            while (rs.next()) {
                if (correos.contains(rs.getString("usermail"))) {

                } else {
                    correos.add(rs.getString("usermail"));
                    System.out.println("Email: " + rs.getString("usermail"));
                    emailStruct.setUsersList(rs.getString("usermail"), rs.getString("usersend"));
                }
            }
            rs.close();
            selReads.close();

            insLogs = conn.prepareStatement(logQuery);
            insLogs.setString(1, mailId);
            insLogs.setString(2, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(procDate));

            String prmVal = "";
            String prmName = "";
            String prmId = "";

            if (emailStruct.isLog()) {
                if (Constantes.DEBUG_MODE.booleanValue()) {
                    System.out.print(
                            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " AppId: " + mailId);
                    System.out.println(", LogDate: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(procDate));
                }
                for (ParamHelperVO prm : emailStruct.getParamLst()) {
                    insLogs.setInt(3, Integer.parseInt(prm.getId()));
                    insLogs.setString(4, prm.getValue());
                    if (prm.getNivel() != null) {
                        insLogs.setInt(5, prm.getNivel().intValue());
                    } else {
                        insLogs.setNull(5, 4);
                    }
                    insLogs.executeUpdate();
                    if (Constantes.DEBUG_MODE.booleanValue()) {
                        System.out.print("                    {Id: " + prm.getId() + ", Value: " + prm.getValue());
                        System.out.println(", Label: " + prm.getNivel() + "}");
                    }
                }
            }

            insLogs.close();
            conn.close();
            connData.close();

            if (isFiles) {
                emailStruct.insUsers();
                emailStruct.sendMail();
            }

        }
    }

    public static PreparedStatement procesarStatement(PreparedStatement ps, Deduccion reg) {
	    try {
	        int column = 1;
	        ps.setString(column++, reg.getId());
	        ps.setString(column++, reg.getTipoDoc());
	        ps.setString(column++, reg.getNroDoc());
	        ps.setString(column++, reg.getDenominacion());
	        ps.setString(column++, reg.getDescBasica());
	        ps.setString(column++, reg.getMontoTotal());
	        ps.setString(column++, reg.getNroPresentacion());
	        ps.setString(column++, reg.getCuit());
	        ps.setString(column++, reg.getTipoDeduccion());
	        ps.setString(column++, reg.getAnio());
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    return ps;
	}

	public static PreparedStatement procesarStatement(PreparedStatement ps, Empleado reg) {
        try {
            int column = 1;
            ps.setString(column++, reg.getPeriodo());
            ps.setString(column++, reg.getNroPresentacion());
            ps.setString(column++, reg.getFechaPresentacion());
            ps.setString(column++, reg.getDirectorio());
            ps.setString(column++, reg.getNombreArchivo());
            ps.setString(column++, reg.getCuit());
            ps.setString(column++, reg.getTipoDoc());
            ps.setString(column++, reg.getApellido());
            ps.setString(column++, reg.getNombre());
            ps.setString(column++, reg.getProvincia());
            ps.setString(column++, reg.getCp());
            ps.setString(column++, reg.getLocalidad());
            ps.setString(column++, reg.getCalle());
            ps.setString(column++, reg.getNro());
            ps.setString(column++, reg.getDpto());

            ps.setInt(column++, reg.getCargas().size());
            ps.setInt(column++, reg.getGanancias().size());
            ps.setInt(column++, reg.getDeducciones().size());
            ps.setInt(column++, reg.getRetenciones().size());
            ps.setInt(column++, reg.getDatosAdicionales().size());
            ps.setString(column++, reg.getCodEmpresa());
            ps.setString(column++, reg.getDescEmpresa());
            ps.setString(column++, reg.getClaseNomina());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ps;
    }

    public static PreparedStatement procesarStatement(PreparedStatement ps, Carga reg) {
        try {
            int column = 1;
            String mesDesde = reg.getMesDesde();
            if (reg.getFechaNac() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaNac = sdf.parse(reg.getFechaNac());
                LocalDate localDate = fechaNac.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int year = localDate.getYear();
                int month = localDate.getMonthValue();
//                int day = localDate.getDayOfMonth();
//                SimpleDateFormat gyf = new SimpleDateFormat("yyyy");
//                SimpleDateFormat gmf = new SimpleDateFormat("month");
//                System.out.println(reg.getFechaNac() + ", mes: " + month + ", aÃ±o: " + year);
                if ((reg.getParentesco().contains("Hijo") || reg.getParentesco().contains("Hija"))
                        && (Integer.valueOf(reg.getAnio()) > year) //                        && (month > Constantes.obtenerMesNum(mesDesde))
                        ) {
//                    mesDesde = Constantes.obtenerMesName(String.valueOf(month));
                    mesDesde = "Enero";
                }
            }
            ps.setString(column++, reg.getTipoDoc());
            ps.setString(column++, reg.getNroDoc());
            ps.setString(column++, reg.getApellido());
            ps.setString(column++, reg.getNombre());
            ps.setString(column++, reg.getFechaNac());
            ps.setString(column++, mesDesde);
            ps.setString(column++, reg.getMesHasta());
            ps.setString(column++, reg.getParentesco());
            ps.setString(column++, reg.getVigenteProximosPeriodos());
            ps.setString(column++, reg.getPorcentajeDeduccion());
            ps.setString(column++, reg.getNroPresentacion());
            ps.setString(column++, reg.getCuit());
            ps.setString(column++, reg.getAnio());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ps;
    }

    public static PreparedStatement procesarStatement(PreparedStatement ps, IngresosAporte reg) {
        try {
            int column = 1;
            ps.setString(column++, reg.getMes());
            ps.setString(column++, reg.getObraSoc());
            ps.setString(column++, reg.getSegSoc());
            ps.setString(column++, reg.getSind());
            ps.setString(column++, reg.getGanBrut());
            ps.setString(column++, reg.getRetGan());
            ps.setString(column++, reg.getRetribNoHab());
            ps.setString(column++, reg.getAjuste());
            ps.setString(column++, reg.getExeNoAlc());
            ps.setString(column++, reg.getSac());
            ps.setString(column++, reg.getHorasExtGr());
            ps.setString(column++, reg.getHorasExtEx());
            ps.setString(column++, reg.getMatDid());
            ps.setString(column++, reg.getGastosMovViat());
            ps.setString(column++, reg.getCuitG());
            ps.setString(column++, reg.getNroPresentacion());
            ps.setString(column++, reg.getCuit());
            ps.setString(column++, reg.getAnio());
            ps.setString(column++, reg.getSegSocCajas());
            ps.setString(column++, reg.getAjusteRemGravadas());
            ps.setString(column++, reg.getAjusteRemExeNoAlcanzadas());
            
            ps.setString(column++, reg.getSegSocAnses());
            ps.setString(column++, reg.getRemunLey19640());
            ps.setString(column++, reg.getNoRetMedCaut());
            ps.setString(column++, reg.getRemunCctPetro());
            ps.setString(column++, reg.getAsignFam());
            ps.setString(column++, reg.getIntPrestEmp());
            ps.setString(column++, reg.getRemunJudiciales());
            ps.setString(column++, reg.getIndemLey4003());
            ps.setString(column++, reg.getCursosSemin());
            ps.setString(column++, reg.getIndumEquipEmp());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ps;
    }

    public static PreparedStatement procesarStatement(PreparedStatement ps, Ganancia reg) {
        try {
            int column = 1;
            ps.setString(column++, reg.getCuitG());
            ps.setString(column++, reg.getDenominacion());
            ps.setString(column++, reg.getNroPresentacion());
            ps.setString(column++, reg.getCuit());
            ps.setString(column++, reg.getAnio());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ps;
    }

    public static PreparedStatement procesarStatement(PreparedStatement ps, Retencion reg) {
        try {
            int column = 1;
            ps.setString(column++, reg.getId());
            ps.setString(column++, reg.getDescBasica());
            ps.setString(column++, reg.getMontoTotal());
            ps.setString(column++, reg.getCod());
            ps.setString(column++, reg.getNroPresentacion());
            ps.setString(column++, reg.getCuit());
            ps.setString(column++, reg.getTipoRetencion());
            ps.setString(column++, reg.getAnio());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ps;
    }

    public static PreparedStatement procesarStatement(PreparedStatement ps, Periodo reg) {
        try {
            int column = 1;
//            MESDESDE,MESHASTA,MONTOMENSUAL,NRODOC,TIPODOC,NROPRESENTACION,CUIT, ANIO
            ps.setString(column++, reg.getIdParent());
            ps.setString(column++, reg.getMesDesde());
            ps.setString(column++, reg.getMesHasta());
            ps.setString(column++, reg.getMontoMensual());
            ps.setString(column++, reg.getNroDoc());
            ps.setString(column++, reg.getTipo());
            ps.setString(column++, reg.getNroPresentacion());
            ps.setString(column++, reg.getCuit());
            ps.setString(column++, reg.getAnio());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ps;
    }

    public static PreparedStatement procesarStatement(PreparedStatement ps, Detalle reg) {
        try {
            int column = 1;
            ps.setString(column++, reg.getNombre());
            ps.setString(column++, reg.getValor());
            ps.setString(column++, reg.getCod());
            ps.setString(column++, reg.getNroPresentacion());
            ps.setString(column++, reg.getCuit());
            ps.setString(column++, reg.getAnio());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ps;
    }

    public static PreparedStatement procesarStatement(PreparedStatement ps, DatoAdicional reg) {
        try {
            int column = 1;
            ps.setString(column++, reg.getNombre());
            ps.setString(column++, reg.getMesDesde());
            ps.setString(column++, reg.getMesHasta());
            ps.setString(column++, reg.getValor());
            ps.setString(column++, reg.getNroPresentacion());
            ps.setString(column++, reg.getCuit());
            ps.setString(column++, reg.getAnio());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ps;
    }

    public static String readFileAsString(String fileName) throws Exception {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }

    public Empleado obtenerXMLData(String filePath, String directorio, String nameFile, int count) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Empleado emp = new Empleado();
        try {
            emp.setDirectorio(directorio);
            emp.setNombreArchivo(nameFile);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName(doc.getDocumentElement().getNodeName());
            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    NodeList nl = element.getElementsByTagName("periodo");
                    if (nl.getLength() > 0) {
                        emp.setPeriodo(element.getElementsByTagName("periodo").item(0).getTextContent());
//                        System.out.println("periodo: " + element.getElementsByTagName("periodo").item(0).getTextContent());
                    } else {
//                        System.out.println("no se encuentra Elemento Periodo");
                    }

                    nl = element.getElementsByTagName("nroPresentacion");
                    if (nl.getLength() > 0) {
                        emp.setNroPresentacion(element.getElementsByTagName("nroPresentacion").item(0).getTextContent());
//                        System.out.println("nroPresentacion: " + element.getElementsByTagName("nroPresentacion").item(0).getTextContent());
                    } else {
//                        System.out.println("no se encuentra Elemento nroPresentacion");
                    }

                    nl = element.getElementsByTagName("fechaPresentacion");
                    if (nl.getLength() > 0) {
                        emp.setFechaPresentacion(element.getElementsByTagName("fechaPresentacion").item(0).getTextContent());
//                        System.out.println("fechaPresentacion: " + element.getElementsByTagName("fechaPresentacion").item(0).getTextContent());
                    } else {
//                        System.out.println("no se encuentra Elemento fechaPresentacion");
                    }

                    nl = element.getElementsByTagName("empleado");
                    if (nl.getLength() > 0) {
                        NodeList nlInsd1 = element.getElementsByTagName("cuit");
                        if (nlInsd1.getLength() > 0) {
                            emp.setCuit(element.getElementsByTagName("cuit").item(0).getTextContent());
//                            System.out.println("cuit: " + element.getElementsByTagName("cuit").item(0).getTextContent());
                        } else {
//                            System.out.println("no se encuentra cuit");
                        }

                        nlInsd1 = element.getElementsByTagName("tipoDoc");
                        if (nlInsd1.getLength() > 0) {
                            emp.setTipoDoc(element.getElementsByTagName("tipoDoc").item(0).getTextContent());
//                            System.out.println("tipoDoc: " + element.getElementsByTagName("tipoDoc").item(0).getTextContent());
                        } else {
//                            System.out.println("no se encuentra tipoDoc");
                        }

                        nlInsd1 = element.getElementsByTagName("apellido");
                        if (nlInsd1.getLength() > 0) {
                            emp.setApellido(element.getElementsByTagName("apellido").item(0).getTextContent());
//                            System.out.println("apellido: " + element.getElementsByTagName("apellido").item(0).getTextContent());
                        } else {
//                            System.out.println("no se encuentra apellido");
                        }

                        nlInsd1 = element.getElementsByTagName("nombre");
                        if (nlInsd1.getLength() > 0) {
                            emp.setNombre(element.getElementsByTagName("nombre").item(0).getTextContent());
//                            System.out.println("nombre: " + element.getElementsByTagName("nombre").item(0).getTextContent());
                        } else {
//                            System.out.println("no se encuentra nombre");
                        }

                        nl = element.getElementsByTagName("direccion");
                        if (nl.getLength() > 0) {
                            NodeList nlInsd2 = element.getElementsByTagName("provincia");
                            if (nlInsd2.getLength() > 0) {
                                emp.setProvincia(obtenerProvincia(element.getElementsByTagName("provincia").item(0).getTextContent()));
//                                System.out.println("provincia: " + element.getElementsByTagName("provincia").item(0).getTextContent());
                            } else {
//                                System.out.println("no se encuentra provincia");
                            }

                            nlInsd2 = element.getElementsByTagName("cp");
                            if (nlInsd2.getLength() > 0) {
                                emp.setCp(element.getElementsByTagName("cp").item(0).getTextContent());
//                                System.out.println("cp: " + element.getElementsByTagName("cp").item(0).getTextContent());
                            } else {
//                                System.out.println("no se encuentra cp");
                            }

                            nlInsd2 = element.getElementsByTagName("localidad");
                            if (nlInsd2.getLength() > 0) {
                                emp.setLocalidad(element.getElementsByTagName("localidad").item(0).getTextContent());
//                                System.out.println("localidad: " + element.getElementsByTagName("localidad").item(0).getTextContent());
                            } else {
//                                System.out.println("no se encuentra localidad");
                            }

                            nlInsd2 = element.getElementsByTagName("calle");
                            if (nlInsd2.getLength() > 0) {
                                emp.setCalle(element.getElementsByTagName("calle").item(0).getTextContent());
//                                System.out.println("calle: " + element.getElementsByTagName("calle").item(0).getTextContent());
                            } else {
//                                System.out.println("no se encuentra calle");
                            }

                            nlInsd2 = element.getElementsByTagName("nro");
                            if (nlInsd2.getLength() > 0) {
                                emp.setNro(element.getElementsByTagName("nro").item(0).getTextContent());
//                                System.out.println("nro: " + element.getElementsByTagName("nro").item(0).getTextContent());
                            } else {
//                                System.out.println("no se encuentra nro");
                            }

                            nlInsd2 = element.getElementsByTagName("dpto");
                            if (nlInsd2.getLength() > 0) {
                                emp.setDpto(element.getElementsByTagName("dpto").item(0).getTextContent());
//                                System.out.println("dpto: " + element.getElementsByTagName("dpto").item(0).getTextContent());
                            } else {
//                                System.out.println("no se encuentra dpto");
                            }
                        } else {
//                            System.out.println("no se encuentra Elemento direccion");
                        }

                    } else {
//                        System.out.println("no se encuentra Elemento empleado");
                    }

                    nl = element.getElementsByTagName("cargaFamilia");
                    if (nl.getLength() > 0) {
//                        System.out.println(nl.getLength());
                        for (int temp2 = 0; temp2 < nl.getLength(); temp2++) {
                            Carga carga = new Carga();
                            Node node2 = nl.item(temp2);

                            if (node2.getNodeType() == Node.ELEMENT_NODE) {

                                Element element2 = (Element) node2;

                                NodeList nlInsd1 = element2.getElementsByTagName("tipoDoc");
                                if (nlInsd1.getLength() > 0) {
                                    carga.setTipoDoc(obtenerTipoDoc(element2.getElementsByTagName("tipoDoc").item(0).getTextContent()));
//                                    System.out.println("tipoDoc: " + element2.getElementsByTagName("tipoDoc").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra dpto");
                                }

                                nlInsd1 = element2.getElementsByTagName("nroDoc");
                                if (nlInsd1.getLength() > 0) {
                                    carga.setNroDoc(element2.getElementsByTagName("nroDoc").item(0).getTextContent());
//                                    System.out.println("nroDoc: " + element2.getElementsByTagName("nroDoc").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra dpto");
                                }

                                nlInsd1 = element2.getElementsByTagName("apellido");
                                if (nlInsd1.getLength() > 0) {
                                    carga.setApellido(element2.getElementsByTagName("apellido").item(0).getTextContent());
//                                    System.out.println("apellido: " + element2.getElementsByTagName("apellido").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra dpto");
                                }

                                nlInsd1 = element2.getElementsByTagName("nombre");
                                if (nlInsd1.getLength() > 0) {
                                    carga.setNombre(element2.getElementsByTagName("nombre").item(0).getTextContent());
//                                    System.out.println("nombre: " + element2.getElementsByTagName("nombre").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra dpto");
                                }

                                nlInsd1 = element2.getElementsByTagName("fechaNac");
                                if (nlInsd1.getLength() > 0) {
                                    carga.setFechaNac(element2.getElementsByTagName("fechaNac").item(0).getTextContent());
//                                    System.out.println("fechaNac: " + element2.getElementsByTagName("fechaNac").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra dpto");
                                }

                                nlInsd1 = element2.getElementsByTagName("mesDesde");
                                if (nlInsd1.getLength() > 0) {
                                    carga.setMesDesde(obtenerMes(element2.getElementsByTagName("mesDesde").item(0).getTextContent()));
//                                    System.out.println("mesDesde: " + element2.getElementsByTagName("mesDesde").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra dpto");
                                }

                                nlInsd1 = element2.getElementsByTagName("mesHasta");
                                if (nlInsd1.getLength() > 0) {
                                    carga.setMesHasta(obtenerMes(element2.getElementsByTagName("mesHasta").item(0).getTextContent()));
//                                    System.out.println("mesHasta: " + element2.getElementsByTagName("mesHasta").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra dpto");
                                }

                                nlInsd1 = element2.getElementsByTagName("parentesco");
                                if (nlInsd1.getLength() > 0) {
                                    carga.setParentesco(obtenerParentesco(element2.getElementsByTagName("parentesco").item(0).getTextContent()));
//                                    System.out.println("parentesco: " + element2.getElementsByTagName("parentesco").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra dpto");
                                }

                                nlInsd1 = element2.getElementsByTagName("vigenteProximosPeriodos");
                                if (nlInsd1.getLength() > 0) {
                                    carga.setVigenteProximosPeriodos(element2.getElementsByTagName("vigenteProximosPeriodos").item(0).getTextContent());
//                                    System.out.println("vigenteProximosPeriodos: " + element2.getElementsByTagName("vigenteProximosPeriodos").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra dpto");
                                }

                                nlInsd1 = element2.getElementsByTagName("porcentajeDeduccion");
                                if (nlInsd1.getLength() > 0) {
                                    carga.setPorcentajeDeduccion(element2.getElementsByTagName("porcentajeDeduccion").item(0).getTextContent());
//                                    System.out.println("porcentajeDeduccion: " + element2.getElementsByTagName("porcentajeDeduccion").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra dpto");
                                }
                            }

                            carga.setNroPresentacion(emp.getNroPresentacion());
                            carga.setCuit(emp.getCuit());
                            if (!emp.getCargas().contains(carga)) {
                                emp.getCargas().add(carga);
                            }
                        }

                    } else {
//                        System.out.println("no se encuentra Elemento cargaFamilia");
                    }

                    nl = element.getElementsByTagName("empEnt");
                    if (nl.getLength() > 0) {
//                        System.out.println(nl.getLength());
                        for (int temp2 = 0; temp2 < nl.getLength(); temp2++) {
                            Ganancia ganancia = new Ganancia();
                            Node node2 = nl.item(temp2);

                            if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                Element element2 = (Element) node2;

                                NodeList nlInsd1 = element2.getElementsByTagName("cuit");
                                if (nlInsd1.getLength() > 0) {
                                    ganancia.setCuitG(element2.getElementsByTagName("cuit").item(0).getTextContent());
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra cuit");
                                }

                                nlInsd1 = element2.getElementsByTagName("denominacion");
                                if (nlInsd1.getLength() > 0) {
                                    ganancia.setDenominacion(element2.getElementsByTagName("denominacion").item(0).getTextContent());
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra denominacion");
                                }

                                NodeList nl2 = element2.getElementsByTagName("ingAp");
                                if (nl2.getLength() > 0) {
//                                    System.out.println(nl.getLength());
                                    for (int temp3 = 0; temp3 < nl2.getLength(); temp3++) {
                                        IngresosAporte ingreso = new IngresosAporte();
                                        Node node3 = nl2.item(temp3);

                                        if (node3.getNodeType() == Node.ELEMENT_NODE) {
                                            Element element3 = (Element) node3;

                                            NodeList nlInsd2 = element3.getElementsByTagName("obraSoc");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setObraSoc(element3.getElementsByTagName("obraSoc").item(0).getTextContent());
//                                    System.out.println("obraSoc: " + element2.getElementsByTagName("obraSoc").item(0).getTextContent());
                                            } else {
//                                    System.out.println("no se encuentra obraSoc");
                                            }

                                            String mes = element3.getAttribute("mes");
                                            if (mes.length() > 0) {
                                                ingreso.setMes(obtenerMes(element3.getAttribute("mes")));
                                            }

                                            nlInsd2 = element3.getElementsByTagName("segSoc");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setSegSoc(element3.getElementsByTagName("segSoc").item(0).getTextContent());
                                            }

                                            nlInsd2 = element3.getElementsByTagName("sind");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setSind(element3.getElementsByTagName("sind").item(0).getTextContent());
                                            }

                                            nlInsd2 = element3.getElementsByTagName("ganBrut");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setGanBrut(element3.getElementsByTagName("ganBrut").item(0).getTextContent());
                                            }

                                            nlInsd2 = element3.getElementsByTagName("retGan");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setRetGan(element3.getElementsByTagName("retGan").item(0).getTextContent());
                                            }

                                            nlInsd2 = element3.getElementsByTagName("retribNoHab");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setRetribNoHab(element3.getElementsByTagName("retribNoHab").item(0).getTextContent());
                                            }

                                            nlInsd2 = element3.getElementsByTagName("ajuste");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setAjuste(element3.getElementsByTagName("ajuste").item(0).getTextContent());
                                            }

                                            nlInsd2 = element3.getElementsByTagName("exeNoAlc");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setExeNoAlc(element3.getElementsByTagName("exeNoAlc").item(0).getTextContent());
                                            }

                                            nlInsd2 = element3.getElementsByTagName("sac");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setSac(element3.getElementsByTagName("sac").item(0).getTextContent());
                                            }

                                            nlInsd2 = element3.getElementsByTagName("horasExtGr");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setHorasExtGr(element3.getElementsByTagName("horasExtGr").item(0).getTextContent());
                                            }

                                            nlInsd2 = element3.getElementsByTagName("horasExtEx");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setHorasExtEx(element3.getElementsByTagName("horasExtEx").item(0).getTextContent());
                                            }

                                            nlInsd2 = element3.getElementsByTagName("matDid");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setMatDid(element3.getElementsByTagName("matDid").item(0).getTextContent());
                                            }

                                            nlInsd2 = element3.getElementsByTagName("gastosMovViat");
                                            if (nlInsd2.getLength() > 0) {
                                                ingreso.setGastosMovViat(element3.getElementsByTagName("gastosMovViat").item(0).getTextContent());
                                            }

                                        }

                                        ingreso.setNroPresentacion(emp.getNroPresentacion());
                                        ingreso.setCuit(emp.getCuit());
                                        ingreso.setCuitG(ganancia.getCuitG());

                                        ganancia.getIngresos().add(ingreso);
                                    }

                                } else {
//                        System.out.println("no se encuentra Elemento cargaFamilia");
                                }

                            }
                            ganancia.setNroPresentacion(emp.getNroPresentacion());
                            ganancia.setCuit(emp.getCuit());
                            emp.getGanancias().add(ganancia);
                        }

                    } else {
//                        System.out.println("no se encuentra Elemento cargaFamilia");
                    }

                    nl = element.getElementsByTagName("deduccion");
                    if (nl.getLength() > 0) {
//                        System.out.println(nl.getLength());
                        for (int temp2 = 0; temp2 < nl.getLength(); temp2++) {
                            Deduccion deduccion = new Deduccion();
                            Node node2 = nl.item(temp2);

                            if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                Element element2 = (Element) node2;

                                NodeList nlInsd1 = element2.getElementsByTagName("tipoDoc");
                                if (nlInsd1.getLength() > 0) {
                                    deduccion.setTipoDoc(obtenerTipoDoc(element2.getElementsByTagName("tipoDoc").item(0).getTextContent()));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra cuit");
                                }

                                deduccion.setTipoDeduccion(obtenerDeduccion(element2.getAttribute("tipo")));

                                nlInsd1 = element2.getElementsByTagName("nroDoc");
                                if (nlInsd1.getLength() > 0) {
                                    deduccion.setNroDoc(element2.getElementsByTagName("nroDoc").item(0).getTextContent());
                                }

                                nlInsd1 = element2.getElementsByTagName("denominacion");
                                if (nlInsd1.getLength() > 0) {
                                    deduccion.setDenominacion(element2.getElementsByTagName("denominacion").item(0).getTextContent());
                                }
                                nlInsd1 = element2.getElementsByTagName("descBasica");
                                if (nlInsd1.getLength() > 0) {
                                    deduccion.setDescBasica(element2.getElementsByTagName("descBasica").item(0).getTextContent());
                                }
                                nlInsd1 = element2.getElementsByTagName("montoTotal");
                                if (nlInsd1.getLength() > 0) {
                                    deduccion.setMontoTotal(element2.getElementsByTagName("montoTotal").item(0).getTextContent());
                                }

                                NodeList nl2 = element2.getElementsByTagName("periodo");
                                if (nl2.getLength() > 0) {
//                                    System.out.println(nl.getLength());
                                    for (int temp3 = 0; temp3 < nl2.getLength(); temp3++) {
                                        Periodo periodo = new Periodo();
                                        Node node3 = nl2.item(temp3);

                                        if (node3.getNodeType() == Node.ELEMENT_NODE) {
                                            Element element3 = (Element) node3;

                                            String attr = element3.getAttribute("mesDesde");
                                            if (!attr.isEmpty()) {
                                                if (attr.length() > 0) {
                                                    periodo.setMesDesde(obtenerMes(element3.getAttribute("mesDesde")));
                                                }

                                                attr = element3.getAttribute("mesHasta");
                                                if (attr.length() > 0) {
                                                    periodo.setMesHasta(obtenerMes(element3.getAttribute("mesHasta")));
                                                }

                                                attr = element3.getAttribute("montoMensual");
                                                if (attr.length() > 0) {
                                                    periodo.setMontoMensual(element3.getAttribute("montoMensual"));
                                                }

                                                periodo.setNroPresentacion(emp.getNroPresentacion());
                                                periodo.setCuit(emp.getCuit());
                                                periodo.setNroDoc(deduccion.getNroDoc());
                                                deduccion.getPeriodos().add(periodo);
                                            }

                                        }

                                    }

                                } else {
//                        System.out.println("no se encuentra Elemento cargaFamilia");
                                }

                            }

                            deduccion.setNroPresentacion(emp.getNroPresentacion());
                            deduccion.setCuit(emp.getCuit());
                            emp.getDeducciones().add(deduccion);
                        }

                    } else {
//                        System.out.println("no se encuentra Elemento cargaFamilia");
                    }

                    nl = element.getElementsByTagName("retPerPago");
                    if (nl.getLength() > 0) {
//                        System.out.println(nl.getLength());
                        int countRet = 0;
                        for (int temp2 = 0; temp2 < nl.getLength(); temp2++) {
                            Retencion retencion = new Retencion();
                            Node node2 = nl.item(temp2);

                            retencion.setCod(String.valueOf(++countRet));
                            if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                Element element2 = (Element) node2;

                                NodeList nlInsd1 = element2.getElementsByTagName("descBasica");
                                if (nlInsd1.getLength() > 0) {
                                    retencion.setDescBasica(element2.getElementsByTagName("descBasica").item(0).getTextContent());
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra cuit");
                                }

                                retencion.setTipoRetencion(obtenerRetPerPagCuenta(element2.getAttribute("tipo")));

                                nlInsd1 = element2.getElementsByTagName("montoTotal");
                                if (nlInsd1.getLength() > 0) {
                                    retencion.setMontoTotal(element2.getElementsByTagName("montoTotal").item(0).getTextContent());
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                                } else {
//                                    System.out.println("no se encuentra denominacion");
                                }

                                NodeList nl2 = element2.getElementsByTagName("periodo");
                                if (nl2.getLength() > 0) {
//                                    System.out.println(nl.getLength());
                                    for (int temp3 = 0; temp3 < nl2.getLength(); temp3++) {
                                        Periodo periodo = new Periodo();
                                        Node node3 = nl2.item(temp3);

                                        if (node3.getNodeType() == Node.ELEMENT_NODE) {
                                            Element element3 = (Element) node3;

                                            String attr = element3.getAttribute("mesDesde");
                                            if (!attr.isEmpty()) {
                                                if (attr.length() > 0) {
                                                    periodo.setMesDesde(obtenerMes(element3.getAttribute("mesDesde")));
                                                }

                                                attr = element3.getAttribute("mesHasta");
                                                if (attr.length() > 0) {
                                                    periodo.setMesHasta(obtenerMes(element3.getAttribute("mesHasta")));
                                                }

                                                attr = element3.getAttribute("montoMensual");
                                                if (attr.length() > 0) {
                                                    periodo.setMontoMensual(element3.getAttribute("montoMensual"));
                                                }

                                                periodo.setNroPresentacion(emp.getNroPresentacion());
                                                periodo.setCuit(emp.getCuit());
                                                periodo.setNroDoc(retencion.getCod());
                                                retencion.getPeriodos().add(periodo);
                                            }
                                        }

                                    }

                                } else {
//                        System.out.println("no se encuentra Elemento cargaFamilia");
                                }

                                nl2 = element2.getElementsByTagName("detalle");
                                if (nl2.getLength() > 0) {
//                                    System.out.println(nl.getLength());
                                    for (int temp3 = 0; temp3 < nl2.getLength(); temp3++) {
                                        Detalle detalle = new Detalle();
                                        Node node3 = nl2.item(temp3);

                                        if (node3.getNodeType() == Node.ELEMENT_NODE) {
                                            Element element3 = (Element) node3;

                                            String attr = element3.getAttribute("nombre");
                                            if (!attr.isEmpty()) {
                                                if (attr.length() > 0) {
                                                    detalle.setNombre(element3.getAttribute("nombre"));
                                                }

                                                attr = element3.getAttribute("valor");
                                                if (attr.length() > 0) {
                                                    detalle.setValor(element3.getAttribute("valor"));
                                                }

                                                detalle.setNroPresentacion(emp.getNroPresentacion());
                                                detalle.setCuit(emp.getCuit());
                                                detalle.setCod(retencion.getCod());
                                                retencion.getDetalles().add(detalle);
                                            }

                                        }

                                    }

                                } else {
//                        System.out.println("no se encuentra Elemento cargaFamilia");
                                }

                            }
                            retencion.setNroPresentacion(emp.getNroPresentacion());
                            retencion.setCuit(emp.getCuit());

                            emp.getRetenciones().add(retencion);
                        }

                    } else {
//                        System.out.println("no se encuentra Elemento cargaFamilia");
                    }

                    NodeList nl2 = element.getElementsByTagName("datoAdicional");
                    if (nl2.getLength() > 0) {
//                        System.out.println(nl.getLength());
                        for (int temp3 = 0; temp3 < nl2.getLength(); temp3++) {
                            DatoAdicional datoAd = new DatoAdicional();
                            datoAd.setId(String.valueOf(count));
                            Node node3 = nl2.item(temp3);

                            if (node3.getNodeType() == Node.ELEMENT_NODE) {
                                Element element3 = (Element) node3;

                                String attr = element3.getAttribute("nombre");
                                if (attr.length() > 0) {
                                    datoAd.setNombre(obtenerDatAdicionales(element3.getAttribute("nombre")));
                                }

                                attr = element3.getAttribute("mesDesde");
                                if (attr.length() > 0) {
                                    datoAd.setMesDesde(obtenerMes(element3.getAttribute("mesDesde")));
                                }

                                attr = element3.getAttribute("mesHasta");
                                if (attr.length() > 0) {
                                    datoAd.setMesHasta(obtenerMes(element3.getAttribute("mesHasta")));
                                }

                                attr = element3.getAttribute("valor");
                                if (attr.length() > 0) {
                                    datoAd.setValor(element3.getAttribute("valor"));
                                }

                            }

                            datoAd.setNroPresentacion(emp.getNroPresentacion());
                            datoAd.setCuit(emp.getCuit());
                            emp.getDatosAdicionales().add(datoAd);
                        }

                    } else {
//                        System.out.println("no se encuentra Elemento cargaFamilia");
                    }

//                    String id = element.getAttribute("id");
//
//                    String firstname = element.getElementsByTagName("firstname").item(0).getTextContent();
//                    String lastname = element.getElementsByTagName("lastname").item(0).getTextContent();
//                    String nickname = element.getElementsByTagName("nickname").item(0).getTextContent();
//
//                    NodeList salaryNodeList = element.getElementsByTagName("salary");
//                    String salary = salaryNodeList.item(0).getTextContent();
//
//                    String currency = salaryNodeList.item(0).getAttributes().getNamedItem("currency").getTextContent();
//
//                    System.out.println("Current Element :" + node.getNodeName());
//                    System.out.println("Staff Id : " + id);
//                    System.out.println("First Name : " + firstname);
//                    System.out.println("Last Name : " + lastname);
//                    System.out.println("Nick Name : " + nickname);
//                    System.out.printf("Salary [Currency] : %,.2f [%s]%n%n", Float.parseFloat(salary), currency);
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

//        System.out.println(emp);
        return emp;
    }
    
    

    public Empleado obtenerXMLJSON(String filePath, String directorio, String nameFile, int count, boolean test) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    //        System.out.println("en metodo obtenerXMLData: " + nameFile);
    int v = 100000000;
        Empleado emp = new Empleado();
        try {
            emp.setDirectorio(directorio);
            emp.setNombreArchivo(nameFile);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            if(!test){

            try (PDDocument pdfDocument = PDDocument.load(new File(filePath.replace(".xml", ".pdf")))) {

                pdfDocument.getClass();
                PDFTextStripperByArea pdfTextStripperByArea = new PDFTextStripperByArea();
                pdfTextStripperByArea.setSortByPosition(Boolean.TRUE);

                PDFTextStripper pdfTextStripper = new PDFTextStripper();

                String pdfFileInText = pdfTextStripper.getText(pdfDocument);

                String lines[] = pdfFileInText.split("\\r?\\n");

//                if (nameFile.contains("20298471397")) {
//                    System.out.println(pdfFileInText);
//                }
                for (String line : lines) {
                    if (line.contains("Agente de RetenciÃ³n:")) {
                        String[] splitAgente = line.replace("Agente de RetenciÃ³n:", "").trim().split(" - ");
                        emp.setCodEmpresa(splitAgente[0].trim());
                        emp.setDescEmpresa(splitAgente[1].trim());
//                        System.out.println(line.replace("Agente de RetenciÃ³n:", "").trim());
                    } else if (line.contains("Apellido y Nombre o DenominaciÃ³n:")) {
                        String[] splitAgente = line.replace("Apellido y Nombre o DenominaciÃ³n: CUIT:", "").trim().split(" ");
                        if (splitAgente.length == 3) {
                            emp.setDescEmpresa(splitAgente[0].trim() + " " + splitAgente[1].trim());
                            emp.setCodEmpresa(splitAgente[2].trim());
                        } else {
                            emp.setDescEmpresa(splitAgente[0].trim() + " " + splitAgente[1].trim() + " " + splitAgente[2].trim());
                            emp.setCodEmpresa(splitAgente[3].trim());
                        }

//                        System.out.println(line.replace("Apellido y Nombre o DenominaciÃ³n:", "").trim());
                    }

                }
            } catch (Exception e) {
//                e.printStackTrace();

            }

        }

            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            StringBuilder builder = new StringBuilder();
            int ptr = 0;
            while ((ptr = inputStream.read()) != -1) {
                builder.append((char) ptr);
                //  System.out.println(ptr);
            }

            String xml = builder.toString();
            JSONObject jsonObjPrinc = XML.toJSONObject(xml);
            if (test) {
                System.out.println(jsonObjPrinc.toString(2));
            }

            if (jsonObjPrinc.toString().contains("20231240013")) {
//                System.out.println(jsonObjPrinc.toString());
            }

            JSONObject jsonObj = jsonObjPrinc.getJSONObject("presentacion");
//            if (jsonObj.toString(4).contains("27335302481")) {
//                System.out.println(jsonObj.toString(4));
//            }

            if (jsonObj.has("periodo")) {
                emp.setPeriodo(String.valueOf(jsonObj.get("periodo")));
                        System.out.println("periodo: " + String.valueOf(jsonObj.get("periodo")));
            } else {
//                        System.out.println("no se encuentra Elemento Periodo");
            }

            if (jsonObj.has("nroPresentacion")) {
                emp.setNroPresentacion(String.valueOf(jsonObj.get("nroPresentacion")));
//                        System.out.println("nroPresentacion: " + element.getElementsByTagName("nroPresentacion").item(0).getTextContent());
            } else {
//                        System.out.println("no se encuentra Elemento nroPresentacion");
            }

            if (jsonObj.has("fechaPresentacion")) {
                emp.setFechaPresentacion(jsonObj.getString("fechaPresentacion"));
//                        System.out.println("fechaPresentacion: " + element.getElementsByTagName("fechaPresentacion").item(0).getTextContent());
            } else {
//                        System.out.println("no se encuentra Elemento fechaPresentacion");
            }

            int id_have_periodo = 1;
            // <editor-fold defaultstate="collapsed" desc="empleado">
            if (jsonObj.has("empleado")) {
                JSONObject empleadoObj = jsonObj.getJSONObject("empleado");
                if (empleadoObj.has("cuit")) {
                    emp.setCuit(String.valueOf(empleadoObj.get("cuit")));
//                            System.out.println("cuit: " + element.getElementsByTagName("cuit").item(0).getTextContent());
                } else {
//                            System.out.println("no se encuentra cuit");
                }

                if (empleadoObj.has("tipoDoc")) {
                    emp.setTipoDoc(String.valueOf(empleadoObj.get("tipoDoc")));
//                            System.out.println("tipoDoc: " + element.getElementsByTagName("tipoDoc").item(0).getTextContent());
                } else {
//                            System.out.println("no se encuentra tipoDoc");
                }

                if (empleadoObj.has("apellido")) {
                    emp.setApellido(empleadoObj.getString("apellido"));
//                            System.out.println("apellido: " + element.getElementsByTagName("apellido").item(0).getTextContent());
                } else {
//                            System.out.println("no se encuentra apellido");
                }

                if (empleadoObj.has("nombre")) {
                    emp.setNombre(empleadoObj.getString("nombre"));
//                            System.out.println("nombre: " + element.getElementsByTagName("nombre").item(0).getTextContent());
                } else {
//                            System.out.println("no se encuentra nombre");
                }

                if (empleadoObj.has("direccion")) {
                    JSONObject direccionObj = empleadoObj.getJSONObject("direccion");
                    if (direccionObj.has("provincia")) {
                        emp.setProvincia(obtenerProvincia(String.valueOf(direccionObj.get("provincia"))));
//                                System.out.println("provincia: " + element.getElementsByTagName("provincia").item(0).getTextContent());
                    } else {
//                                System.out.println("no se encuentra provincia");
                    }

                    if (direccionObj.has("cp")) {
                        emp.setCp(String.valueOf(direccionObj.get("cp")));
//                                System.out.println("cp: " + element.getElementsByTagName("cp").item(0).getTextContent());
                    } else {
//                                System.out.println("no se encuentra cp");
                    }

                    if (direccionObj.has("localidad")) {
                        emp.setLocalidad(direccionObj.getString("localidad"));
//                                System.out.println("localidad: " + element.getElementsByTagName("localidad").item(0).getTextContent());
                    } else {
//                                System.out.println("no se encuentra localidad");
                    }

                    if (direccionObj.has("calle")) {
                        emp.setCalle(String.valueOf(direccionObj.get("calle")));
//                                System.out.println("calle: " + element.getElementsByTagName("calle").item(0).getTextContent());
                    } else {
//                                System.out.println("no se encuentra calle");
                    }

                    if (direccionObj.has("nro")) {
                        emp.setNro(String.valueOf(direccionObj.get("nro")));
//                                System.out.println("nro: " + element.getElementsByTagName("nro").item(0).getTextContent());
                    } else {
//                                System.out.println("no se encuentra nro");
                    }

                    if (direccionObj.has("dpto")) {
                        emp.setDpto(String.valueOf(direccionObj.get("dpto")));
//                                System.out.println("dpto: " + element.getElementsByTagName("dpto").item(0).getTextContent());
                    } else {
//                                System.out.println("no se encuentra dpto");
                    }
                } else {
//                            System.out.println("no se encuentra Elemento direccion");
                }

            } else {
//                        System.out.println("no se encuentra Elemento empleado");
            }
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="cargas">
            if (jsonObj.has("cargasFamilia")) {
                JSONObject cargasObj = jsonObj.getJSONObject("cargasFamilia");
//                System.out.println("cuenta con cargasFamilia");
                if (cargasObj.has("cargaFamilia")) {
//                    System.out.println("cuenta con cargaFamilia");
                    if (cargasObj.get("cargaFamilia") instanceof JSONArray) {
//                        System.out.println("cargaFamilia es Arreglo");
//                        System.out.println("cargaFamilia es Arreglo: " + emp.getCuit());
                        JSONArray cargaArray = cargasObj.getJSONArray("cargaFamilia");
                        for (Object object : cargaArray) {
                            Carga carga = new Carga();
                            JSONObject cargaObj = (JSONObject) object;

                            if (cargaObj.has("tipoDoc")) {
                                carga.setTipoDoc(obtenerTipoDoc(String.valueOf(cargaObj.get("tipoDoc"))));
//                                    System.out.println("tipoDoc: " + element2.getElementsByTagName("tipoDoc").item(0).getTextContent());
                            }

                            if (cargaObj.has("nroDoc")) {
                                carga.setNroDoc(String.valueOf(cargaObj.get("nroDoc")));
//                                    System.out.println("nroDoc: " + element2.getElementsByTagName("nroDoc").item(0).getTextContent());
                            }

                            if (cargaObj.has("apellido")) {
                                carga.setApellido(cargaObj.getString("apellido"));
//                                    System.out.println("apellido: " + element2.getElementsByTagName("apellido").item(0).getTextContent());
                            }

                            if (cargaObj.has("nombre")) {
                                carga.setNombre(cargaObj.getString("nombre"));
//                                    System.out.println("nombre: " + element2.getElementsByTagName("nombre").item(0).getTextContent());
                            }

                            if (cargaObj.has("fechaNac")) {
                                carga.setFechaNac(cargaObj.getString("fechaNac"));
//                                    System.out.println("fechaNac: " + element2.getElementsByTagName("fechaNac").item(0).getTextContent());
                            }

                            if (cargaObj.has("mesDesde")) {
                                carga.setMesDesde(obtenerMes(String.valueOf(cargaObj.get("mesDesde"))));
//                                    System.out.println("mesDesde: " + element2.getElementsByTagName("mesDesde").item(0).getTextContent());
                            }

                            if (cargaObj.has("mesHasta")) {
                                carga.setMesHasta(obtenerMes(String.valueOf(cargaObj.get("mesHasta"))));
//                                    System.out.println("mesHasta: " + element2.getElementsByTagName("mesHasta").item(0).getTextContent());
                            }

                            if (cargaObj.has("parentesco")) {
                                carga.setParentesco(obtenerParentesco(String.valueOf(cargaObj.get("parentesco"))));
//                                    System.out.println("parentesco: " + element2.getElementsByTagName("parentesco").item(0).getTextContent());
                            }

                            if (cargaObj.has("vigenteProximosPeriodos")) {
                                carga.setVigenteProximosPeriodos(cargaObj.getString("vigenteProximosPeriodos"));
//                                    System.out.println("vigenteProximosPeriodos: " + element2.getElementsByTagName("vigenteProximosPeriodos").item(0).getTextContent());
                            }

                            if (cargaObj.has("porcentajeDeduccion")) {
                                carga.setPorcentajeDeduccion(String.valueOf(cargaObj.get("porcentajeDeduccion")));
//                                    System.out.println("porcentajeDeduccion: " + element2.getElementsByTagName("porcentajeDeduccion").item(0).getTextContent());
                            }

                            carga.setNroPresentacion(emp.getNroPresentacion());
                            carga.setCuit(emp.getCuit());
                            carga.setAnio(emp.getPeriodo());
                            if (!emp.getCargas().contains(carga)) {
                                emp.getCargas().add(carga);
                            }
                        }
                    } else {
//                        System.out.println("cargaFamilia es Objeto");
                        Carga carga = new Carga();
                        JSONObject cargaObj = cargasObj.getJSONObject("cargaFamilia");

                        if (cargaObj.has("tipoDoc")) {
                            carga.setTipoDoc(obtenerTipoDoc(String.valueOf(cargaObj.get("tipoDoc"))));
//                                    System.out.println("tipoDoc: " + element2.getElementsByTagName("tipoDoc").item(0).getTextContent());
                        }

                        if (cargaObj.has("nroDoc")) {
                            carga.setNroDoc(String.valueOf(cargaObj.get("nroDoc")));
//                                    System.out.println("nroDoc: " + element2.getElementsByTagName("nroDoc").item(0).getTextContent());
                        }

                        if (cargaObj.has("apellido")) {
                            carga.setApellido(cargaObj.getString("apellido"));
//                                    System.out.println("apellido: " + element2.getElementsByTagName("apellido").item(0).getTextContent());
                        }

                        if (cargaObj.has("nombre")) {
                            carga.setNombre(cargaObj.getString("nombre"));
//                                    System.out.println("nombre: " + element2.getElementsByTagName("nombre").item(0).getTextContent());
                        }

                        if (cargaObj.has("fechaNac")) {
                            carga.setFechaNac(cargaObj.getString("fechaNac"));
//                                    System.out.println("fechaNac: " + element2.getElementsByTagName("fechaNac").item(0).getTextContent());
                        }

                        if (cargaObj.has("mesDesde")) {
                            carga.setMesDesde(obtenerMes(String.valueOf(cargaObj.get("mesDesde"))));
//                                    System.out.println("mesDesde: " + element2.getElementsByTagName("mesDesde").item(0).getTextContent());
                        }

                        if (cargaObj.has("mesHasta")) {
                            carga.setMesHasta(obtenerMes(String.valueOf(cargaObj.get("mesHasta"))));
//                                    System.out.println("mesHasta: " + element2.getElementsByTagName("mesHasta").item(0).getTextContent());
                        }

                        if (cargaObj.has("parentesco")) {
                            carga.setParentesco(obtenerParentesco(String.valueOf(cargaObj.get("parentesco"))));
//                                    System.out.println("parentesco: " + element2.getElementsByTagName("parentesco").item(0).getTextContent());
                        }

                        if (cargaObj.has("vigenteProximosPeriodos")) {
                            carga.setVigenteProximosPeriodos(cargaObj.getString("vigenteProximosPeriodos"));
//                                    System.out.println("vigenteProximosPeriodos: " + element2.getElementsByTagName("vigenteProximosPeriodos").item(0).getTextContent());
                        }

                        if (cargaObj.has("porcentajeDeduccion")) {
                            carga.setPorcentajeDeduccion(String.valueOf(cargaObj.get("porcentajeDeduccion")));
//                                    System.out.println("porcentajeDeduccion: " + element2.getElementsByTagName("porcentajeDeduccion").item(0).getTextContent());
                        }

                        carga.setNroPresentacion(emp.getNroPresentacion());
                        carga.setCuit(emp.getCuit());
                        carga.setAnio(emp.getPeriodo());
                        if (!emp.getCargas().contains(carga)) {
                            emp.getCargas().add(carga);
                        }
                    }
                } else {

                }

            } else {
//                System.out.println("no se encuentra Elemento cargaFamilia");
            }
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Ganancias">
            try {
                if (jsonObj.has("ganLiqOtrosEmpEnt")) {
                    JSONObject gananObj = jsonObj.getJSONObject("ganLiqOtrosEmpEnt");
//                        System.out.println(nl.getLength());
                    if (gananObj.has("empEnt")) {
//                        System.out.println(gananObj.get("empEnt"));
                        if (gananObj.get("empEnt") instanceof JSONObject) {
                            JSONObject empObj = gananObj.getJSONObject("empEnt");
                            if (empObj.has("ingresosAportes")) {
//                                System.out.println(empObj.get("ingresosAportes").getClass());
                                if (empObj.get("ingresosAportes") instanceof JSONObject) {
                                    JSONObject ingrAportObj = empObj.getJSONObject("ingresosAportes");
                                    if (ingrAportObj.has("ingAp")) {
                                        if (ingrAportObj.get("ingAp") instanceof JSONArray) {
                                            JSONArray ingrAptArray = ingrAportObj.getJSONArray("ingAp");

                                            Ganancia ganancia = new Ganancia();

                                            if (empObj.has("cuit")) {
                                                ganancia.setCuitG(String.valueOf(empObj.get("cuit")));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                                            }

                                            if (empObj.has("denominacion")) {
                                                ganancia.setDenominacion(empObj.getString("denominacion"));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                                            }
                                            for (Object object : ingrAptArray) {
                                                JSONObject ingrAptObj = (JSONObject) object;

                                                IngresosAporte ingreso = obtenerIngreso(ingrAptObj);

                                                ingreso.setNroPresentacion(emp.getNroPresentacion());
                                                ingreso.setCuit(emp.getCuit());
                                                ingreso.setAnio(emp.getPeriodo());
                                                ingreso.setCuitG(ganancia.getCuitG());

                                                ganancia.getIngresos().add(ingreso);

                                            }
                                            ganancia.setNroPresentacion(emp.getNroPresentacion());
                                            ganancia.setCuit(emp.getCuit());
                                            ganancia.setAnio(emp.getPeriodo());
                                            emp.getGanancias().add(ganancia);
                                        } else {
                                            JSONObject ingrAptObj = ingrAportObj.getJSONObject("ingAp");
                                            Ganancia ganancia = new Ganancia();

                                            if (empObj.has("cuit")) {
                                                ganancia.setCuitG(String.valueOf(empObj.get("cuit")));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                                            }

                                            if (empObj.has("denominacion")) {
                                                ganancia.setDenominacion(empObj.getString("denominacion"));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                                            }

                                            IngresosAporte ingreso = obtenerIngreso(ingrAptObj);

                                            ingreso.setNroPresentacion(emp.getNroPresentacion());
                                            ingreso.setCuit(emp.getCuit());
                                            ingreso.setAnio(emp.getPeriodo());
                                            ingreso.setCuitG(ganancia.getCuitG());

                                            ganancia.getIngresos().add(ingreso);

                                            ganancia.setNroPresentacion(emp.getNroPresentacion());
                                            ganancia.setCuit(emp.getCuit());
                                            ganancia.setAnio(emp.getPeriodo());
                                            emp.getGanancias().add(ganancia);
                                        }
                                    }
                                } else {
                                    JSONArray ingresosAptArray = empObj.getJSONArray("ingresosAportes");
                                    for (Object obje : ingresosAptArray) {
                                        JSONObject ingrAportObj = (JSONObject) obje;
                                        if (ingrAportObj.has("ingAp")) {
                                            if (ingrAportObj.get("ingAp") instanceof JSONArray) {
                                                JSONArray ingrAptArray = ingrAportObj.getJSONArray("ingAp");

                                                Ganancia ganancia = new Ganancia();

                                                if (empObj.has("cuit")) {
                                                    ganancia.setCuitG(String.valueOf(empObj.get("cuit")));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                                                }

                                                if (empObj.has("denominacion")) {
                                                    ganancia.setDenominacion(empObj.getString("denominacion"));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                                                }
                                                for (Object object : ingrAptArray) {
                                                    JSONObject ingrAptObj = (JSONObject) object;

                                                    IngresosAporte ingreso = obtenerIngreso(ingrAptObj);

                                                    ingreso.setNroPresentacion(emp.getNroPresentacion());
                                                    ingreso.setCuit(emp.getCuit());
                                                    ingreso.setAnio(emp.getPeriodo());
                                                    ingreso.setCuitG(ganancia.getCuitG());

                                                    ganancia.getIngresos().add(ingreso);

                                                }
                                                ganancia.setNroPresentacion(emp.getNroPresentacion());
                                                ganancia.setCuit(emp.getCuit());
                                                ganancia.setAnio(emp.getPeriodo());
                                                emp.getGanancias().add(ganancia);
                                            } else {
                                                JSONObject ingrAptObj = ingrAportObj.getJSONObject("ingAp");
                                                Ganancia ganancia = new Ganancia();

                                                if (empObj.has("cuit")) {
                                                    ganancia.setCuitG(String.valueOf(empObj.get("cuit")));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                                                }

                                                if (empObj.has("denominacion")) {
                                                    ganancia.setDenominacion(empObj.getString("denominacion"));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                                                }

                                                IngresosAporte ingreso = obtenerIngreso(ingrAptObj);

                                                ingreso.setNroPresentacion(emp.getNroPresentacion());
                                                ingreso.setCuit(emp.getCuit());
                                                ingreso.setAnio(emp.getPeriodo());
                                                ingreso.setCuitG(ganancia.getCuitG());

                                                ganancia.getIngresos().add(ingreso);

                                                ganancia.setNroPresentacion(emp.getNroPresentacion());
                                                ganancia.setCuit(emp.getCuit());
                                                ganancia.setAnio(emp.getPeriodo());
                                                emp.getGanancias().add(ganancia);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
//                            System.out.println("es un Array empEnt");
                            JSONArray empArra = gananObj.getJSONArray("empEnt");
                            for (Object obj : empArra) {
                                JSONObject empObj = (JSONObject) obj;
                                if (empObj.has("ingresosAportes")) {
                                    if (empObj.get("ingresosAportes") instanceof JSONObject) {
                                        JSONObject ingrAportObj = empObj.getJSONObject("ingresosAportes");
                                        if (ingrAportObj.has("ingAp")) {
                                            if (ingrAportObj.get("ingAp") instanceof JSONArray) {
                                                JSONArray ingrAptArray = ingrAportObj.getJSONArray("ingAp");

                                                Ganancia ganancia = new Ganancia();

                                                if (empObj.has("cuit")) {
                                                    ganancia.setCuitG(String.valueOf(empObj.get("cuit")));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                                                }

                                                if (empObj.has("denominacion")) {
                                                    ganancia.setDenominacion(empObj.getString("denominacion"));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                                                }
                                                for (Object object : ingrAptArray) {
                                                    JSONObject ingrAptObj = (JSONObject) object;

                                                    IngresosAporte ingreso = obtenerIngreso(ingrAptObj);

                                                    ingreso.setNroPresentacion(emp.getNroPresentacion());
                                                    ingreso.setCuit(emp.getCuit());
                                                    ingreso.setAnio(emp.getPeriodo());
                                                    ingreso.setCuitG(ganancia.getCuitG());

                                                    ganancia.getIngresos().add(ingreso);

                                                }
                                                ganancia.setNroPresentacion(emp.getNroPresentacion());
                                                ganancia.setCuit(emp.getCuit());
                                                ganancia.setAnio(emp.getPeriodo());
                                                emp.getGanancias().add(ganancia);
                                            } else {
                                                JSONObject ingrAptObj = ingrAportObj.getJSONObject("ingAp");
                                                Ganancia ganancia = new Ganancia();

                                                if (empObj.has("cuit")) {
                                                    ganancia.setCuitG(String.valueOf(empObj.get("cuit")));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                                                }

                                                if (empObj.has("denominacion")) {
                                                    ganancia.setDenominacion(empObj.getString("denominacion"));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                                                }

                                                IngresosAporte ingreso = obtenerIngreso(ingrAptObj);

                                                ingreso.setNroPresentacion(emp.getNroPresentacion());
                                                ingreso.setCuit(emp.getCuit());
                                                ingreso.setAnio(emp.getPeriodo());
                                                ingreso.setCuitG(ganancia.getCuitG());

                                                ganancia.getIngresos().add(ingreso);

                                                ganancia.setNroPresentacion(emp.getNroPresentacion());
                                                ganancia.setCuit(emp.getCuit());
                                                ganancia.setAnio(emp.getPeriodo());
                                                emp.getGanancias().add(ganancia);
                                            }
                                        }
                                    } else {
                                        JSONArray ingresosAptArray = empObj.getJSONArray("ingresosAportes");
                                        for (Object obje : ingresosAptArray) {
                                            JSONObject ingrAportObj = (JSONObject) obje;
                                            if (ingrAportObj.has("ingAp")) {
                                                if (ingrAportObj.get("ingAp") instanceof JSONArray) {
                                                    JSONArray ingrAptArray = ingrAportObj.getJSONArray("ingAp");

                                                    Ganancia ganancia = new Ganancia();

                                                    if (empObj.has("cuit")) {
                                                        ganancia.setCuitG(String.valueOf(empObj.get("cuit")));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                                                    }

                                                    if (empObj.has("denominacion")) {
                                                        ganancia.setDenominacion(empObj.getString("denominacion"));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                                                    }
                                                    for (Object object : ingrAptArray) {
                                                        JSONObject ingrAptObj = (JSONObject) object;

                                                        IngresosAporte ingreso = obtenerIngreso(ingrAptObj);

                                                        ingreso.setNroPresentacion(emp.getNroPresentacion());
                                                        ingreso.setCuit(emp.getCuit());
                                                        ingreso.setAnio(emp.getPeriodo());
                                                        ingreso.setCuitG(ganancia.getCuitG());

                                                        ganancia.getIngresos().add(ingreso);

                                                    }
                                                    ganancia.setNroPresentacion(emp.getNroPresentacion());
                                                    ganancia.setCuit(emp.getCuit());
                                                    ganancia.setAnio(emp.getPeriodo());
                                                    emp.getGanancias().add(ganancia);
                                                } else {
                                                    JSONObject ingrAptObj = ingrAportObj.getJSONObject("ingAp");
                                                    Ganancia ganancia = new Ganancia();

                                                    if (empObj.has("cuit")) {
                                                        ganancia.setCuitG(String.valueOf(empObj.get("cuit")));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                                                    }

                                                    if (empObj.has("denominacion")) {
                                                        ganancia.setDenominacion(empObj.getString("denominacion"));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                                                    }

                                                    IngresosAporte ingreso = obtenerIngreso(ingrAptObj);

                                                    ingreso.setNroPresentacion(emp.getNroPresentacion());
                                                    ingreso.setCuit(emp.getCuit());
                                                    ingreso.setAnio(emp.getPeriodo());
                                                    ingreso.setCuitG(ganancia.getCuitG());

                                                    ganancia.getIngresos().add(ingreso);

                                                    ganancia.setNroPresentacion(emp.getNroPresentacion());
                                                    ganancia.setCuit(emp.getCuit());
                                                    ganancia.setAnio(emp.getPeriodo());
                                                    emp.getGanancias().add(ganancia);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("nameFile: " + nameFile);
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Deducciones">
           
            if (jsonObj.has("deducciones")) {
                JSONObject deduccObj = jsonObj.getJSONObject("deducciones");
                if (deduccObj.has("deduccion")) {
                    if (deduccObj.get("deduccion") instanceof JSONArray) {
                        JSONArray deduccArray = deduccObj.getJSONArray("deduccion");
                         
                        String vs = "";
                        for (Object object : deduccArray) {
                            Deduccion deduccion = new Deduccion();
                            JSONObject deduObj = (JSONObject) object;

                            deduccion.setId(String.valueOf(id_have_periodo++));
                            if (deduObj.has("tipoDoc")) {
                                deduccion.setTipoDoc(obtenerTipoDoc(String.valueOf(deduObj.get("tipoDoc"))));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                            }else{
                                deduccion.setTipoDoc("CUIT");
                            }

                            if (deduObj.has("tipo")) {
                                deduccion.setTipoDeduccion(obtenerDeduccion(String.valueOf(deduObj.get("tipo"))));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                            }else{
                                 deduccion.setTipoDeduccion("SIN TIPO");
                            }

                            if (deduObj.has("nroDoc")) {
                                deduccion.setNroDoc(String.valueOf(deduObj.get("nroDoc")));
                            }else{
                                
                                Double fiveDigits = 10000 + Math.random() * 90000;
                                //System.out.println("nrdo:"+fiveDigits.intValue());
                                deduccion.setNroDoc(String.valueOf(fiveDigits.intValue()));
                                
                            }
                            

                            if (deduObj.has("denominacion")) {
                                deduccion.setDenominacion(deduObj.getString("denominacion"));
                            }else{
                                deduccion.setDenominacion("SIN DENOMINACION");
                            }

                            if (deduObj.has("descBasica")) {
                                deduccion.setDescBasica(deduObj.getString("descBasica"));
                            }

                            if (deduObj.has("montoTotal")) {
                                deduccion.setMontoTotal(String.valueOf(deduObj.get("montoTotal")));
                            }

                            if (deduObj.has("periodos")) {
                                JSONObject periodosObj = deduObj.getJSONObject("periodos");
                                if (periodosObj.has("periodo")) {
                                    if (periodosObj.get("periodo") instanceof JSONArray) {
                                        JSONArray periodoArray = periodosObj.getJSONArray("periodo");
                                        for (Object objec : periodoArray) {
                                            Periodo periodo = new Periodo();
                                            JSONObject periodObj = (JSONObject) objec;

                                            if (periodObj.has("mesDesde")) {
                                                periodo.setMesDesde(obtenerMes(String.valueOf(periodObj.get("mesDesde"))));
                                            }

                                            if (periodObj.has("mesHasta")) {
                                                periodo.setMesHasta(obtenerMes(String.valueOf(periodObj.get("mesHasta"))));
                                            }

                                            if (periodObj.has("montoMensual")) {
                                                periodo.setMontoMensual(String.valueOf(periodObj.get("montoMensual")));
                                            }

                                            periodo.setNroPresentacion(emp.getNroPresentacion());
                                            periodo.setCuit(emp.getCuit());
                                            periodo.setIdParent(deduccion.getId());
                                            periodo.setAnio(emp.getPeriodo());
                                            periodo.setTipo(deduccion.getTipoDeduccion());
                                            periodo.setNroDoc(deduccion.getNroDoc());
                                            deduccion.getPeriodos().add(periodo);
                                        }
                                    } else {
                                        Periodo periodo = new Periodo();
                                        JSONObject periodObj = periodosObj.getJSONObject("periodo");

                                        if (periodObj.has("mesDesde")) {
                                            periodo.setMesDesde(obtenerMes(String.valueOf(periodObj.get("mesDesde"))));
                                        }

                                        if (periodObj.has("mesHasta")) {
                                            periodo.setMesHasta(obtenerMes(String.valueOf(periodObj.get("mesHasta"))));
                                        }

                                        if (periodObj.has("montoMensual")) {
                                            periodo.setMontoMensual(String.valueOf(periodObj.get("montoMensual")));
                                        }

                                        periodo.setNroPresentacion(emp.getNroPresentacion());
                                        periodo.setCuit(emp.getCuit());
                                        periodo.setIdParent(deduccion.getId());
                                        periodo.setAnio(emp.getPeriodo());
                                        periodo.setTipo(deduccion.getTipoDeduccion());
                                        periodo.setNroDoc(deduccion.getNroDoc());
                                        deduccion.getPeriodos().add(periodo);

                                    }
                                }
                            } else if (deduObj.has("detalles")) {
                                if (deduObj.has("descAdicional")) {
                                    deduccion.setDescBasica(deduObj.getString("descAdicional"));
                                }
                                JSONObject periodosObj = deduObj.getJSONObject("detalles");
                                if (periodosObj.has("detalle")) {
                                    if (periodosObj.get("detalle") instanceof JSONArray) {
                                        JSONArray periodoArray = periodosObj.getJSONArray("detalle");
                                        for (Object objec : periodoArray) {
                                            Periodo periodo = new Periodo();
                                            JSONObject periodObj = (JSONObject) objec;

                                            if (periodObj.has("nombre") && periodObj.get("nombre").equals("mes")) {
//                                                System.out.println(periodObj.get("nombre") + " - " + emp.getCuit() + " - " + emp.getPeriodo());

//                                                if (periodObj.has("mesDesde")) {
                                                periodo.setMesDesde(obtenerMes(String.valueOf(periodObj.get("valor"))));
//                                                }

//                                                if (periodObj.has("mesHasta")) {
                                                periodo.setMesHasta(obtenerMes(String.valueOf(periodObj.get("valor"))));
//                                                }

//                                                if (periodObj.has("montoMensual")) {
                                                periodo.setMontoMensual(deduccion.getMontoTotal());
//                                                }

                                                periodo.setNroPresentacion(emp.getNroPresentacion());
                                                periodo.setCuit(emp.getCuit());
                                                periodo.setIdParent(deduccion.getId());
                                                periodo.setAnio(emp.getPeriodo());
                                                periodo.setTipo(deduccion.getTipoDeduccion());
                                                periodo.setNroDoc(deduccion.getNroDoc());
                                                deduccion.getPeriodos().add(periodo);
                                            }
                                        }
                                    } else {
                                        Periodo periodo = new Periodo();
                                        JSONObject periodObj = periodosObj.getJSONObject("detalle");

                                        if (periodObj.has("nombre") && periodObj.get("nombre").equals("mes")) {
//                                            System.out.println(periodObj.get("nombre") + " - " + emp.getCuit() + " - " + emp.getPeriodo());

//                                            if (periodObj.has("mesDesde")) {
                                            periodo.setMesDesde(obtenerMes(String.valueOf(periodObj.get("valor"))));
//                                            }

//                                            if (periodObj.has("mesHasta")) {
                                            periodo.setMesHasta(obtenerMes(String.valueOf(periodObj.get("valor"))));
//                                            }

//                                            if (periodObj.has("montoMensual")) {
                                            periodo.setMontoMensual(deduccion.getMontoTotal());
//                                            }

                                            periodo.setNroPresentacion(emp.getNroPresentacion());
                                            periodo.setCuit(emp.getCuit());
                                            periodo.setIdParent(deduccion.getId());
                                            periodo.setAnio(emp.getPeriodo());
                                            periodo.setTipo(deduccion.getTipoDeduccion());
                                            periodo.setNroDoc(deduccion.getNroDoc());
                                            deduccion.getPeriodos().add(periodo);
                                        }

                                    }
                                }
                            }

                            deduccion.setNroPresentacion(emp.getNroPresentacion());
                            deduccion.setCuit(emp.getCuit());
                            deduccion.setAnio(emp.getPeriodo());
//                            if (deduccion.toString().contains("23284706374") && deduccion.toString().contains("2022") && deduccion.toString().contains("nroPresentacion=")) {
//                                System.out.println(deduccion + " - " + emp.getCuit() + " - " + emp.getPeriodo());
//                            }
                            emp.getDeducciones().add(deduccion);
                        }
                    } else {
                        Deduccion deduccion = new Deduccion();
                        JSONObject deduObj = deduccObj.getJSONObject("deduccion");

                        deduccion.setId(String.valueOf(id_have_periodo++));
                        if (deduObj.has("tipoDoc")) {
                            deduccion.setTipoDoc(obtenerTipoDoc(String.valueOf(deduObj.get("tipoDoc"))));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                        }

                        if (deduObj.has("tipo")) {
                            deduccion.setTipoDeduccion(obtenerDeduccion(String.valueOf(deduObj.get("tipo"))));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                        }

                        if (deduObj.has("nroDoc")) {
                            deduccion.setNroDoc(String.valueOf(deduObj.get("nroDoc")));
                        }

                        if (deduObj.has("denominacion")) {
                            deduccion.setDenominacion(deduObj.getString("denominacion"));
                        }

                        if (deduObj.has("descBasica")) {
                            deduccion.setDescBasica(deduObj.getString("descBasica"));
                        }

                        if (deduObj.has("montoTotal")) {
                            deduccion.setMontoTotal(String.valueOf(deduObj.get("montoTotal")));
                        }

                        if (deduObj.has("periodos")) {
                            JSONObject periodosObj = deduObj.getJSONObject("periodos");
                            if (periodosObj.has("periodo")) {
                                if (periodosObj.get("periodo") instanceof JSONArray) {
                                    JSONArray periodoArray = periodosObj.getJSONArray("periodo");
                                    for (Object object : periodoArray) {
                                        Periodo periodo = new Periodo();
                                        JSONObject periodObj = (JSONObject) object;

                                        if (periodObj.has("mesDesde")) {
                                            periodo.setMesDesde(obtenerMes(String.valueOf(periodObj.get("mesDesde"))));
                                        }

                                        if (periodObj.has("mesHasta")) {
                                            periodo.setMesHasta(obtenerMes(String.valueOf(periodObj.get("mesHasta"))));
                                        }

                                        if (periodObj.has("montoMensual")) {
                                            periodo.setMontoMensual(String.valueOf(periodObj.get("montoMensual")));
                                        }

                                        periodo.setNroPresentacion(emp.getNroPresentacion());
                                        periodo.setTipo(deduccion.getTipoDeduccion());
                                        periodo.setCuit(emp.getCuit());
                                        periodo.setIdParent(deduccion.getId());
                                        periodo.setAnio(emp.getPeriodo());
                                        periodo.setNroDoc(deduccion.getNroDoc());
                                        deduccion.getPeriodos().add(periodo);
                                    }
                                } else {
                                    Periodo periodo = new Periodo();
                                    JSONObject periodObj = periodosObj.getJSONObject("periodo");

                                    if (periodObj.has("mesDesde")) {
                                        periodo.setMesDesde(obtenerMes(String.valueOf(periodObj.get("mesDesde"))));
                                    }

                                    if (periodObj.has("mesHasta")) {
                                        periodo.setMesHasta(obtenerMes(String.valueOf(periodObj.get("mesHasta"))));
                                    }

                                    if (periodObj.has("montoMensual")) {
                                        periodo.setMontoMensual(String.valueOf(periodObj.get("montoMensual")));
                                    }

                                    periodo.setNroPresentacion(emp.getNroPresentacion());
                                    periodo.setTipo(deduccion.getTipoDeduccion());
                                    periodo.setCuit(emp.getCuit());
                                    periodo.setIdParent(deduccion.getId());
                                    periodo.setAnio(emp.getPeriodo());
                                    periodo.setNroDoc(deduccion.getNroDoc());
                                    deduccion.getPeriodos().add(periodo);

                                }
                            }
                        } else if (deduObj.has("detalles")) {
                            if (deduObj.has("descAdicional")) {
                                deduccion.setDescBasica(deduObj.getString("descAdicional"));
                            }
                            JSONObject periodosObj = deduObj.getJSONObject("detalles");
                            if (periodosObj.has("detalle")) {
                                if (periodosObj.get("detalle") instanceof JSONArray) {
                                    JSONArray periodoArray = periodosObj.getJSONArray("detalle");
                                    for (Object objec : periodoArray) {
                                        Periodo periodo = new Periodo();
                                        JSONObject periodObj = (JSONObject) objec;

                                        if (periodObj.has("nombre") && periodObj.get("nombre").equals("mes")) {
//                                            System.out.println(periodObj.get("nombre") + " - " + emp.getCuit() + " - " + emp.getPeriodo());

//                                            if (periodObj.has("mesDesde")) {
                                            periodo.setMesDesde(obtenerMes(String.valueOf(periodObj.get("valor"))));
//                                            }

//                                            if (periodObj.has("mesHasta")) {
                                            periodo.setMesHasta(obtenerMes(String.valueOf(periodObj.get("valor"))));
//                                            }

//                                            if (periodObj.has("montoMensual")) {
                                            periodo.setMontoMensual(deduccion.getMontoTotal());
//                                            }

                                            periodo.setNroPresentacion(emp.getNroPresentacion());
                                            periodo.setCuit(emp.getCuit());
                                            periodo.setIdParent(deduccion.getId());
                                            periodo.setAnio(emp.getPeriodo());
                                            periodo.setTipo(deduccion.getTipoDeduccion());
                                            periodo.setNroDoc(deduccion.getNroDoc());
                                            deduccion.getPeriodos().add(periodo);
                                        }
                                    }
                                } else {
                                    Periodo periodo = new Periodo();
                                    JSONObject periodObj = periodosObj.getJSONObject("detalle");

                                    if (periodObj.has("nombre") && periodObj.get("nombre").equals("mes")) {
//                                        System.out.println(periodObj.get("nombre") + " - " + emp.getCuit() + " - " + emp.getPeriodo());

//                                        if (periodObj.has("mesDesde")) {
                                        periodo.setMesDesde(obtenerMes(String.valueOf(periodObj.get("valor"))));
//                                        }

//                                        if (periodObj.has("mesHasta")) {
                                        periodo.setMesHasta(obtenerMes(String.valueOf(periodObj.get("valor"))));
//                                        }

//                                        if (periodObj.has("montoMensual")) {
                                        periodo.setMontoMensual(deduccion.getMontoTotal());
//                                        }

                                        periodo.setNroPresentacion(emp.getNroPresentacion());
                                        periodo.setCuit(emp.getCuit());
                                        periodo.setIdParent(deduccion.getId());
                                        periodo.setAnio(emp.getPeriodo());
                                        periodo.setTipo(deduccion.getTipoDeduccion());
                                        periodo.setNroDoc(deduccion.getNroDoc());
                                        deduccion.getPeriodos().add(periodo);
                                    }

                                }
                            }
                        }

//                        if (deduccion.toString().contains("23284706374") && deduccion.toString().contains("2022") && deduccion.toString().contains("nroPresentacion=")) {
//                            System.out.println(deduccion + " - " + emp.getCuit() + " - " + emp.getPeriodo());
//                        }
                        deduccion.setNroPresentacion(emp.getNroPresentacion());
                        deduccion.setCuit(emp.getCuit());
                        deduccion.setAnio(emp.getPeriodo());
                        emp.getDeducciones().add(deduccion);

                    }
                }
            }
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Retenciones">
            if (jsonObj.has("retPerPagos")) {
                JSONObject retencioPagosnObj = jsonObj.getJSONObject("retPerPagos");
//                        System.out.println(nl.getLength());
                if (retencioPagosnObj.has("retPerPago")) {
                    if (retencioPagosnObj.get("retPerPago") instanceof JSONArray) {
                        JSONArray retencioPagoArray = retencioPagosnObj.getJSONArray("retPerPago");
                        int countRet = 0;
                        for (Object object : retencioPagoArray) {
                            JSONObject retencioPagonObj = (JSONObject) object;
                            Retencion retencion = new Retencion();

                            retencion.setCod(String.valueOf(++countRet));

                            retencion.setId(String.valueOf(id_have_periodo++));
                            if (retencioPagonObj.has("descBasica")) {
                                retencion.setDescBasica(retencioPagonObj.getString("descBasica"));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                            }

                            if (retencioPagonObj.has("tipo")) {
                                retencion.setTipoRetencion(obtenerRetPerPagCuenta(String.valueOf(retencioPagonObj.get("tipo"))));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                            }

                            if (retencioPagonObj.has("montoTotal")) {
                                retencion.setMontoTotal(String.valueOf(retencioPagonObj.get("montoTotal")));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                            }

                            if (retencioPagonObj.has("nroDoc")) {
                                retencion.setNroDoc(String.valueOf(retencioPagonObj.get("nroDoc")));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                            }

                            if (retencioPagonObj.has("periodos")) {
                                JSONObject periodosObj = retencioPagonObj.getJSONObject("periodos");
                                if (periodosObj.has("periodo")) {
                                    if (periodosObj.get("periodo") instanceof JSONArray) {
                                        JSONArray perArray = periodosObj.getJSONArray("periodo");
                                        for (Object objec : perArray) {
                                            Periodo periodo = new Periodo();
                                            JSONObject periodObj = (JSONObject) objec;

                                            if (periodObj.has("mesDesde")) {
                                                periodo.setMesDesde(obtenerMes(String.valueOf(periodObj.get("mesDesde"))));
                                            }

                                            if (periodObj.has("mesHasta")) {
                                                periodo.setMesHasta(obtenerMes(String.valueOf(periodObj.get("mesHasta"))));
                                            }

                                            if (periodObj.has("montoMensual")) {
                                                periodo.setMontoMensual(String.valueOf(periodObj.get("montoMensual")));
                                            }

                                            periodo.setNroPresentacion(emp.getNroPresentacion());
                                            periodo.setCuit(emp.getCuit());
                                            periodo.setIdParent(retencion.getId());
                                            periodo.setAnio(emp.getPeriodo());

                                            periodo.setNroDoc(retencion.getCod());
                                            retencion.getPeriodos().add(periodo);
                                        }
                                    } else {
                                        Periodo periodo = new Periodo();
                                        JSONObject periodObj = periodosObj.getJSONObject("periodo");

                                        if (periodObj.has("mesDesde")) {
                                            periodo.setMesDesde(obtenerMes(String.valueOf(periodObj.get("mesDesde"))));
                                        }

                                        if (periodObj.has("mesHasta")) {
                                            periodo.setMesHasta(obtenerMes(String.valueOf(periodObj.get("mesHasta"))));
                                        }

                                        if (periodObj.has("montoMensual")) {
                                            periodo.setMontoMensual(String.valueOf(periodObj.get("montoMensual")));
                                        }

                                        periodo.setNroPresentacion(emp.getNroPresentacion());
                                        periodo.setCuit(emp.getCuit());
                                        periodo.setIdParent(retencion.getId());
                                        periodo.setAnio(emp.getPeriodo());
                                        periodo.setNroDoc(retencion.getCod());
                                        retencion.getPeriodos().add(periodo);

                                    }
                                }
                            }

                            retencion.setNroPresentacion(emp.getNroPresentacion());
                            retencion.setCuit(emp.getCuit());
                            retencion.setAnio(emp.getPeriodo());
                            emp.getRetenciones().add(retencion);
                        }
                    } else {
                        JSONObject retencioPagonObj = retencioPagosnObj.getJSONObject("retPerPago");
                        Retencion retencion = new Retencion();
                        int countRet = 0;

                        retencion.setId(String.valueOf(id_have_periodo++));
                        retencion.setCod(String.valueOf(++countRet));

                        if (retencioPagonObj.has("descBasica")) {
                            retencion.setDescBasica(retencioPagonObj.getString("descBasica"));
//                                    System.out.println("cuit: " + element2.getElementsByTagName("cuit").item(0).getTextContent());
                        }

                        if (retencioPagonObj.has("tipo")) {
                            retencion.setTipoRetencion(obtenerRetPerPagCuenta(String.valueOf(retencioPagonObj.get("tipo"))));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                        }

                        if (retencioPagonObj.has("montoTotal")) {
                            retencion.setMontoTotal(String.valueOf(retencioPagonObj.get("montoTotal")));
//                                    System.out.println("denominacion: " + element2.getElementsByTagName("denominacion").item(0).getTextContent());
                        }

                        if (retencioPagonObj.has("periodos")) {
                            JSONObject periodosObj = retencioPagonObj.getJSONObject("periodos");
                            if (periodosObj.has("periodo")) {
                                if (periodosObj.get("periodo") instanceof JSONArray) {
                                    JSONArray periodoArray = periodosObj.getJSONArray("periodo");
                                    for (Object object : periodoArray) {
                                        Periodo periodo = new Periodo();
                                        JSONObject periodObj = (JSONObject) object;

                                        if (periodObj.has("mesDesde")) {
                                            periodo.setMesDesde(obtenerMes(String.valueOf(periodObj.get("mesDesde"))));
                                        }

                                        if (periodObj.has("mesHasta")) {
                                            periodo.setMesHasta(obtenerMes(String.valueOf(periodObj.get("mesHasta"))));
                                        }

                                        if (periodObj.has("montoMensual")) {
                                            periodo.setMontoMensual(String.valueOf(periodObj.get("montoMensual")));
                                        }

                                        periodo.setNroPresentacion(emp.getNroPresentacion());
                                        periodo.setCuit(emp.getCuit());
                                        periodo.setIdParent(retencion.getId());
                                        periodo.setAnio(emp.getPeriodo());
                                        periodo.setNroDoc(retencion.getCod());
                                        retencion.getPeriodos().add(periodo);
                                    }
                                } else {
                                    Periodo periodo = new Periodo();
                                    JSONObject periodObj = periodosObj.getJSONObject("periodo");

                                    if (periodObj.has("mesDesde")) {
                                        periodo.setMesDesde(obtenerMes(String.valueOf(periodObj.get("mesDesde"))));
                                    }

                                    if (periodObj.has("mesHasta")) {
                                        periodo.setMesHasta(obtenerMes(String.valueOf(periodObj.get("mesHasta"))));
                                    }

                                    if (periodObj.has("montoMensual")) {
                                        periodo.setMontoMensual(String.valueOf(periodObj.get("montoMensual")));
                                    }

                                    periodo.setNroPresentacion(emp.getNroPresentacion());
                                    periodo.setCuit(emp.getCuit());
                                    periodo.setIdParent(retencion.getId());
                                    periodo.setAnio(emp.getPeriodo());
                                    periodo.setNroDoc(retencion.getCod());
                                    retencion.getPeriodos().add(periodo);

                                }
                            }
                        }

                        retencion.setNroPresentacion(emp.getNroPresentacion());
                        retencion.setCuit(emp.getCuit());
                        retencion.setAnio(emp.getPeriodo());
                        emp.getRetenciones().add(retencion);
                    }
                }

            } else {
//                        System.out.println("no se encuentra Elemento cargaFamilia");
            }
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="DatosAdiconales">
            if (jsonObj.has("datosAdicionales")) {
                JSONObject datAdicesObj = jsonObj.getJSONObject("datosAdicionales");
//                        System.out.println(nl.getLength());
                if (datAdicesObj.has("datoAdicional")) {
                    if (datAdicesObj.get("datoAdicional") instanceof JSONArray) {
                        JSONArray datAdicArray = datAdicesObj.getJSONArray("datoAdicional");
                        for (Object object : datAdicArray) {
                            JSONObject datAdicObj = (JSONObject) object;
                            DatoAdicional datoAd = new DatoAdicional();
                            datoAd.setId(String.valueOf(count));

                            if (datAdicObj.has("nombre")) {
                                datoAd.setNombre(obtenerDatAdicionales(datAdicObj.getString("nombre")));
                            }

                            if (datAdicObj.has("mesDesde")) {
                                datoAd.setMesDesde(obtenerMes(String.valueOf(datAdicObj.get("mesDesde"))));
                            }

                            if (datAdicObj.has("mesHasta")) {
                                datoAd.setMesHasta(obtenerMes(String.valueOf(datAdicObj.get("mesHasta"))));
                            }

                            if (datAdicObj.has("valor")) {
                                datoAd.setValor(datAdicObj.getString("valor"));
                            }

                            datoAd.setNroPresentacion(emp.getNroPresentacion());
                            datoAd.setCuit(emp.getCuit());
                            datoAd.setAnio(emp.getCuit());

                            emp.getDatosAdicionales().add(datoAd);
                        }
                    } else {
                        JSONObject datAdicObj = datAdicesObj.getJSONObject("datoAdicional");
                        DatoAdicional datoAd = new DatoAdicional();
                        datoAd.setId(String.valueOf(count));

                        if (datAdicObj.has("nombre")) {
                            datoAd.setNombre(obtenerDatAdicionales(datAdicObj.getString("nombre")));
                        }

                        if (datAdicObj.has("mesDesde")) {
                            datoAd.setMesDesde(obtenerMes(String.valueOf(datAdicObj.get("mesDesde"))));
                        }

                        if (datAdicObj.has("mesHasta")) {
                            datoAd.setMesHasta(obtenerMes(String.valueOf(datAdicObj.get("mesHasta"))));
                        }

                        if (datAdicObj.has("valor")) {
                            datoAd.setValor(datAdicObj.getString("valor"));
                        }

                        datoAd.setNroPresentacion(emp.getNroPresentacion());
                        datoAd.setCuit(emp.getCuit());
                        datoAd.setAnio(emp.getCuit());
                        emp.getDatosAdicionales().add(datoAd);

                    }
                }

            } else {
//                        System.out.println("no se encuentra Elemento cargaFamilia");
            }
            // </editor-fold>

        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println(emp);
        return emp;
    }
    

    

    public List<Empleado> obtenerDatosXMLDesdeRuta(String ruta) {
        List<Empleado> empleados = new ArrayList<>();

        if (ruta == null || ruta.trim().isEmpty()) {
            System.out.println("La ruta es vacia o nula");
            return empleados;
        }

        File directorio = new File(ruta);
        if (!directorio.exists()) {
            System.out.println("La ruta no existe: " + ruta);
            return empleados;
        }

        if (!directorio.isDirectory()) {
            System.out.println("La ruta no corresponde a un directorio: " + ruta);
            return empleados;
        }

        File[] archivos = directorio.listFiles();
        if (archivos == null || archivos.length == 0) {
            System.out.println("No files in directory");
            return empleados;
        }

        int count = 1;
        for (File archivo : archivos) {
            if (archivo == null || archivo.isDirectory()) {
                continue;
            }

            String nombreArchivo = archivo.getName();
            if (nombreArchivo.contains("$") || !nombreArchivo.toLowerCase().endsWith(".xml")) {
                continue;
            }

            empleados.add(obtenerXMLJSON(
                    archivo.getPath(),
                    directorio.getName(),
                    nombreArchivo,
                    count,
                    false));
            count++;
        }

        return empleados;
    }

    
    public void insertarDatos(Date procDate, String mailId, List<Empleado> empleados) throws SQLException {

        File configFile = new File("E:\\BSM\\SendMail\\Conf\\", "myconfigRisServer.properties");

        Properties props = new Properties();
        MailHelperVO emailStruct = new MailHelperVO();

        emailStruct.setId(mailId);
        try {
            InputStream stream = new FileInputStream(configFile);
            props.load(stream);
        } catch (FileNotFoundException e) {
            System.err.println("FAILED: failed to open config file. " + e);
        } catch (IOException e) {
            System.err.println("FAILED: failed to load propierties. " + e);
        }
        String driver = props.getProperty("driver");

        Connection conn = null;
        Connection connData = null;
        Connection connData2 = null;
        ResultSet rs = null;
        PreparedStatement selReads = null;
        PreparedStatement insLogs = null;

        String hora = new SimpleDateFormat("HH:mm:ss").format(procDate);
        String query = "";
        String logQuery = "";
        List<String> correos = new ArrayList<>();

        try {
            String url = props.getProperty("bsmgridped.url");
            String username = props.getProperty("bsmgridped.username");
            String password = props.getProperty("bsmgridped.password");
            if (Constantes.DEBUG_MODE.booleanValue()) {
                System.err.println("\tURL: " + url + " User: " + username + " Passwd: " + password);
            }
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println("FAILED: failed to load Oracle JDBC driver. " + e);
        }

        String url = props.getProperty("bsmgridbmcone.url");
        String user = props.getProperty("bsmgridbmcone.username");
        String password = props.getProperty("bsmgridbmcone.password");

        List<String> listaDir = new ArrayList<>();

        if (Constantes.DEBUG_MODE) {
            System.err.println("new Connect[" + emailStruct.getInstance() + "]: " + url + ", " + user + "/" + password);
        }
        try {
            connData = DriverManager.getConnection(url, user, password);
        } catch (SQLException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        String url2 = props.getProperty("bsmopert.url");
        String user2 = props.getProperty("bsmopert.username");
        String password2 = props.getProperty("bsmopert.password");
        try {
            connData2 = DriverManager.getConnection(url2, user2, password2);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        boolean isFiles = false;
        String tablaInfo = "";
        String files = "";
        String file = "";

        tablaInfo += "<br>";
        System.out.println(props.getProperty("comedores.port"));

        List<Carga> cargas = new ArrayList<>();
        List<Ganancia> ganancias = new ArrayList<>();
        List<IngresosAporte> ingresos = new ArrayList<>();
        List<Deduccion> deducciones = new ArrayList<>();
        List<Periodo> periodosDe = new ArrayList<>();
        List<Retencion> retenciones = new ArrayList<>();
        List<Periodo> periodosRe = new ArrayList<>();
        List<Detalle> detallesRe = new ArrayList<>();
        List<DatoAdicional> datosAdiconales = new ArrayList<>();
        List<OtrosEmp> otrosEmp = new ArrayList<>();

        int countX = 1;

        connData.setAutoCommit(false);

        String cadena = "DELETE FROM BSM_SIRADIG_ARG_EMPLEADOS ";
        selReads = connData.prepareStatement(cadena);
        int resuldel = selReads.executeUpdate(cadena);
        System.out.println("Delete resultado: " + resuldel);

        cadena = "DELETE FROM BSM_SIRADIG_ARG_CARGA";
        selReads = connData.prepareStatement(cadena);
        resuldel = selReads.executeUpdate(cadena);
        System.out.println("Delete BSM_SIRADIG_ARG_CARGA resultado: " + resuldel);

        cadena = "DELETE FROM BSM_SIRADIG_ARG_GANANCIA";
        selReads = connData.prepareStatement(cadena);
        resuldel = selReads.executeUpdate(cadena);
        System.out.println("Delete BSM_SIRADIG_ARG_GANANCIA resultado: " + resuldel);

        cadena = "DELETE FROM BSM_SIRADIG_ARG_INGRESOS";
        selReads = connData.prepareStatement(cadena);
        resuldel = selReads.executeUpdate(cadena);
        System.out.println("Delete BSM_SIRADIG_ARG_INGRESOS resultado: " + resuldel);

        cadena = "DELETE FROM BSM_SIRADIG_ARG_DEDUCCIONES";
        selReads = connData.prepareStatement(cadena);
        resuldel = selReads.executeUpdate(cadena);
        System.out.println("Delete BSM_SIRADIG_ARG_DEDUCCIONES resultado: " + resuldel);

        cadena = "DELETE FROM BSM_SIRADIG_ARG_PERIODOS";
        selReads = connData.prepareStatement(cadena);
        resuldel = selReads.executeUpdate(cadena);
        System.out.println("Delete BSM_SIRADIG_ARG_PERIODOS resultado: " + resuldel);

        cadena = "DELETE FROM BSM_SIRADIG_ARG_RETENCIONES";
        selReads = connData.prepareStatement(cadena);
        resuldel = selReads.executeUpdate(cadena);
        System.out.println("Delete BSM_SIRADIG_ARG_RETENCIONES resultado: " + resuldel);

        cadena = "DELETE FROM BSM_SIRADIG_ARG_DETALLES";
        selReads = connData.prepareStatement(cadena);
        resuldel = selReads.executeUpdate(cadena);
        System.out.println("Delete BSM_SIRADIG_ARG_DETALLES resultado: " + resuldel);

        cadena = "DELETE FROM BSM_SIRADIG_ARG_DATOS_ADICIONALES";
        selReads = connData.prepareStatement(cadena);
        resuldel = selReads.executeUpdate(cadena);
        System.out.println("Delete BSM_SIRADIG_ARG_DATOS_ADICIONALES resultado: " + resuldel);

        Statement select = null;
        ResultSet result = null;

        if (Constantes.DEBUG_MODE.booleanValue()) {
            System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " Comenzar a insertar");
        }

        // Obtener datos de empleado de catalgo BAR_HC
        select = connData2.createStatement();
        result = select
                .executeQuery("SELECT E.ID,E.GPID,E.NOMBRE,E.REGISTRO_FISCAL,E.ESTADO_HR,C.DESCRIPCION,N.CLASE_NOMINA "
                        + "FROM BAR_HC E "
                        + "JOIN BAR_COMPANIA C ON E.ID_COMPANIA = C.ID "
                        + "JOIN BAR_TIPO_NOMINA N ON E.ID_TIPO_NOMINA = N.ID ");

        List<BarHC> lHc = new ArrayList<>();
        while (result.next()) {
            BarHC e = new BarHC();
            e.setId(result.getString("ID"));
            e.setGpid(result.getString("GPID"));
            e.setEstadoHr(result.getString("ESTADO_HR"));
            e.setEmpCod(result.getString("DESCRIPCION"));
            e.setTipoNomina(result.getString("CLASE_NOMINA"));
            e.setNombre(result.getString("NOMBRE"));
            e.setRegistroFiscal(result.getString("REGISTRO_FISCAL") != null ? result.getString("REGISTRO_FISCAL") : "");
            lHc.add(e);
        }

        if (select != null) {
            select.close();
            select = null;
        }
        if (result != null) {
            result.close();
            result = null;
        }
        if (connData2 != null) {
            connData2.close();
        }

        System.out.println("Empleados obtenidos:" + lHc.size());

        try (PreparedStatement ps = connData.prepareStatement("INSERT INTO BSM_SIRADIG_ARG_EMPLEADOS "
                + "(PERIODO,NROPRESENTACION,FECHAPRESENTACION,DIRECTORIO,NOMBREARCHIVO,CUIT,TIPODOC,APELLIDO,NOMBRE,PROVINCIA,"
                + "CP,LOCALIDAD,CALLE,NRO,DPTO"
                + ", CARGAS, GANANCIAS, DEDUCCIONES, RETENCIONES, ADICIONALES,CODEMPLEADOR, DESC_EMPLEADOR, CLASE_NOMINA,ROWDATE) "
                + "VALUES (?, ?, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, SYSDATE)")) {

            int i = 0;
            for (Empleado reg : empleados) {

                if (Constantes.DEBUG_MODE.booleanValue()) {
                    // System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new
                    // Date()) + " COD EMP:" + reg.getDescEmpresa()+"-"+"CUIT:"+reg.getCuit()+"-");
                }
                List<BarHC> lHcF = lHc.stream().filter(p -> p.getRegistroFiscal().equals(reg.getCuit()))
                        .collect(Collectors.toList());
                BarHC hc = null;
                if (!lHcF.isEmpty()) {
                    hc = lHcF.get(0);
                    // System.out.println("Se encontro concepto "+regla.getCid()+" en listado");
                }

                if (hc != null) {
                    if (reg.getCodEmpresa().equals("SIN VALOR")) {
                        if (hc.getEmpCod().equals("PEPSICO DE ARGENTINA SRL")) {
                            reg.setCodEmpresa("30537647716");
                            reg.setDescEmpresa("PEPSICO DE ARGENTINA SOCIEDAD DE RESPONSABILIDAD LIMITADA");
                        } else {
                            reg.setCodEmpresa("30504141124");
                            reg.setDescEmpresa(hc.getEmpCod());
                        }
                    }

                    reg.setClaseNomina(hc.getTipoNomina());
                    if (Constantes.DEBUG_MODE.booleanValue()) {
                    }
                }

                if (i % 30000 == 0) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (empleados.get(i).getCargas().size() > 0) {
                        cargas.addAll(empleados.get(i).getCargas());
                    }

                    if (empleados.get(i).getGanancias().size() > 0) {
                        ganancias.addAll(empleados.get(i).getGanancias());
                    }

                    if (empleados.get(i).getDeducciones().size() > 0) {
                        deducciones.addAll(empleados.get(i).getDeducciones());
                    }

                    if (empleados.get(i).getRetenciones().size() > 0) {
                        retenciones.addAll(empleados.get(i).getRetenciones());
                    }

                    if (empleados.get(i).getDatosAdicionales().size() > 0) {
                        datosAdiconales.addAll(empleados.get(i).getDatosAdicionales());
                    }

                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;
                } else if (i == (empleados.size() - 1)) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (empleados.get(i).getCargas().size() > 0) {
                        cargas.addAll(empleados.get(i).getCargas());
                    }

                    if (empleados.get(i).getGanancias().size() > 0) {
                        ganancias.addAll(empleados.get(i).getGanancias());
                    }
                    if (empleados.get(i).getDeducciones().size() > 0) {
                        deducciones.addAll(empleados.get(i).getDeducciones());
                    }

                    if (empleados.get(i).getRetenciones().size() > 0) {
                        retenciones.addAll(empleados.get(i).getRetenciones());
                    }
                    if (empleados.get(i).getDatosAdicionales().size() > 0) {
                        datosAdiconales.addAll(empleados.get(i).getDatosAdicionales());
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;

                } else {
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (empleados.get(i).getCargas().size() > 0) {
                        cargas.addAll(empleados.get(i).getCargas());
                    }
                    if (empleados.get(i).getGanancias().size() > 0) {
                        ganancias.addAll(empleados.get(i).getGanancias());
                    }
                    if (empleados.get(i).getDeducciones().size() > 0) {
                        deducciones.addAll(empleados.get(i).getDeducciones());
                    }
                    if (empleados.get(i).getRetenciones().size() > 0) {
                        retenciones.addAll(empleados.get(i).getRetenciones());
                    }
                    if (empleados.get(i).getDatosAdicionales().size() > 0) {
                        datosAdiconales.addAll(empleados.get(i).getDatosAdicionales());
                    }
                    countX++;
                }
                i++;
            }

            ps.executeBatch();
            ps.clearBatch();
        }
        connData.setAutoCommit(false);
        countX = 0;
        try (PreparedStatement ps = connData.prepareStatement("INSERT INTO BSM_SIRADIG_ARG_CARGA "
                + "(TIPODOC,NRODOC,APELLIDO,NOMBRE,FECHANAC,MESDESDE,MESHASTA,PARENTESCO,VIGENTEPROXIMOSPERIODOS,PORCENTAJEDEDUCCION,NROPRESENTACION,CUIT, ANIO) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            int i = 0;
            for (Carga reg : cargas) {
                if (i % 30000 == 0) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;
                } else if (i == (cargas.size() - 1)) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;
                } else {
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    countX++;
                }
                i++;
            }

            ps.executeBatch();
            ps.clearBatch();
        }

        connData.setAutoCommit(false);
        countX = 0;
        try (PreparedStatement ps = connData.prepareStatement(
                "INSERT INTO BSM_SIRADIG_ARG_GANANCIA (CUITG,DENOMINACION,NROPRESENTACION,CUIT, ANIO) "
                        + "VALUES (?, ?, ?, ?, ?)")) {
            int i = 0;
            for (Ganancia reg : ganancias) {
                if (i % 30000 == 0) {
                    System.out.println("#" + countX + " i: " + i);

                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (ganancias.get(i).getIngresos().size() > 0) {
                        ingresos.addAll(ganancias.get(i).getIngresos());
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;
                } else if (i == (ganancias.size() - 1)) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (ganancias.get(i).getIngresos().size() > 0) {
                        ingresos.addAll(ganancias.get(i).getIngresos());
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;

                } else {
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (ganancias.get(i).getIngresos().size() > 0) {
                        ingresos.addAll(ganancias.get(i).getIngresos());
                    }
                    countX++;
                }
                i++;
            }

            ps.executeBatch();
            ps.clearBatch();
        }
        connData.setAutoCommit(false);
        countX = 0;
        try (PreparedStatement ps = connData.prepareStatement(
                "INSERT INTO BSM_SIRADIG_ARG_INGRESOS (MES,OBRASOC,SEGSOC,SIND,GANBRUT,RETGAN,RETRIBNOHAB,AJUSTE,EXENOALC,SAC,HORASEXTGR,HORASEXTEX,MATDID,GASTOSMOVVIAT,CUITG,NROPRESENTACION,CUIT, ANIO,SEGSOCCAJAS,REMGRAVADAS,REMEXENOGRAV,"+
                "SEGSOCANSES,REMUNLEY19640,NORETMEDCAUT,REMUNCCTPETRO,ASIGNFAM,INTPRESTEMP,REMUNJUDICIALES,INDEMLEY4003,CURSOSSEMIN,INDUMEQUIPEMP) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            int i = 0;
            for (IngresosAporte reg : ingresos) {

                if (i % 30000 == 0) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;
                } else if (i == (ingresos.size() - 1)) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;

                } else {
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    countX++;
                }
                i++;
            }

            ps.executeBatch();
            ps.clearBatch();
        }

        connData.setAutoCommit(false);
        countX = 0;

        try (PreparedStatement ps = connData.prepareStatement("INSERT INTO BSM_SIRADIG_ARG_DEDUCCIONES "
                + "(ID,TIPODOC,NRODOC,DENOMINACION,DESCBASICA,MONTOTOTAL,NROPRESENTACION,CUIT,TIPO, ANIO, ROWDATE) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)")) {

            System.out.println("Num Deducciones: " + deducciones.size());
            int i = 0;
            for (Deduccion reg : deducciones) {

                if (i % 30000 == 0) {

                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (reg.getPeriodos().size() > 0) {
                        periodosDe.addAll(reg.getPeriodos());
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;

                } else if (i == (deducciones.size() - 1)) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (reg.getPeriodos().size() > 0) {
                        periodosDe.addAll(reg.getPeriodos());
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;
                } else {
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (reg.getPeriodos().size() > 0) {
                        periodosDe.addAll(reg.getPeriodos());
                    }
                    countX++;
                }
                i++;
            }
        }

        connData.setAutoCommit(false);
        try (PreparedStatement ps = connData.prepareStatement("INSERT INTO BSM_SIRADIG_ARG_PERIODOS "
                + "(ID_PARENT,MESDESDE,MESHASTA,MONTOMENSUAL,NRODOC,TIPODOC,NROPRESENTACION,CUIT, ANIO) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            int i = 0;
            for (Periodo reg : periodosDe) {

                if (i % 30000 == 0) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;
                } else if (i == (periodosDe.size() - 1)) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;

                } else {
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    countX++;
                }
                i++;
            }

            ps.executeBatch();
            ps.clearBatch();
        }

        connData.setAutoCommit(false);
        countX = 0;
        try (PreparedStatement ps = connData.prepareStatement("INSERT INTO BSM_SIRADIG_ARG_RETENCIONES "
                + "(ID,DESCBASICA,MONTOTOTAL,COD,NROPRESENTACION,CUIT,TIPO, ANIO) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            int i = 0;
            for (Retencion reg : retenciones) {

                if (i % 30000 == 0) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (retenciones.get(i).getPeriodos().size() > 0) {
                        periodosRe.addAll(retenciones.get(i).getPeriodos());
                    }

                    if (retenciones.get(i).getDetalles().size() > 0) {
                        detallesRe.addAll(retenciones.get(i).getDetalles());
                    }

                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;
                } else if (i == (retenciones.size() - 1)) {
                    System.out.println("#" + countX + " i: " + i);

                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (retenciones.get(i).getPeriodos().size() > 0) {
                        periodosRe.addAll(retenciones.get(i).getPeriodos());
                    }
                    if (retenciones.get(i).getDetalles().size() > 0) {
                        detallesRe.addAll(retenciones.get(i).getDetalles());
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;
                } else {
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (reg.getPeriodos().size() > 0) {
                        periodosRe.addAll(retenciones.get(i).getPeriodos());
                    }
                    if (reg.getDetalles().size() > 0) {
                        detallesRe.addAll(retenciones.get(i).getDetalles());
                    }
                    countX++;
                }
                i++;
            }

            ps.executeBatch();
            ps.clearBatch();
        }
        connData.setAutoCommit(false);
        countX = 0;
        try (PreparedStatement ps = connData.prepareStatement("INSERT INTO BSM_SIRADIG_ARG_PERIODOS "
                + "(ID_PARENT, MESDESDE,MESHASTA,MONTOMENSUAL,NRODOC,TIPODOC,NROPRESENTACION,CUIT, ANIO) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            int i = 0;
            for (Periodo reg : periodosRe) {

                if (i % 30000 == 0) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;

                } else if (i == (periodosRe.size() - 1)) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;

                } else {
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    countX++;
                }
                i++;
            }

            ps.executeBatch();
            ps.clearBatch();
        }

        connData.setAutoCommit(false);
        countX = 0;
        try (PreparedStatement ps = connData
                .prepareStatement("INSERT INTO BSM_SIRADIG_ARG_DETALLES (NOMBRE,VALOR,COD,NROPRESENTACION,CUIT, ANIO) "
                        + "VALUES (?, ?, ?, ?, ?, ?)")) {
            int i = 0;
            for (Detalle reg : detallesRe) {
                if (i % 30000 == 0) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;

                } else if (i == (detallesRe.size() - 1)) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;

                } else {
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    countX++;
                }
                i++;
            }

            ps.executeBatch();
            ps.clearBatch();

        }

        connData.setAutoCommit(false);
        countX = 0;
        try (PreparedStatement ps = connData.prepareStatement(
                "INSERT INTO BSM_SIRADIG_ARG_DATOS_ADICIONALES (NOMBRE,MESDESDE,MESHASTA,VALOR,NROPRESENTACION,CUIT, ANIO) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            int i = 0;
            for (DatoAdicional reg : datosAdiconales) {
                if (i % 30000 == 0) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;
                } else if (i == (datosAdiconales.size() - 1)) {
                    System.out.println("#" + countX + " i: " + i);
                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    countX++;

                } else {

                    try {
                        procesarStatement(ps, reg).addBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ps.addBatch();
                    countX++;
                }
                i++;
            }

            ps.executeBatch();
            ps.clearBatch();
        }

        connData.commit();

        conn.close();
        connData.close();

    }
    
    
    public IngresosAporte obtenerIngreso(JSONObject ingrAptObj) {
        IngresosAporte ingreso = new IngresosAporte();

        if (ingrAptObj.has("obraSoc")) {
            ingreso.setObraSoc(String.valueOf(ingrAptObj.get("obraSoc")));
//                                    System.out.println("obraSoc: " + element2.getElementsByTagName("obraSoc").item(0).getTextContent());
        } else {
//                                    System.out.println("no se encuentra obraSoc");
        }

        if (ingrAptObj.has("mes")) {
            ingreso.setMes(obtenerMes(String.valueOf(ingrAptObj.get("mes"))));
        }

        if (ingrAptObj.has("segSoc")) {
            ingreso.setSegSoc(String.valueOf(ingrAptObj.get("segSoc")));
        }

        if (ingrAptObj.has("sind")) {
            ingreso.setSind(String.valueOf(ingrAptObj.get("sind")));
        }

        if (ingrAptObj.has("ganBrut")) {
            ingreso.setGanBrut(String.valueOf(ingrAptObj.get("ganBrut")));
        }

        if (ingrAptObj.has("retGan")) {
            ingreso.setRetGan(String.valueOf(ingrAptObj.get("retGan")));
        }

        if (ingrAptObj.has("retribNoHab")) {
            ingreso.setRetribNoHab(String.valueOf(ingrAptObj.get("retribNoHab")));
        }

        if (ingrAptObj.has("ajuste")) {
            ingreso.setAjuste(String.valueOf(ingrAptObj.get("ajuste")));
        }

        if (ingrAptObj.has("exeNoAlc")) {
            ingreso.setExeNoAlc(String.valueOf(ingrAptObj.get("exeNoAlc")));
        }

        if (ingrAptObj.has("sac")) {
            ingreso.setSac(String.valueOf(ingrAptObj.get("sac")));
        }

        if (ingrAptObj.has("horasExtGr")) {
            ingreso.setHorasExtGr(String.valueOf(ingrAptObj.get("horasExtGr")));
        }

        if (ingrAptObj.has("horasExtEx")) {
            ingreso.setHorasExtEx(String.valueOf(ingrAptObj.get("horasExtEx")));
        }

        if (ingrAptObj.has("matDid")) {
            ingreso.setMatDid(String.valueOf(ingrAptObj.get("matDid")));
        }

        if (ingrAptObj.has("gastosMovViat")) {
            ingreso.setGastosMovViat(String.valueOf(ingrAptObj.get("gastosMovViat")));
        }
        
        //Ajuste 2026-03-03
        
        if (ingrAptObj.has("segSocCajas")) {
            ingreso.setSegSocCajas(String.valueOf(ingrAptObj.get("segSocCajas")));
        }

        if (ingrAptObj.has("ajusteRemGravadas")) {
            ingreso.setAjusteRemGravadas(String.valueOf(ingrAptObj.get("ajusteRemGravadas")));
        }

        if (ingrAptObj.has("ajusteRemExeNoAlcanzadas")) {
            ingreso.setAjusteRemExeNoAlcanzadas(String.valueOf(ingrAptObj.get("ajusteRemExeNoAlcanzadas")));
        }

        //Ajuste 2026-05-22

        if (ingrAptObj.has("remunLey19640")) {
            ingreso.setRemunLey19640(String.valueOf(ingrAptObj.get("remunLey19640")));
        }

        if (ingrAptObj.has("segSocANSES")) {
            ingreso.setSegSocAnses(String.valueOf(ingrAptObj.get("segSocANSES")));
        }

        if (ingrAptObj.has("noRetMedCaut")) {
            ingreso.setNoRetMedCaut(String.valueOf(ingrAptObj.get("noRetMedCaut")));
        }

        if (ingrAptObj.has("remunCctPetro")) {
            ingreso.setRemunCctPetro(String.valueOf(ingrAptObj.get("remunCctPetro")));
        }

         if (ingrAptObj.has("asignFam")) {
            ingreso.setAsignFam(String.valueOf(ingrAptObj.get("asignFam")));
        }

        if (ingrAptObj.has("intPrestEmp")) {
            ingreso.setIntPrestEmp(String.valueOf(ingrAptObj.get("intPrestEmp")));
        }

        if (ingrAptObj.has("remunJudiciales")) {
            ingreso.setRemunJudiciales(String.valueOf(ingrAptObj.get("remunJudiciales")));
        }

        if (ingrAptObj.has("indemLey4003")) {
            ingreso.setIndemLey4003(String.valueOf(ingrAptObj.get("indemLey4003")));
        }

        if (ingrAptObj.has("cursosSemin")) {
            ingreso.setCursosSemin(String.valueOf(ingrAptObj.get("cursosSemin")));
        }

        if (ingrAptObj.has("indumEquipEmp")) {
            ingreso.setIndumEquipEmp(String.valueOf(ingrAptObj.get("indumEquipEmp")));
        }

        


        return ingreso;
    }

    public String obtenerProvincia(String cod) {
        switch (cod) {
            case "0":
                return "Ciudad AutÃ³noma de Buenos Aires";
            case "1":
                return "Buenos Aires";
            case "2":
                return "Catamarca";
            case "3":
                return "CÃ³rdoba";
            case "4":
                return "Corrientes";
            case "5":
                return "Entre RÃ­os";
            case "6":
                return "Jujuy";
            case "7":
                return "Mendoza";
            case "8":
                return "La Rioja";
            case "9":
                return "Salta";
            case "10":
                return "San Juan";
            case "11":
                return "San Luis";
            case "12":
                return "Santa Fe";
            case "13":
                return "Santiago del Estero";
            case "14":
                return "TucumÃ¡n";
            case "16":
                return "Chaco";
            case "17":
                return "Chubut";
            case "18":
                return "Formosa";
            case "19":
                return "Misiones";
            case "20":
                return "NeuquÃ©n";
            case "21":
                return "La Pampa";
            case "22":
                return "RÃ­o Negro";
            case "23":
                return "Santa Cruz";
            case "24":
                return "Tierra del Fuego";
            default:
                return cod;
        }
    }

    public String obtenerTipoDoc(String cod) {
        switch (cod) {
            case "80":
                return "CUIT";
            case "86":
                return "CUIL";
            case "87":
                return "CDI";
            case "96":
                return "DNI";
            case "89":
                return "LC";
            case "90":
                return "LE";
            case "92":
                return "En TrÃ¡mite";
//            case "99":
//                return "En TrÃ¡mite";
            default:
                return cod;
        }
    }

    public String obtenerParentesco(String cod) {
        switch (cod) {
            case "1":
                return "CÃ³nyuge";
            case "3":
                return "Hijo/a Menor de 18 AÃ±os";
            case "30":
                return "Hijastro/a Menor de 18 AÃ±os";
            case "31":
                return "Hijo/a Incapacitado para el Trabajo";
            case "32":
                return "Hijastro/a Incapcacitado para el Trabajo";
            case "33":
                return "Padre";
            case "34":
                return "Madre";
            case "35":
                return "Nieto/a Meneor de 24 AÃ±os";
            case "36":
                return "Nieto/a Incapacitado para el Trabajo";
            case "37":
                return "Bisnieto/a Meneor de 24 AÃ±os";
            case "38":
                return "Bisnieto/a Incapacitado para el Trabajo";
            case "39":
                return "Abuelo/a";
            case "40":
                return "Bisabuelo/a";
            case "41":
                return "Padrastro/Madrastra";
            case "42":
                return "Hermano/a Menor de 24 AÃ±os";
            case "43":
                return "Hermano/a Incapacitado para el Trabajo";
            case "44":
                return "Suegro/a";
            case "45":
                return "Yerno/Nuera Menor de 24 AÃ±os";
            case "46":
                return "Yerno/Nuera Incapacitado para el Trabajo";
            case "51":
                return "UniÃ³n convivencial";
            default:
                return cod;
        }
    }

    public String obtenerDeduccion(String cod) {
        switch (cod) {
            case "1":
                return "Cuotas MÃ©dico-Asistenciales";
            case "2":
                return "Primas de Seguro para el caso de muerte / riesgo de muerte";
            case "3":
                return "Donaciones";
            case "4":
                return "Intereses PrÃ©stamo Hipotecario";
            case "5":
                return "Gastos de Sepelio";
            case "7":
                return "Gastos MÃ©dicos y ParamÃ©dicos";
            case "8":
                return "DeducciÃ³n del Personal DomÃ©stico";
            case "9":
                return "Aporte a Sociedades de GarantÃ­a RecÃ­proca";
            case "10":
                return "Vehiculos de Corredores y Viajantes de Comercio";
            case "11":
                return "PerÃ­odos 2018 y anteriores: Gastos de Movilidad, ViÃ¡ticos "
                        + "y Representaciones e "
                        + "Intereses de Corredores y Viajantes de Comercio "
                        + "PerÃ­odo 2019 en adelante: Gastos de Movilidad e "
                        + "Intereses de Corredores y Viajantes de Comercio";
            case "21":
                return "Gastos de AdquisiciÃ³n de Indumentaria y Equipamiento "
                        + "para uso Exclusivo en el Lugar de Trabajo";
            case "22":
                return "Alquiler de Inmuebles destinados a casa habitaciÃ³n";
            case "23":
                return "Primas de Ahorro correspondientes a Seguros Mixtos";
            case "24":
                return "Aportes correspondientes a Planes de Seguro de Retiro Privados";
            case "25":
                return "AdquisiciÃ³n de Cuotapartes de Fondos Comunes de "
                        + "InversiÃ³n con fines de retiro "
                        + "99 Otras Deducciones";
            case "29":
                return "Pago a Cuenta - Per. Trajetas p Pago Serv. A No Resid - RG 4815/2020, Ley 27541 Art. 35 inc. c)";
            case "30":
                return "Pago a Cuenta - Per. Agen. de Viajes y Turismo - RG 4815/2020, Ley 27541 Art. 35 inc. d)";
            case "31":
                return "Pago a Cuenta - Per. Serv. Transporte el Exterior - RG 4815/2020, Ley 27541 Art. 35 inc. e)";
            case "32":
                return "Gastos de educaciÃ³n";
            case "33":
                return "Beneficios para Locatarios (Inquilinos)";
            case "99":
                return "Otras deducciones";

            default:
                return cod;
        }
    }

    public String obtenerMotivos(String cod) {
        switch (cod) {
            case "1":
                return "Aportes para fondos de JubilaciÃ³n, Retiros, Pensiones o "
                        + "Subsidios destinados al ANSES";
            case "2":
                return "Cajas Provinciales o Municipales";
            case "3":
                return "Impuesto sobre los CrÃ©ditos y DÃ©bitos en Cuenta Bancaria "
                        + "sin CBU [nota: reemplazado por Pago a Cuenta CÃ³d. 14 â€“ "
                        + "ver Tabla 9]";
            case "4":
                return "Beneficios Derivados de RegÃ­menes que impliquen "
                        + "tratamientos Preferenciales que se Efectivicen Mediante "
                        + "Deducciones";
            case "5":
                return "Beneficios Derivados de RegÃ­menes que impliquen "
                        + "tratamientos Preferenciales que No se Efectivicen "
                        + "Mediante Deducciones";
            case "6":
                return "Actores - Retribuciones Abonadas a Representantes - R.G. "
                        + "NÂ° 2442/08";
            case "7":
                return "Cajas Complementarias de PrevisiÃ³n";
            case "8":
                return "Fondos Compensadores de PrevisiÃ³n";
            case "9":
                return "Otros";
            default:
                return cod;
        }
    }

    public String obtenerAjustes(String cod) {
        switch (cod) {
            case "1":
                return "Montos Retroactivos";
            case "2":
                return "Reintegros de Soc. de Garantia RecÃ­procas Art. 79 "
                        + "PÃ¡rrafo 2 y PÃ¡rrafo 3";
            default:
                return cod;
        }
    }

    public String obtenerTipoTarjeta(String cod) {
        switch (cod) {
            case "1":
                return "Tarjeta de CrÃ©dito / Compra";
            case "2":
                return "Tarjeta de DÃ©bito";
            default:
                return cod;
        }
    }

    public String obtenerIdTarjeta(String cod) {
        switch (cod) {
            case "1":
                return "MasterCard";
            case "2":
                return "Visa";
            case "3":
                return "American Express";
            case "4":
                return "Cabal";
            case "5":
                return "Italcred";
            case "6":
                return "Naranja";
            case "7":
                return "Nativa";
            case "8":
                return "Diners Club";
            case "99":
                return "Otra";
            default:
                return cod;
        }
    }

    public String obtenerRetPerPagCuenta(String cod) {
        switch (cod) {
            case "6":
                return "Impuestos sobre CrÃ©ditos y DÃ©bitos en cuenta Bancaria";
            case "12":
                return "Retenciones y Percepciones Aduaneras";
            case "13":
                return "Pago a Cuenta - Compras en el Exterior";
            case "14":
                return "Impuesto sobre los Movimientos de Fondos Propios o de Terceros";
            case "15":
                return "Pago a Cuenta - Compra de Paquetes TurÃ­sticos";
            case "26":
                return "Pago a Cuenta - Compra de Pasajes";
            case "17":
                return "Pago a Cuenta - Compra de Moneda Extranjera para Turismo / Transf. al Exterior";
            case "18":
                return "Pago a Cuenta - AdquisiciÃ³n de moneda extranjera para tenencia de billetes extranjeros en el paÃ­s";
            case "19":
                return "Pago a Cuenta - Compra de Paquetes TurÃ­sticos en efectivo";
            case "20":
                return "Pago a Cuenta - Compra de Pasajes en efectivo";
            case "27":
                return "Pago a Cuenta - C. de Billetes y Divisas en M. Ext. - RG 4815/2020, Ley 27541 Art. 35 inc. a)";
            case "28":
                return "Pago a Cuenta - Per. Tarjetas p. Compras en el Ext. - RG 4815/2020, Ley 27541 Art. 35 inc. b)";
                case "29":
                return "Pago a Cuenta - Per. Trajetas p Pago Serv. A No Resid - RG 4815/2020, Ley 27541 Art. 35 inc. c)";
            case "30":
                return "Pago a Cuenta - Per. Agen. de Viajes y Turismo - RG 4815/2020, Ley 27541 Art. 35 inc. d)";
            case "31":
                return "Pago a Cuenta - Per. Serv. Transporte el Exterior - RG 4815/2020, Ley 27541 Art. 35 inc. e)";
            case "32":
                return "Gastos de educaciÃ³n";
            default:
                return cod;
        }
    }

    public String obtenerTiposNorma(String cod) {
        switch (cod) {
            case "0":
                return "Ley";
            case "1":
                return "Decreto";
            case "2":
                return "RG";
            default:
                return cod;
        }
    }

    public String obtenerMes(String cod) {
        switch (cod) {
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
                return cod;
        }
    }

    public String obtenerDatAdicionales(String cod) {
        switch (cod) {
            case "exencionGan2016SAC1":
                return "exenciÃ³n del impuesto a las ganancias aplicable a la primera cuota";
            case "trabRegPatagonica":
                return "Trabajador RegiÃ³n PatagÃ³nica";
            case "jubPensRegPatagonica":
                return "Jubilado, Pensionado y/o Retirado Trabajador RegiÃ³n PatagÃ³nica";
            case "jubPensOtrosIngresos":
                return "Jubilado, Pensionado y/o Retirado Percibe otros ingresos por monotributo / relaciÃ³n de dependencia / participaciÃ³n en sociedades / actividad autÃ³noma. etc.";
            case "jubPensTribBienes":
                return "Jubilado, Pensionado y/o Retirado TributÃ³ Bienes Personales en el Ãºltimo perÃ­odo fiscal anterior al que estÃ¡ declarando";
            case "jubPensTribOtrosBienes":
                return "Jubilado, Pensionado y/o Retirado Tiene mÃ¡s bienes, por los que tributÃ³ Bienes Personales en el Ãºltimo perÃ­odo fiscal anterior al que estÃ¡ declarando, aparte de su casa-habitaciÃ³n";
            default:
                return cod;
        }
    }

}
