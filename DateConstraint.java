package csp;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

/**
 * DateConstraint superclass: all date constraints will have
 * an L_VAL variable and some operation that compares it to
 * some other variable or date value.
 */
public abstract class DateConstraint {

    public final int L_VAL;
    public final String OP;
    
    private final Set<String> LEGAL_OPS = new HashSet<>(
        Arrays.asList("==", "!=", "<", "<=", ">", ">=")
    );
    
    DateConstraint (int lVal, String operator) {
        if (!LEGAL_OPS.contains(operator)) {
            throw new IllegalArgumentException("Invalid constraint operator");
        }
        if (lVal < 0) {
            throw new IllegalArgumentException("Invalid variable index");
        }
        
        L_VAL = lVal;
        OP = operator;
    }
    
    /**
     * The arity of a constraint determines the number of variables
     * found within
     * @return 1 for UnaryDateConstraints, 2 for Binary
     */
    
//    Note: Since both UnaryDateConstraints and
//    BinaryDateConstraints are subclasses of 
//    DateConstraint, but have different types for 
//    their R_VAL, you will need to be comfortable casting 
//    to one of the two subclasses where appropriate. 
//    See the arity() method in DateConstraint for a handy utility in this pursuit.
//    
    public int arity () {
        return (this instanceof UnaryDateConstraint) ? 1 : 2;
    }
    
    @Override
    public String toString () {
        return L_VAL + " " + OP;
    }
    
}
