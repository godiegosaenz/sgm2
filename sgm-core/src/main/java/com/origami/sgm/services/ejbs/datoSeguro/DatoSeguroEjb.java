/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.datoSeguro;


import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatNacionalidad;
import com.origami.sgm.entities.CatPais;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.ServicioExterno;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author CarlosLoorVargas
 */
@Stateless(name = "datoSeguro")
public class DatoSeguroEjb implements DatoSeguroServices {

    @javax.inject.Inject
    private Entitymanager services;
    @javax.inject.Inject
    protected Entitymanager manager;
    protected JSONArray iCivil, iCias;
    protected org.primefaces.json.JSONObject child, info;
    protected String field;
    protected ServicioExterno se;
    protected DatoSeguro ds = null;

    @Override
    public DatoSeguro getDatos(String cedula, boolean empresa, Integer intentos) {
        HttpsURLConnection cx = null;
        try {
            if (!empresa) {
                se = (ServicioExterno) manager.find(Querys.getServicioExternoByIdent, new String[]{"identificador"}, new Object[]{"ERRC"});
            } else {
                se = (ServicioExterno) manager.find(Querys.getServicioExternoByIdent, new String[]{"identificador"}, new Object[]{"ERSC"});
            }
            if (se == null) {
                return null;
            }
            cx = getAuthConex(se.getUrl() + cedula + "/" + se.getComplemento(), se.getUsuario(), se.getClave());
            if (cx != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader((cx.getInputStream())));
                String output = null;
                while ((output = br.readLine()) != null) {
//                    System.out.println(output);
                    org.primefaces.json.JSONObject jasonObjet = new org.primefaces.json.JSONObject(output);

                    if (empresa) {
                        info = jasonObjet.getJSONObject("DatosTramite").getJSONObject("InformacionCivil");
                        if (jasonObjet.getJSONObject("DatosTramite").getJSONObject("InformacionSuperCias") != null) {
                            iCias = jasonObjet.getJSONObject("DatosTramite").getJSONObject("InformacionSuperCias").getJSONObject("Accionistas").getJSONObject("Companias").getJSONArray("Compania");
                            ds = this.iterate(null, iCias, info, empresa);
                        }
                    } else {
                        if (!jasonObjet.getJSONObject("DatosTramite").isNull("InformacionCivil")) {
                            iCivil = jasonObjet.getJSONObject("DatosTramite").getJSONArray("InformacionCivil");
                            System.out.println("iCivil.toString()" + iCivil.toString());
                            ds = this.iterate(iCivil, null, info, empresa);
                        }
                    }
                }
            } else {
                return null;
            }
        } catch (IOException | JSONException e) {
            intentos++;
            if (intentos < 3) {
                this.getDatos(cedula, empresa, intentos);
            } else {
                ds = null;
                Logger.getLogger(DatoSeguroEjb.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return ds;
    }

    @Override
    public URLConnection configureConnection(URLConnection con) {
        try {
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            con.setConnectTimeout(30000);//30000
            con.setReadTimeout(40000);//40000
            if (con instanceof HttpsURLConnection) {
                HttpsURLConnection conHttps = (HttpsURLConnection) con;
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }};

                HostnameVerifier allHostsValid = new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                };
                try {
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
                    con = conHttps;
                } catch (NoSuchAlgorithmException | KeyManagementException e) {
                    Logger.getLogger(DatoSeguroEjb.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(DatoSeguroEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return con;
    }

    @Override
    public CatEnte getEnteFromDatoSeguro(DatoSeguro data) {
        CatEnte ente = null;
        EnteCorreo correo;
        EnteTelefono telefono;
        String fields[];
        Integer num;
        String nombre = "";

        try {
            if (data != null) {
                ente = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{data.getIdentificacion()});
                if (ente != null) {
                    ente.setDireccion(data.getDireccion());
                    return ente;
                } else {
                    ente = new CatEnte();
                    ente.setFechaCre(new Date());
                    ente.setEsPersona(data.getIdentificacion().trim().length() == 10);
                }

                fields = data.getDescripcion().split(" ");
                num = fields.length;

                switch (num) {
                    case 3:
                        ente.setNombres(fields[2]);
                        ente.setApellidos(fields[0] + " " + fields[1]);
                        break;
                    case 4:
                        ente.setNombres(fields[2] + " " + fields[3]);
                        ente.setApellidos(fields[0] + " " + fields[1]);
                        break;
                    case 5:
                        ente.setNombres(fields[2] + " " + fields[3] + " " + fields[4]);
                        ente.setApellidos(fields[0] + " " + fields[1]);
                        break;
                    default:
                        ente.setApellidos(fields[0] + " " + fields[1]);
                        for (int i = 2; i < num; i++) {
                            nombre = nombre + fields[i];
                            if (i != num - 1) {
                                nombre = nombre + " ";
                            }
                        }
                        ente.setNombres(nombre);
                        break;
                }

                ente.setCiRuc(data.getIdentificacion());
                ente.setDireccion(data.getDireccion());
                ente.setFechaNacimiento(data.getFecNacto());
                if (data.getNacionalidad() != null) {
                    if (data.getNacionalidad().equalsIgnoreCase("Ecuatoriana")) {
                        ente.setNacionalidad(new CatNacionalidad(1L));
                        ente.setPais(new CatPais(1l));
                    } else {
                        ente.setNacionalidad(new CatNacionalidad(2L));
                    }
                    ente.setDiscapacidad(new CtlgItem(203l));
                    ente.setPorcentaje(BigDecimal.ZERO);
                }
                if (data.getCondicion().equalsIgnoreCase("FALLECIDO")) {
                    ente.setEstado("F");
                }
                ente = (CatEnte) services.persist(ente);
                if (data.getEmail() != null && data.getEmail().trim().length() > 0) {
                    correo = new EnteCorreo();
                    correo.setEmail(data.getEmail());
                    correo.setEnte(ente);
                    services.persist(correo);
                }
                if (data.getTelefono() != null && data.getTelefono().trim().length() > 0) {
                    telefono = new EnteTelefono();
                    telefono.setTelefono(data.getTelefono());
                    telefono.setEnte(ente);
                    services.persist(telefono);
                }
            }
        } catch (Exception e) {
            ente = null;
            e.printStackTrace();
        }
        return ente;
    }

    @Override
    public CatEnte llenarEnte(DatoSeguro data, CatEnte ente, Boolean cabiarCiRuc) {
        String fields[];
        Integer num;
        String nombre = "";

        try {
            if (data != null) {
                data.setDescripcion(verificarContenido(data.getDescripcion()));
                fields = data.getDescripcion().split(" ");
                num = fields.length;

                switch (num) {
                    case 3:
                        ente.setNombres(fields[2]);
                        ente.setApellidos(fields[0] + " " + fields[1]);
                        break;
                    case 4:
                        ente.setNombres(fields[2] + " " + fields[3]);
                        ente.setApellidos(fields[0] + " " + fields[1]);
                        break;
                    case 5:
                        ente.setNombres(fields[2] + " " + fields[3] + " " + fields[4]);
                        ente.setApellidos(fields[0] + " " + fields[1]);
                        break;
                    default:
                        ente.setApellidos(fields[0] + " " + fields[1]);
                        for (int i = 2; i < num; i++) {
                            nombre = nombre + fields[i];
                            if (i != num - 1) {
                                nombre = nombre + " ";
                            }
                        }
                        ente.setNombres(nombre);
                        break;
                }
                if (cabiarCiRuc) {
                    ente.setCiRuc(data.getIdentificacion());
                }
                if (data.getDireccion() != null) {
                    ente.setDireccion(verificarContenido(data.getDireccion()));
                }

                if (data.getCondicion().equalsIgnoreCase("FALLECIDO")) {
                    ente.setEstado("A");
                }
                ente.setFechaNacimiento(data.getFecNacto());
            }
        } catch (Exception e) {
            ente = null;
            LOG.log(Level.SEVERE, "Llenar Ente", e);
        }
        return ente;
    }
    private static final Logger LOG = Logger.getLogger(DatoSeguroEjb.class.getName());

    private DatoSeguro iterate(JSONArray civil, JSONArray cias, JSONObject obj, boolean empresa) {
        DateFormat sdf;
        try {
            if (civil != null && civil.length() > 0) {
                ds = new DatoSeguro();
                for (int i = 0; i < civil.length(); i++) {
                    child = civil.getJSONObject(i);
                    field = child.getString("NombreCampo");
                    if (field.equalsIgnoreCase("CEDULA")) {
                        ds.setIdentificacion(child.get("Valor").toString());
                        System.out.println("CEDULA" + ds.getIdentificacion());
                    }
                    if (field.equalsIgnoreCase("NOMBRE")) {
                        ds.setDescripcion(child.getString("Valor"));
                        System.out.println("NOMBRE" + ds.getDescripcion());
                    }
                    if (field.equalsIgnoreCase("GENERO")) {
                        ds.setGenero(child.getString("Valor"));
                        System.out.println("NOMBRE" + ds.getDescripcion());
                    }
                    if (field.equalsIgnoreCase("CONDICIONCIUDADANO")) {
                        ds.setCondicion(child.getString("Valor"));
                    }
                    if (field.equalsIgnoreCase("FECHANACIMIENTO")) {
                        sdf = new SimpleDateFormat("dd/MM/yyyy");
                        ds.setFecNacto(sdf.parse(child.getString("Valor")));
                    }
                    if (field.equalsIgnoreCase("NACIONALIDAD")) {
                        ds.setNacionalidad(child.getString("Valor"));
                    }
                    if (field.equalsIgnoreCase("ESTADOCIVIL")) {
                        ds.setEstadoCivil(child.getString("Valor"));
                    }
                    if (field.equalsIgnoreCase("CONYUGE")) {
                        ds.setConyuge(child.getString("Valor"));
                    }
                }
            }
            if (empresa) {
                ds = new DatoSeguro();
                if (obj.getString("Valor") != null) {
                    ds.setIdentificacion(obj.getString("Valor"));
                } else {
                    return null;
                }
                for (int i = 0; i < cias.length(); i++) {
                    child = cias.getJSONObject(i);
                    field = child.getString("NombreCampo");
                    if (field.equalsIgnoreCase("CIAFCONSTITUCION")) {
                        if (child.getString("Valor") != null) {
                            ds.setFecConst(child.getString("Valor"));
                        }
                    }
                    if (field.equalsIgnoreCase("CIAOBJETOSOCIAL")) {
                        if (child.getString("Valor") != null) {
                            ds.setObjSocial(child.getString("Valor"));
                        }
                    }

                }
            }

        } catch (JSONException | ParseException e) {
            Logger.getLogger(DatoSeguroEjb.class.getName()).log(Level.SEVERE, null, e);
            ds = null;
        }
        return ds;
    }

    private HttpsURLConnection getAuthConex(String urlp, String user, String pass) {
        HttpsURLConnection cx = null;
        try {
            URL url = new URL(urlp);
            cx = (HttpsURLConnection) configureConnection(url.openConnection());
            if (cx != null) {
                String userpass = user + ":" + pass;
                String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
                cx.setRequestProperty("Content-Type", "application/json");
                cx.setRequestProperty("Authorization", basicAuth);
                if (cx.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    return null;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(DatoSeguroEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return cx;
    }

    /**
     *
     * @param descripcion
     * @return
     */
    private String verificarContenido(String descripcion) {
        Charset utf8 = Charset.forName("UTF-8");
        String Buffer = new String(descripcion.getBytes(), utf8);
        return Buffer;
    }

    @Override
    public String getData() {
//        
//        final StringRequest getEnte=new StringRequest(Request.Method.GET, "",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                       // getRolname(progressDialog, login, userName,password);
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                       if(error.toString().equals("com.android.volley.TimeoutError")){
//                            Toast.makeText(login, R.string.toast_errordeconex,Toast.LENGTH_LONG).show();
//                           progressDialog.dismiss();
//                        }else
//                        if(error.toString().equals("com.android.volley.AuthFailureError")) {
//                            Toast.makeText(login, R.string.toast_loginIncorrecto, Toast.LENGTH_LONG).show();
//                            progressDialog.dismiss();
//                        }else{
//                            Toast.makeText(login,R.string.toast_errordeconex,Toast.LENGTH_LONG).show();
//                            progressDialog.dismiss();
//                        }
//                    }
//                }
//        ){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                String creds = String.format("%s:%s",userName,password);
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                headers.put("Authorization", auth);
//                return headers;
//            }
//
//        };
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    

}
