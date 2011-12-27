package edu.umn.msi.tropix.storage.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/storage/")
public interface StorageService {
  @GET
  @Path("/data/{id}/")
  String getData(@PathParam("id") final String id);
}
