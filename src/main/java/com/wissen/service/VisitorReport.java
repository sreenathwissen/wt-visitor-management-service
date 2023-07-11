package com.wissen.service;

import java.io.File;
import java.time.LocalDateTime;

/**
 * Service class for report generation related operation for visitor.
 *
 * @author Ankit Garg
 */
public interface VisitorReport {

	public File generateVisitorReport(LocalDateTime startDateTime, LocalDateTime endDateTime);
}