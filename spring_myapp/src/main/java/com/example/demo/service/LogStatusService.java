package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.CallLog;
import com.example.demo.repository.CallLogRepository;

@Service
@Transactional
public class LogStatusService {

    @Autowired
    private CallLogRepository logRepository;

    /**
     * 確認者用ステータスを更新
     * @param logId 通話ログID
     * @param statusKakunin 更新後の値（0=未確認, 1=確認済）
     * @return 成功時 true
     */
    public boolean updateStatusKakunin(Long logId, Integer statusKakunin) {
        CallLog log = logRepository.findById(logId).orElse(null);
        if (log == null) {
            return false;
        }
        log.setStatusKakunin(statusKakunin);
        logRepository.save(log);
        return true;
    }
    /**
     * 管理者用ステータスを更新
     * @param logId 通話ログID
     * @param statusKanri 更新後の値（0=未確認, 1=確認済）
     * @return 成功時 true
     */
    public boolean updateStatusKanri(Long logId, Integer statusKanri) {
        CallLog log = logRepository.findById(logId).orElse(null);
        if (log == null) {
            return false;
        }
        log.setStatusKanri(statusKanri);
        logRepository.save(log);
        return true;
    }

}
