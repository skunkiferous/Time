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
 * As a small improvement on java.util.Timer, we allow any kind of object as
 * task, be requiring a "Executor" that knows how to run the given objects.
 *
 * We also accept nano-seconds values for convenience, but they are internally
 * converted to milli-seconds first.
 *
 * @author monster
 */
public interface Scheduler<T> {

    /** "Executes" the given task. */
    interface Executor<T> {
        /** "Executes" the given task. */
        void run(T task);
    }

    /** @see java.util.Timer.cancel() */
    void cancel();

    /** @see java.util.Timer.purge() */
    int purge();

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    void schedule(T task, final Date timeUTC);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    void schedule(T task, final Instant timeUTC);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    void schedule(T task, final ZonedDateTime dateTime);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date) */
    void schedule(T task, final LocalDateTime dateTime);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    void schedule(T task, final Date firstTimeUTC, final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    void schedule(T task, final Instant firstTimeUTC, final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    void schedule(T task, final ZonedDateTime firstTime, final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    void schedule(T task, final LocalDateTime firstTime, final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    void scheduleNS(T task, final Date firstTimeUTC, final long periodNS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    void scheduleNS(T task, final Instant firstTimeUTC, final long periodNS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    void scheduleNS(T task, final ZonedDateTime firstTime, final long periodNS);

    /** @see java.util.Timer.schedule(TimerTask,java.util.Date,long) */
    void scheduleNS(T task, final LocalDateTime firstTime, final long periodNS);

    /** @see java.util.Timer.schedule(TimerTask,long) */
    void schedule(T task, final long delayMS);

    /** @see java.util.Timer.schedule(TimerTask,long) */
    void scheduleNS(T task, final long delayNS);

    /** @see java.util.Timer.schedule(TimerTask,long,long) */
    void schedule(T task, final long delayMS, final long periodMS);

    /** @see java.util.Timer.schedule(TimerTask,long,long) */
    void scheduleNS(T task, final long delayNS, final long periodNS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    void scheduleAtFixedRate(T task, final Date firstTimeUTC,
            final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    void scheduleAtFixedRate(T task, final Instant firstTimeUTC,
            final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    void scheduleAtFixedRate(T task, final ZonedDateTime firstTime,
            final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    void scheduleAtFixedRate(T task, final LocalDateTime firstTime,
            final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    void scheduleAtFixedRateNS(T task, final Date firstTimeUTC,
            final long periodNS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    void scheduleAtFixedRateNS(T task, final Instant firstTimeUTC,
            final long periodNS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    void scheduleAtFixedRateNS(T task, final ZonedDateTime firstTime,
            final long periodNS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    void scheduleAtFixedRateNS(T task, final LocalDateTime firstTime,
            final long periodNS);

    /** @see scheduleAtFixedRate(TimerTask,long,long) */
    void scheduleAtFixedRate(final T task, final long delayMS,
            final long periodMS);

    /** @see java.util.Timer.scheduleAtFixedRate(TimerTask,long,long) */
    void scheduleAtFixedRateNS(T task, final long delayNS, final long periodNS);
}
