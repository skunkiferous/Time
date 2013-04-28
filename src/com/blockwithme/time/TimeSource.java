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
 *
 * The logical time is represented as a long, and it usually moves
 * forward, but does not need to keep in sync with the real time. Also, it is
 * conceivable that not all parts of the application are at the same logical
 * time, within the same real-time moment.
 *
 * @author monster
 */
public interface TimeSource extends AutoCloseable, ClockServiceSource,
        TimeSourceCreator {

    /** Returns the name of the time source. */
    String name();

    /** Returns the nano-time at which this time source was created. */
    long startTime();

    /** Returns the total nano-time spent in paused state, since this time source was created. */
    long pausedTime();

    /** Returns the current clock ticks. */
    long ticks();

    /** Sets the current clock ticks. */
    void setTicks(long ticks);

    /**
     * Modifies the clock ticks by the given amount, and returns the new value.
     */
    long offsetTicks(long delta);

    /**
     * Returns the expected number of ticks per second (never negative).
     * It must be a real number, because a slow time source could give less
     * then one one tick per second, for example one tick per minute.
     */
    float ticksPerSecond();

    /** Returns the expected tick period, in nanoseconds (never negative). */
    long tickPeriode();

    /**
     * Pauses the clock. No more ticks are produced.
     * Note that the pause effectively starts at the *next* tick.
     */
    void pause();

    /**
     * Un-pauses the clock. Note that the clock effectively resumes at the
     * *next* tick.
     */
    void unpause();

    /**
     * Returns the number of ticks from the parent time source required, before
     * one tick of this clock passes. If this clock goes in the opposite
     * direction as the parent clock, it will be negative, but can never be 0.
     */
    int parentRatio();

    /** Sets the parent ratio. */
    void setParentRatio(int ratio);

    /** Returns the *last* clock tick, if any. */
    Time lastTick();

    /**
     * Register a TimeListener, which is called at every clock tick.
     * It will always be called from the same thread.
     *
     * The time listener must *never* do long running, or blocking, operations!
     * This would delay all the other time listeners, and cause fluctuation
     * in the global tick period.
     */
    Task<TimeListener> registerListener(final TimeListener listener);

    /**
     * Creates and returns a new derived TimeSource.
     *
     * A derived time source is useful, to create a time source with a smaller
     * speed (larger period) then the parent time source. A derived time source
     * can also go in the opposite direction as the parent, or be paused
     * individually. But it is also paused when the parent is paused.
     *
     * @param name cannot be null or empty
     */
    @Override
    TimeSource newTimeSource(String name);
}
