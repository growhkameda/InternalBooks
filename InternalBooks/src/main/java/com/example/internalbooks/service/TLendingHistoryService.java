package com.example.internalbooks.service;

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
import com.example.internalbooks.repository.TLendingHistoryRepository;
import com.example.internalbooks.repository.TUserRepository;

@Service
@Transactional
public class TLendingHistoryService {

    @Autowired
    private TLendingHistoryRepository lendingHistoryRepository;
    
    @Autowired
    private TUserRepository userRepository;
    
    //コンストラクタインジェクション
//    public TLendingHistoryService(TLendingHistoryRepository lendingHistoryRepository, TUserRepository userRepository) {
//        this.lendingHistoryRepository = lendingHistoryRepository;
//        this.userRepository = userRepository;
//    }

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
     * 返却時にレビューをDBへ保存するメソッド
     */
	public TLendingHistoryEntity lendRegistration(DtoBookHistoryRegistration dtlend) {
		
		TLendingHistoryEntity tlend = new TLendingHistoryEntity();
		// dtoの内容をEntityにマッピング
        tlend.setBookId(dtlend.getBookId());
        tlend.setLendingDate(dtlend.getLendingDate());
        tlend.setScheduledReturnDate(dtlend.getScheduledReturnDate());
        tlend.setReturnDate(dtlend.getReturnDate());
        tlend.setUserId(dtlend.getUserId());
        tlend.setReview(dtlend.getReview());
//        tlend.setStatus("返却済み");
		
        lendingHistoryRepository.save(tlend);        
		
		return tlend;
    }

}
