package edu.umn.msi.tropix.storage.service;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.cxf.feature.Features;

@Path("/storage/")
@Features(features = "org.apache.cxf.feature.LoggingFeature") 
public interface StorageService {
  @GET
  @Path("/user/{userIdentity}/data/{dataId}/")
  String getData(@PathParam("userIdentity") final String userId, @PathParam("dataId") final String dataId);

  @PUT
  @Path("/user/{userIdentity}/data/{dataId}/")
  String putData(@PathParam("userIdentity") final String userId, @PathParam("dataId") final String dataId);
}
