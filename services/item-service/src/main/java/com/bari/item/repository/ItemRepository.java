package com.bari.item.repository;

import com.bari.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 아이템 레포지토리.
 * soft delete를 고려해서 deletedAt IS NULL 조건을 포함합니다.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * 삭제되지 않은 모든 아이템 조회.
     * 목록 조회 API에서 사용합니다.
     *
     * @return 삭제되지 않은 아이템 목록
     */
    List<Item> findAllByDeletedAtIsNull();

    /**
     * ID로 삭제되지 않은 아이템 조회.
     * 단건 조회 API에서 사용합니다.
     *
     * @param id 아이템 ID
     * @return 아이템 Optional
     */
    Optional<Item> findByIdAndDeletedAtIsNull(Long id);
}
