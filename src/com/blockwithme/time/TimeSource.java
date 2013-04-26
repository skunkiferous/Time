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
 * The TimeSource represents a source for the application logical time.
 * The logical time is represented as a long, and it is expected to move
 * forward, but does not need to keep in sync with the real time. Also, in is
 * conceivable that not all parts of the application are at the same logical
 * time, within the same real-time moment.
 *
 * @author monster
 */
public interface TimeSource extends AutoCloseable {

    /** Returns the ClockService that created this TimeSource. */
    ClockService clockService();

    /** Returns the time at which this time source was created. */
    long startTime();

    /** Returns the clock ticks, since the clock start. */
    long ticks();

    /** Sets the clock ticks, since the clock start. */
    void setTicks(long ticks);

    /**
     * Modifies the logical time by the give number of units,
     * and returns the new value.
     */
    long offsetTicks(long delta);

    /** Pauses the clock. No more ticks are produced. */
    void pause();

    /** Un-pause the clock. */
    void unpause();

    /**
     * Returns the number of ticks from the parent time source required, before
     * one tick of this clock passes. If this clock goes in the opposite
     * direction as the parent clock, it will be negative, but can never be 0.
     */
    int parentRatio();

    /** Sets the parent ratio. */
    void setParentRatio(int ratio);

    /** Returns the *last* tick, if any. */
    Time lastTick();

    /** Register a TimeListener, which is called at every clock tick. */
    Task<TimeListener> registerListener(final TimeListener listener);

    /** Creates and returns a new derived TimeSource. */
    TimeSource createTimeSource();
}
