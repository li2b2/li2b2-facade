package de.sekmi.histream.i2b2.services;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


import javax.ws.rs.core.UriInfo;

@Path("/webclient")
public class Webclient {
	private static final Logger log = Logger.getLogger(Webclient.class.getName());

	@GET
	@Path("{path: .*}")
	public Response test(@PathParam("path") String path){
		if( path.equals("i2b2_config_data.js") ){
			// send test config data
			return Response.ok(getClass().getResourceAsStream("/i2b2_config_data.js")).build();
		}

		InputStream in = getClass().getResourceAsStream("/webclient/"+path);
		if( in == null ){
			// not found
			log.warning(path+" not found");
			return Response.status(Status.NOT_FOUND).build();
		}else{
			return Response.ok(in).build();
		}
	}
}
