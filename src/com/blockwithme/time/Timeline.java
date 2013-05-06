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
 * Timelines are all about the ordering events. One important property is that
 * they usually use *relative* time, instead of absolute time, which makes
 * thinking about time easier in most cases.
 *
 * Timelines are lightweight objects; they do not require their own thread.
 * As such, it is expected that an application could have many of them. Each
 * timeline, except the "core timeline" (which is bound to the core
 * scheduler tick frequency), builds on the tick frequency of it's parent, and
 * normally keeps the same frequency, or go slower, then the parent. Therefore,
 * timelines form a hierarchy. By changing the local scaling, or the paused
 * state, of a timeline, you also affect all the children timelines.
 *
 * Clock "ticks" just represent a position in a timeline. They normally
 * represent the (approximate) number of "updates" of the listeners. Timing
 * issues might cause one "update call" to count for multiple updates. The
 * ticks are counted forward, starting from 0, at creation, or after a reset.
 *
 * The tick period/frequency is somewhat arbitrary, since it the tick period
 * must be a multiple of the core scheduler tick period, which defaults
 * (currently) to 60. A non-integer multiple will result if variation of the
 * tick period over time. While 60 ticks per seconds is too much for most
 * simulations, the ideal speed varies between use-cases, with 10, 20 and 30
 * being usual frequencies for games. To allow all those frequencies to be
 * supported, we need a speed which is a multiple of all those speeds. 15 and
 * 12 are also possible frequencies, but are less "popular". To put those
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
 * The logical time is represented as a double, to be able to meet any possible
 * needs. We also return it as a rounded long. Depending on the scaling and
 * offset, it could be used to simulate the time going backward. The real time
 * always moves forward, but the logical time can be paused, or go backward,
 * and therefore the logical time should normally be the source of all
 * time-related decisions within an application.
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
     * That means it is itself explicitly paused, or one of it's ancestor is.
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
     * coming tick(s), not for not for the past ones, as the step could vary
     * over time.
     */
    double globalTickStep();

    /**
     * If this timeline has it's duration fixed ahead of time, it will be
     * returned as a number of ticks. 0 can be used to signify no fixed
     * duration.
     */
    long fixedDurationTicks();

    /**
     * If the timeline has a fixed duration, should it just end, or reset, when
     * reaching it's end?
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

    /** Returns the fixed offset added to produce "time". */
    double timeOffset();

    /**
     * Returns the *expected* tick period, in microseconds, for the coming ticks.
     * It depends on the global tick step, and should ideally be a multiple of
     * the core tick period.
     */
    long tickPeriod();

    /**
     * Returns the *expected* number of ticks per second, based on the current
     * tick period.
     */
    double ticksPerSecond();

    /**
     * Returns the microtime at which this timeline was started.
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
     * Returns the total microtime spent in paused state, since this timeline
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
     * Returns the total microtime spent in running state, since this timeline
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
     * Returns the total microtime spent in any state, since this timeline was
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

    /** Returns the number of time the timeline was reset. */
    long resetCount();

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
     * required, to allow any kind of scaling and offset.
     */
    double time();

    /** The time, rounded. */
    long timeAsLong();

    /** Returns the *last* clock tick, if any. */
    Time lastTick();

    /**
     * Creates a new time Interval. Use Long.MIN_VALUE or Long.MAX_VALUE as
     * limit, to simulate open intervals. Start and end relate to the timeline
     * tick count.
     */
    Interval newInterval(long start, long end);

    /** Creates an Interval representing the entire timeline. */
    Interval toInterval();

    ///////////////////////////////////////////////////////////////////////////
    // The following methods causes modification to the Timeline.
    ///////////////////////////////////////////////////////////////////////////

    /** Resets the timeline to the beginning, as if it was just created. */
    void reset();

    /**
     * Pauses the timeline. No more ticks are produced.
     * Note that the pause effectively starts at the *next* core tick.
     */
    void pause();

    /**
     * Un-pauses the timeline. Note that the clock effectively resumes at the
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
     * Register a TimeListener, which is called at every *running* clock tick.
     * It will always be called from the same thread.
     *
     * The time listener must *never* do long, or blocking, operations!
     * This would delay all the other time listeners, not just of this timeline,
     * but of *all timelines* and cause fluctuation in the global tick period.
     */
    Task<TimeListener> registerListener(final TimeListener listener);
}
