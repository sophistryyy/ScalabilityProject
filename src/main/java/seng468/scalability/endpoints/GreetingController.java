package seng468.scalability.endpoints;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";

	@GetMapping("/greeting")
	public Map<String, Object> greeting(@RequestParam(value = "name", defaultValue = "World") String name) {

		Map<String, Object> data =  new LinkedHashMap<String, Object>();

		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put("success", true);
		response.put("data", "HELLO");
		return response;
	}
}