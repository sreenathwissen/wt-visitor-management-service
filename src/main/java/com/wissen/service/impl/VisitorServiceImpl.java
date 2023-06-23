package com.wissen.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wissen.decorator.VisitorDecorator;
import com.wissen.dto.FilterRequest;
import com.wissen.dto.VisitorDto;
import com.wissen.enrich.FilterResult;
import com.wissen.enrich.FilterSpecification;
import com.wissen.entity.Timing;
import com.wissen.entity.Visitor;
import com.wissen.exceptions.VisitorManagementException;
import com.wissen.repository.VisitorRepository;
import com.wissen.service.TimingService;
import com.wissen.service.VisitorService;

/**
 * Implementation class for visitor service.
 *
 * @author Vishal Tomar
 */
@Service
public class VisitorServiceImpl implements VisitorService {

	@Autowired
	private VisitorRepository visitorRepository;

	@Autowired
	private VisitorDecorator visitorDecorator;

	@Autowired
	private TimingService timingService;

	@Value("${email.subject}")
	String emailSubject;

	@Value("${email.body}")
	String emailBody;

	/**
	 * Filter Specification is used to enrich the input request This class will form
	 * dynamic queries No dao layer is been called in this class, just a transformer
	 */
	@Autowired
	FilterSpecification<Visitor> filterSpecification;

	/**
	 * Save visitor details.
	 *
	 * @param visitorDto
	 * @return savedVisitor
	 */
	@Override
	@Transactional
	public Visitor saveVisitorDetails(VisitorDto visitorDto) {

		// decorating before saving
		Visitor visitor = visitorDecorator.decorateBeforeSaving(visitorDto);

		// if user already checked in
		List<Timing> timings = this.timingService.findByVisitorAndOutTime(visitor, null);
		if (!CollectionUtils.isEmpty(timings))
			throw new VisitorManagementException("Visitor already checked in.");

		// save/update the details
		Visitor savedVisitor = this.visitorRepository.save(visitor);

		// decorating after saving
		visitorDecorator.decorateAfterSaving(savedVisitor, null);

		return savedVisitor;
	}

	/**
	 * Method to fetch values from the Visitor table Dynamic Query will be formed
	 * based on the request filter If the list is empty then all visitor will be
	 * fetched Else will return only specific results *
	 *
	 * @param requestFilters
	 * @return List of visitor response based on the filter request
	 */
	@Override
	public List<Visitor> fetchVisitorsDetails(List<FilterRequest> requestFilters) {
		List<Visitor> visitors = new ArrayList<>();
		if (CollectionUtils.isEmpty(requestFilters)) {
			// TODO if no filter are passed, then fetch only 30 days visitor details
			visitors.addAll(visitorRepository.findAll());
		} else {
			Specification<Visitor> specificationRequest = filterSpecification
					.getSpecificationFromFilters(requestFilters);
			visitors.addAll(visitorRepository.findAll(specificationRequest));
		}

		// Decorating images for UI.
		this.visitorDecorator.decorateImageForUi(visitors);

		return visitors;
	}

	/**
	 * Method will save or update the details based on the details provided
	 *
	 * @param outDetails
	 * @return List of saved or updated details
	 */
	@Override
	public List<Visitor> saveOrUpdateVisitors(List<Visitor> outDetails) {
		return visitorRepository.saveAll(outDetails);
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public List<Visitor> getVisitorsByPhoneNoOrEmail(String phNo, String email) {
		return this.visitorRepository.findByPhoneNumberOrEmail(phNo, email);
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public List<Visitor> getVisitorByTypeNameOrTiming(List<FilterRequest> requestFilters) {

		List<Visitor> visitors = new ArrayList<>();
		if (CollectionUtils.isEmpty(requestFilters)) {
			// TODO fetch only 30days records pass in time in the request
			visitors.addAll(visitorRepository.findAll());
		} else {
			Specification<Visitor> specificationRequest = filterSpecification
					.getSpecificationByTypeNameOrTiming(requestFilters);
			visitors.addAll(Sets.newHashSet(visitorRepository.findAll(specificationRequest)));
			visitors = FilterResult.filterExtraByFilterRequest(requestFilters, visitors);
		}

		if (CollectionUtils.isNotEmpty(visitors)) {
			// Decorating images for UI.
			this.visitorDecorator.decorateImageForUi(visitors);
		}

		return Lists.newArrayList(visitors);
	}

}
