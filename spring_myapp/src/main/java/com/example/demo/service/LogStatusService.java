package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.CallLog;
import com.example.demo.repository.CallLogRepository;

@Service
@Transactional// メソッドの中で行われる複数のDB操作をひとまとめに管理
public class LogStatusService {

	// 通話ログのDB操作用リポジトリを自動で入れる
    @Autowired
    private CallLogRepository logRepository;
    /**
     * 確認者用ステータス更新
     * @param logId 通話ログID
     * @param statusKakunin 更新後の値（0=未確認, 1=確認済）
     * @return 成功時 true
     */
    public boolean updateStatusKakunin(Long logId, Integer statusKakunin) {
        CallLog log = logRepository.findById(logId).orElse(null);
        if (log == null) {// IDで通話ログを検索。存在しなければnullを返す
            return false;// 該当ログがなければfalseを返す
        }
        log.setStatusKakunin(statusKakunin);// 確認者用ステータスを更新
        logRepository.save(log);// DB に保存（更新）
        return true;// 更新成功なら true を返す
    }
    
    /**
     * 管理者用ステータス更新
     * @param logId 通話ログID
     * @param statusKanri 更新後の値（0=未確認, 1=確認済）
     * @return 成功時 true
     */
    public boolean updateStatusKanri(Long logId, Integer statusKanri) {
        CallLog log = logRepository.findById(logId).orElse(null);
        if (log == null) {
            return false;
        }
        log.setStatusKanri(statusKanri);// 管理者用ステータスを更新
        logRepository.save(log);
        return true;
    }

}
