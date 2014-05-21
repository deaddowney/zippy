/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.graal.phases.common.inlining.info;

import com.oracle.graal.api.code.Assumptions;
import com.oracle.graal.api.meta.ResolvedJavaMethod;
import com.oracle.graal.nodes.FixedWithNextNode;
import com.oracle.graal.nodes.Invoke;
import com.oracle.graal.nodes.StructuredGraph;
import com.oracle.graal.phases.common.inlining.InliningUtil;

import com.oracle.graal.phases.common.inlining.info.elem.Inlineable;
import com.oracle.graal.phases.common.inlining.info.elem.InlineableMacroNode;
import com.oracle.graal.phases.common.inlining.info.elem.InlineableGraph;

public abstract class AbstractInlineInfo implements InlineInfo {

    protected final Invoke invoke;

    public AbstractInlineInfo(Invoke invoke) {
        this.invoke = invoke;
    }

    @Override
    public StructuredGraph graph() {
        return invoke.asNode().graph();
    }

    @Override
    public Invoke invoke() {
        return invoke;
    }

    protected static void inline(Invoke invoke, ResolvedJavaMethod concrete, Inlineable inlineable, Assumptions assumptions, boolean receiverNullCheck) {
        if (inlineable instanceof InlineableGraph) {
            StructuredGraph calleeGraph = ((InlineableGraph) inlineable).getGraph();
            InliningUtil.inline(invoke, calleeGraph, receiverNullCheck);
        } else {
            assert inlineable instanceof InlineableMacroNode;

            Class<? extends FixedWithNextNode> macroNodeClass = ((InlineableMacroNode) inlineable).getMacroNodeClass();
            InliningUtil.inlineMacroNode(invoke, concrete, macroNodeClass);
        }

        InliningUtil.InlinedBytecodes.add(concrete.getCodeSize());
        assumptions.recordMethodContents(concrete);
    }
}
