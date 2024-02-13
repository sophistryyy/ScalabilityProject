package seng468.scalability.endpoints;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class GeneralErrorController implements ErrorController {
	@RequestMapping("/error")
	public Map<String, Object> responseError(HttpServletRequest request) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		String message = "Error";
		if (status != null) {
			message = status.toString();
			Integer statusCode = Integer.valueOf(status.toString());
			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				message = "PATH NOT FOUND";
			} else if (statusCode == HttpStatus.FORBIDDEN.value()) {
				message = "INVALID ACCESS";
			}
		}
		
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put("success", false);
		response.put("data", null);
		response.put("message", message);

		return response;
	}
}