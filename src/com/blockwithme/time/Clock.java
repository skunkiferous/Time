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

import com.blockwithme.time.Scheduler.Executor;

/**
 * Gives static access to the ClockService.
 *
 * All the delegating methods will throw a InternalError, if the
 * ClockService has not been set yet.
 *
 * @author monster
 */
public class Clock {

    /** Dummy ClockService implementation, to be used until initialized. */
    private static final class DummyClockService implements ClockService {

        @Override
        public long currentTimeMillis() {
            throw new InternalError("ClockService not initialized!");
        }

        @Override
        public long currentTimeNanos() {
            throw new InternalError("ClockService not initialized!");
        }

        @Override
        public Date date() {
            throw new InternalError("ClockService not initialized!");
        }

        @Override
        public Calendar calendar() {
            throw new InternalError("ClockService not initialized!");
        }

        @Override
        public org.threeten.bp.Clock clock() {
            throw new InternalError("ClockService not initialized!");
        }

        @Override
        public TimeZone localTimeZone() {
            throw new InternalError("ClockService not initialized!");
        }

        @Override
        public Calendar localCalendar() {
            throw new InternalError("ClockService not initialized!");
        }

        @Override
        public org.threeten.bp.Clock localClock() {
            throw new InternalError("ClockService not initialized!");
        }

        @Override
        public <T> Scheduler<T> createNewScheduler(final Executor<T> executor) {
            throw new InternalError("ClockService not initialized!");
        }

        @Override
        public Scheduler<Runnable> getDefaultRunnableScheduler() {
            throw new InternalError("ClockService not initialized!");
        }
    }

    /** The ClockService instance. */
    private static volatile ClockService clockService = new DummyClockService();

    /**
     * Cannot be instantiated.
     */
    private Clock() {
        // NOP
    }

    /** Sets the ClockService. */
    public static void setClockService(final ClockService newClockService) {
        if (newClockService == null) {
            throw new IllegalArgumentException("newClockService is null");
        }
        clockService = newClockService;
    }

    /** Returns the ClockService. */
    public static ClockService getClockService() {
        ClockService result = clockService;
        if (result instanceof DummyClockService) {
            result = null;
        }
        return result;
    }

    /** Returns the current *UTC* time, in milliseconds. */
    public static long currentTimeMillis() {
        return clockService.currentTimeMillis();
    }

    /** Returns the current *UTC* time, in nanoseconds. */
    public static long currentTimeNanos() {
        return clockService.currentTimeNanos();
    }

    /** Returns a new Date, using the current *UTC* time. */
    public static Date date() {
        return clockService.date();
    }

    /** Returns a new Calendar, using the current *UTC* time. */
    public static Calendar calendar() {
        return clockService.calendar();
    }

    /**
     * Returns a nano-seconds precision *UTC* Clock instance.
     *
     * Note that the time should be (relatively) correct for UTC,
     * even if the local clock is wrong.
     *
     * @see org.threeten.bp.Clock#systemUTC()
     */
    public static org.threeten.bp.Clock clock() {
        return clockService.clock();
    }

    /** The original default local time zone. */
    public static TimeZone localTimeZone() {
        return clockService.localTimeZone();
    }

    /**
     * Returns a nano-seconds precision default timezone Clock instance.
     *
     * Note that the time should be (relatively) correct for the given
     * timezone, even if the local clock is wrong.
     *
     * @see org.threeten.bp.Clock#systemDefaultZone()
     */
    public static org.threeten.bp.Clock localClock() {
        return clockService.localClock();
    }

    /** Returns a new Calendar, using the current *local* time. */
    public static Calendar localCalendar() {
        return clockService.localCalendar();
    }

    /** Creates a new Scheduler<T>, using the given executor. */
    public static <T> Scheduler<T> createNewScheduler(final Executor<T> executor) {
        return clockService.createNewScheduler(executor);
    }

    /** Returns a default Scheduler, for executing Runnables. */
    public static Scheduler<Runnable> getDefaultRunnableScheduler() {
        return clockService.getDefaultRunnableScheduler();
    }
}
