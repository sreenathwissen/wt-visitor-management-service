package com.wissen.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wissen.constants.Constants;
import com.wissen.dto.FilterRequest;
import com.wissen.dto.VisitorDto;
import com.wissen.entity.Visitor;
import com.wissen.exceptions.VisitorManagementException;
import com.wissen.model.response.VisitorManagementResponse;
import com.wissen.service.VisitorReport;
import com.wissen.service.VisitorService;
import com.wissen.util.ResponseUtil;
import com.wissen.validation.VisitorValidation;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller Class for visitor.
 *
 * @author Vishal Tomar
 */
@RestController
@Slf4j
@RequestMapping("/api/visitor")
public class VisitorController {

	@Autowired
	private VisitorService visitorService;

	@Autowired
	private VisitorValidation visitorValidation;

	@Autowired
	VisitorReport visitorReport;

	/**
	 * Saving visitor details.
	 *
	 * @param visitorDto
	 * @return
	 */
	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(value = "API to save visitor details", nickname = "Save Visitor")
	public VisitorManagementResponse saveVisitorDetails(@RequestBody @Valid VisitorDto visitorDto) {
		try {
			log.info("Saving visitors details");
			// validation
			List<Visitor> matchedVisitors = this.visitorService.getVisitorsByPhoneNoOrEmail(visitorDto.getPhoneNumber(),
					visitorDto.getEmail());
			this.visitorValidation.validateVisitor(matchedVisitors, visitorDto);

			Visitor savedData = this.visitorService.saveVisitorDetails(visitorDto);
			return ResponseUtil.getResponse(savedData);
		} catch (VisitorManagementException e) {
			log.error(Constants.EXCEPTION_LOG_PREFIX, e.getMessage());
			return ResponseUtil.getResponse(e.getMessage(), "Visitor Details", e);
		} catch (Exception e) {
			log.error(Constants.EXCEPTION_LOG_PREFIX, e.getMessage());
			return ResponseUtil.getResponse("Not able to save the visitor details.", "Visitor Details", e);
		}
	}

	@PostMapping("/fetch")
	@ApiOperation(value = "API to get visitors details", nickname = "getVisitorsDetails")
	public VisitorManagementResponse getVisitorsDetails(
			@RequestBody(required = false) List<FilterRequest> requestFilters) {
		try {
			// TODO try using generic way- add join if in/out time is passed as part of
			// filter
			log.info("Getting visitors details");

			// validating
			this.visitorValidation.validateFetchRequest(requestFilters);

			List<Visitor> visitors = this.visitorService.getVisitorByFilter(requestFilters);
			return ResponseUtil.getResponse(visitors);
		} catch (Exception e) {
			log.error(Constants.EXCEPTION_LOG_PREFIX, e.getMessage());
			return ResponseUtil.getResponse(e.getMessage(), "Visitors details", e);
		}
	}

	@GetMapping("/generateReport")
	@ApiOperation(value = "API to generate visitor details report", nickname = "getVisitorsReport")
	public VisitorManagementResponse getVisitorsReport(
			@RequestParam(defaultValue = "dd-MM-yyyy HH:mm:ss") String startDateTime,
			@RequestParam(defaultValue = "dd-MM-yyyy HH:mm:ss") String endDateTime) {
		try {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			LocalDateTime startTime = LocalDateTime.parse(startDateTime, formatter);
			LocalDateTime endTime = LocalDateTime.parse(endDateTime, formatter);

			return ResponseUtil.getResponse(visitorReport.generateVisitorReport(startTime, endTime));
		} catch (Exception e) {
			log.error(Constants.EXCEPTION_LOG_PREFIX, e.getMessage());
			return ResponseUtil.getResponse(e.getMessage(), "Visitors Report", e);
		}
	}

}