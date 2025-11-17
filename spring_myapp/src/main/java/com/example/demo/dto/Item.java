package com.example.demo.dto;

public class Item {
	private Integer id;
    private String name;
    private String category;

    public Item(Integer id,String name, String category) {
    	this.id = id;
        this.name = name;
        this.category = category;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
}
