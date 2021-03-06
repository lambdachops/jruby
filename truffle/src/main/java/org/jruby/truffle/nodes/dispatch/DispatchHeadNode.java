/*
 * Copyright (c) 2013, 2015 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.nodes.dispatch;

import org.jruby.truffle.nodes.RubyNode;
import org.jruby.truffle.nodes.cast.ProcOrNullNode;
import org.jruby.truffle.runtime.LexicalScope;
import org.jruby.truffle.runtime.RubyContext;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

public class DispatchHeadNode extends Node {

    protected final RubyContext context;
    protected final boolean ignoreVisibility;
    protected final boolean indirect;
    protected final MissingBehavior missingBehavior;
    protected final LexicalScope lexicalScope;
    protected final DispatchAction dispatchAction;
    protected final boolean isSplatted;
    protected final RubyNode[] argumentNodes;
    protected final ProcOrNullNode block;

    @Child private DispatchNode first;

    public DispatchHeadNode(
            RubyContext context,
            boolean ignoreVisibility,
            boolean indirect,
            MissingBehavior missingBehavior,
            LexicalScope lexicalScope,
            DispatchAction dispatchAction,
            RubyNode[] argumentNodes,
            ProcOrNullNode block,
            boolean isSplatted) {
        this.context = context;
        this.ignoreVisibility = ignoreVisibility;
        this.indirect = indirect;
        this.missingBehavior = missingBehavior;
        this.lexicalScope = lexicalScope;
        this.dispatchAction = dispatchAction;
        this.argumentNodes = argumentNodes;
        this.isSplatted = isSplatted;
        this.block = block;
        first = new UnresolvedDispatchNode(context, ignoreVisibility, indirect, missingBehavior, dispatchAction, argumentNodes, block, isSplatted);
    }

    public Object dispatch(
            VirtualFrame frame,
            Object receiverObject,
            Object methodName,
            Object blockObject,
            Object argumentsObjects) {
        return first.executeDispatch(
                frame,
                receiverObject,
                methodName,
                blockObject,
                argumentsObjects);
    }

    public void reset(String reason) {
        first.replace(new UnresolvedDispatchNode(
                context, ignoreVisibility, indirect, missingBehavior, dispatchAction, argumentNodes, block, isSplatted), reason);
    }

    public DispatchNode getFirstDispatchNode() {
        return first;
    }

    public LexicalScope getLexicalScope() {
        return lexicalScope;
    }

    public DispatchAction getDispatchAction() {
        return dispatchAction;
    }

    public void forceUncached() {
        adoptChildren();
        first.replace(new UncachedDispatchNode(context, ignoreVisibility, dispatchAction, argumentNodes, block, isSplatted));
    }
    
    public RubyNode[] getArgumentNodes() {
        return argumentNodes;
    }

    public ProcOrNullNode getBlock() {
        return block;
    }
}
