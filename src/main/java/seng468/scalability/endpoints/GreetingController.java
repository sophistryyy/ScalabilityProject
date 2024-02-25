package seng468.scalability.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import seng468.scalability.models.response.Response;

@RestController
public class GreetingController {

	@GetMapping("/greeting")
	public Response greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return Response.ok("HELLOO");
	}
}