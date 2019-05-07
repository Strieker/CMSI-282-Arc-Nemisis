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
	private static List<LocalDate> preprocess(List<LocalDate> states, Set<DateConstraint> constraints){
		 for (DateConstraint d : constraints) {
	            LocalDate leftDate = states.get(d.L_VAL);
	            LocalDate rightDate = (d.arity() == 1) ? ((UnaryDateConstraint) d).R_VAL :
	            states.get(((BinaryDateConstraint) d).R_VAL);	           
	            if (!singleStateIsConstraintConsistent(leftDate, rightDate, constraints)) {
	            	// okay to remove just the lval or need to also remove the right 
	                states.remove(d.L_VAL);
	            }
	        }
		 return states; 
	}
	
	
	private static boolean singleStateIsConstraintConsistent(LocalDate L_VAL, LocalDate R_VAL, Set<DateConstraint> constraints){
		
		boolean sat = false; 
		for (DateConstraint d : constraints) {
	            switch (d.OP) {
	            case "==": if (L_VAL.isEqual(R_VAL))  sat = true; break;
	            case "!=": if (!L_VAL.isEqual(R_VAL)) sat = true; break;
	            case ">":  if (L_VAL.isAfter(R_VAL))  sat = true; break;
	            case "<":  if (L_VAL.isBefore(R_VAL)) sat = true; break;
	            case ">=": if (L_VAL.isAfter(R_VAL) || L_VAL.isEqual(R_VAL))  sat = true; break;
	            case "<=": if (L_VAL.isBefore(R_VAL) || L_VAL.isEqual(R_VAL)) sat = true; break;
	            }
	            
	        }
		 return sat; 
	}
	
	private static boolean allStatesAreConsistent(LocalDate L_VAL, List<LocalDate> states, Set<DateConstraint> constraints){
		 for (DateConstraint d : constraints) {
			 	LocalDate leftDate = (L_VAL == null) ? states.get(d.L_VAL) : L_VAL;
	            LocalDate rightDate = (d.arity() == 1) ? ((UnaryDateConstraint) d).R_VAL :
	            	states.get(((BinaryDateConstraint) d).R_VAL);	           
	            if (!singleStateIsConstraintConsistent(leftDate, rightDate, constraints)) {
	            	// okay to remove just the lval or need to also remove the right 
	                return false;
	            }
	        }
		 return true; 
	}
	
	
	
	public static List<LocalDate> recursiveBackTracking (List<LocalDate> csp, List<LocalDate> variables, List<LocalDate> domains, Set<DateConstraint> constraints, int indexOfUnassignedVariable) {
        // WHY ONLY 0? 
        System.out.println(indexOfUnassignedVariable);
        // when replace indexOfUnassignedVariable == variables.size()
		if((!variables.isEmpty() && allStatesAreConsistent(null, variables, constraints)) || domains.size() == 0) {
        	return variables;
        }
        // THIS IS THE ISSUE AREA 
        for(LocalDate domain : domains) {
//        	IS THAT WHAT YOU MEANT 
        	if(allStatesAreConsistent(domain, variables, constraints)) {
        		variables.add(indexOfUnassignedVariable, domain);
            	csp = recursiveBackTracking(csp, variables, domains, constraints, indexOfUnassignedVariable++);
            	// AM I DOING THIS PART RIGHT? 
            	 if(!csp.isEmpty() && !allStatesAreConsistent(null, csp, constraints)) {
                 	variables.remove(domain);
                 } else {
                	 return csp; 
                 }
        	}
        }
        // IS THAT GOOD ENOUGH AS RETURN FAILURE 
        return csp; 
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
    	List<LocalDate> domains = preprocess(makeDomains(nMeetings, rangeStart, rangeEnd), constraints);
    	for(LocalDate date : domains) {
    		System.out.println(date);
    	}
    	List<LocalDate> variables = new ArrayList<>(nMeetings);
    	List<LocalDate> csp = variables;
    	List<LocalDate> solution = recursiveBackTracking(csp, variables, domains,constraints, 0);
    	for(LocalDate date : solution) {
    		System.out.println(date);
    	}
    	if(solution.isEmpty()) {
    		return null;
    	}
    	return solution;
    }
    
}
