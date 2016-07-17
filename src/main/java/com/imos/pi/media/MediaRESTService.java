/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.media;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 *
 * @author Alok
 */
@Path("media")
public class MediaRESTService {
    
    @Path("mp3/play")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response playMP3Song(String data) {
        
        JSONObject status = new JSONObject(data);
        status.put("status", "OK");
        
        System.out.println("POST : "+status.toString());
        
        return Response.status(Response.Status.CREATED).entity(status.toString()).build();
    }
    
    @Path("mp3/play")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response configureAlarmTime(String data) {
        
        JSONObject status = new JSONObject(data);
        status.put("status", "OK");
        
        System.out.println("POST : "+status.toString());
        
        return Response.status(Response.Status.CREATED).entity(status.toString()).build();
    }
    
    @Path("mp3/play")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAlarmTime() {
        
        JSONObject status = new JSONObject();
        status.put("status", "OK");
        
        System.out.println("POST : "+status.toString());
        
        return Response.status(Response.Status.CREATED).entity(status.toString()).build();
    }
}
