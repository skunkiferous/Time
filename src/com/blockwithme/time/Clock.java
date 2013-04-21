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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.blockwithme.time.Scheduler.Handler;

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

        /** How long to wait before failing? */
        private static final long WAIT_FOR_REAL_SERVICE = 5000;

        /** Blocks the callers, instead of failing immediately. */
        private final CountDownLatch startSignal = new CountDownLatch(1);

        private ClockService waitForStart() {
            try {
                startSignal.await(WAIT_FOR_REAL_SERVICE, TimeUnit.MILLISECONDS);
            } catch (final InterruptedException e) {
                // NOP
            }
            final ClockService result = Clock.getClockService();
            if (result != null) {
                return result;
            }
            throw new InternalError("ClockService not initialized!");
        }

        @Override
        public long currentTimeMillis() {
            return waitForStart().currentTimeMillis();
        }

        @Override
        public long currentTimeNanos() {
            return waitForStart().currentTimeNanos();
        }

        @Override
        public long startTimeNanos() {
            return waitForStart().startTimeNanos();
        }

        @Override
        public long elapsedTimeNanos() {
            return waitForStart().elapsedTimeNanos();
        }

        @Override
        public Date date() {
            return waitForStart().date();
        }

        @Override
        public Calendar calendar() {
            return waitForStart().calendar();
        }

        @Override
        public org.threeten.bp.Clock clock() {
            return waitForStart().clock();
        }

        @Override
        public TimeZone localTimeZone() {
            return waitForStart().localTimeZone();
        }

        @Override
        public Calendar localCalendar() {
            return waitForStart().localCalendar();
        }

        @Override
        public org.threeten.bp.Clock localClock() {
            return waitForStart().localClock();
        }

        @Override
        public Scheduler createNewScheduler(final Handler errorHandler) {
            return waitForStart().createNewScheduler(errorHandler);
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
    @Inject
    public static void setClockService(final ClockService newClockService) {
        if (newClockService == null) {
            throw new IllegalArgumentException("newClockService is null");
        }
        final ClockService old = clockService;
        clockService = newClockService;
        if (old instanceof DummyClockService) {
            final DummyClockService dummy = (DummyClockService) old;
            dummy.startSignal.countDown();
        }
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

    /** Returns the start *UTC* time, in nanoseconds, when the service was created. */
    public static long startTimeNanos() {
        return clockService.startTimeNanos();
    }

    /** Returns the elapsed time, in nanoseconds, since the service was created. */
    public static long elapsedTimeNanos() {
        return clockService.elapsedTimeNanos();
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

    /** Creates a new Scheduler, using the given Error Handler. */
    public static Scheduler createNewScheduler(final Handler errorHandler) {
        return clockService.createNewScheduler(errorHandler);
    }
}
