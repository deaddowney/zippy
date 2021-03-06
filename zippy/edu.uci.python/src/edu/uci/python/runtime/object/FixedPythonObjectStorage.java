/*
 * Copyright (c) 2014, Regents of the University of California
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.uci.python.runtime.object;

import java.util.*;

import com.oracle.truffle.api.*;

import edu.uci.python.runtime.standardtype.*;

public class FixedPythonObjectStorage extends PythonObject {

    public static final int PRIMITIVE_INT_STORAGE_LOCATIONS_COUNT = 5;
    protected int primitiveInt0;
    protected int primitiveInt1;
    protected int primitiveInt2;
    protected int primitiveInt3;
    protected int primitiveInt4;

    public static final int PRIMITIVE_DOUBLE_STORAGE_LOCATIONS_COUNT = 5;
    protected double primitiveDouble0;
    protected double primitiveDouble1;
    protected double primitiveDouble2;
    protected double primitiveDouble3;
    protected double primitiveDouble4;

    public static final int FIELD_OBJECT_STORAGE_LOCATIONS_COUNT = 5;
    protected Object fieldObject0;
    protected Object fieldObject1;
    protected Object fieldObject2;
    protected Object fieldObject3;
    protected Object fieldObject4;

    public FixedPythonObjectStorage(PythonClass pythonClass) {
        super(pythonClass);
    }

    public static PythonObject create(PythonClass clazz) {
        return new FixedPythonObjectStorage(clazz);
    }

    @Override
    public void syncObjectLayoutWithClass() {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        assert verifyLayout();

        /**
         * This is a zombie Python object carried by a FixedPythonObjectStorage. For some reason
         * this zombie object is still alive. It is most likely stored in a data structure in the
         * first constructor calls. An subsequent access to this zombie will reach here.
         * <p>
         * Note that we cannot simply sync with pythonClass.getInstanceObjectLayout(). Since the
         * layout has switched to a FlexibleObjectStorageLayout. A layout sync will cause
         * unpredictable memory accesses. Therefore, we need to renew and assign a valid object
         * layout for the zombie.
         * <p>
         * Hopefully this does not happen too often!
         *
         * @author zwei
         */
        if (pythonClass.getInstanceObjectLayout() instanceof FlexibleObjectLayout) {
            usePrivateLayout = true;
            updateLayout(getObjectLayout().copy());
            return;
        }

        if (objectLayout != pythonClass.getInstanceObjectLayout()) {
            updateLayout(pythonClass.getInstanceObjectLayout());
        }

        assert verifyLayout();
    }

    @Override
    public void updateLayout(ObjectLayout newLayout) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        assert verifyLayout();

        // Get the current values of instance variables
        final Map<String, Object> instanceVariableMap = getAttributes();

        // Use new Layout
        objectLayout = newLayout;

        // Synchronize instance object layout with the class
        if (!usePrivateLayout) {
            pythonClass.updateInstanceObjectLayout(newLayout);
        }

        // Make all primitives as unset
        setPrimitiveSetMap(0);

        // Create a new array for objects
        allocateSpillArray();

        // Restore values
        setAttributes(instanceVariableMap);

        assert verifyLayout();
    }

}
