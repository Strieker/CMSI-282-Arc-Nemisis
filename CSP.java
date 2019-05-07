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

//	public List<LocalDate> recursiveBacktracking(){
//	
//}
	public static List<LocalDate> makeDateList(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd){
		List<LocalDate> datesInRange = new ArrayList<>();
		LocalDate currentDate = rangeStart;
		while(currentDate.equals(rangeStart)){
			datesInRange.add(currentDate);
			currentDate = currentDate.plusDays(1);
		}
		return datesInRange; 
	}
	
	public static List<Integer> makeDateVars(int nMeetings){
		List<Integer> dateVariables = new ArrayList<>();
		for(int i = 0; i < nMeetings; i++) {
			dateVariables.add(i);
		}
		return dateVariables;
	}
	
	// IS THIS PREPROCESSING 
	// IS THIS ACTUALLY MODIFYING THE ORIGINAL LIST 
	public List<LocalDate> taylorDownDateVariables(List<LocalDate> dateVariables, Set<DateConstraint> constraints){
		 for (DateConstraint d : constraints) {
	            LocalDate leftDate = dateVariables.get(d.L_VAL);
	            LocalDate rightDate;
	            if (d.arity() == 1) {
	            	rightDate = ((UnaryDateConstraint) d).R_VAL;
	            } else {
	            	rightDate = dateVariables.get(((BinaryDateConstraint) d).R_VAL);
	            }
	                          
	            boolean sat = false;
	            switch (d.OP) {
	            case "==": if (leftDate.isEqual(rightDate))  sat = true; break;
	            case "!=": if (!leftDate.isEqual(rightDate)) sat = true; break;
	            case ">":  if (leftDate.isAfter(rightDate))  sat = true; break;
	            case "<":  if (leftDate.isBefore(rightDate)) sat = true; break;
	            case ">=": if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate))  sat = true; break;
	            case "<=": if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) sat = true; break;
	            }
	            if (!sat) {
	            	// okay to remove just the lval or need to also remove the right 
	                dateVariables.remove(d.L_VAL);
	            }
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
        throw new UnsupportedOperationException();
    }
    
}
