package exceptions;

import javax.ws.rs.core.Response;

public class ErrorMessageHandler {

		public ErrorMessageHandler() {
		}
		
		public static Response toResponse(Response.Status responseStatus, ErrorMessage errorMessage) {
			Response response = Response.status(responseStatus).entity(errorMessage).build();
			
			return response;
		}
}
