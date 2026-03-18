package com.bari.item.controller;

import com.bari.common.response.ApiResponse;
import com.bari.security.annotation.CurrentUserId;
import com.bari.item.dto.request.ItemRequest;
import com.bari.item.dto.response.ItemResponse;
import com.bari.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 아이템 API 컨트롤러.
 *
 * ========================================================
 * [X-Header 인증 방식 설명 — 팀원 필독]
 * ========================================================
 *
 * 이 서비스는 api-gateway를 통한 X-Header 인증 방식을 사용합니다.
 *
 * [인증 흐름]
 * 1. 클라이언트가 api-gateway(8080)로 요청 (Authorization: Bearer {JWT})
 * 2. api-gateway의 JwtGatewayFilter가 JWT 검증
 * 3. 검증 성공 시 X-User-Id, X-User-Role 헤더 추가 후 item-service(8087)로 전달
 * 4. item-service의 HeaderAuthenticationFilter가 헤더를 읽어 SecurityContext 설정
 * 5. @CurrentUserId 어노테이션이 SecurityContext에서 userId를 자동 추출
 *
 * [api-gateway를 통한 호출]
 * curl -H "Authorization: Bearer {JWT}" http://localhost:8080/api/items
 *
 * [직접 호출 시 (로컬 개발/테스트)]
 * api-gateway를 거치지 않고 직접 item-service를 호출할 때는
 * X-User-Id, X-User-Role 헤더를 수동으로 설정해야 합니다:
 *
 * curl -H "X-User-Id: 1" -H "X-User-Role: USER" http://localhost:8087/api/items
 * curl -H "X-User-Id: 1" -H "X-User-Role: USER" \
 *      -H "Content-Type: application/json" \
 *      -d '{"name":"사과","description":"신선한 사과","price":3000}' \
 *      http://localhost:8087/api/items
 *
 * [새 서비스 추가 시 이 패턴을 그대로 따르면 됩니다]
 * ========================================================
 */
@Tag(name = "아이템 API", description = "아이템 CRUD (X-Header 인증 방식 예시)")
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 아이템 목록 조회 — 인증 불필요 예시.
     * SecurityConfig에서 GET /api/items는 permitAll로 설정되어 있습니다.
     *
     * @return 아이템 목록 (200 OK)
     */
    @Operation(summary = "아이템 목록 조회", description = "인증 없이 모든 아이템 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getItems() {
        List<ItemResponse> items = itemService.getItems();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    /**
     * 아이템 단건 조회 — 인증 불필요 예시.
     *
     * @param id 아이템 ID
     * @return 아이템 정보 (200 OK)
     */
    @Operation(summary = "아이템 단건 조회", description = "특정 아이템의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> getItem(@PathVariable Long id) {
        ItemResponse item = itemService.getItem(id);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    /**
     * 아이템 생성 — 인증 필요.
     *
     * [X-Header 인증 방식]
     * api-gateway가 JWT를 검증하고 X-User-Id, X-User-Role 헤더를 주입합니다.
     * @CurrentUserId 어노테이션이 SecurityContext에서 userId를 자동으로 추출합니다.
     *
     * 직접 호출 시:
     * curl -X POST \
     *      -H "X-User-Id: 1" \
     *      -H "X-User-Role: USER" \
     *      -H "Content-Type: application/json" \
     *      -d '{"name":"사과","price":3000}' \
     *      http://localhost:8087/api/items
     *
     * @param request 아이템 생성 요청
     * @param userId  현재 사용자 ID (X-User-Id 헤더에서 자동 추출)
     * @return 생성된 아이템 (201 Created)
     */
    @Operation(summary = "아이템 생성", description = "새 아이템을 생성합니다. 인증이 필요합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ItemResponse>> createItem(
            @Valid @RequestBody ItemRequest request,
            @CurrentUserId Long userId) {

        ItemResponse item = itemService.createItem(request, userId);
        return ResponseEntity.status(201).body(ApiResponse.created(item));
    }

    /**
     * 아이템 수정 — 인증 필요, 본인 소유만 가능.
     *
     * [X-Header 인증 방식]
     * api-gateway가 JWT를 검증하고 X-User-Id, X-User-Role 헤더를 주입합니다.
     * @CurrentUserId 어노테이션이 SecurityContext에서 userId를 자동으로 추출합니다.
     *
     * 직접 호출 시:
     * curl -X PUT \
     *      -H "X-User-Id: 1" \
     *      -H "X-User-Role: USER" \
     *      -H "Content-Type: application/json" \
     *      -d '{"name":"배","price":5000}' \
     *      http://localhost:8087/api/items/1
     *
     * @param id      아이템 ID
     * @param request 수정 요청
     * @param userId  현재 사용자 ID (X-User-Id 헤더에서 자동 추출)
     * @return 수정된 아이템 (200 OK)
     */
    @Operation(summary = "아이템 수정", description = "아이템을 수정합니다. 본인이 생성한 아이템만 수정 가능합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest request,
            @CurrentUserId Long userId) {

        ItemResponse item = itemService.updateItem(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    /**
     * 아이템 삭제 — 인증 필요, 본인 소유만 가능.
     *
     * [X-Header 인증 방식]
     * api-gateway가 JWT를 검증하고 X-User-Id, X-User-Role 헤더를 주입합니다.
     * @CurrentUserId 어노테이션이 SecurityContext에서 userId를 자동으로 추출합니다.
     *
     * 직접 호출 시:
     * curl -X DELETE \
     *      -H "X-User-Id: 1" \
     *      -H "X-User-Role: USER" \
     *      http://localhost:8087/api/items/1
     *
     * @param id     아이템 ID
     * @param userId 현재 사용자 ID (X-User-Id 헤더에서 자동 추출)
     * @return 200 OK
     */
    @Operation(summary = "아이템 삭제", description = "아이템을 삭제합니다. 본인이 생성한 아이템만 삭제 가능합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @PathVariable Long id,
            @CurrentUserId Long userId) {

        itemService.deleteItem(id, userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
