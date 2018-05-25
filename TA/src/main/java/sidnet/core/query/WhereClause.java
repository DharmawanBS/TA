/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.core.query;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Oliver
 */
public class WhereClause {
    private LinkedList<Predicate> predicateList;
    private LinkedList<LogicalOp> logicalOperatorList;
    
    public WhereClause()
    {
        predicateList = new LinkedList();
        logicalOperatorList = new LinkedList();
    }
    
    public void addPredicate(Predicate predicate)
    {
        if (predicateList.size() > 0)
        {
            System.out.println("[WHERECLAUSE][addPredicate(_)] - a LogicalOp must be specified for the second predicate in relation with the previous predicate. Use the (_,_) constructor instead");
            System.exit(-1);
        }
        predicateList.add(predicate);
    }
    
    public void addPredicate(LogicalOp logicalOperator, Predicate predicate)
    {
        if (predicateList.size() > 0)
            logicalOperatorList.add(logicalOperator);
        predicateList.add(predicate);
            
    }
    
    // for greater number of propositions an evaluation tree has to be built
    public Eval evaluatePredicate(PredefinedOperator predefinedOperator, double numericalValue)
    {
        Eval[] evalVector = new Eval[predicateList.size()];
        for (int i = 0; i < 0; i++)
            evalVector[i] = Eval.SUCCESS;
        
        int index = 0;
        
        if (predicateList.size() == 0)
            return Eval.SUCCESS;
        
        Iterator it = predicateList.iterator();
        while(it.hasNext()) 
        {
            Predicate pred = (Predicate)it.next();
     
            evalVector[index] = pred.evaluatePredicate(predefinedOperator, numericalValue);
                  
            index++;
        }
        
        if (evalVector.length == 1)
            return evalVector[0];
        
        Eval finalEval = Eval.SUCCESS;
        
        LogicalOp logicalOp = logicalOperatorList.get(0);
        
        if (logicalOp == LogicalOp.OR)
            if (evalVector[0] == Eval.FAIL && evalVector[1] == Eval.FAIL)
                finalEval = Eval.FAIL;
        if (logicalOp == LogicalOp.AND)
            if (evalVector[0] == Eval.FAIL || evalVector[1] == Eval.FAIL)
                finalEval = Eval.FAIL;
    
        if (evalVector.length == 2)
            return finalEval;
        
        if (logicalOp == LogicalOp.OR)
            if (finalEval == Eval.FAIL && evalVector[2] == Eval.FAIL)
                finalEval = Eval.FAIL;
        if (logicalOp == LogicalOp.AND)
            if (finalEval == Eval.FAIL || evalVector[1] == Eval.FAIL)
                finalEval = Eval.FAIL;
        
        return finalEval;  
    }
    
}
