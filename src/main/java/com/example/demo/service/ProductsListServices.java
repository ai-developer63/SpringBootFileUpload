package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.SampleDao;
import com.example.demo.model.Products;

import jakarta.annotation.PostConstruct;

@Service
public class ProductsListServices {

	@Autowired
	SampleDao sampleDao;

	List<Products> initialTotalData = new ArrayList<>();

	@PostConstruct
	public void init() {
		initialTotalData = sampleDao.getallproductdata();

	}

	// To get all data
	public List<Products> getAllProducts() {
		return initialTotalData;
	}

	// To get Data with ID
	public List<Products> getProductsWithId(String id) {
		int idNum = Integer.parseInt(id);
		List<Products> filtereddata = new ArrayList<>();
		for (Products data : initialTotalData) {

			if (initialTotalData.size() < idNum) {

			}

			if (data.getId() == (idNum)) {
				filtereddata.add(data);
			}
		}
		return filtereddata;
	}

}
