/*
 * Copyright (c) 2021, 2021, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021, 2021, Red Hat Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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

package com.oracle.svm.test.jfr.utils.poolparsers;

import java.io.IOException;

import com.oracle.svm.core.jfr.JfrType;
import com.oracle.svm.test.jfr.utils.RecordingInput;

public class ThreadGroupConstantPoolParser extends AbstractRepositoryParser {

    @Override
    public void reset() {
        /* 0 is the null thread group, 1 is the virtual thread group. */
        foundIds.add(0L);
        foundIds.add(1L);
    }

    @Override
    public void parse(RecordingInput input) throws IOException {
        int numberOfThreadGroups = input.readInt();
        for (int i = 0; i < numberOfThreadGroups; i++) {
            addFoundId(input.readLong()); // ThreadGroupId.
            addExpectedId(JfrType.ThreadGroup, input.readLong()); // ParentThreadGroupId.
            input.readUTF(); // ThreadGroupName.
        }
    }
}
