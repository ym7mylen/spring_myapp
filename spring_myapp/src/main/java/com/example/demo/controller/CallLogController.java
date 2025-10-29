package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.CallLog;
import com.example.demo.repository.CallLogRepository;

@Controller
@RestController
@RequestMapping("/call-logs")
public class CallLogController {
	// top.html を表示するメソッド
	@GetMapping("/top")
	public String topPage() {
	    return "top";  // top.html を返す
	}

	// call_log_details.html を表示するメソッド（ID指定でログ詳細を表示）
	@GetMapping("/call_log/{id}")
	public String callLogDetailsPage(@PathVariable Long id) {
		return "call_log_details";  // call_log_details.html を返す
	}

    @Autowired
    private CallLogRepository callLogRepository;

    @GetMapping
    public List<CallLog> getAllCallLogs() {
        return callLogRepository.findAll();
    }

    @PostMapping
    public CallLog createCallLog(@RequestBody CallLog callLog) {
        return callLogRepository.save(callLog);
    }
}
