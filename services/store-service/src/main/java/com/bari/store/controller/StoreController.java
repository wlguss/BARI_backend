package com.bari.store.controller;

import com.bari.store.dto.request.StoreRequestDto;
import com.bari.store.dto.response.StoreResponseDto;
import com.bari.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /**
     * 1. 매장 전체 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<StoreResponseDto>> getAllStores() {
        return ResponseEntity.ok(storeService.getAllStores());
    }

    /**
     * 2. 특정 매장 상세 조회 (RQ-1003)
     */
    @GetMapping("/{id}")
    public ResponseEntity<StoreResponseDto> getStoreDetail(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStoreDetail(id));
    }

    /**
     * 3. 매장 정보 수정 (RQ-1004)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateStore(
            @PathVariable("id") Long id, 
            @RequestBody StoreRequestDto requestDto) { 
        
       
        System.out.println("수정 요청 ID: " + id);
        System.out.println("수정 내용: " + requestDto.toString());
        
        storeService.updateStore(id, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 4. 매장 신규 등록
     */
    @PostMapping 
    public ResponseEntity<Long> createStore(@RequestBody StoreRequestDto requestDto) {
        Long storeId = storeService.createStore(requestDto);
        return ResponseEntity.ok(storeId);
    }

    /**
     * 5. 매장 삭제 
     * 실제 데이터를 지우지 않고 deleted_at 컬럼을 업데이트합니다.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }
}