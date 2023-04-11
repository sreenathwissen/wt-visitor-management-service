package com.wissen.service;

import com.wissen.dto.FilterRequest;
import com.wissen.entity.Visitor;

import java.util.List;

/**
 * Service for visitor details related operation.
 *
 * @author Vishal Tomar
 */
public interface VisitorService {

    /**
     * Save visitor details.
     *
     * @param visitor
     * @return savedVisitor
     */
    public Visitor saveVisitorDetails(Visitor visitor);

    /**
     * Method to update out time.
     *
     * @param id
     * @return visitor
     */
    public Visitor logOut(String id);

    /**
     * Method to fetch values from the Visitor table
     * Dynamic Query will be formed based on the request filter
     * If the list is empty then all vistor will be fetched
     * Else will return only specific results
     * @param requestFilters
     * @return                  List of visitor response based on the filter request
     */
    List<Visitor> fetchVisitorsDetails(List<FilterRequest> requestFilters);
}
