package api.servicio;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ControladorExcepciones {


	 @ExceptionHandler(ResponseStatusException.class)
	    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
	        if (ex.getStatusCode() == HttpStatus.NO_CONTENT) {
	            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	        } else {
	            return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
	        }
	    }
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex, WebRequest request) {
		return new ResponseEntity<>("Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	@ExceptionHandler(SQLException.class)
	public ResponseEntity<String> databaseError(RuntimeException ex) {
		return new ResponseEntity<>("Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public String handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
		return "Método HTTP no permitido. Solo se aceptan peticiones GET.";
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        Map<String, String> response = new HashMap<>();
        response.put("error", "Falta el parámetro requerido: '" + name + "'");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
