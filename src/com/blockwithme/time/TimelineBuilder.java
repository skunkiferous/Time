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
 * A TimelineBuilder allows the creation of a new Timeline as "child" of an
 * existing Timeline. Timelines are complex enough, that a simple "create"
 * method cannot cover all use-cases.
 *
 * @author monster
 */
public interface TimelineBuilder {
    /**
     * Create the new timeline, using the currently specified parameters.
     * @param name specifies the name of the new Timeline. It cannot be null or empty.
     */
    Timeline create(String name);

    /** The new Timeline should be created in the paused state. */
    TimelineBuilder paused();

    /** The new Timeline should be created in the running state. */
    TimelineBuilder running();

    /**
     * Sets the micro-time at which this timeline will be started. It should be
     * in the future. By default, the time when create() is called is used.
     * Null means use the default behavior.
     */
    TimelineBuilder setStartTimePoint(Long startTimePoint);

    /** Sets the local tick step. */
    TimelineBuilder setLocalTickStep(double step);

    /**
     * Specifies if this timeline has it's duration fixed ahead of time. It
     * should be a number of ticks. 0 can be used to signify no fixed duration.
     */
    TimelineBuilder setFixedDurationTicks(long fixedDurationTicks);

    /**
     * Specifies, if the timeline has a fixed duration, if it should just end,
     * or reset itself?
     */
    TimelineBuilder setLoopWhenReachingEnd(boolean loopWhenReachingEnd);

    /** Sets the local scaling applied to runningElapsedTicks(). */
    TimelineBuilder setLocalTickScaling(double scaling);

    /** Sets the fixed offset added to produce "time()". */
    TimelineBuilder setTimeOffset(double timeOffset);

    /**
     * Adjusts the local tick step, to reach the desired number of ticks per
     * second. Note that there are limitations. Firstly, it cannot be more then
     * the core tick frequency. Secondly, it unless is a factor of the core
     * tick frequency, it will only be *on average* following the desired
     * number of ticks per second. And finally, this can also be overriden by
     * the parent, if the parent cahnges it's ow
     * @param i
     * @return
     */
    TimelineBuilder setTicksPerSecond(double ticksPerSecond);
}
