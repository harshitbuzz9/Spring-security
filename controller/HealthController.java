package com.bridge.herofincorp.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bridge.herofincorp.configs.APILogger;

@RestController
public class HealthController {

	@GetMapping("/v1/healthcheck")
	public String health() {

		return "I am Working for partner app";
	}

	@GetMapping("/")
	public String healthRoot() {

		return "I am Working..";
	}


}
