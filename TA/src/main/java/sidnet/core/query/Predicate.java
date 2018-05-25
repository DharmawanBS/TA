/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.core.query;

/**
 *
 * @author Oliver
 */
public class Predicate {
    private PredefinedOperator predefinedOperator;
    private LogicalOp logicalOperator;
    private double numericalOperator;
    
    public Predicate(PredefinedOperator predefinedOperator, LogicalOp logicalOperator, double numericalOperator)
    {
        this.predefinedOperator = predefinedOperator;
        this.logicalOperator    = logicalOperator;
        this.numericalOperator  = numericalOperator;
    }
    
    public Eval evaluatePredicate(PredefinedOperator predefinedOperator, double numericalValue)
    {
        if (this.predefinedOperator != predefinedOperator)
            return Eval.SUCCESS;
        
        if (logicalOperator == LogicalOp.GREATER && numericalValue > numericalOperator)
            return Eval.SUCCESS;
        
        if (logicalOperator == LogicalOp.GREATER_OR_EQUAL && numericalValue >= numericalOperator)
            return Eval.SUCCESS;

        if (logicalOperator == LogicalOp.EQUAL && numericalValue == numericalOperator)
            return Eval.SUCCESS;

        if (logicalOperator == LogicalOp.LESS_OR_EQUAL && numericalValue <= numericalOperator)
            return Eval.SUCCESS;

        if (logicalOperator == LogicalOp.LESS && numericalValue < numericalOperator)
            return Eval.SUCCESS;
        
        if (logicalOperator == LogicalOp.NOT_EQUAL && numericalValue != numericalOperator)
            return Eval.SUCCESS;

        return Eval.FAIL;    
    }
    
    public PredefinedOperator getPredefinedOperator()
    {
        return predefinedOperator;
    }
    
    public LogicalOp getLogicaloperator()
    {
        return logicalOperator;
    }
    
    public double getNumericalOperator()
    {
        return numericalOperator;
    }
}
