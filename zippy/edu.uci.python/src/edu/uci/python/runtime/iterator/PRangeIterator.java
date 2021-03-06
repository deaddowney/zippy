/*
 * Copyright (c) 2013, Regents of the University of California
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
package edu.uci.python.runtime.iterator;

import edu.uci.python.runtime.datatype.*;
import edu.uci.python.runtime.exception.*;

public final class PRangeIterator implements PIterator, PIntegerIterator {

    private int index;
    private final int stop;
    private final int step;

    public PRangeIterator(PRange range) {
        this.index = range.getStart();
        this.stop = range.getStop();
        this.step = range.getStep();
    }

    public int getStart() {
        return index;
    }

    public int getStop() {
        return stop;
    }

    public int getStep() {
        return step;
    }

    @Override
    public Object __next__() throws StopIterationException {
        return __nextInt__();
    }

    public int __nextInt__() {
        if (index < stop) {
            int value = index;
            index += step;
            return value;
        }

        throw StopIterationException.INSTANCE;
    }

    public static final class PRangeReverseIterator implements PIterator, PIntegerIterator {

        private int index;
        private final int stop;
        private final int step;

        public PRangeReverseIterator(PRange range) {
            this.index = range.getStop() - 1;
            this.stop = range.getStart() - 1;
            this.step = range.getStep();
        }

        public PRangeReverseIterator(int index, int stop, int step) {
            this.index = index;
            this.stop = stop;
            this.step = step;
        }

        @Override
        public Object __next__() throws StopIterationException {
            return __nextInt__();
        }

        public int __nextInt__() {
            if (index > stop) {
                int value = index;
                index -= step;
                return value;
            }

            throw StopIterationException.INSTANCE;
        }
    }

}
