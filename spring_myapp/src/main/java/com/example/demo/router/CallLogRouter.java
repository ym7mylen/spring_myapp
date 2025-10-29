package com.example.demo.router;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.CallLog;
import com.example.demo.repository.CallLogRepository;

@RestController
public class CallLogRouter {

    @Autowired
    private CallLogRepository callLogRepository;

    // 通話ログ一覧の取得
    @GetMapping("/callLogs")
    public List<CallLog> getAllCallLogs() {
        return callLogRepository.findAll();
    }

    // 通話ログ詳細の取得
    @GetMapping("/callLogs/{id}")
    public CallLog getCallLogById(@PathVariable Long id) {
        Optional<CallLog> log = callLogRepository.findById(id);
        return log.orElse(null); // ログが見つからない場合はnullを返す
    }

    // 特定ユーザーの通話ログ一覧の取得（ユーザーIDでフィルタ）
    @GetMapping("/callLogs/user/{userId}")
    public List<CallLog> getCallLogsByUserId(@PathVariable Long userId) {
        return callLogRepository.findByUserId(userId);
    }
}
