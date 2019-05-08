package csp;

import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.Set;
import java.util.List;
import java.util.*;

//X = {0, 1, 2}
//D_i = {2019-1-1, 2019-1-2, 2019-1-3}
//C = {
//    // Unary Constraints:
//    0 != 2019-1-2    // "Meeting 0 cannot occur on Jan. 2nd"
//    2 < 2019-1-3     // "Meeting 2 must occur before Jan. 3rd"
//    
//    // Binary Constraints:
//    0 != 1           // "Meetings 0 and 1 must be on separate days"
//    1 == 2           // "Meetings 1 and 2 must be on the same day"
//}
//
//Possible Solution:
//[2019-1-1, 2019-1-2, 2019-1-2]
//

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some unary and binary constraints
 * on the dates of each meeting.
 */
//
//int nMeetings describing how many meetings need to be scheduled, and are indexed from 0 to n-1.
//LocalDate rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
//LocalDate rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
//Set<DateConstraint> constraints A Set of date constraints on the meeting times in the formats specified above.

public class CSP {

	private static List<LocalDate> makeDomains(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd){
		List<LocalDate> domainsInRange = new ArrayList<>();
		LocalDate currentState = rangeStart;
		while(!currentState.equals(rangeEnd)){
			domainsInRange.add(currentState);
			currentState = currentState.plusDays(1);
		}
		domainsInRange.add(rangeEnd);
		return domainsInRange; 
	}
	
	public static List<LocalDate> makeDateVars(int nMeetings){
		List<LocalDate> dateVariables = new ArrayList<>();
		for(int i = 0; i < nMeetings; i++) {
			dateVariables.add(null);
		}
		return dateVariables;
	}
	

//	A UnaryDateConstraint is parameterized 
//	by an int L_VAL for the variable being
//	constrained, a String OP denoting the 
//	comparative operator, and LocalDate R_VAL 
//	denoting the date to constrain the L_VAL to.
	
	
//	Similarly, a BinaryDateConstraint 
//	is parameterized by an int L_VAL 
//	for the variable being constrained, 
//	a String OP denoting the comparative operator, 
//	and int R_VAL denoting the second variable.

	
	// IS THIS PREPROCESSING TAYLORING DOWN DOMAIN POSSIBILITIES THAT WILL NEVER BE LOOKED AT 
	// IS THIS ACTUALLY MODIFYING THE ORIGINAL LIST 
	private static List<LocalDate> preprocess(List<LocalDate> domains, Set<DateConstraint> constraints){
		for (DateConstraint d : constraints) {
			if(d.arity() == 1) {
				boolean sat = false; 
	            LocalDate leftDate = domains.get(d.L_VAL),
	                      rightDate = ((UnaryDateConstraint) d).R_VAL;
	            switch (d.OP) {
	            case "==": if (leftDate.isEqual(rightDate))  sat = true; break;
	            case "!=": if (!leftDate.isEqual(rightDate)) sat = true; break;
	            case ">":  if (leftDate.isAfter(rightDate))  sat = true; break;
	            case "<":  if (leftDate.isBefore(rightDate)) sat = true; break;
	            case ">=": if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate))  sat = true; break;
	            case "<=": if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) sat = true; break;
	            }
	            if(!sat) {
	            	domains.remove(d.L_VAL);
	            }
			}
			
		}
		 return domains; 
	}
	
	private static boolean allStatesAreConsistent(List<LocalDate> assignments, Set<DateConstraint> constraints){ 

		for (DateConstraint d : constraints) {
	        boolean sat = false;
            LocalDate leftDate = assignments.get(d.L_VAL),
                      rightDate = (d.arity() == 1) 
                          ? ((UnaryDateConstraint) d).R_VAL 
                          : assignments.get(((BinaryDateConstraint) d).R_VAL);
            if(leftDate == null || rightDate == null) {continue;}
            switch (d.OP) {
            case "==": if (leftDate.isEqual(rightDate))  sat = true; break;
            case "!=": if (!leftDate.isEqual(rightDate)) sat = true; break;
            case ">":  if (leftDate.isAfter(rightDate))  sat = true; break;
            case "<":  if (leftDate.isBefore(rightDate)) sat = true; break;
            case ">=": if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate))  sat = true; break;
            case "<=": if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) sat = true; break;
            }
            if (!sat) {
            	return false;
            }
        }
        return true;
	}
	
	
	private static boolean arcConsistent(List<LocalDate> assignments, Set<DateConstraint> constraints){ 
		
		PriorityQueue<LocalDate[]> arcs = new PriorityQueue<>();
		
		for (DateConstraint d : constraints) {
	        boolean sat = false;
            LocalDate leftDate = assignments.get(d.L_VAL),
                      rightDate = assignments.get(((BinaryDateConstraint) d).R_VAL);
                          
                      
            if(leftDate == null || rightDate == null) {continue;}
            switch (d.OP) {
            case "==": if (leftDate.isEqual(rightDate))  sat = true; break;
            case "!=": if (!leftDate.isEqual(rightDate)) sat = true; break;
            case ">":  if (leftDate.isAfter(rightDate))  sat = true; break;
            case "<":  if (leftDate.isBefore(rightDate)) sat = true; break;
            case ">=": if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate))  sat = true; break;
            case "<=": if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) sat = true; break;
            }
            if (!sat) {
            	return false;
            }
        }
		return true;
	}
	
	
	public static List<LocalDate> recursiveBackTracking (List<LocalDate> variables, List<LocalDate> domains, Set<DateConstraint> constraints, int indexOfUnassignedVariable, int stopSize) {
        // WHY ONLY 0? 
        System.out.println(indexOfUnassignedVariable);
		if(indexOfUnassignedVariable == stopSize && allStatesAreConsistent(variables, constraints)/* || domains.size() == 0*/) {
    		System.out.println("I GOT HERE 1");
        	return variables;
        }
        for(LocalDate value : domains) {
    		variables.set(indexOfUnassignedVariable, value);
        	if(allStatesAreConsistent(variables, constraints)) {
        		System.out.println("I GOT HERE 2");
        		System.out.println(variables.toString());
        		List<LocalDate>  result = recursiveBackTracking(variables, domains, constraints, indexOfUnassignedVariable + 1, stopSize);
            	 if(result != null) {
                 	return result;
                 } 
        	} 
        	// WHY IF PUT IN A NULL IT MESSES THE WHOLE THING UP? 
            variables.set(indexOfUnassignedVariable, null);

        }
        return null; 
    }
	
	

	
    /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */
	
    public static List<LocalDate> solve (int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {
//    	List<LocalDate> domains = makeDomains(nMeetings, rangeStart, rangeEnd);

    	List<LocalDate> domains = preprocess(makeDomains(nMeetings, rangeStart, rangeEnd), constraints);
//    	for(LocalDate date : domains) {
//    		System.out.println(date);
//    	}
    	List<LocalDate> variables = makeDateVars(nMeetings);
//    	List<LocalDate> csp = variables;
    	List<LocalDate> solution = recursiveBackTracking(variables, domains,constraints, 0, nMeetings);
    	
    	if(solution == null) {
    		System.out.println("Solution was null");
    		return null;
    	} 
    	return solution;
    }
    
}
