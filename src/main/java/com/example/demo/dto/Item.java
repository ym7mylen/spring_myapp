package com.example.demo.dto;

//商品のID、名前、カテゴリを保持し、外部に送信するクラス
public class Item {
	private Long id;
    private String name;
    private String category;

    public Item(Long id,String name, String category) {
    this.id = id;
    this.name = name;
    this.category = category;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    
}


