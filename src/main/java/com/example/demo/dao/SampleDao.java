package com.example.demo.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.model.Products;

@Repository
public class SampleDao {

	 public List<Products> getallproductdata(){
		 List<Products> list = new ArrayList<>();
		    list.add(new Products(1, "Iphone 100", 7000.2));
		    list.add(new Products(2, "Computer", 145678.365));
		    list.add(new Products(3, "mouse", 1255.5));
		return list;
	 }
}
