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
 * The Timeline represents a source for the application logical time.
 *
 * From the Oxford Dictionary:
 *  "a graphical representation of a period of time, on which important events
 *   are marked."
 *
 * The important part in there is "events". Timelines are all about ordering
 * events.
 *
 * The logical time is represented as a long, and it usually moves
 * forward, but does not need to keep in sync with the real time. Also, it is
 * conceivable that not all parts of the application are at the same logical
 * time, within the same real-time moment. In particular, the real time always
 * moves forward, but the logical time can be paused, and therefore should
 * normally be the source of all time-related decisions within an application.
 *
 * Timelines are lightweight objects; they do not require their own thread.
 * As such, it is expected that an application could have many of them. Each
 * timeline , except the "core timelines" (which are bound to the core
 * scheduler frequency), builds on the frequency of it's parent, and can either
 * keep the same frequency, or go slower. Therefore, timelines for a
 * hierarchy. By changing the clock divider, or the paused state, of a time
 * source, you also affect all the children timelines.
 *
 * Clock "ticks" just represent a position in a timeline. They do not have an
 * intrinsic meaning. That is because the origin value, 0, is totally arbitrary,
 * and does not need to be related to anything. So we can leave it to 0 at the
 * creation of the timeline, and it can be updated to any desired value.
 * Secondly, the tick period/frequency is also arbitrary, but somewhat less
 * arbitrary, since it the tick period must be a multiple of the core scheduler
 * tick period, which defaults (currently) to 60. While 60 ticks per seconds is
 * too much for most simulations, the ideal speed varies between use-cases,
 * with 10, 20 and 30 being usual frequencies. To allow all those frequencies
 * to be supported, we need a speed which is a multiple of all those speeds. 15
 * and 12 are also possible frequencies, but are less "popular". To put those
 * numbers in perspective, "traditional" cinema uses 24 frames per second (FPS),
 * but this is rarely used in computer software. It is also beneficial when the
 * display frame rate is a multiple of the simulation frame rate. Since many
 * modern display are LCD today, with a typical, as of this writing, refresh
 * rate of 60 Hz, we can use 10, 12, 15, 20 or 30 and match the refresh rate.
 * Many AAA games run at 30 cycles per second, but this might be too much for
 * a "lower budget" game.
 *
 * The "tick count" could for example measure the time since the start of a
 * level, or the start of the whole game. But the most likely usage of a
 * derived timeline, is to allow the implementation of cinematics. That is
 * to represent the progression along the timeline of some some action, like
 * swinging a sword. In that case, the tick count will probably be reset to 0
 * at the start of the scene, and the displayed frames matched to that tick
 * count.
 *
 * @author monster
 */
public interface Timeline extends AutoCloseable, ClockServiceSource,
        TimelineCreator {

    /** Returns the name of the timeline. */
    String name();

    /** Returns true, if the timeline is currently paused. */
    boolean paused();

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

    /** Returns the nano-time at which this timeline was created. */
    long startTime();

    /** Returns the total nano-time spent in paused state, since this timeline was created. */
    long pausedTime();

    /** Returns the real-time offset, in nanoseconds (usually 0). */
    long realTimeOffset();

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
     * It must be a real number, because a slow timeline could give less
     * then one one tick per second, for example one tick per minute.
     */
    float ticksPerSecond();

    /** Returns the expected tick period, in nanoseconds (never negative). */
    long tickPeriode();

    /**
     * Returns the number of ticks from the parent timeline required, before
     * one tick of this clock passes. If this clock goes in the opposite
     * direction as the parent clock, it will be negative, but can never be 0.
     */
    int clockDivider();

    /** Sets the parent ratio. */
    void setClockDivider(int divider);

    /** Returns the *last* clock tick, if any. */
    Time lastTick();

    /** Tries to convert the Time instance to the own time scale. Returns null on failure. */
    Time convert(Time time);

    /**
     * Register a TimeListener, which is called at every clock tick.
     * It will always be called from the same thread.
     *
     * The time listener must *never* do long running, or blocking, operations!
     * This would delay all the other time listeners, and cause fluctuation
     * in the global tick period.
     */
    Task<TimeListener> registerListener(final TimeListener listener);
}
