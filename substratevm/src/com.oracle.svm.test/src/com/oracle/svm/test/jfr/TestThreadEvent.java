/*
 * Copyright (c) 2021, 2022, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021, 2022, Red Hat Inc. All rights reserved.
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

package com.oracle.svm.test.jfr;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.oracle.svm.test.jfr.events.ThreadEvent;

import jdk.jfr.consumer.RecordedEvent;

/**
 * Test if event ({@link ThreadEvent}) with {@link Thread} payload is working.
 */
public class TestThreadEvent extends JfrRecordingTest {
    @Override
    public String[] getTestedEvents() {
        return new String[]{ThreadEvent.class.getName()};
    }

    @Override
    protected void validateEvents(List<RecordedEvent> events) throws Throwable {
        assertEquals(1, events.size());
    }

    @Test
    public void test() throws Exception {
        ThreadEvent event = new ThreadEvent();
        event.thread = Thread.currentThread();
        event.commit();
    }
}
