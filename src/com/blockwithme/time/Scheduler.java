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

import java.util.Date;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZonedDateTime;

/**
 * The scheduler allows running "tasks" in the future, and optionally specify
 * that those tasks should be repeated at regular intervals.
 *
 * The meaning of the methods is the same as the equivalent methods of
 * java.util.Timer. At the time of this writing, java.util.Timer is used
 * internally.
 *
 * The points in time are based on ClockService.currentTimeMillis(), rather then
 * System.currentTimeMillis(), to deal with inaccurate local clocks.
 *
 * We also accept microseconds values for convenience, in the methods suffixed
 * with "MUS", but *currently* do not offer sub-millisecond precision.
 *
 * The scheduler instances are lightweight, which means they do not get their
 * own thread, and so can be cheaply created and destroyed. OTOH, it means that
 * *tasks should never block or take a long time* to perform, as this will block
 * all other scheduled tasks.
 *
 * @author monster
 */
public interface Scheduler extends AutoCloseable, ClockServiceSource {

    /** Exception handler. */
    interface Handler {
        /** Handles exceptions. */
        void onError(final Object task, final Throwable error);
    }

    /** Returns the name of the scheduler. */
    String name();

    ///////////////////////////////////////////////////////////////////////
    // The rest of the methods are similar to what is in java.util.Timer //
    ///////////////////////////////////////////////////////////////////////

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    Task<Runnable> scheduleOnce(Runnable task, final Date time);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    Task<Runnable> scheduleOnce(Runnable task, final Instant time);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    Task<Runnable> scheduleOnce(Runnable task, final ZonedDateTime dateTime);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    Task<Runnable> scheduleOnce(Runnable task, final LocalDateTime dateTime);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    Task<Runnable> scheduleOnce(Runnable task, final LocalTime time);

    /** @see java.util.Timer.schedule(TimerTask,long) */
    Task<Runnable> scheduleOnce(Runnable task, final long delayMS);

    /** @see java.util.Timer.schedule(TimerTask,long) */
    Task<Runnable> scheduleOnceMUS(Runnable task, final long delayMUS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriod(Runnable task, final Date firstTime,
            final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriod(Runnable task,
            final Instant firstTime, final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriod(Runnable task,
            final ZonedDateTime firstTime, final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriod(Runnable task,
            final LocalDateTime firstTime, final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriod(Runnable task,
            final LocalTime firstTime, final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriodMUS(Runnable task,
            final Date firstTime, final long periodMUS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriodMUS(Runnable task,
            final Instant firstTime, final long periodMUS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriodMUS(Runnable task,
            final ZonedDateTime firstTime, final long periodMUS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriodMUS(Runnable task,
            final LocalDateTime firstTime, final long periodMUS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriodMUS(Runnable task,
            final LocalTime firstTime, final long periodMUS);

    /** @see java.util.Timer.schedule(TimerTask,long,long) */
    Task<Runnable> scheduleAtFixedPeriod(Runnable task, final long delayMS,
            final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,long,long) */
    Task<Runnable> scheduleAtFixedPeriodMUS(Runnable task, final long delayMUS,
            final long periodMUS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRate(Runnable task, final Date firstTime,
            final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRate(Runnable task, final Instant firstTime,
            final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRate(Runnable task,
            final ZonedDateTime firstTime, final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRate(Runnable task,
            final LocalDateTime firstTime, final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRate(Runnable task,
            final LocalTime firstTime, final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRateMUS(Runnable task, final Date firstTime,
            final long periodMUS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRateMUS(Runnable task,
            final Instant firstTime, final long periodMUS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRateMUS(Runnable task,
            final ZonedDateTime firstTime, final long periodMUS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRateMUS(Runnable task,
            final LocalDateTime firstTime, final long periodMUS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRateMUS(Runnable task,
            final LocalTime firstTime, final long periodMUS);

    /** @see scheduleAtFixedRate(TimerTask,long,long) */
    Task<Runnable> scheduleAtFixedRate(final Runnable task, final long delayMS,
            final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,long,long) */
    Task<Runnable> scheduleAtFixedRateMUS(Runnable task, final long delayMUS,
            final long periodMUS);
}
