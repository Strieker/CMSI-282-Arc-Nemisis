package csp;

import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.Set;
import java.util.List;
import java.util.*;

/**
 * CSP: Calendar Satisfaction Problem Solver Provides a solution for scheduling
 * some n meetings in a given period of time and according to some unary and
 * binary constraints on the dates of each meeting.
 */
//
//int nMeetings describing how many meetings need to be scheduled, and are indexed from 0 to n-1.
//LocalDate rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
//LocalDate rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
//Set<DateConstraint> constraints A Set of date constraints on the meeting times in the formats specified above.

public class CSP {
	
	public static List<LocalDate> makeDateVars(int nMeetings){
		List<LocalDate> dateVariables = new ArrayList<>();
		for(int i = 0; i < nMeetings; i++) {
			dateVariables.add(null);
		}
		return dateVariables;
	}
	
	private static List<LocalDate> makeDeepCopyOfVariableDomain(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd){
		List<LocalDate> currentDomainSet = new ArrayList<>();
		LocalDate currentState = rangeStart;
		while (!currentState.equals(rangeEnd)) {
			currentDomainSet.add(currentState);
			currentState = currentState.plusDays(1);
		}
		currentDomainSet.add(rangeEnd);
		return currentDomainSet;
	}
	
	private static List<List<LocalDate>> makeDomains(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd) {
		List<List<LocalDate>> domains = new ArrayList<>();
		for (int i = 0; i < nMeetings; i++) {
			domains.add(makeDeepCopyOfVariableDomain(nMeetings, rangeStart, rangeEnd));
		}
		return domains;

	}

	// 2 DATES AND AN OPERATOR PASS : INDIVIDUAL CONSTRAINT CHECK
	
	private static boolean isConsistent(LocalDate leftDate, LocalDate rightDate, DateConstraint d) {
		boolean sat = false;
		switch (d.OP) {
		case "==":
			if (leftDate.isEqual(rightDate))
				sat = true;
			break;
		case "!=":
			if (!leftDate.isEqual(rightDate))
				sat = true;
			break;
		case ">":
			if (leftDate.isAfter(rightDate))
				sat = true;
			break;
		case "<":
			if (leftDate.isBefore(rightDate))
				sat = true;
			break;
		case ">=":
			if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate))
				sat = true;
			break;
		case "<=":
			if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate))
				sat = true;
			break;
		}
		return sat;

	}
	
	private static List<List<LocalDate>> nodeConsistency(List<List<LocalDate>> domains, Set<DateConstraint> constraints) {
		for (DateConstraint d : constraints) {
			if (d.arity() == 2) {
				continue;
			}
			for (int i = 0; i < domains.size(); i++) {
				if (i == d.L_VAL) {
					for(int j = 0; j < domains.get(i).size(); j++) {
						LocalDate leftDate = domains.get(i).get(j);
						LocalDate rightDate = ((UnaryDateConstraint) d).R_VAL;
						if(!isConsistent(leftDate, rightDate, d)) {
							domains.get(i).remove(leftDate);
							j--;
						}
					}
					
				}
			}
		}
		return domains;
	}
	
	private static boolean constraintCheck(List<LocalDate> assignments, Set<DateConstraint> constraints) {
		for (DateConstraint d : constraints) {
			LocalDate leftDate = assignments.get(d.L_VAL);
			LocalDate rightDate = (d.arity() == 1) ?
					((UnaryDateConstraint) d).R_VAL :
				assignments.get(((BinaryDateConstraint) d).R_VAL);
			if(leftDate == null || rightDate == null) {continue;}
			if(!isConsistent(leftDate, rightDate, d)) {
							return false;
						}
					}
				
		return true;
	}

	

	// FOR ANY ONE CONSTRAINT LOOK AT THE DOMAINS IT MENTIONS, LOOK AT ONE OF THE
	// ARCS, AND EACH VALUE IN THE TAIL, SEE IF A VALUE IN THE HEAD SATISFIES THAT
	// CONSTRAINT
	// IF NO VALUES IN THE HEAD SATISFY THE CONSTRAINT FOR THAT TAIL VALUE, THEN
	// PRUNE IT FROM THE TAIL DOMAIN

