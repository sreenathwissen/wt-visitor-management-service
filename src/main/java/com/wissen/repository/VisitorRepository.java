package com.wissen.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wissen.entity.Visitor;

/**
 * Repository class for visitor entity.
 *
 * @author Vishal Tomar
 */
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, String>, JpaSpecificationExecutor<Visitor> {

	/**
	 * Method to fetch visitors by phoneNumber and email.
	 * 
	 * @param phoneNumber
	 * @param email
	 * @return
	 */
	List<Visitor> findByPhoneNumberOrEmail(String phoneNumber, String email);

	@Query("SELECT COUNT(v) > 0 FROM Visitor v WHERE v.email = :identifier OR v.phoneNumber = :identifier")
	boolean existsByEmailIdOrPhoneNumber(@Param("identifier") String identifier);

	/**
	 * Method to fetch the visitor by email
	 * 
	 * @param identifier
	 * @return
	 */
	@Query("SELECT v FROM Visitor v WHERE v.email = :identifier OR v.phoneNumber = :identifier")
	Visitor findByEmailOrPhoneNumber(@Param("identifier") String identifier);

	/**
	 * Find visitor based on visitor Id
	 * 
	 * @param id
	 * @return Optional with visitor details
	 */
	Optional<Visitor> findByVisitorId(String id);

	/**
	 * 
	 * Method to fetch all the visitors based on the dateTime range
	 * 
	 * @param startDateTime : Start DateTime
	 * @param endDateTime   : End DateTime
	 * @return : List of Visitors
	 */
	@Query("SELECT DISTINCT v FROM Visitor v JOIN FETCH v.timings t WHERE t.inTime BETWEEN :startDateTime AND :endDateTime")
	List<Visitor> findByTimingsInTimeBetween(@Param("startDateTime") LocalDateTime startDateTime,
			@Param("endDateTime") LocalDateTime endDateTime);
}
