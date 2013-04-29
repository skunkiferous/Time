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

import java.util.Calendar;
import java.util.Date;

import org.threeten.bp.Instant;

/**
 * A TimelineBuilder allows the creation of a new Timeline as "child" of an
 * existing Timeline. Timelines are complex enough, that a simple "create"
 * method cannot cover all use-cases. By default, the new timeline has the
 * same values and parameters as the parent timeline.
 *
 * The same TimelineBuilder can only be used once.
 *
 * @author monster
 */
public interface TimelineBuilder {
    /** Create the new timeline, using the currently specified parameters. */
    Timeline create();

    /** The new Timeline should be created in the paused state. */
    TimelineBuilder pause();

    /** The new Timeline should be created in the running state. */
    TimelineBuilder unpause();

    /**
     * Sets the ticker count to the desired value.
     *
     * This overrides all previous calls to setTickerCount() or
     * offsetTickerCount().
     */
    TimelineBuilder setTickerCount(long ticks);

    /**
     * Offsets the ticker count to the desired value.
     *
     * This overrides all previous calls to setTickerCount() or
     * offsetTickerCount().
     */
    TimelineBuilder offsetTickerCount(long ticksOffset);

    /**
     * Sets the "real-time" to the desired value.
     *
     * This overrides all previous calls to setRealTime() or
     * offsetRealTime().
     */
    TimelineBuilder setRealTime(long realTimeNanos);

    /**
     * Sets the "real-time" to the desired value.
     *
     * This overrides all previous calls to setRealTime() or
     * offsetRealTime().
     */
    TimelineBuilder setRealTime(Date realTime);

    /**
     * Sets the "real-time" to the desired value.
     *
     * This overrides all previous calls to setRealTime() or
     * offsetRealTime().
     */
    TimelineBuilder setRealTime(Calendar realTime);

    /**
     * Sets the "real-time" to the desired value.
     *
     * This overrides all previous calls to setRealTime() or
     * offsetRealTime().
     */
    TimelineBuilder setRealTime(Instant realTime);

    /**
     * Sets the "real-time" to the desired value.
     *
     * This overrides all previous calls to setRealTime() or
     * offsetRealTime().
     */
    TimelineBuilder offsetRealTime(long realTimeNanosOffset);

    /**
     * Sets the clock divider, to the desired value. 0 is not allowed.
     * A negative value causes the Timeline to go in the opposite direction as it's parent.
     *
     * This overrides all previous calls to setClockDivider() or
     * multiplyClockDivider().
     */
    TimelineBuilder setClockDivider(int clockDivider);

    /**
     * Multiplies the clock divider, by the desired value. 0 is not allowed.
     * The result will be rounded. Zero will never result from rounding.
     * A negative value causes the Timeline to go in the opposite direction as it's parent.
     *
     * This overrides all previous calls to setClockDivider() or
     * multiplyClockDivider().
     */
    TimelineBuilder multiplyClockDivider(float clockDividerFactor);
}
