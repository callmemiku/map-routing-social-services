package ru.moscow.hackathon.coordinator.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.FENotificationDTO;
import ru.moscow.hackathon.coordinator.service.FrontendService;

@CrossOrigin(maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("/update")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FrontendAwareController {

    FrontendService service;

    @GetMapping("/gather")
    public Mono<Page<FENotificationDTO>> gather(
            @RequestParam("page") Integer pageNumber,
            @RequestParam("size") Integer size
    ) {
        //todo: сортировку на фронт прикрутить
        var page = PageRequest.of(
                pageNumber,
                size
        );
        return service.response(page);
    }
}
