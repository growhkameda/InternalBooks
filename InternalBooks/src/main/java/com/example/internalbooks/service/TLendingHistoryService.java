package com.example.internalbooks.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internalbooks.dto.DtoBookHistory;
import com.example.internalbooks.entity.TLendingHistoryEntity;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.repository.TLendingHistoryRepository;
import com.example.internalbooks.repository.TUserRepository;

@Service
@Transactional
public class TLendingHistoryService {

    @Autowired
    private TLendingHistoryRepository lendingHistoryRepository;
    
    @Autowired
    private TUserRepository userRepository;

    // 書籍IDから履歴を取得
//    public List<TLendingHistoryEntity> getHistoryByBookId(String bookId) {
//        return lendingHistoryRepository.findByBookId(bookId);
//    }

    // ユーザーIDから履歴を取得
//    public List<TLendingHistoryEntity> getHistoryByUserId(Integer userId) {
//        return lendingHistoryRepository.findByUserId(userId);
//    }

    // 新しい貸出履歴を保存
//    public TLendingHistoryEntity saveHistory(TLendingHistoryEntity history) {
//        return lendingHistoryRepository.save(history);
//    }
    
    public List<DtoBookHistory> getHistoryByBookId(Integer bookId) {
        List<TLendingHistoryEntity> entities = lendingHistoryRepository.findByBookId(bookId);

        List<DtoBookHistory> dtoList = new ArrayList<>();
        for (TLendingHistoryEntity e : entities) {
            DtoBookHistory dto = new DtoBookHistory();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日(E)");
            dto.setLendingDate(
            	e.getLendingDate() != null ? e.getLendingDate().format(formatter) : "-"
            );
            dto.setScheduledReturnDate(
                e.getScheduledReturnDate() != null ? e.getScheduledReturnDate().format(formatter) : "-"
            );
            dto.setReturnDate(
                e.getReturnDate() != null ? e.getReturnDate().format(formatter) : "-"
            );
            dto.setReview(e.getReview());
            
            // userName はまだ仮セット
            TUserEntity user = userRepository.findById(e.getUserId()).orElse(null);
            dto.setUserName(user != null ? user.getName() : "不明ユーザー");
            dtoList.add(dto);
        }
        return dtoList;
    }
	
//	public void ReturnCompleted(Integer bookId, String review) {
	    // t_lendinghistory を更新して review を保存する
//		Optional<TLendingHistoryEntity> optionalHistory = lendingHistoryRepository.findByBookIdAndStatus(bookId, "貸出中");
//	    if (optionalHistory.isPresent()) {
//	    	TLendingHistoryEntity history = optionalHistory.get();
//	    	history.setReview(review);
//	    	history.setReturnDate(LocalDateTime.now());
////	    	history.setStatus("返却済み");
//	        lendingHistoryRepository.save(history);
//	    }
//	}

    /** 木俣(2025/10/26)
     * 書籍IDのリストから返却予定日のリストを取得する
     */
    public List<DtoBookHistory> getScheduledReturnDatesByBookIds(List<Integer> bookIds) {
        List<DtoBookHistory> bookHistoryList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日(E)");
        
        for (Integer bookId : bookIds) {
            DtoBookHistory history = new DtoBookHistory();
            
            // 書籍IDから最新の貸出履歴を取得する
            List<TLendingHistoryEntity> entities = lendingHistoryRepository.findByBookId(bookId);
            if (!entities.isEmpty()) {
                TLendingHistoryEntity latestHistory = entities.get(0);
                if (latestHistory.getScheduledReturnDate() != null) {
                    history.setScheduledReturnDate(latestHistory.getScheduledReturnDate().format(formatter));
                } else {
                    history.setScheduledReturnDate("-");
                }
            } else {
                // 履歴がない場合は"-"を設定(DB管理者に連絡してね)
                history.setScheduledReturnDate("-");
            }
            
            bookHistoryList.add(history);
        }
        
        return bookHistoryList;
    }

}
