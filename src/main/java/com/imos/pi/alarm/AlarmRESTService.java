/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imos.pi.alarm;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 *
 * @author Alok
 */
@Path("alarm")
public class AlarmRESTService {

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAlarm(String songName, byte rawData) {

        JSONObject status = new JSONObject(songName);
        status.put("status", "OK");

        System.out.println("POST : " + status.toString());

        return Response.status(Response.Status.CREATED).entity(status.toString()).build();
    }

    @Path("delete/{id}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteAlarm(String data) {

        JSONObject status = new JSONObject(data);
        status.put("status", "OK");

        System.out.println("POST : " + status.toString());

        return Response.status(Response.Status.CREATED).entity(status.toString()).build();
    }
    
    @Path("delete/all")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteAllAlarm(String data) {

        JSONObject status = new JSONObject(data);
        status.put("status", "OK");

        System.out.println("POST : " + status.toString());

        return Response.status(Response.Status.CREATED).entity(status.toString()).build();
    }

    @Path("edit/{id}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editAlarm(String data) {

        JSONObject status = new JSONObject(data);
        status.put("status", "OK");

        System.out.println("POST : " + status.toString());

        return Response.status(Response.Status.CREATED).entity(status.toString()).build();
    }

    @Path("all")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response findAllAlarm(String data) {

        JSONObject status = new JSONObject(data);
        status.put("status", "OK");

        System.out.println("POST : " + status.toString());

        return Response.status(Response.Status.CREATED).entity(status.toString()).build();
    }

    @Path("{id}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response findAlarm(String data) {

        JSONObject status = new JSONObject(data);
        status.put("status", "OK");

        System.out.println("POST : " + status.toString());

        return Response.status(Response.Status.CREATED).entity(status.toString()).build();
    }
}
