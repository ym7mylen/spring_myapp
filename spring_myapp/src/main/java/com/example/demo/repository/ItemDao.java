package com.example.demo.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.dto.Item;

@Repository 
public class ItemDao {

    public void insertOrUpdate(Item item) {

        try (Connection con = DBManager.getConnection()) {

            String sql = "INSERT INTO items (name, category) VALUES (?, ?) "
                       + "ON DUPLICATE KEY UPDATE category = VALUES(category)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, item.getName());
            ps.setString(2, item.getCategory());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, name, category FROM items")) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new Item(
                		rs.getInt("id"),           // id を取得
                        rs.getString("name"),
                        rs.getString("category")
                    ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
