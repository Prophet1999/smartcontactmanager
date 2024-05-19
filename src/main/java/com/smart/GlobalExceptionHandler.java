package com.smart;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@ControllerAdvice
public class GlobalExceptionHandler {

	@GetMapping("/error")
	public String errorPage()
	{
		return "Normal/userError";
	}
	
	@ExceptionHandler(Exception.class)
	public String exceptionPage()
	{
		return "redirect:/error";
	}
}
