package com.example.demo.model;

public class Products {
	
	int id;
	String Name;
	double price;
	@Override
	public String toString() {
		return "products [id=" + id + ", Name=" + Name + ", price=" + price + "]";
	}
	public Products() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Products(int id, String name, double price) {
		super();
		this.id = id;
		Name = name;
		this.price = price;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	

}
