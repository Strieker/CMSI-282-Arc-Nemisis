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
	
	// FOR ANY ONE CONSTRAINT LOOK AT THE DOMAINS IT MENTIONS, LOOK AT ONE OF THE
		// ARCS, AND EACH VALUE IN THE TAIL, SEE IF A VALUE IN THE HEAD SATISFIES THAT
		// CONSTRAINT
		// IF NO VALUES IN THE HEAD SATISFY THE CONSTRAINT FOR THAT TAIL VALUE, THEN
		// PRUNE IT FROM THE TAIL DOMAIN

		private static  List<List<LocalDate>> arcConsistency (List<List<LocalDate>> domains, Set<DateConstraint> constraints){
			for (DateConstraint d : constraints) {
				if (d.arity() == 1) {
					continue;
				}
				for (int i = 0; i < domains.size(); i++) {
					if (i == d.L_VAL) {
						List<LocalDate> leftVal = domains.get(d.L_VAL);
						List<LocalDate> rightVal = domains.get(((BinaryDateConstraint) d).R_VAL);
						int indexMove = 0; 
						boolean isConsistentSomewhere = false;
						for(LocalDate domainL : leftVal) {
							for(LocalDate domainR : rightVal) {
								if(isConsistent(domainL, domainR, d)) {
									isConsistentSomewhere = true;
								} else if(!isConsistent(domainL, domainR, d) && indexMove == rightVal.size() - 1 && isConsistentSomewhere) {
									rightVal.remove(domainL);
								}
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
	
	public static List<LocalDate> recursiveBackTracking(List<LocalDate> assignment, List<List<LocalDate>> domains,
			Set<DateConstraint> constraints, int indexOfUnassignedVariable, int stopSize) {
		if (indexOfUnassignedVariable == stopSize
				&& constraintCheck(assignment, constraints)/* || domains.size() == 0 */) {
			return assignment;
		}
		for (LocalDate value : domains.get(indexOfUnassignedVariable)) {
			assignment.set(indexOfUnassignedVariable, value);
			if (constraintCheck(assignment, constraints)) {
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

		List<List<LocalDate>> domains = arcConsistency(nodeConsistency(makeDomains(nMeetings, rangeStart, rangeEnd), constraints), constraints);
		
		List<LocalDate> assignment = makeDateVars(nMeetings);
    	List<LocalDate> solution = recursiveBackTracking(assignment, domains,constraints, 0, nMeetings);
//
    	if(solution == null) {
    		return null;
    	} 
		return solution;
	}

	// list of sets

}
