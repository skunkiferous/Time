/*
 * Copyright (C) 2013 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blockwithme.time;

/**
 * A time interval.
 *
 * It can refer either to an interval within real-time, or within some logical
 * application time. If it represents the real time, relativeTo() will be null.
 * Otherwise, relativeTo() will define the context for this interval.
 *
 * To make things simple, intervals are closed and "inclusive" at both start
 * and end. Since time is represented as longs, both for real-time in
 * microseconds, and also logical application time, we can just use
 * Long.MIN_VALUE or Long.AMX_VALUE as limit, to simulate open intervals.
 *
 * @author monster
 */
public interface Interval {
    /**
     * Returns the Timeline towards which this time is relative.
     * Null is used to represent real-time intervals.
     */
    Timeline relativeTo();

    /** The *inclusive* start of the time interval. */
    long start();

    /** The *inclusive* end of the time interval. */
    long end();

    /** Is this time value strictly before the interval? */
    boolean beforeInterval(long time);

    /** Is this time value strictly after the interval? */
    boolean afterInterval(long time);

    /** Is this time value strictly within the interval? */
    boolean inInterval(long time);

    /**
     * Is this time value strictly before the interval?
     *
     * @param time must not be null, and if relativeTo() is not null, must come from that timeline, or be convertible to it.
     */
    boolean beforeInterval(Time time);

    /**
     * Is this time value strictly after the interval?
     *
     * @param time must not be null, and if relativeTo() is not null, must come from that timeline, or be convertible to it.
     */
    boolean afterInterval(Time time);

    /**
     * Is this time value strictly within the interval?
     *
     * @param time must not be null, and if relativeTo() is not null, must come from that timeline, or be convertible to it.
     */
    boolean inInterval(Time time);
}
