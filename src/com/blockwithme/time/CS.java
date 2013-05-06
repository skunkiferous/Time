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

import javax.inject.Inject;

import com.blockwithme.time.Scheduler.Handler;

/**
 * Gives static access to the ClockService.
 *
 * All the delegating methods will throw a NullPointerException, if the
 * ClockService has not been set yet.
 *
 * This class is useful, when working outside of the context of OSGi and Guice.
 *
 * @author monster
 */
public class CS {

    /** The ClockService instance. */
    private static volatile ClockService clockService;

    /**
     * Cannot be instantiated.
     */
    private CS() {
        // NOP
    }

    /** Sets the ClockService. */
    @Inject
    public static void setClockService(final ClockService newClockService) {
        clockService = newClockService;
    }

    /** Returns the ClockService. */
    public static ClockService getClockService() {
        return clockService;
    }

    /** Returns the current time, in milliseconds. */
    public static long currentTimeMillis() {
        return clockService.currentTimeMillis();
    }

    /** Returns the current time, in microseconds. */
    public static long currentTimeMicros() {
        return clockService.currentTimeMicros();
    }

    /** Returns a new Date, using the current time. */
    public static Date date() {
        return clockService.date();
    }

    /** Returns a new Calendar, using the current time. */
    public static Calendar calendar() {
        return clockService.calendar();
    }

    /**
     * Returns a microseconds precision *UTC* Clock instance.
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
     * Returns a microseconds precision default timezone Clock instance.
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

    /**
     * Creates a new Scheduler, using the given Error Handler.
     * @param name cannot be null or empty
     */
    public static Scheduler newScheduler(final String name,
            final Handler errorHandler) {
        return clockService.newScheduler(name, errorHandler);
    }

    /** Closes the service.
     * @throws Exception */
    public static void close() throws Exception {
        final ClockService service = clockService;
        if (service != null) {
            service.close();
            clockService = null;
        }
    }
}
