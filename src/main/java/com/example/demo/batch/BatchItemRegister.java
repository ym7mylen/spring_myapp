package com.example.demo.batch;

import java.util.List;

import com.example.demo.dto.Item;
import com.example.demo.model.ItemEntity;
import com.example.demo.service.ItemService;

public class BatchItemRegister {

	//mainという関数は他のクラスからアクセスでき返り値は無い
	//Javaを実行したら、ここから処理を始めてください
    public static void main(String[] args) {

        System.out.println("=== バッチ処理開始 ===");

        //itemsという変数は、中身Itemが入ったリスト箱でリストの中身は
        //idがnullで商品名がiPhone 15、カテゴリ名がスマートフォン、
        //idがnullで商品名がGalaxy S23、カテゴリ名がスマートフォン、
        //idがnullで商品名がiPad Air、カテゴリ名がタブレットを作る
        // ダミーの商品リスト
        List<Item> items = List.of(
            new Item(null, "iPhone 15", "スマートフォン"),
            new Item(null, "Galaxy S23", "スマートフォン"),
            new Item(null, "iPad Air", "タブレット")
        );

        //itemServiceという変数に新しく作ったItemServiceを入れる
        ItemService itemService = new ItemService();

        //itemsの中にある要素を1つずつ取り出して、itemとして使う
        //itemEntityという変数は、itemをEntityに変換したものである
        //itemEntityを使ってitemServiceの中に登録または更新の処理をする
        for (Item item : items) {
        	ItemEntity itemEntity = convertToEntity(item);
            itemService.insertOrUpdate(itemEntity);
        }

        System.out.println("=== バッチ処理終了 ===");
    }
    
    //ItemEntityの関数は、Itemクラス型のオブジェクトのitemという値の引数としこれをEntity化したものである
    //ItemEntityクラスの型のオブジェクトのitemEntityという変数は新しく作ったItemEntityである
    //itemから取得したIdをitemEntityの中に設定する
    //itemから取得したNameをitemEntityの中に設定する
    //itemから取得したCategoryをitemEntityの中に設定する
    //itemEntityに返す
 // Item -> ItemEntity に変換するメソッド
    private static ItemEntity convertToEntity(Item item) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(item.getId());  // id は null のままでよい
        itemEntity.setName(item.getName());
        itemEntity.setCategory(item.getCategory());
        return itemEntity;
    }
}


