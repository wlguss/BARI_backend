package com.bari.item.service;

import com.bari.common.exception.BusinessException;
import com.bari.item.dto.request.ItemRequest;
import com.bari.item.dto.response.ItemResponse;
import com.bari.item.entity.Item;
import com.bari.item.exception.ItemErrorCode;
import com.bari.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 아이템 비즈니스 로직 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * 전체 아이템 목록 조회 (삭제되지 않은 것만).
     *
     * @return 아이템 목록
     */
    public List<ItemResponse> getItems() {
        return itemRepository.findAllByDeletedAtIsNull().stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 아이템 단건 조회.
     *
     * @param id 아이템 ID
     * @return 아이템 정보
     * @throws BusinessException ITEM_NOT_FOUND — 존재하지 않거나 삭제된 아이템
     */
    public ItemResponse getItem(Long id) {
        Item item = itemRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ItemErrorCode.ITEM_NOT_FOUND));
        return ItemResponse.from(item);
    }

    /**
     * 아이템 생성.
     * createdBy에 현재 사용자의 userId를 저장합니다.
     * userId는 api-gateway가 주입한 X-User-Id 헤더에서 추출됩니다.
     *
     * @param request 아이템 생성 요청 DTO
     * @param userId  생성자 userId (X-User-Id 헤더에서 추출)
     * @return 생성된 아이템 정보
     */
    @Transactional
    public ItemResponse createItem(ItemRequest request, Long userId) {
        Item item = Item.create(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                userId
        );

        Item savedItem = itemRepository.save(item);
        log.info("아이템 생성 완료 - itemId: {}, userId: {}", savedItem.getId(), userId);

        return ItemResponse.from(savedItem);
    }

    /**
     * 아이템 수정.
     * 본인이 생성한 아이템만 수정 가능합니다 (createdBy == userId 체크).
     *
     * @param id      아이템 ID
     * @param request 수정 요청 DTO
     * @param userId  요청자 userId
     * @return 수정된 아이템 정보
     * @throws BusinessException ITEM_NOT_FOUND — 존재하지 않거나 삭제된 아이템
     * @throws BusinessException ITEM_FORBIDDEN — 본인이 생성하지 않은 아이템
     */
    @Transactional
    public ItemResponse updateItem(Long id, ItemRequest request, Long userId) {
        Item item = itemRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ItemErrorCode.ITEM_NOT_FOUND));

        // 본인 소유 아이템인지 확인
        if (!item.getCreatedBy().equals(userId)) {
            throw new BusinessException(ItemErrorCode.ITEM_FORBIDDEN);
        }

        item.update(request.getName(), request.getDescription(), request.getPrice());
        log.info("아이템 수정 완료 - itemId: {}, userId: {}", id, userId);

        return ItemResponse.from(item);
    }

    /**
     * 아이템 삭제 (soft delete).
     * 본인이 생성한 아이템만 삭제 가능합니다 (createdBy == userId 체크).
     *
     * @param id     아이템 ID
     * @param userId 요청자 userId
     * @throws BusinessException ITEM_NOT_FOUND — 존재하지 않거나 이미 삭제된 아이템
     * @throws BusinessException ITEM_FORBIDDEN — 본인이 생성하지 않은 아이템
     */
    @Transactional
    public void deleteItem(Long id, Long userId) {
        Item item = itemRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ItemErrorCode.ITEM_NOT_FOUND));

        // 본인 소유 아이템인지 확인
        if (!item.getCreatedBy().equals(userId)) {
            throw new BusinessException(ItemErrorCode.ITEM_FORBIDDEN);
        }

        // soft delete 처리 (BaseTimeEntity.softDelete())
        item.softDelete();
        log.info("아이템 삭제 완료 - itemId: {}, userId: {}", id, userId);
    }
}
