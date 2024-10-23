package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.service.ProductsListServices;


@org.springframework.stereotype.Controller
public class Controller {

	@Autowired
	ProductsListServices productsListServices;

	//get All data
	@GetMapping("/alldata")
	public String getMethodName(Model model) {
		model.addAttribute("products", productsListServices.getAllProducts());
		return "index";
	}
	
	
	//getFiltered data
	@GetMapping("/data/{id}")
	public String getMethodName(@PathVariable("id") String id,Model model) {
		model.addAttribute("products", productsListServices.getProductsWithId(id));
		return "index";
	}
	

}
