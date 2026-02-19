package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.assembler.DietDayAssembler;
import io.github.patrykkukula.diet_ms.dto.DietDayDto;
import io.github.patrykkukula.diet_ms.mapper.DietDayMapper;
import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.repository.DietDayRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DietDayService {
    private final DietDayRepository dietDayRepository;
    private final DietDayAssembler dietDayAssembler;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public DietDayDto createDietDay(DietDayDto dietDayDto) {
        DietDay dietDay = dietDayAssembler.assemble(dietDayDto);

        DietDay savedDiet = dietDayRepository.save(dietDay);

        return DietDayMapper.mapDietDayToDietDayDto(savedDiet);
    }

}
