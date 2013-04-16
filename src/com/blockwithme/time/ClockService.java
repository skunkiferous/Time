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

/**
 * ClockService defines methods related to getting the current time, in
 * multiple forms. Normally, it will be the UTC time, except for
 * systemDefaultZone().
 *
 * @author monster
 */
public interface ClockService {

    /** Returns the current *UTC* time, in milliseconds. */
    long currentTimeMillis();

    /** Returns the current *UTC* time, in nanoseconds. */
    long currentTimeNanos();

    /** Returns a new Date, using the current *UTC* time. */
    Date date();

    /** Returns a new Calendar, using the current *UTC* time. */
    Calendar calendar();

    /**
     * Returns a nano-seconds precision *UTC* Clock instance.
     *
     * Note that the time should be (relatively) correct for UTC,
     * even if the local clock is wrong.
     *
     * @see org.threeten.bp.Clock#systemUTC()
     */
    Clock clock();

    /** The original default local time zone. */
    TimeZone localTimeZone();

    /** Returns a new Calendar, using the current *local* time. */
    Calendar localCalendar();

    /**
     * Returns a nano-seconds precision default timezone Clock instance.
     *
     * Note that the time should be (relatively) correct for the given
     * timezone, even if the local clock is wrong.
     *
     * @see org.threeten.bp.Clock#systemDefaultZone()
     */
    Clock localClock();

    /** Creates a new Scheduler<T>, using the given executor. */
    <T> Scheduler<T> createNewScheduler(final Scheduler.Executor<T> executor);

    /** Returns a default Scheduler, for executing Runnables. */
    Scheduler<Runnable> getDefaultRunnableScheduler();
}
