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

import org.threeten.bp.Instant;

/**
 * Time-related constants.
 *
 * Since both milliseconds and microseconds start with "m", I will use MUS as
 * shortcut for microseconds, aka mu-seconds, because the symbol for "micro"-
 * something is called "mu".
 *
 * @author monster
 */
public interface TimeConstants {

    /** One milli-second, in microseconds. */
    long MILLI_MUS = 1000L;

    /** One second, in milli-seconds. */
    long SECOND_MS = 1000L;

    /** One second, in microseconds. */
    long SECOND_MUS = SECOND_MS * MILLI_MUS;

    /** One minute, in seconds. */
    long MINUTE_SECONDS = 60L;

    /** One minute, in milli-seconds. */
    long MINUTE_MS = MINUTE_SECONDS * SECOND_MS;

    /** One minute, in microseconds. */
    long MINUTE_MUS = MINUTE_MS * MILLI_MUS;

    /** One hour, in minutes. */
    long HOUR_MINUTES = 60L;

    /** One hour, in seconds. */
    long HOUR_SECONDS = HOUR_MINUTES * MINUTE_SECONDS;

    /** One hour, in milli-seconds. */
    long HOUR_MS = HOUR_MINUTES * MINUTE_MS;

    /** One hour, in microseconds. */
    long HOUR_MUS = HOUR_MS * MILLI_MUS;

    /** One day, in hours. */
    long DAY_HOURS = 24L;

    /** One day, in minutes. */
    long DAY_MINUTES = DAY_HOURS * HOUR_MINUTES;

    /** One day, in seconds. */
    long DAY_SECONDS = DAY_MINUTES * MINUTE_SECONDS;

    /** One day, in milli-seconds. */
    long DAY_MS = DAY_HOURS * HOUR_MS;

    /** One day, in microseconds. */
    long DAY_MUS = DAY_MS * MILLI_MUS;

    /** One microsecond, in nanoseconds. */
    long MICROSECOND_NANOS = 1000L;

    /** Smallest possible time, expressed in microseconds. */
    Instant MIN_TIME_MICRO_VALUE = Time.toInstant(Long.MIN_VALUE);

    /** Largest possible time, expressed in microseconds. */
    Instant MAX_TIME_MICRO_VALUE = Time.toInstant(Long.MAX_VALUE);

    /** The Instant for "time 0", or beginning of epoch. */
    Instant ZERO_TIME = Instant.ofEpochSecond(0, 0);
}
