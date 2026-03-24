// package com.bari.store;

// import com.bari.store.dto.request.StoreRequestDto;
// import com.bari.store.dto.response.StoreResponseDto;
// import com.bari.store.entity.Store;
// import com.bari.store.repository.StoreRepository;
// import com.bari.store.service.StoreService;
// import com.bari.user.repository.UserRepository;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.BDDMockito.given;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;

// @ExtendWith(MockitoExtension.class)
// public class StoreServiceTest {

//     @InjectMocks
//     private StoreService storeService;

//     @Mock
//     private StoreRepository storeRepository;

//     @Mock
//     private UserRepository userRepository;

//     @Test
//     @DisplayName("1. 매장 등록 성공 테스트")
//     void createStore_Success() {
//         com.bari.user.entity.User mockUser = com.bari.user.entity.User.builder().id(1L).build();
//         given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));

//         StoreRequestDto requestDto = new StoreRequestDto("맛집", "설명", "주소", "번호", "시간", "한식", "img.jpg");
//         Store savedStore = Store.builder().id(1L).name("맛집").owner(mockUser).build();
//         given(storeRepository.save(any(Store.class))).willReturn(savedStore);

//         Long savedId = storeService.createStore(1L, requestDto);

//         assertThat(savedId).isEqualTo(1L);
//         verify(userRepository, times(1)).findById(1L);
//     }

//     @Test
//     @DisplayName("1.1 존재하지 않는 유저가 매장 등록 시 예외 발생")
//     void createStore_UserNotFound() {
//         Long invalidUserId = 999L;
//         StoreRequestDto requestDto = new StoreRequestDto("맛집", "설명", "주소", "번호", "시간", "한식", "img.jpg");
//         given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());

//         assertThrows(IllegalArgumentException.class, () -> storeService.createStore(invalidUserId, requestDto));
//     }

//     @Test
//     @DisplayName("2. 매장 삭제 테스트 (Soft Delete)")
//     void deleteStore_SoftDelete_Success() {
//         Long storeId = 1L;
//         Store store = Store.builder().id(storeId).name("삭제할매장").build();
//         given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

//         storeService.deleteStore(storeId);
//         assertThat(store.getDeletedAt()).isNotNull();
//     }

//     @Test
//     @DisplayName("2.1 이미 삭제된 매장을 삭제하려 할 때 예외 발생")
//     void deleteStore_AlreadyDeleted() {
//         Long storeId = 1L;
//         Store store = Store.builder().id(storeId).name("이미삭제된매장").build();
//         store.delete();

//         given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

//         assertThrows(IllegalArgumentException.class, () -> storeService.deleteStore(storeId));
//     }

//     @Test
//     @DisplayName("3. 특정 매장 상세 조회 테스트")
//     void getStoreDetail_Success() {
//         Long storeId = 1L;
//         Store store = Store.builder().id(storeId).name("맛집").build();
//         given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

//         StoreResponseDto response = storeService.getStoreDetail(storeId);
//         assertThat(response.getStoreName()).isEqualTo("맛집");
//     }

//     @Test
//     @DisplayName("2.3 존재하지 않는 매장 조회 시 예외 발생")
//     void getStoreDetail_NotFound() {
//         Long invalidId = 999999L;
//         given(storeRepository.findById(invalidId)).willReturn(Optional.empty());

//         assertThrows(IllegalArgumentException.class, () -> storeService.getStoreDetail(invalidId));
//     }

//     @Test
//     @DisplayName("4. 매장 목록 조회 테스트")
//     void getAllStores_Success() {
//         Store store1 = Store.builder().id(1L).name("맛집1").build();
//         Store store2 = Store.builder().id(2L).name("맛집2").build();
//         given(storeRepository.findAll()).willReturn(Arrays.asList(store1, store2));

//         List<StoreResponseDto> responses = storeService.getAllStores();
//         assertThat(responses).hasSize(2);
//     }

//     @Test
//     @DisplayName("5. 매장 정보 수정 테스트")
//     void updateStore_Success() {
//         Long storeId = 1L;
//         Store store = Store.builder().id(storeId).name("기존이름").build();
//         StoreRequestDto updateDto = new StoreRequestDto("새이름", "설명", "주소", "번호", "시간", "카테고리", "이미지");
//         given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

//         storeService.updateStore(storeId, updateDto);
//         assertThat(store.getName()).isEqualTo("새이름");
//     }

//     @Test
//     @DisplayName("5.2 이미 삭제된 매장을 수정하려 할 때 예외 발생")
//     void updateStore_AlreadyDeleted() {
//         Long storeId = 1L;
//         Store store = Store.builder().id(storeId).name("삭제된맛집").build();
//         store.delete(); 

//         StoreRequestDto updateDto = new StoreRequestDto("새이름", "설명", "주소", "번호", "시간", "카테고리", "이미지");
//         given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

//         assertThrows(IllegalArgumentException.class, () -> storeService.updateStore(storeId, updateDto));
//     }
// }