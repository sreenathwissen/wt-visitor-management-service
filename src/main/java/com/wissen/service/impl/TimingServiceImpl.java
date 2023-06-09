package com.wissen.service.impl;

import com.google.common.collect.Lists;
import com.wissen.entity.Timing;
import com.wissen.entity.Visitor;
import com.wissen.exceptions.VisitorManagementException;
import com.wissen.repository.TimingRepository;
import com.wissen.service.TimingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation class for timings.
 *
 * @author Vishal Tomar
 */
@Service
public class TimingServiceImpl implements TimingService {

    @Autowired
    private TimingRepository timingRepository;

    @Override
    public List<Timing> updateOutTimeWhereNull() {
        return this.timingRepository.updateOutTimeWhereNull(null);
    }

    /**
     * Method to save timings.
     *
     * @param timings
     * @return savedResult
     */
    @Override
    public List<Timing> saveOrUpdateTimings(List<Timing> timings) {
        return this.timingRepository.saveAll(timings);
    }

    @Override
    public List<Timing> logOut(Long timingsId, String visitorId) {
        LocalDateTime now = LocalDateTime.now();

        // update all records for asked visitorId
        if(timingsId == 0L) {
            Visitor visitor = Visitor.builder().visitorId(visitorId).build();
            List<Timing> timings = this.timingRepository.findByVisitorAndOutTime(visitor, null);
            if(CollectionUtils.isEmpty(timings)) {
                throw new VisitorManagementException("Visitor Details doesn't have IN records, please refresh and try again.");
            }
            timings.stream().forEach(timing -> timing.setOutTime(now));
            return this.timingRepository.saveAll(timings);
        } else {
            // update detail for specific record.
            Optional<Timing> optional = this.timingRepository.findById(timingsId);
            if(Objects.isNull(optional))
                throw new VisitorManagementException("Visitor Details doesn't have IN records, please refresh and try again.");
            Timing timing = optional.get();
            timing.setOutTime(now);
            return Lists.newArrayList(timingRepository.save(timing));
        }
    }

    /**
     * Method to find by outTime.
     *
     * @param visitor
     * @param outTime
     *
     * @return result
     */
    @Override
    public List<Timing> findByVisitorAndOutTime(Visitor visitor, LocalDateTime outTime) {
        return this.timingRepository.findByVisitorAndOutTime(visitor, outTime);
    }
}
