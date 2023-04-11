package com.wissen.controller;

import com.wissen.constants.Constants;
import com.wissen.dto.FilterRequest;
import com.wissen.entity.Visitor;
import com.wissen.model.response.VisitorManagementResponse;
import com.wissen.service.VisitorService;
import com.wissen.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    /**
     * Saving visitor details.
     *
     * @param visitor
     * @return
     */
    @PostMapping(consumes ={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "API to save visitor details", nickname = "getRefData")
    public VisitorManagementResponse saveVisitorDetails(@RequestBody @Valid Visitor visitor) {
        try {
            log.info("Saving visitors details");
            Visitor savedData = this.visitorService.saveVisitorDetails(visitor);
            return ResponseUtil.getResponse(savedData);
        }catch (Exception e) {
            log.error(Constants.EXCEPTION_LOG_PREFIX, e.getMessage());
            return ResponseUtil.getResponse(e.getMessage(), "Visitor Details", e);
        }
    }

    /**
     * Method to update out time.
     *
     * @param id
     * @return visitor
     */
    @PutMapping("/logout")
    @ApiOperation(value = "API to Update logout time", nickname = "logout")
    public VisitorManagementResponse logout(@RequestParam(required = true) String id) {
        try {
            log.info("Updating logout time for id : {}", id);
            Visitor visitor = this.visitorService.logOut(id);
            return ResponseUtil.getResponse(visitor);
        }catch (Exception e) {
            log.error(Constants.EXCEPTION_LOG_PREFIX, e.getMessage());
            return ResponseUtil.getResponse("Not able to update log out time.", "Logout time", e);
        }
    }

    /**
     * Method to fetch values from the Visitor table
     * Dynamic Query will be formed based on the request filter
     * If the list is empty then all visitor data will be fetched
     * Else will return only specific results
     * @param requestFilters
     * @return
     */
    @PostMapping("/fetch")
    @ApiOperation(value = "API to get visitors details", nickname = "getVisitorsDetails")
    public VisitorManagementResponse fetchVisitorsDetails(@RequestBody(required = false) List<FilterRequest> requestFilters) {
        try {
            log.info("Getting visitors details");
            List<Visitor> visitors = this.visitorService.fetchVisitorsDetails(requestFilters);
            return ResponseUtil.getResponse(visitors);
        }catch (Exception e) {
            log.error(Constants.EXCEPTION_LOG_PREFIX, e.getMessage());
            return ResponseUtil.getResponse(e.getMessage(), "Visitors details", e);
        }
    }

}
