package de.sekmi.li2b2.services;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


@Path("/webclient")
public class Webclient {
	private static final Logger log = Logger.getLogger(Webclient.class.getName());
	/**
	 * Folder where the webclient's sources are stored (e.g. in the i2b2webclient-*.zip release file)
	 * This may need to be adjusted with new webclient releases, because the i2b2 maintainers change
	 * the folder name between releases.
	 */
	public static final String WEBCLIENT_SOURCES_RESOURCE_PATH = "/i2b2-webclient-master/";
	@GET
	@Path("{path: .*}")
	public Response test(@PathParam("path") String path){
		if( path.equals("i2b2_config_data.js") ){
			// send test config data
			return Response.ok(getClass().getResourceAsStream("/i2b2_config_data.js")).build();
		}else if( path.equals("") ){
			path = "default.htm";
		}

		InputStream in = getClass().getResourceAsStream(WEBCLIENT_SOURCES_RESOURCE_PATH+path);
		if( in == null ){
			// not found
			log.warning(path+" not found");
			return Response.status(Status.NOT_FOUND).build();
		}else{
			return Response.ok(in).build();
		}
	}
}
