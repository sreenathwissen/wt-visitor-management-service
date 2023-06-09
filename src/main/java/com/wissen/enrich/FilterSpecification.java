package com.wissen.enrich;

import com.google.common.collect.Lists;
import com.wissen.constants.Constants;
import com.wissen.constants.enums.DataType;
import com.wissen.dto.FilterRequest;
import com.wissen.entity.Employee;
import com.wissen.entity.Timing;
import com.wissen.entity.Visitor;
import com.wissen.exceptions.VisitorManagementException;
import com.wissen.util.VisitorManagementUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to enrich the input request
 * Will form dynamic queries based on the filter request
 * @author User - Sreenath Sampangi
 * @created 30/03/2023 - 22:46
 * @project wt-visitor-management-service
 */
@Component
public class FilterSpecification<T> {

    final String PERCENTAGE = "%";

    /**
     * Based on the operation in the filter request the queries are formed
     * By default combing multiple filter request will be and clause
     * @param predicates
     * @param criteriaBuilder
     * @param root
     * @param input
     */
    private void updatePredicate(List<Predicate> predicates, CriteriaBuilder criteriaBuilder, Root<T> root,
                                 FilterRequest input, Join<Visitor, Timing> join, boolean isJoin) {
        String fieldName = input.getFieldName();
        List<Object> values = DataType.getValue(input.getValues(), input.getDataType());
        Object value = values.get(0);
        switch (input.getOperator()){
            case EQUALS:
                if(isJoin)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(join.get(fieldName), value)));
                else
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get(fieldName), value)));
                return;
            case NOT_EQ:
                if(isJoin)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.notEqual(join.get(fieldName), value)));
                else
                    predicates.add(criteriaBuilder.and(criteriaBuilder.notEqual(root.get(fieldName), value)));
                return;
            case GREATER_THAN:
                if (isJoin)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.gt(join.get(fieldName), (Number) value)));
                else
                    predicates.add(criteriaBuilder.and(criteriaBuilder.gt(root.get(fieldName), (Number) value)));
                return;
            case GREATER_THAN_EQUALS:
                if (isJoin)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.ge(join.get(fieldName), (Number) value)));
                else
                    predicates.add(criteriaBuilder.and(criteriaBuilder.ge(root.get(fieldName), (Number) value)));
                return;
            case LESS_THAN:
                if(isJoin)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.lt(join.get(fieldName), (Number) value)));
                else
                    predicates.add(criteriaBuilder.and(criteriaBuilder.lt(root.get(fieldName), (Number) value)));
                return;
            case LESS_THAN_EQUALS:
                if(isJoin)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.le(join.get(fieldName), (Number) value)));
                else
                    predicates.add(criteriaBuilder.and(criteriaBuilder.le(root.get(fieldName), (Number) value)));
                return;
            case LIKE:
                if(isJoin)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(join.get(fieldName), PERCENTAGE.concat((String) value).concat(PERCENTAGE))));
                else
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get(fieldName), PERCENTAGE.concat((String) value).concat(PERCENTAGE))));
                return;
            case IN:
                Expression<String> parentExpression = null;
                if(isJoin) {
//                    parentExpression = join.get(input.getFieldName());
                    if(input.getFieldName().equalsIgnoreCase(Constants.WISSEN_ID)){
                        parentExpression = join.get("employee");
                        Employee emp= new Employee();
                        emp.setWissenId(input.getValues().get(0));
                        Predicate inPredicate = parentExpression.in(Lists.newArrayList(emp));
                        predicates.add(inPredicate);
                        return;

                    }else{
                        parentExpression = join.get(input.getFieldName());
                    }
                }
                else
                    parentExpression =root.get(fieldName);

                Predicate inPredicate = parentExpression.in(input.getValues());
                predicates.add(inPredicate);
                return;
            case BETWEEN:
                 handleBetweenOperator(predicates, values, input, criteriaBuilder, root, fieldName, join, isJoin);
                return;
            default:
                throw new VisitorManagementException("Operation not supported yet");
        }
    }

    /**
     * Method to handle betweenOperator for different datatype.
     *
     * @param predicates
     * @param values
     * @param inputRequest
     * @param criteriaBuilder
     * @param root
     * @param fieldName
     */
    private void handleBetweenOperator(List<Predicate> predicates, List<Object> values, FilterRequest inputRequest,
                                       CriteriaBuilder criteriaBuilder, Root<T> root, String fieldName,
                                       Join<Visitor, Timing> join, boolean isJoin) {

        switch (inputRequest.getDataType()) {
            case DATE :
                if(isJoin)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.between(join.get(fieldName),
                            getDateTime(values.get(0)), getDateTime(values.get(1)))));
                else
                    predicates.add(criteriaBuilder.and(criteriaBuilder.between(root.get(fieldName),
                        getDateTime(values.get(0)), getDateTime(values.get(1)))));
                break;
            case INTEGER:
                if(isJoin)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.between(join.get(fieldName), (Integer) values.get(0),
                            (Integer) values.get(1))));
                else
                    predicates.add(criteriaBuilder.and(criteriaBuilder.between(root.get(fieldName), (Integer) values.get(0),
                            (Integer) values.get(1))));
                break;
            case FLOAT:
                if(isJoin)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.between(join.get(fieldName), (Float) values.get(0),
                            (Float) values.get(1))));
                else
                    predicates.add(criteriaBuilder.and(criteriaBuilder.between(root.get(fieldName), (Float) values.get(0),
                            (Float) values.get(1))));
                break;
            case DOUBLE:
                if(isJoin)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.between(join.get(fieldName), (Double) values.get(0),
                            (Double) values.get(1))));
                else
                    predicates.add(criteriaBuilder.and(criteriaBuilder.between(root.get(fieldName), (Double) values.get(0),
                            (Double) values.get(1))));
                break;
        }

    }

    public Specification<T> getSpecificationFromFilters(List<FilterRequest> filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


            for (FilterRequest input : filter) {
                if(VisitorManagementUtils.getAllowedTimingFilterField().contains(input.getFieldName())) {
                    //field has timing table attributes
                    Join<Visitor, Timing> join = root.join("timings");
                    updatePredicate(predicates, criteriaBuilder, root, input, join, true);
                } else {
                    updatePredicate(predicates, criteriaBuilder, root, input, null, false);
                }

            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }


    private LocalDateTime getDateTime(Object value) {
        return LocalDateTime.parse(value.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
