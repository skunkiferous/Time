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
import java.util.TimeZone;

import org.threeten.bp.Clock;
import org.threeten.bp.Duration;

import com.blockwithme.time.Scheduler.Handler;

/**
 * ClockService defines methods related to getting the current time, in
 * multiple forms. Normally, it will be the UTC time, except for
 * systemDefaultZone().
 *
 * @author monster
 */
public interface ClockService extends AutoCloseable {

    /** Returns the current time, in milliseconds. */
    long currentTimeMillis();

    /** Returns the current time, in microseconds. */
    long currentTimeMicros();

    /** Returns a new Date, using currentTimeMillis(). */
    Date date();

    /** Returns a new Calendar, using currentTimeMillis(). */
    Calendar calendar();

    /**
     * Returns a microseconds precision *UTC* Clock instance.
     *
     * Note that the time should be (relatively) correct for UTC,
     * even if the local clock is wrong.
     *
     * @see org.threeten.bp.Clock#systemUTC()
     */
    Clock clock();

    /**
     * The original default local time zone. This is useful, for the case where
     * we set the local time zone to GMT, which might be required to get
     * third-party APIs to use UTC/GMT as default.
     */
    TimeZone localTimeZone();

    /** Returns a new Calendar, using the current *local* time. */
    Calendar localCalendar();

    /**
     * Returns a microseconds precision default timezone Clock instance.
     *
     * Note that the time should be (relatively) correct for the given
     * timezone, even if the local clock is wrong.
     *
     * @see org.threeten.bp.Clock#systemDefaultZone()
     */
    Clock localClock();

    /**
     * Sleeps (approximately) for the given amount of microseconds.
     *
     * The precision should be much better then Thread.sleep(), but consumes
     * more CPU then a normal sleep. Thread.sleep(long) should still be
     * preferred for long sleeps, that do not need to be precise.
     *
     * Note that *in most implementations* of the JDK, Thread.sleep(long,int)
     * does *not* give you precise sleep, as the amount of nanosecond is just
     * rounded to the nearest millisecond!
     *
     * @throws InterruptedException
     */
    void sleepMicros(final long sleepMicros) throws InterruptedException;

    /** Returns the core timeline. */
    Timeline coreTimeline();

    /**
     * Creates a new Scheduler, for executing Runnable tasks.
     *
     * @param name cannot be null or empty
     * @param errorHandler can be null.
     */
    Scheduler newScheduler(final String name, final Handler errorHandler);

    /**
     * Creates a new time Interval. Use Long.MIN_VALUE or Long.MAX_VALUE as
     * limit, to simulate open intervals. Start and end relate to the real
     * time in microseconds.
     *
     * @see newDuration(long,long)
     */
    Interval newInterval(long startMicros, long endMicros);

    /**
     * Creates a new time Interval, starting now, of the give duration in
     * microseconds.
     *
     * @see newDuration(long)
     */
    Interval newInterval(long durationMicros);

    /**
     * Creates a new Duration. Start and end relate to the real time in
     * microseconds, and cannot be ong.MIN_VALUE or Long.MAX_VALUE, as
     * durations are just that, durations, and so cannot be "open".
     */
    Duration newDuration(long startMicros, long endMicros);

    /** Creates a new Duration, of the give duration in microseconds. */
    Duration newDuration(long durationMicros);
}
