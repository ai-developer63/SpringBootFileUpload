package com.example.demo.api_controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Products;
import com.example.demo.service.ProductsListServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class ApiInitialController {

	@Autowired
	ProductsListServices productlist;
	
	@GetMapping("/productlist")
	public List<Products> getAllProducts() {
		return productlist.getAllProducts();
	}
	
	@GetMapping("/productlist/{id}")
	public List<Products> getProductsWithID(@PathVariable("id") String id) {
		return productlist.getProductsWithId(id);
	}
	
}
