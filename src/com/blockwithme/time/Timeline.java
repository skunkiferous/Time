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
 * scheduler tick frequency), builds on the tick frequency of it's parent, and
 * normally keep the same frequency, or go slower, then the parent. Therefore,
 * timelines form a hierarchy. By changing the local scaling, or the paused
 * state, of a timeline, you also affect all the children timelines.
 *
 * Clock "ticks" just represent a position in a timeline. They normally
 * represent the (approximate) number of "updates" of the listeners. Timing
 * issues might cause one "update call" to count for multiple updates. Zero is
 * normally chosen as the start tick count, but could be set to something else.
 *
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
 * TODO: We need to map between timelines: time-points, intervals, and durations.
 *
 * @author monster
 */
public interface Timeline extends ClockServiceSource, AutoCloseable {

    /** Returns the name of the timeline. */
    String name();

    /**
     * Returns true, if the timeline is currently "locally" paused.
     * That means it is itself explicitly paused, independent of it's parent.
     */
    boolean pausedLocally();

    /**
     * Returns true, if the timeline is currently "globally" paused.
     * That means it is itself explicitly paused, or it's parent.
     */
    boolean pausedGlobally();

    /**
     * Returns the local tick step. It should always be positive. It is
     * multiplied by the parent global tick step, to obtain the child global
     * tick step.
     */
    double localTickStep();

    /**
     * Returns the global tick step, that controls the tick period.
     * In other words, it controls how much time passes between two ticks.
     *
     * The step represents the number of "core ticks" required, before a new
     * tick will be produced in this timeline. Since core ticks are discrete,
     * the globalTickStep() will only be accurate, if it is an integral value.
     * Otherwise, we might end-up with an alternating number of core ticks per
     * "own tick".
     *
     * It is important to realize that it only represents the step for the
     * coming tick(s), not for not for the past ones, as the step could vary over
     * time.
     *
     * The step never affects the startTickValue().
     */
    double globalTickStep();

    /**
     * If this timeline has it's duration fixed ahead of time, it will be
     * returned as a number of ticks. 0 can be used to signify no fixed
     * duration.
     */
    long fixedDurationTicks();

    /**
     * If the timeline has a fixed duration, should it just end, or reset?
     */
    boolean loopWhenReachingEnd();

    /** Returns the local scaling applied to runningElapsedTicks(). */
    double localTickScaling();

    /**
     * Returns the global scaling, applied to runningElapsedTicks().
     * It results from multiplying the local scaling, by the parent global
     * scaling.
     */
    double globalTickScaling();

    /** Returns the fixed offset added to time. */
    double timeOffset();

    /**
     * Returns the *expected* tick period, in nanoseconds, for the coming ticks.
     * It depends on the global tick step, and should be a multiple of the core
     * tick period.
     */
    long tickPeriod();

    /**
     * Returns the *expected* number of ticks per second, based on the current
     * tick period.
     */
    double ticksPerSecond();

    /**
     * Returns the nano-time at which this timeline was started.
     * It would be conceivable, that this time is still in the future.
     * It can change, after a reset() or "loop".
     */
    long startTimePoint();

    /**
     * Returns the time in seconds at which this timeline was started.
     * It would be conceivable, that this time is still in the future.
     * It can change, after a reset() or "loop".
     */
    double startTimePointSec();

    /**
     * Returns the total nano-time spent in paused state, since this timeline
     * was created.
     * It will be reset to 0, after a reset() or "loop".
     */
    long pausedElapsedTime();

    /**
     * Returns the total time in seconds spent in paused state, since this
     * timeline was created.
     * It will be reset to 0, after a reset() or "loop".
     */
    double pausedElapsedTimeSec();

    /**
     * Returns the total nano-time spent in running state, since this timeline
     * was created. This could also be called the relative, or elapsed, time.
     * It will be reset to 0, after a reset() or "loop".
     */
    long runningElapsedTime();

    /**
     * Returns the total time in seconds spent in running state, since this
     * timeline was created. This could also be called the relative, or
     * elapsed, time. It will be reset to 0, after a reset() or "loop".
     */
    double runningElapsedTimeSec();

    /**
     * Returns the total nano-time spent in any state, since this timeline was
     * created.
     * It will be reset to 0, after a reset() or "loop".
     */
    long totalElapsedTime();

    /**
     * Returns the total time in seconds spent in any state, since this
     * timeline was created.
     * It will be reset to 0, after a reset() or "loop".
     */
    double totalElapsedTimeSec();

    /** Returns the total ticks spent in paused state, since this timeline was created. */
    long pausedElapsedTicks();

    /**
     * Returns the total ticks spent in running state, since this timeline was
     * created.
     * It will be reset to 0, after a reset() or "loop".
     */
    long runningElapsedTicks();

    /**
     * Returns the total ticks spent in any state, since this timeline was
     * created.
     * It will be reset to 0, after a reset() or "loop".
     */
    long totalElapsedTicks();

    /**
     * If fixedDurationTicks() is not 0, then we can compute how far we got
     * along this timeline, and return it as a fraction, within [0,1].
     * Returns -1 if fixedDurationTicks() is 0.
     */
    double progress();

    /**
     * Returns the "time" of this timeline. This is the value that most parts
     * of the application are interested in. It's meaning is arbitrary. It is
     * obtained by: (timeOffset() + globalTickScaling() * runningElapsedTicks()).
     * Being a double, it is not as precise as a long would be, but this is
     * required, to allow any kind of scaling.
     */
    double time();

    /** Returns the *last* clock tick, if any. */
    Time lastTick();

    ///////////////////////////////////////////////////////////////////////////
    // The following methods causes modification to the Timeline.
    ///////////////////////////////////////////////////////////////////////////

    /** Resets the timeline to the beginning, as if it was just created. */
    void reset();

    /**
     * Pauses the clock. No more ticks are produced.
     * Note that the pause effectively starts at the *next* core tick.
     */
    void pause();

    /**
     * Un-pauses the clock. Note that the clock effectively resumes at the
     * *next* core tick.
     */
    void unpause();

    /**
     * Creates a new TimelineBuilder that allows the creation of a new child
     * Timeline.
     * @param cloneState if true, all parameters will be copied from self.
     * @param scheduler the scheduler that holds the timeline.
     */
    TimelineBuilder newChildTimeline(boolean cloneState, Scheduler scheduler);

    /**
     * Creates a new TimelineBuilder that allows the creation of a new sibling
     * Timeline.
     * @param cloneState if true, all parameters will be copied from self.
     * @param scheduler the scheduler that holds the timeline.
     */
    TimelineBuilder newSiblingTimeline(boolean cloneState, Scheduler scheduler);

    /**
     * Register a TimeListener, which is called at every clock tick.
     * It will always be called from the same thread.
     *
     * The time listener must *never* do long, or blocking, operations!
     * This would delay all the other time listeners, and cause fluctuation
     * in the global tick period.
     */
    Task<TimeListener> registerListener(final TimeListener listener);

    /**
     * Register a Ticker, which is called at every core clock tick.
     * It will always be called from the same thread.
     *
     * The Ticker must *never* do long, or blocking, operations!
     * This would delay all the other time listeners, and cause fluctuation
     * in the global tick period.
     */
    Task<Ticker> registerListener(final Ticker listener);
}
