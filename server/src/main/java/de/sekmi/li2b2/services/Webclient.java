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
	 * Specify the subfolder in the zip file containing the file {@code default.htm}, e.g. {@code /webclient/} or {@code /i2b2-webclient-master/}.
	 */
	public static final String WEBCLIENT_SOURCES_RESOURCE_PATH = "/i2b2-webclient-1.7.12.0001/";
	@GET
	@Path("{path: .*}")
	public Response get(@PathParam("path") String path){
		if( path.equals("i2b2_config_data.js") ){
			// send test config data
			return Response.ok(getClass().getResourceAsStream("/i2b2_config_data.js")).build();
		}else if( path.equals("") ){
			path = "default.htm";
		}
//		

//		if( path.equals("default.htm") ) {
//			// inject code
//			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(WEBCLIENT_SOURCES_RESOURCE_PATH+path)));
//			
//			return Response.ok(new InjectingFilterReader(br, Pattern.compile("^</head>"),"<script>alert('allo');</script>",true)).build();
//		}else {
			InputStream in = getClass().getResourceAsStream(WEBCLIENT_SOURCES_RESOURCE_PATH+path);
			if( in == null ){
				// not found
				log.warning(path+" not found");
				return Response.status(Status.NOT_FOUND).build();
			}else{
				return Response.ok(in).build();
			}
//		}
	}

}
