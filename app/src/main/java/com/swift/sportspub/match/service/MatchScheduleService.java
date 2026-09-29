package com.swift.sportspub.match.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;
import com.swift.sportspub.match.dto.MatchScheduleResponse;
import com.swift.sportspub.match.dto.MatchSummary;
import com.swift.sportspub.match.repository.MatchRepository;
import com.swift.sportspub.team.entity.SportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchScheduleService {

    private final MatchRepository matchRepository;

    @Transactional(readOnly = true)
    public MatchScheduleResponse getSchedule(
            SportType sportType,
            LocalDate date,
            LocalDate from,
            LocalDate to,
            Long teamId
    ) {
        SportType resolvedSportType = sportType != null ? sportType : SportType.KBO;
        DateRange range = resolveDateRange(date, from, to);

        List<MatchSummary> matches = matchRepository
                .findSchedule(resolvedSportType, range.from(), range.to(), teamId)
                .stream()
                .map(MatchSummary::from)
                .toList();

        return MatchScheduleResponse.of(matches);
    }

    private DateRange resolveDateRange(LocalDate date, LocalDate from, LocalDate to) {
        boolean hasDate = date != null;
        boolean hasFrom = from != null;
        boolean hasTo = to != null;

        if (hasDate && (hasFrom || hasTo)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT,
                    "date 와 from/to 는 동시에 사용할 수 없습니다.");
        }
        if (hasFrom ^ hasTo) {
            throw new BusinessException(ErrorCode.INVALID_INPUT,
                    "from 과 to 는 함께 전송해야 합니다.");
        }
        if (hasFrom && to.isBefore(from)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT,
                    "to 는 from 보다 같거나 이후여야 합니다.");
        }

        if (hasFrom) {
            return new DateRange(from, to);
        }
        LocalDate target = hasDate ? date : LocalDate.now();
        return new DateRange(target, target);
    }

    private record DateRange(LocalDate from, LocalDate to) {
    }
}
