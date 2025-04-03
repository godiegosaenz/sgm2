package util;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.origami.config.SisVars;
import com.origami.sgm.entities.CatEdificacionPisosDet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.Id;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.collection.internal.PersistentBag;
import org.primefaces.context.RequestContext;

/**
 *
 * @author User
 */
public class Utils {

    private static final int[] PATTERN = {2, 1, 2, 1, 2, 1, 2, 1, 2};
    private static final int[] CASO_9 = {4, 3, 2, 7, 6, 5, 4, 3, 2};
    private static final int[] CASO_6 = {3, 2, 7, 6, 5, 4, 3, 2};
    private static final String NUMERIC_REGEX = "^[0-9]+$";
    private static final String DECIMAL_REGEX = "^[+]?\\d+([.]\\d+)?$";
    private static final String EMAIL_REGEX = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)*(\\.[A-Za-z]{1,})$";
    private static String[] nombreMunicipio = null;

    public static final String ilustreMunicipio = "ILUSTRE MUNICIPALIDAD CANTON LOMAS DE SARGENTILLO";

    public static BigDecimal bigdecimalTo2Decimals(BigDecimal inNumber) {
        String temp = inNumber.toString();
        BigDecimal outNumber;
        int indice = temp.indexOf('.');
        if (((inNumber.toString().length() - 1) - indice) > 2) {
            String tempNew = temp.substring(0, indice + 3);
            outNumber = new BigDecimal(tempNew);
            if (((temp.length()) - (indice + 1)) >= 3) {
                if (Integer.parseInt(temp.substring(tempNew.length(), tempNew.length() + 1)) >= 5) {
                    outNumber = outNumber.add(new BigDecimal("0.01"));
                }
            }
        } else {
            outNumber = inNumber;
        }
        return outNumber;
    }

    public static List<String> separadorComas(String correos) {
        List<String> correosResulList = new ArrayList<>();
        String temp = correos;
        int indice = temp.indexOf(',');
        if (indice > 0) {
            do {
                String correo1 = temp.substring(0, indice);
                correosResulList.add(correo1);
                String correoRestante = temp.substring(indice + 1, temp.length());
                temp = correoRestante;
                indice = temp.indexOf(',');

            } while (indice > 0);
            correosResulList.add(temp);
        } else {
            correosResulList.add(correos);
        }
        return correosResulList;
    }

    public static Boolean isRepetido(Collection<String> val, Object nuevo) {
        boolean i = false;
        for (String x : val) {
            if (x.equals(nuevo)) {
                i = true;
                return i;
            }
        }
        return i;
    }

    public static synchronized boolean validarEmailConExpresion(String email) {
        return validatePattern(EMAIL_REGEX, email);
    }

    public static Long restarFechas(Date fechaActual, Date fechaIngreso) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(fechaActual);
        cal2.setTime(fechaIngreso);
        long milis1 = cal1.getTimeInMillis();
        long milis2 = cal2.getTimeInMillis();
        long diff = milis2 - milis1;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        long diffHour = diff / (60 * 60 * 1000);
        diffHour = diffHour - (diffDays * 24);
        if (diffHour > 0) {
            diffDays++;
        }
        return diffDays;
    }

    public static synchronized boolean validateCCRuc(final String identificacion) {
        if (identificacion == null) {
            return false;
        }
        if (identificacion.trim().isEmpty()) {
            return false;
        }
        if (!validateNumberPattern(identificacion)) {
            return false;
        }
        if (identificacion.length() != 10 & identificacion.length() != 13) {
            return false;
        }
        int[] coeficientes = null;
        int indiceDigitoVerificador = 9;
        int modulo = 11;

        if ((identificacion.length() == 13) && !identificacion.substring(10, 13).equals("001")) {
            return false;
        }
        if (identificacion.charAt(2) == '9') {
            coeficientes = CASO_9;
        } else if (identificacion.charAt(2) == '6') {
            coeficientes = CASO_6;
            indiceDigitoVerificador = 8;
        } else if (identificacion.charAt(2) < '6') {
            coeficientes = PATTERN;
            modulo = 10;
        }
        return verify(identificacion.toCharArray(), coeficientes, indiceDigitoVerificador, modulo);
    }

    private static boolean verify(final char[] array, final int[] coeficientes,
            final int indiceDigitoVerificador, final int modulo) {
        if (coeficientes == null) {
            return false;
        }
        int sum = 0;
        int aux;
        for (int i = 0; i < coeficientes.length; i++) {
            aux = new Integer(String.valueOf(array[i])) * coeficientes[i];
            if ((modulo == 10) && (aux > 9)) {
                aux -= 9;
            }
            sum += aux;
        }
        int mod = sum % modulo;
        mod = mod == 0 ? modulo : mod;
        final int res = (modulo - mod);
        Integer valorVerificar = null;
        if (array.length == 13) {
            valorVerificar = Integer.valueOf(String.valueOf(array[array.length - (13 - indiceDigitoVerificador)]));
        } else if (array.length == 10) {
            valorVerificar = Integer.valueOf(String.valueOf(array[array.length - (10 - indiceDigitoVerificador)]));
        }
        return res == valorVerificar;
    }

    public static synchronized boolean validateNumberPattern(final String valor) {
        return validatePattern(NUMERIC_REGEX, valor);
    }

    public static synchronized boolean validateDecimalPattern(final String valor) {
        return validatePattern(DECIMAL_REGEX, valor);
    }

    public static synchronized boolean validatePattern(final String patron, final String valor) {
        final Pattern patter = Pattern.compile(patron);
        final Matcher matcher = patter.matcher(valor);
        return matcher.matches();
    }

    public static Archivo crearDocumentoArchivo(InputStream is, String ruta, String nombreArchivo, String tipoArchivo) throws IOException {
        File file = new File(ruta);
        try (OutputStream out = new FileOutputStream(file)) {
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            is.close();
        }
        Archivo documento = new Archivo();
        documento.setNombre(nombreArchivo);
        documento.setTipo(tipoArchivo);
        documento.setRuta(ruta);
        return documento;
    }

    public static Date sumarRestarDiasFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, dias);
        return calendar.getTime();
    }

    public static String pasarUtf(String cadena) throws UnsupportedEncodingException {
        if (cadena != null) {
            byte[] bytes = cadena.getBytes("ISO-8859-1");
            cadena = new String(bytes, "UTF-8");
        }
        return cadena;
    }

    public static String randomNumericString() {
        int i = (int) (Math.random() * 100000);
        return String.valueOf(i);
    }

    public static Integer getDateValues(String formatValue, Date value) {
        Integer res = 0;
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        f.format(value);
        switch (formatValue.toUpperCase()) {
            case "Y":
                res = f.getCalendar().get(Calendar.YEAR);
                break;
            case "M":
                res = f.getCalendar().get(Calendar.MONTH);
                break;
            case "D":
                res = f.getCalendar().get(Calendar.DAY_OF_MONTH);
                break;
        }
        return res;
    }

    public static String completarCadenaConCeros(String cadena, Integer longitud) {
        if (cadena == null) {
            return null;
        }
        if (cadena.length() > longitud) {
            return cadena;
        }
        String ceros = "";
        for (int i = 0; i < longitud; i++) {
            ceros = ceros + "0";
        }
        int tamanio = cadena.length();
        ceros = ceros.substring(0, longitud - tamanio);
        cadena = ceros + cadena;
        return cadena;
    }

    public static String convertirMesALetra(Integer fechames) {
        String mes;
        switch (fechames) {
            case 1:
                mes = "ENERO";
                break;
            case 2:
                mes = "FEBRERO";
                break;

            case 3:
                mes = "MARZO";
                break;

            case 4:
                mes = "ABRIL";
                break;

            case 5:
                mes = "MAYO";
                break;

            case 6:
                mes = "JUNIO";
                break;

            case 7:
                mes = "JULIO";
                break;

            case 8:
                mes = "AGOSTO";
                break;

            case 9:
                mes = "SEPTIEMBRE";
                break;

            case 10:
                mes = "OCTUBRE";
                break;

            case 11:
                mes = "NOVIEMBRE";
                break;

            default:
                mes = "DICIEMBRE";
        }
        return mes;
    }

    public static String quitarSaltos(String cadena) {
        return cadena.replace("\r", "").replace("\n", "");
    }

    public static int getNumberOfPagesDocumento(byte[] bytes) {
        int ret = -2;
        try {
            RandomAccessFileOrArray pdfFile = new RandomAccessFileOrArray(bytes);
            PdfReader reader = new PdfReader(pdfFile, new byte[0]);
            ret = reader.getNumberOfPages();
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public static boolean isDecimal(String cad) {
        try {
            Double.parseDouble(cad);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isEmpty(List<?> l) {
        return l == null || l.isEmpty();
    }

    public static boolean isEmpty(Collection l) {
        return l == null || l.isEmpty();
    }

    public static boolean isNotEmpty(List<?> l) {
        return !Utils.isEmpty(l);
    }

    public static boolean isNotEmpty(Collection l) {
        return !Utils.isEmpty(l);
    }

    /**
     * Si el String es nulo returna vacio, caso contrario el mismo valor.
     *
     * @param nombres
     * @return
     */
    public static String isEmpty(String nombres) {
        if (nombres == null || nombres.trim().isEmpty()) {
            return "";
        }
        return nombres;
    }

    /**
     * Verifica que el valor numerico no sea nulo <code>value</code> y retorna
     * el mismo valor de <code>value</code> caso contrario retorna -1.
     *
     * @param <T>
     * @param value Valor a verificar.
     * @return si el valor de <code>value</code> es nulo retorna -1 caso
     * contrario el valor de <code>value</code>
     */
    public static <T> T isNull(T value) {
        if (value == null || value.toString().trim().length() < 0) {
            return (T) new BigInteger("-1");
        }
        return (T) value;
    }

    /**
     * Verifica si <code>value</code> es nulo y retorna <code>true</code>, caso
     * contrario retorna <code>false</code>.
     *
     * @param value Tipo de Dato Númerico de cualquier tipo primitivo o objecto.
     * @return True si el null caso contrario false.
     */
    public static Boolean isNumberNull(Number value) {
        if (value == null || value.longValue() < 0L) {
            return true;
        }
        return false;
    }

    public static <T> T get(final List<T> values, int idx) {
        if (values.size() > idx) {
            return values.get(idx);
        }
        return null;
    }

    public static <T> T get(final Collection<T> values, int idx) {
        if (values.size() > idx) {
            List<T> result = new ArrayList<>(values);
            return result.get(idx);
        }
        return null;
    }

    /**
     * Le da formato a la fecha con el pattern que se le pasa como parametro
     *
     * @param pattern Formato que se desea obtener.
     * @param fechaFin Fecha a dar formato.
     * @return Fecha con el formato esperado.
     */
    public static String dateFormatPattern(String pattern, Date fechaFin) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(fechaFin);
    }

    public static String dateFormatPattern(SimpleDateFormat format, Date fecha) {
        return format.format(fecha);
    }

    public static boolean isNum(String nom) {
        try {
            Long.parseLong(nom);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public static Integer getAnio(Date fechaIngreso) {
        Calendar c = Calendar.getInstance();
        c.setTime(fechaIngreso);
        return c.get(Calendar.YEAR);
    }

    public static Integer getMes(Date fechaIngreso) {
        Calendar c = Calendar.getInstance();
        c.setTime(fechaIngreso);
        return c.get(Calendar.MONTH);
    }

    public static Integer getDia(Date fechaIngreso) {
        Calendar c = Calendar.getInstance();
        c.setTime(fechaIngreso);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static Date getPrimerDiaDelAnio(Integer anio) {
        Calendar fecha = Calendar.getInstance();
        fecha.set(anio, Calendar.JANUARY, 1, 0, 0, 0);
        return fecha.getTime();
    }

    public static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    public static String encriptaEnMD5(String stringAEncriptar) {
        return DigestUtils.md5Hex(stringAEncriptar);
    }

    public static String encriptSHAHex(String dato) {
        return DigestUtils.sha512Hex(dato);
    }

    public static String encriptRAS(String dato) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            SecretKeySpec secretKey = new SecretKeySpec(dato.getBytes(), "RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return DigestUtils.sha512Hex(dato);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static int boolean2int(Boolean x) {
        if (x) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     *
     * @param <T>
     * @param object Lista de objecto principal
     * @param previousValues lista a retornar
     * @param duplicateArray Lista a comparar si estan repetidos.
     * @param compare 0 para realizar comparacion en binario, para hacer la
     * comparacion como texto 1
     * @return List
     */
    public static <T> List<T> verificarRepetidos(final List<T> object, final List<T> previousValues, final List<T> duplicateArray, final int compare) {
        Iterator<T> iterator = object.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            int count = 0;
            for (T t : duplicateArray) {
                if (compare == 0) {
                    if (next.equals(t)) {
                        count++;
                    }
                } else {
                    if (String.valueOf(next).equalsIgnoreCase(String.valueOf(t))) {
                        count++;
                    }
                }
            }
            if (count == 0 && !previousValues.contains(next)) {
                previousValues.add(next);
            }

        }
        return previousValues;
    }

    public static String encode(String text) {
        try {
            return new String(Base64.encodeBase64(text.getBytes()));
        } catch (Exception e) {
        }
        return null;
    }

    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getMunicipio() {
        nombreMunicipio = SisVars.NOMBREMUNICIPIO.split("GOBIERNO AUTÓNOMO DESCENTRALIZADO MUNICIPAL DEL ");
        return nombreMunicipio[1].toString();
    }

    public static void openDialog(String urlPage, Map<String, List<String>> params) {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "70%");
        options.put("height", "450");
        options.put("closable", true);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        RequestContext.getCurrentInstance().openDialog(urlPage, options, params);
    }

    public static void openDialog(String urlPage, Map<String, List<String>> params, String height) {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "70%");
        options.put("height", height);
        options.put("closable", true);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        RequestContext.getCurrentInstance().openDialog(urlPage, options, params);
    }

    public static void openDialog(String urlPage, Map<String, List<String>> params, String height, String widthPorcentaje) {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", widthPorcentaje + "%");
        options.put("height", height);
        options.put("closable", true);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        RequestContext.getCurrentInstance().openDialog(urlPage, options, params);
    }

    public static List getList(List asList) {
        if (asList == null) {
            return new ArrayList<>();
        } else {
            return asList;
        }
    }

    public static String retornarVacio(Object valor) {
        if (valor == null) {
            return "vacio";
        }
        if (valor.toString().isEmpty()) {
            return "vacio";
        }
        return valor.toString();
    }

    public static String cargarUrlReporte(String subReporte) {
        if (subReporte == null) {
            return null;
        }
        try {
            String temp = subReporte.substring(subReporte.indexOf(SisVars.REPORTES), subReporte.length());
            temp = temp.replace("//", "/");
            temp = temp.replace("\\", "/");
            if (temp.endsWith("jasper")) {
                URL resource = Utils.class.getResource(temp);
                if (resource != null) {
                    registrarUrlReportes(resource);
                    return resource.getFile();
                } else {
                    System.out.println("No se hallo recurso: " + temp);
                    return null;
                }
            } else {
                if (temp.endsWith("//")) {
                    return SisVars.URL_REPORTES + temp.replace("//", "/");
                } else {
                    return SisVars.URL_REPORTES + temp;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public static String cargarUrlImagenes(String subReporte) {
        if (subReporte == null) {
            return null;
        }
        try {// \/css/smb/logo.png
            String temp = null;
            if (subReporte.toLowerCase().endsWith("png") || subReporte.toLowerCase().endsWith("jpg")) {
                subReporte = subReporte.replace("//", "/");
                subReporte = subReporte.replace("\\", "/");
                if (subReporte.contains("/css")) {
                    temp = subReporte.substring(subReporte.indexOf("/css"), subReporte.length());
                } else if (subReporte.contains("/image")) {
                    temp = subReporte.substring(subReporte.indexOf("/image"), subReporte.length());
                } else if (subReporte.contains("/resources")) {
                    temp = subReporte.substring(subReporte.indexOf("/resources"), subReporte.length());
                } else if (subReporte.contains("/template")) {
                    temp = subReporte.substring(subReporte.indexOf("/template"), subReporte.length());
                }
                URL resource = Utils.class.getResource("/META-INF/resources" + temp);
                if (resource != null) {
                    return "jar:" + resource.getFile();
                } else {
                    System.out.println("No se hallo recurso: " + temp);
                    return null;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public static void reemplazarRutaSubReportes(Map<String, Object> paramt) {
        try {
            registrarUrlReportes1(null);
            for (Map.Entry<String, Object> entry : paramt.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key.equalsIgnoreCase("SUBREPORT_DIR")) {
                    if (value != null) {
                        paramt.replace(key, Utils.cargarUrlReporte(value.toString()));
                    }
                }
                if (key.equalsIgnoreCase("SUBREPORT_DIR_TITLE")) {
                    if (value != null) {
                        paramt.replace(key, Utils.cargarUrlReporte(value.toString()));
                    }
                }
                if (key.equalsIgnoreCase("SUBREPORT_TITLE")) {
                    if (value != null) {
                        paramt.replace(key, Utils.cargarUrlReporte(value.toString()));
                    }
                }
                if (key.equalsIgnoreCase("LOGO")) {
                    if (value != null) {
                        paramt.replace(key, Utils.cargarUrlImagenes(value.toString()));
                    }
                }
                if (key.equalsIgnoreCase("logo1")) {
                    if (value != null) {
                        paramt.replace(key, Utils.cargarUrlImagenes(value.toString()));
                    }
                }
                if (key.equalsIgnoreCase("LOGO2")) {
                    if (value != null) {
                        paramt.replace(key, Utils.cargarUrlImagenes(value.toString()));
                    }
                }
                if (key.equalsIgnoreCase("LOGO_FOOTER")) {
                    if (value != null) {
                        paramt.replace(key, Utils.cargarUrlImagenes(value.toString()));
                    }
                }

            }
        } catch (Exception e) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void registrarUrlReportes(URL resource) {
        try {
            if (resource != null) {
                String[] split = resource.getFile().split(SisVars.REPORTES + "/");
                if (split != null) {
                    if (SisVars.URL_REPORTES == null) {
                        SisVars.URL_REPORTES = "jar:" + split[0];
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void registrarUrlReportes1(String nombre) {
        if (nombre == null) {
            registrarUrlReportes(Utils.class.getResource("/" + SisVars.REPORTES + "/titleReporte.jasper"));
        } else {
            registrarUrlReportes(Utils.class.getResource(nombre));
        }
    }

    public static Collection<CatEdificacionPisosDet> removerItemCollecion(Collection c, int index) {
        if (Utils.isEmpty(c)) {
            return null;
        }
        List l = (List) c;
        l.remove(index);
        return l;
    }

    public static Collection setCollection(Collection c, Object obj) {
        List l = (List) c;
        l.set(l.indexOf(obj), obj);
        return l;
    }
    
    public static int getPartDate(Date date, int part) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (part) {
            case 1: {
                return calendar.get(Calendar.DAY_OF_MONTH);
            }
            case 2: {
                return calendar.get(Calendar.MONTH) + 1;
            }
            case 3: {
                return calendar.get(Calendar.YEAR);
            }
            default: {
                return 0;
            }
        }
    }
    
    public static Object instanceConsString(String type, String value) {
        Object obj = null;
        try {
            Class clazz = Class.forName(type);

            for (java.lang.reflect.Constructor con : clazz.getConstructors()) {
                for (Class parameterType : con.getParameterTypes()) {
                    if (parameterType.equals(String.class)) {
                        if (con.getParameterTypes().length == 1) {
                            obj = con.newInstance(value);
                            break;
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | 
                IllegalArgumentException | InstantiationException | SecurityException | 
                InvocationTargetException e) {
            return null;
        }
        return obj;
    }
    
    public static Object instanceConsString(Class type, String value) {
        Object obj = null;
        try {
            Class clazz = type;

            for (java.lang.reflect.Constructor con : clazz.getConstructors()) {
                for (Class parameterType : con.getParameterTypes()) {
                    if (parameterType.equals(String.class)) {
                        if (con.getParameterTypes().length == 1) {
                            obj = con.newInstance(value);
                            break;
                        }
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException e) {
            return null;
        }
        return obj;
    }

    
    public static String getIdEntity(Object entry) {
        Object entity = null;
        if (entry instanceof PersistentBag) {
            PersistentBag bag = (PersistentBag) entry;
            if (bag.size() > 0) {
                entity = bag.get(0);
            }
        } else {
            entity = entry;
        }

        if (entity != null) {
            String packages = entity.getClass().getPackage().getName().concat(".");
            String name = entity.getClass().getSimpleName();
            Field fiel[] = entity.getClass().getDeclaredFields();
            for (Field f : fiel) {
                if (isAnnotationType(f, Id.class)) {
                    try {
                        f.setAccessible(true);
                        Object value = f.get(entity);
                        if (value != null) {
                            return packages.concat(name).concat(":").concat(value.toString()).concat(":").concat(value.getClass().getName());
                        } else {
                            return null;
                        }
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return null;
    }
    
    public static Boolean isAnnotationType(Field field, Class clazz) {
        return field.isAnnotationPresent(clazz);
    }
    
    public static Object getTypeObject(Class clazz, String nameField) {
        try {
            Field temp = null;
            Object ob = null;
            if (nameField.contains(".")) {
                String[] split = nameField.split("\\.");
                for (String sp : split) {
                    temp = clazz.getDeclaredField(sp);
                    try {
                        ob = temp.get(clazz.newInstance());
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException ie) {
                    }
                    clazz = temp.getType();
                }
                return clazz;
            } else {
                return clazz.getDeclaredField(nameField).getGenericType();
            }
        } catch (NoSuchFieldException | SecurityException e) {
        }
        return null;
    }

    
}
