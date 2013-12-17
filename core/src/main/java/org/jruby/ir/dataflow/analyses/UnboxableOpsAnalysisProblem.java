package org.jruby.ir.dataflow.analyses;

import java.util.HashMap;
import java.util.Map;
import org.jruby.ir.IRClosure;
import org.jruby.ir.IRScope;
import org.jruby.ir.dataflow.DataFlowProblem;
import org.jruby.ir.dataflow.FlowGraphNode;
import org.jruby.ir.operands.TemporaryVariable;
import org.jruby.ir.operands.Variable;
import org.jruby.ir.representations.BasicBlock;

// This problem tries to find unboxable (currently, Float and Fixnum) operands
// by inferring types and optimistically assuming that Float and Fixnum numeric
// operations have not been / will not be modified in those classes.
//
// This is a very simplistic type analysis. Doesn't analyze Array/Splat/Range
// operands and array/splat dereference/iteration since that requires assumptions
// to be made about those classes.
//
// In this analysis, we will treat:
// * 'null' as Dataflow.TOP
// * a concrete class as known type
// * 'Object.class' as Dataflow.BOTTOM
//
// Type of a variable will change at most twice: TOP --> class --> BOTTOM

public class UnboxableOpsAnalysisProblem extends DataFlowProblem {
    public UnboxableOpsAnalysisProblem() {
        super(DataFlowProblem.DF_Direction.FORWARD);
    }

    public String getName() {
        return "Unboxable Operands Analysis";
    }

    public FlowGraphNode buildFlowGraphNode(BasicBlock bb) {
        return new UnboxableOpsAnalysisNode(this, bb);
    }

    @Override
    public String getDataFlowVarsForOutput() {
        return "";
    }

    public void unbox() {
        Map<Variable, TemporaryVariable> unboxMap = new HashMap<Variable, TemporaryVariable>();
        for (FlowGraphNode n : getInitialWorkList()) {
            ((UnboxableOpsAnalysisNode) n).unbox(unboxMap);
        }
    }
}
