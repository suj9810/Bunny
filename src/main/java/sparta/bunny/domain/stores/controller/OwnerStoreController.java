package sparta.bunny.domain.stores.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.stores.dto.StoreRequestDto;
import sparta.bunny.domain.stores.dto.StoreResponseDto;
import sparta.bunny.domain.stores.service.OwnerStoreService;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class OwnerStoreController {

    private final OwnerStoreService ownerStoreService;

}