//	private static List<List<LocalDate>> arcConstraint(List<List<LocalDate>> domains, Set<DateConstraint> constraints) {
//
//		for (DateConstraint d : constraints) {
//
//			if (d.arity() == 1) {
//				continue;
//			}
//			LocalDate leftDate = null;
//			LocalDate rightDate = null;
//			LocalDate toRemove = null;
//
//			for (int i = 0; i < domains.size(); i++) {
//				leftDate = domains.get(i).get(d.L_VAL);
//				for (int j = 0; j < domains.size(); j++) {
//					rightDate = domains.get(j).get(((BinaryDateConstraint) d).R_VAL);
//					if (!constraintCheck(domains.get(i), constraints, true, toRemove, leftDate, rightDate)) {
//						domains.get(i).remove(leftDate);
//					}
//				}
//			}
//
//		}
//		return domains;
//	}
//
	
	public static List<LocalDate> recursiveBackTracking(List<LocalDate> assignment, List<List<LocalDate>> domains,
			Set<DateConstraint> constraints, int indexOfUnassignedVariable, int stopSize) {
		// WHY ONLY 0?
		System.out.println(indexOfUnassignedVariable);
		if (indexOfUnassignedVariable == stopSize
				&& constraintCheck(assignment, constraints)/* || domains.size() == 0 */) {
			System.out.println("I GOT HERE 1");
			for(LocalDate assig : assignment) {
				System.out.println(assig);
			}
			return assignment;
		}
//        for(List<LocalDate> domain : domains) {
		for (LocalDate value : domains.get(indexOfUnassignedVariable)) {
			assignment.set(indexOfUnassignedVariable, value);
			if (constraintCheck(assignment, constraints)) {
				System.out.println("I GOT HERE 2");
				System.out.println(assignment.toString());
				List<LocalDate> result = recursiveBackTracking(assignment, domains, constraints,
						indexOfUnassignedVariable + 1, stopSize);
				if (result != null) {
					return result;
				}
			}
			assignment.set(indexOfUnassignedVariable, null);
		}
		return null;
	}

	/**
	 * Public interface for the CSP solver in which the number of meetings, range of
	 * allowable dates for each meeting, and constraints on meeting times are
	 * specified.
	 * 
	 * @param nMeetings   The number of meetings that must be scheduled, indexed
	 *                    from 0 to n-1
	 * @param rangeStart  The start date (inclusive) of the domains of each of the n
	 *                    meeting-variables
	 * @param rangeEnd    The end date (inclusive) of the domains of each of the n
	 *                    meeting-variables
	 * @param constraints Date constraints on the meeting times (unary and binary
	 *                    for this assignment)
	 * @return A list of dates that satisfies each of the constraints for each of
	 *         the n meetings, indexed by the variable they satisfy, or null if no
	 *         solution exists.
	 */

	public static List<LocalDate> solve(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd,
			Set<DateConstraint> constraints) {
//    	List<LocalDate> domains = makeDomains(nMeetings, rangeStart, rangeEnd);

		List<List<LocalDate>> domains = nodeConsistency(makeDomains(nMeetings, rangeStart, rangeEnd), constraints);
		for (List<LocalDate> date : domains) {
			System.out.println("fuck");
			System.out.println(date.toString());
		}
		List<LocalDate> assignment = makeDateVars(nMeetings);
    	List<LocalDate> solution = recursiveBackTracking(assignment, domains,constraints, 0, nMeetings);
//
    	if(solution == null) {
    		System.out.println("Solution was null");
    		return null;
    	} 
		return solution;
	}

	// list of sets

}
