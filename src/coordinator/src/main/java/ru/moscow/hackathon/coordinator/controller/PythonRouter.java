package ru.moscow.hackathon.coordinator.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.EventDTO;
import ru.moscow.hackathon.coordinator.entity.AsuprEntity;
import ru.moscow.hackathon.coordinator.entity.BtiEntity;
import ru.moscow.hackathon.coordinator.entity.MoekConnectionEntity;
import ru.moscow.hackathon.coordinator.entity.OdpuEntity;
import ru.moscow.hackathon.coordinator.entity.PowerEfficiencyEntity;
import ru.moscow.hackathon.coordinator.entity.WallsMaterialsEntity;
import ru.moscow.hackathon.coordinator.repository.jpa.AsuprJpaRepository;
import ru.moscow.hackathon.coordinator.repository.jpa.BtiJpaRepository;
import ru.moscow.hackathon.coordinator.repository.jpa.EventJpaRepository;
import ru.moscow.hackathon.coordinator.repository.jpa.MoekJpaRepository;
import ru.moscow.hackathon.coordinator.repository.jpa.OdpuJpaRepository;
import ru.moscow.hackathon.coordinator.repository.jpa.PowerEfficiencyJpaRepository;
import ru.moscow.hackathon.coordinator.repository.jpa.WallsMaterialsJpaRepository;

@RestController
@RequestMapping("/python")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PythonRouter {

    AsuprJpaRepository asuprJpaRepository;
    BtiJpaRepository btiJpaRepository;
    EventJpaRepository eventJpaRepository;
    MoekJpaRepository moekJpaRepository;
    OdpuJpaRepository odpuJpaRepository;
    PowerEfficiencyJpaRepository powerEfficiencyJpaRepository;
    WallsMaterialsJpaRepository wallsMaterialsJpaRepository;

    @GetMapping("/bti")
    public Mono<Page<BtiEntity>> gatherBti(
            @RequestParam("page") Integer pageNumber,
            @RequestParam("size") Integer size
    ) {
        var page = PageRequest.of(
                pageNumber,
                size
        );
        return Mono.just(btiJpaRepository.findAllBy(page));
    }

    @GetMapping("/asupr")
    public Mono<Page<AsuprEntity>> gatherAsupr(
            @RequestParam("page") Integer pageNumber,
            @RequestParam("size") Integer size
    ) {
        var page = PageRequest.of(
                pageNumber,
                size
        );
        return Mono.just(asuprJpaRepository.findAllBy(page));
    }

    @GetMapping("/event")
    public Mono<Page<EventDTO>> gatherEvent(
            @RequestParam("page") Integer pageNumber,
            @RequestParam("size") Integer size
    ) {
        var page = PageRequest.of(
                pageNumber,
                size
        );
        return Mono.just(eventJpaRepository.findAllBy(page));
    }

    @GetMapping("/moek")
    public Mono<Page<MoekConnectionEntity>> gatherMoek(
            @RequestParam("page") Integer pageNumber,
            @RequestParam("size") Integer size
    ) {
        var page = PageRequest.of(
                pageNumber,
                size
        );
        return Mono.just(moekJpaRepository.findAllBy(page));
    }

    @GetMapping("/odpu")
    public Mono<Page<OdpuEntity>> gatherOdpu(
            @RequestParam("page") Integer pageNumber,
            @RequestParam("size") Integer size
    ) {
        var page = PageRequest.of(
                pageNumber,
                size
        );
        return Mono.just(odpuJpaRepository.findAllBy(page));
    }

    @GetMapping("/walls-materials")
    public Mono<Page<WallsMaterialsEntity>> gatherWM(
            @RequestParam("page") Integer pageNumber,
            @RequestParam("size") Integer size
    ) {
        var page = PageRequest.of(
                pageNumber,
                size
        );
        return Mono.just(wallsMaterialsJpaRepository.findAllBy(page));
    }

    @GetMapping("/power-efficiency")
    public Mono<Page<PowerEfficiencyEntity>> gatherPE(
            @RequestParam("page") Integer pageNumber,
            @RequestParam("size") Integer size
    ) {
        var page = PageRequest.of(
                pageNumber,
                size
        );
        return Mono.just(powerEfficiencyJpaRepository.findAllBy(page));
    }
}
