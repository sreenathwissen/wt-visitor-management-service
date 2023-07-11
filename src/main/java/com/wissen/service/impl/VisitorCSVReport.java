package com.wissen.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wissen.constants.Constants;
import com.wissen.entity.Timing;
import com.wissen.entity.Visitor;
import com.wissen.exceptions.VisitorManagementException;
import com.wissen.repository.VisitorRepository;
import com.wissen.service.VisitorReport;

import lombok.extern.slf4j.Slf4j;

/**
 * This class exposes the API to generate the visitor reports. It fetches the
 * visitor details from DB and write into CSV file.
 * 
 * @author Ankit Garg
 *
 */
@Service
@Slf4j
public class VisitorCSVReport implements VisitorReport {

	@Autowired
	private VisitorRepository visitorRepository;

	@Value("${visitor.report.filename}")
	private String visitorReportFilename;

	@Value("${visitor.report.file.ext}")
	private String visitorReportFileExt;

	@Value("${visitor.report.file.time.format}")
	private String visitorReportFileTimeFormat;

	@Value("${visitor.report.header}")
	private String visitorReportHeader;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private DateTimeFormatter visitorFileFormatter;

	/**
	 * It fetches all the records from visitor table and generate the report with
	 * visitors and timing details
	 */
	@Override
	public File generateVisitorReport(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		log.info("Generating visitors report");
		visitorFileFormatter = DateTimeFormatter.ofPattern(visitorReportFileTimeFormat);
		String dateFormat = LocalDateTime.now().format(visitorFileFormatter);
		List<Visitor> visitorList = visitorRepository.findByTimingsInTimeBetween(startDateTime, endDateTime);
		String fileName = visitorReportFilename + "_" + dateFormat + visitorReportFileExt;
		try (FileWriter writer = new FileWriter(fileName)) {
			// Write the headers to the CSV file
			writer.append(visitorReportHeader);
			writer.append(Constants.NEW_LINE);

			for (Visitor visitor : visitorList) {
				String record = StringUtils.join(visitor.getVisitorId(), Constants.COMMA, visitor.getEmail(),
						Constants.COMMA, visitor.getFullName(), Constants.COMMA, visitor.getIdProofNumber(),
						Constants.COMMA, visitor.getLocation(), Constants.COMMA, visitor.getPhoneNumber(),
						Constants.COMMA, visitor.getProofType(), Constants.COMMA, visitor.getTempCardNo(),
						Constants.COMMA, Constants.COMMA, Constants.NEW_LINE);
				writer.append(record);

				if (visitor.getTimings() != null) {
					for (Timing timing : visitor.getTimings()) {
						LocalDateTime inTime = timing.getInTime();
						LocalDateTime outTime = timing.getOutTime();
						String formattedInTime = "";
						String formattedOutTime = "";
						if (inTime != null)
							formattedInTime = inTime.format(formatter);
						if (outTime != null)
							formattedOutTime = outTime.format(formatter);
						String timingRecord = StringUtils.join(Constants.COMMA, Constants.COMMA, Constants.COMMA,
								Constants.COMMA, Constants.COMMA, Constants.COMMA, Constants.COMMA, Constants.COMMA,
								formattedInTime, Constants.COMMA, formattedOutTime, Constants.NEW_LINE);
						writer.append(timingRecord);
					}
				}
			}
			// Flush and close the writer
			writer.flush();
			writer.close();
			File reportFile = new File(fileName);
			log.info("Visitors report generated at location : {}", reportFile.getAbsolutePath());
			return reportFile;
		} catch (IOException e) {
			throw new VisitorManagementException(Constants.UNABLE_TO_WRITE_TO_FILE);
		}
	}

}
