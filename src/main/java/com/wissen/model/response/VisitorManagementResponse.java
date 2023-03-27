package com.wissen.model.response;

import com.wissen.constants.enums.ResponseStatus;
import com.wissen.model.Error;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class for API response.
 *
 * @author Vishal Tomar
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorManagementResponse {

    ResponseStatus responseStatus;
    Object responseData;
    Error error;
}
