package com.wissen.decorator;

import com.wissen.dto.VisitorDto;
import com.wissen.entity.Employee;
import com.wissen.entity.Timing;
import com.wissen.entity.Visitor;
import com.wissen.util.VisitorManagementUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Decorator class for visitor.
 *
 * @author Vishal Tomar
 */
@Component
public class VisitorDecorator {

    /**
     * Method to decorate visitor before saving to db.
     *
     * @param visitorDto
     */
    public Visitor decorateBeforeSaving(VisitorDto visitorDto) {

        Visitor visitor = new Visitor();
        visitor.setTimings(new ArrayList<>());

        //check the payload data is insert/update
        if (StringUtils.isEmpty(visitorDto.getVisitorId())) {
            //insert, since visitor id is empty
            visitor.setVisitorId(UuidUtil.getTimeBasedUuid().toString());
        }else{
            visitor.setVisitorId(visitorDto.getVisitorId());
        }
        visitor.setFullName(visitorDto.getFullName());
        visitor.setEmail(visitorDto.getEmail());
        visitor.setPhoneNumber(visitorDto.getPhoneNumber());
        visitor.setLocation(visitorDto.getLocation());
        visitor.setProofType(visitorDto.getProofType());
        visitor.setIdProofNumber(visitorDto.getIdProofNumber());
        visitor.setTempCardNo(visitorDto.getTempCardNo());
        // setting image to save
        visitor.setVisitorImage(VisitorManagementUtils.convertBase64ToByte(visitorDto.getVisitorImageBase64()));


        LocalDateTime now = LocalDateTime.now();
        //decorating visitor details before saving
        Employee emp = new Employee();
        emp.setWissenId(visitorDto.getEmployeeId());
        visitor.getTimings().add(Timing.builder()
                .id(null)
                .inTime(now)
                .outTime(null)
                .visitor(visitor)
                        .employee(emp)
//                .employeeId(visitorDto.getEmployeeId())
                .visitorType(visitorDto.getVisitorType())
                        .assetList(visitorDto.getAssetList())
                .build());

        return visitor;

    }

    /**
     * Decorate saved data after saving.
     *
     * @param visitor
     */
    public void decorateAfterSaving(Visitor visitor, List<Timing> timings) {
        visitor.setVisitorImageBase64(VisitorManagementUtils.convertByteToBase64(visitor.getVisitorImage()));
    }

    /**
     * Decorate image for UI response.
     *
     * @param visitors
     */
    public void decorateImageForUi(List<Visitor> visitors) {
        visitors.forEach(visitor -> {
            if(visitor.getVisitorImage()!=null){
                visitor.setVisitorImageBase64(VisitorManagementUtils.convertByteToBase64(visitor.getVisitorImage()));
                visitor.setVisitorImage(null);
            }
        });
    }

}
