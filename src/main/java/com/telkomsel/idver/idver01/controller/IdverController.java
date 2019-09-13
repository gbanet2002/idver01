package com.telkomsel.idver.idver01.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.telkomsel.idver.idver01.model.Idver;


@Controller
public class IdverController {
	@Autowired
	Idver iv;
	
	@RequestMapping(value="/Idver-API", method = RequestMethod.GET)
	@ResponseBody
	public String APICONSENT(@RequestParam String username, @RequestParam String password, @RequestParam String product_code, @RequestParam String msisdn, @RequestParam String homeloc, @RequestParam String workloc, @RequestParam String distance, @RequestParam String similarity ) {
		String resp = iv.idver(username, password, product_code, msisdn, homeloc, workloc, distance, similarity);
		return resp;
	}
}
