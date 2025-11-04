package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;//JpaRepositoryをインポートし、SQLを書かずに基本的なCRUD操作（登録・更新・削除・検索）が使える
import org.springframework.stereotype.Repository;

import com.example.demo.model.CallUser;//CallUserクラスをインポート

@Repository
public interface CallUserRepository extends JpaRepository<CallUser, Long> {// CallUserRepositoryの定義
	// 以下の処理が自動的に利用可能になる
	// findAll()      → 全件取得
	// findById()     → ID指定で1件取得
	// save()         → 新規登録・更新
	// deleteById()   → ID指定で削除
	// count()        → レコード件数を取得
	CallUser findByUserName(String userName);
}