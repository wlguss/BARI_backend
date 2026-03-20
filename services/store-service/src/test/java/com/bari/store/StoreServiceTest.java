package com.bari.store;

import com.bari.store.dto.request.StoreRequestDto;
import com.bari.store.dto.response.StoreResponseDto;
import com.bari.store.entity.Store;
import com.bari.store.repository.StoreRepository;
import com.bari.store.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Test
    @DisplayName("1. 매장 등록 성공 테스트")
    void createStore_Success() {
        StoreRequestDto requestDto = new StoreRequestDto(
            "맛집", "맛있는 가게", "서울시", "010-1234-5678", "09:00~21:00", "한식", "img.jpg"
        );
        Store savedStore = Store.builder().id(1L).name("맛집").build();
        
        given(storeRepository.save(any(Store.class))).willReturn(savedStore);

        Long savedId = storeService.createStore(requestDto);

        assertThat(savedId).isEqualTo(1L);
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    @DisplayName("2. 매장 삭제 테스트 (Soft Delete)")
    void deleteStore_SoftDelete_Success() {
        Long storeId = 1L;
        Store store = Store.builder().id(storeId).name("삭제할매장").build();
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        storeService.deleteStore(storeId);

        assertThat(store.getDeletedAt()).isNotNull(); 
    }

    @Test
    @DisplayName("3. 특정 매장 상세 조회 테스트")
    void getStoreDetail_Success() { 
        Long storeId = 1L;
        Store store = Store.builder().id(storeId).name("맛집").build();
        
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        
        StoreResponseDto response = storeService.getStoreDetail(storeId); 

        assertThat(response.getStoreName()).isEqualTo("맛집");
    }

    @Test
@DisplayName("2.3 존재하지 않는 매장 조회 시 예외 발생")
void getStoreDetail_NotFound() {
    // 리포지토리가 빈 값(Optional.empty())을 돌려주도록 설정
    Long invalidId = 999999L;
    given(storeRepository.findById(invalidId)).willReturn(Optional.empty());

    // productService를 호출했을 때 IllegalArgumentException이 터지는지 확인

    assertThrows(IllegalArgumentException.class, () -> {
        storeService.getStoreDetail(invalidId);
    });
}
    

    @Test
    @DisplayName("4. 매장 목록 조회 테스트")
    void getAllStores_Success() { 
        Store store1 = Store.builder().id(1L).name("맛집1").build();
        Store store2 = Store.builder().id(2L).name("맛집2").build();
        given(storeRepository.findAll()).willReturn(Arrays.asList(store1, store2));

        
        List<StoreResponseDto> responses = storeService.getAllStores();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getStoreName()).isEqualTo("맛집1");
    }

    @Test
    @DisplayName("5. 매장 정보 수정 테스트")
    void updateStore_Success() {
        Long storeId = 1L;
        Store store = Store.builder().id(storeId).name("기존이름").build();
        StoreRequestDto updateDto = new StoreRequestDto(
            "새이름", "설명", "주소", "번호", "시간", "카테고리", "이미지"
        );

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        storeService.updateStore(storeId, updateDto);

        assertThat(store.getName()).isEqualTo("새이름");
    }

    
}