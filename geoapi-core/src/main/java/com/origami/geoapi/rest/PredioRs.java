/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.geoapi.rest;

import com.origami.geoapi.utils.Bbox;
import com.origami.geoapi.utils.WmsImageUtils;
import com.origami.geoapi.predios.CatastroGeoConfigs;
import com.origami.geoapi.predios.PrediosService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 *
 * @author Fernando
 */
@RequestScoped
@Path("/predio")
public class PredioRs {
    
    @Inject
        protected CatastroGeoConfigs catConf;
    @Inject
    protected PrediosService predioSrv;
    
    @GET
    @Path("/croquis/{cod}")
    @Produces("image/png")
    public Response croquisFicha(@PathParam("cod") String codCatas){
        
        Bbox bbx = predioSrv.predioBboxEnvelope(codCatas);
       // Bbox bbx = predioSrv.predioBboxEnvelopeGid(Integer.valueOf(codCatas));
        
        Client client = ClientBuilder.newClient();
        WebTarget wt = client.target(catConf.getWmsUrl())
            .queryParam("service", "WMS")
            .queryParam("version", "1.1.0")
            .queryParam("request", "GetMap")
            .queryParam("layers", catConf.getCroquisLayer())
            .queryParam("bbox", bbx.getXmin() + "," + bbx.getYmin() + "," + bbx.getXmax() + "," + bbx.getYmax())
            .queryParam("width", "768")
            .queryParam("height", WmsImageUtils.heightCalculate(bbx, 768))
            .queryParam("srs", catConf.getSrid())
            .queryParam("format", "image/png")
            .queryParam("env", "numpredio:" + codCatas); //env=clave:value1
            //.queryParam("env", "clave:" + codCatas); //env=clave:value1
        final InputStream is = wt.request().get(InputStream.class);
        
        StreamingOutput so = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                byte[] buffer = new byte[1024]; // Adjust if you want
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1)
                {
                    output.write(buffer, 0, bytesRead);
                    output.flush();
                }
            }
        };
        
        return Response.ok(so).build();
    }
    
    @GET
    @Path("/croquisFusion/{codigos}")
    @Produces("image/png")
    public Response croquisFusionPredios(@PathParam("codigos") String codCatasListComa){
        return null;
    }
    
}
