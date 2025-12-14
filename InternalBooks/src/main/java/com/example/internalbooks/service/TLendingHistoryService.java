package com.example.internalbooks.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internalbooks.dto.DtoBookHistory;
import com.example.internalbooks.dto.DtoBookHistoryRegistration;
import com.example.internalbooks.entity.TLendingHistoryEntity;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.repository.TBookRepository;
import com.example.internalbooks.repository.TLendingHistoryRepository;
import com.example.internalbooks.repository.TUserRepository;

@Service
@Transactional
public class TLendingHistoryService {

    @Autowired
    private TLendingHistoryRepository lendingHistoryRepository;
    
    @Autowired
    private TUserRepository userRepository;
    
    @Autowired
    private TBookRepository bookRepository;
    
    //コンストラクタインジェクション
    public TLendingHistoryService(TLendingHistoryRepository lendingHistoryRepository, TUserRepository userRepository) {
        this.lendingHistoryRepository = lendingHistoryRepository;
        this.userRepository = userRepository;
    }

    // 書籍IDから履歴を取得
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
    
	
    /**
     * 貸出時にレビューをDBへ保存するメソッド
     */
	public TLendingHistoryEntity rentalCompleted(DtoBookHistoryRegistration dtlend) {
		
		TLendingHistoryEntity tlend = new TLendingHistoryEntity();

		// dtoの内容をEntityにマッピング
        tlend.setBookId(dtlend.getBookId());
        
        tlend.setLendingDate(LocalDateTime.now());
        
    	tlend.setScheduledReturnDate(LocalDateTime.now().plusDays(7));
        
        tlend.setReturnDate(null);
        tlend.setUserId(dtlend.getUserId());
    	tlend.setReview(null);
//        tlend.setStatus("返却済み");
		
        lendingHistoryRepository.save(tlend);
        
        // 書籍の borrowerId を貸出ユーザーIDに更新
        bookRepository.updateBorrowerByBookId(dtlend.getBookId(), dtlend.getUserId());
		
		return tlend;
    }
    
    
	/**
     * 返却時にレビューをDBへ保存するメソッド
     */
	public TLendingHistoryEntity returnCompleted(DtoBookHistoryRegistration dtlend) {
		
		TLendingHistoryEntity tlend = lendingHistoryRepository.
				findActiveLendingHistory(dtlend.getBookId(), dtlend.getUserId()).orElse(new TLendingHistoryEntity());
//				new TLendingHistoryEntity();
		// dtoの内容をEntityにマッピング
        tlend.setBookId(dtlend.getBookId());
        
        // DTOの値がnullでなければ上書き
        if (dtlend.getLendingDate() != null) {
        	tlend.setLendingDate(dtlend.getLendingDate());
        }
        
        if (dtlend.getScheduledReturnDate() != null) {
        	tlend.setScheduledReturnDate(dtlend.getScheduledReturnDate());
        }
        
        tlend.setReturnDate(LocalDateTime.now());
        tlend.setUserId(dtlend.getUserId());
        
        // reviewが入力されていれば更新
        if (dtlend.getReview() != null && !dtlend.getReview().isEmpty()) {
        	tlend.setReview(dtlend.getReview());
        }
//        tlend.setStatus("返却済み");
		
        lendingHistoryRepository.save(tlend);
        
        bookRepository.clearBorrowerByBookId(dtlend.getBookId());
		
		return tlend;
    }

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
