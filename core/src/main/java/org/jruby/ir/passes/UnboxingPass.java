package org.jruby.ir.passes;

import java.util.Arrays;
import java.util.List;
import org.jruby.ir.IRClosure;
import org.jruby.ir.IRScope;
import org.jruby.ir.dataflow.DataFlowConstants;
import org.jruby.ir.dataflow.analyses.UnboxableOpsAnalysisProblem;

public class UnboxingPass extends CompilerPass {
    public static List<Class<? extends CompilerPass>> DEPENDENCIES = Arrays.<Class<? extends CompilerPass>>asList(CFGBuilder.class, LiveVariableAnalysis.class);

    public String getLabel() {
        return "Unboxing Pass";
    }

    @Override
    public List<Class<? extends CompilerPass>> getDependencies() {
        return DEPENDENCIES;
    }

    @Override
    public Object execute(IRScope scope, Object... data) {
        UnboxableOpsAnalysisProblem problem = new UnboxableOpsAnalysisProblem();
        problem.setup(scope);
        problem.compute_MOP_Solution();
        problem.unbox();

        for (IRClosure cl: scope.getClosures()) {
            run(cl, true);
        }

        // SSS FIXME: This should be done differently
        problem.getScope().setDataFlowSolution(DataFlowConstants.LVP_NAME, null);

        return true;
    }

    public void invalidate(IRScope scope) {
        // FIXME: Can we reset this?
    }
}
