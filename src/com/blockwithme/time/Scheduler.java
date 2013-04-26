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
 * The meaning of the methods is the same as those of java.util.Timer.
 *
 * The points in time are based on Clock.currentTimeMillis(), rather then
 * System.currentTimeMillis(), to deal with inaccurate local clocks.
 * (With the exception of ZonedDateTime, which contains it's own timezone
 * specification.)
 *
 * We also accept nano-seconds values for convenience, in the methods suffixed
 * with "NS".
 *
 * The scheduler instances are lightweight, which mean they do not get their
 * own thread, and so can be cheaply created and destroyed. OTOH, it means that
 * tasks should never block or take a long time to perform, as this will block
 * all other scheduled tasks.
 *
 * @author monster
 */
public interface Scheduler extends AutoCloseable {

    /** Exception handler. */
    interface Handler {
        /** Handles exceptions. */
        void onError(final Runnable task, final Throwable error);
    }

    /** One second, in milli-seconds. */
    long SECOND_MS = 1000L;

    /** One second, in nano-seconds. */
    long SECOND_NS = SECOND_MS * 1000000L;

    /** One minute, in milli-seconds. */
    long MINUTE_MS = 60L * 1000L;

    /** One minute, in nano-seconds. */
    long MINUTE_NS = MINUTE_MS * 1000000L;

    /** One hour, in milli-seconds. */
    long HOUR_MS = 60L * MINUTE_MS;

    /** One hour, in nano-seconds. */
    long HOUR_NS = HOUR_MS * 1000000L;

    /** One day, in milli-seconds. */
    long DAY_MS = 24L * HOUR_MS;

    /** One day, in nano-seconds. */
    long DAY_NS = DAY_MS * 1000000L;

    /** The number of clock ticks per second. */
    int TICKS_PER_SECOND = 60;

    /** The duration of a clock tick in nanoseconds. */
    long TICK_IN_NS = 1000000000L / TICKS_PER_SECOND;

    /** Returns the ClockService that created this Scheduler. */
    ClockService clockService();

    /** @see java.util.Timer.cancel() */
    @Override
    void close() throws Exception;

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    Task<Runnable> scheduleOnce(Runnable task, final Date timeUTC);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    Task<Runnable> scheduleOnce(Runnable task, final Instant timeUTC);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    Task<Runnable> scheduleOnce(Runnable task, final ZonedDateTime dateTime);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    Task<Runnable> scheduleOnce(Runnable task, final LocalDateTime dateTime);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    Task<Runnable> scheduleOnce(Runnable task, final LocalTime time);

    /** @see java.util.Timer.schedule(TimerTask,long) */
    Task<Runnable> scheduleOnce(Runnable task, final long delayMS);

    /** @see java.util.Timer.schedule(TimerTask,long) */
    Task<Runnable> scheduleOnceNS(Runnable task, final long delayNS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriod(Runnable task,
            final Date firstTimeUTC, final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriod(Runnable task,
            final Instant firstTimeUTC, final long periodMS);

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
    Task<Runnable> scheduleAtFixedPeriodNS(Runnable task,
            final Date firstTimeUTC, final long periodNS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriodNS(Runnable task,
            final Instant firstTimeUTC, final long periodNS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriodNS(Runnable task,
            final ZonedDateTime firstTime, final long periodNS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriodNS(Runnable task,
            final LocalDateTime firstTime, final long periodNS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedPeriodNS(Runnable task,
            final LocalTime firstTime, final long periodNS);

    /** @see java.util.Timer.schedule(TimerTask,long,long) */
    Task<Runnable> scheduleAtFixedPeriod(Runnable task, final long delayMS,
            final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,long,long) */
    Task<Runnable> scheduleAtFixedPeriodNS(Runnable task, final long delayNS,
            final long periodNS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRate(Runnable task, final Date firstTimeUTC,
            final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRate(Runnable task,
            final Instant firstTimeUTC, final long periodMS);

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
    Task<Runnable> scheduleAtFixedRateNS(Runnable task,
            final Date firstTimeUTC, final long periodNS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRateNS(Runnable task,
            final Instant firstTimeUTC, final long periodNS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRateNS(Runnable task,
            final ZonedDateTime firstTime, final long periodNS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRateNS(Runnable task,
            final LocalDateTime firstTime, final long periodNS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    Task<Runnable> scheduleAtFixedRateNS(Runnable task,
            final LocalTime firstTime, final long periodNS);

    /** @see scheduleAtFixedRate(TimerTask,long,long) */
    Task<Runnable> scheduleAtFixedRate(final Runnable task, final long delayMS,
            final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,long,long) */
    Task<Runnable> scheduleAtFixedRateNS(Runnable task, final long delayNS,
            final long periodNS);

    /** Register a Runnable, which is called at every clock tick. */
    Task<Runnable> scheduleTicker(final Runnable task);
}
