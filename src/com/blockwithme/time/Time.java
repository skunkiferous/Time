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

import java.util.Objects;

import org.threeten.bp.Instant;

/**
 * Represents the time as an application logical clock tick.
 * We try to offer all possible measurements of time, useful for a real
 * time application / game.
 *
 * Comparison is based strictly on the value of tickTime.
 *
 * All time values as long are in nanoseconds.
 *
 * @author monster
 */
public class Time implements Comparable<Time>, ClockServiceSource {

    /** One milli-second, in nano-seconds. */
    public static final long MILLI_NS = 1000000L;

    /** One second, in milli-seconds. */
    public static final long SECOND_MS = 1000L;

    /** One second, in nano-seconds. */
    public static final long SECOND_NS = SECOND_MS * MILLI_NS;

    /** One minute, in milli-seconds. */
    public static final long MINUTE_MS = 60L * 1000L;

    /** One minute, in nano-seconds. */
    public static final long MINUTE_NS = MINUTE_MS * MILLI_NS;

    /** One hour, in milli-seconds. */
    public static final long HOUR_MS = 60L * MINUTE_MS;

    /** One hour, in nano-seconds. */
    public static final long HOUR_NS = HOUR_MS * MILLI_NS;

    /** One day, in milli-seconds. */
    public static final long DAY_MS = 24L * HOUR_MS;

    /** One day, in nano-seconds. */
    public static final long DAY_NS = DAY_MS * MILLI_NS;

    /** The source of this time instance. */
    public final Timeline source;

    /**
     * The last tick, if any. Will be cleared, when this instance becomes the
     * new last tick, to prevent an infinite chain of Time instances.
     */
    public volatile Time lastTick;

    /** Time of this tick, in nanoseconds. */
    public final long tickTime;

    /** Nanoseconds elapsed, since last tick. */
    public final long tickDuration;

    /** Current ticks count. */
    public final long ticks;

    /** Elapsed ticks, since last tick (can be negative!). */
    public final long tickStep;

    /** The beginning of this second, in nanoseconds. */
    public final long secondStart;

    /** The elapsed time in nanoseconds, since the start of this second. */
    public final int secondRest;

    /** The elapsed time as a fraction, since the start of this second. */
    public final double secondFraction;

    /**
     * The elapsed time, in nanoseconds, since the clock was created, including
     * pauses.
     */
    public final long elapsedTime;

    /**
     * The elapsed time, in nanoseconds, since the clock was created, excluding
     * pauses.
     */
    public final long elapsedUnpausedTime;

    /** Was the clock paused, between this and the previous tick? By how much ticks? */
    public final long pausedTicks;

    /** Was the clock paused, between this and the previous tick? By how much nanos? */
    public final long pausedTime;

    /** toString */
    private String toString;

    /** Time of this tick, in nanoseconds, as an Instant. */
    private Instant tickTimeInstant;

    /** Creates a Time instance. */
    public Time(final Timeline theSource, final long now,
            final Time theLastTick, final long theTicks,
            final long thePausedTicks, final long thePausedTime) {
        source = Objects.requireNonNull(theSource, "theSource");
        tickTime = now;
        lastTick = theLastTick;
        tickDuration = tickTime - ((lastTick == null) ? 0 : lastTick.tickTime);
        ticks = theTicks;
        tickStep = ticks - ((lastTick == null) ? 0 : lastTick.ticks);
        secondStart = (now / SECOND_NS) * SECOND_NS;
        secondRest = (int) (now - secondStart);
        secondFraction = ((double) secondRest) / SECOND_NS;
        final long startTime = source.startTime();
        elapsedTime = now - startTime;
        elapsedUnpausedTime = elapsedTime - source.pausedTime();
        pausedTicks = thePausedTicks;
        pausedTime = thePausedTime;
    }

    /** toString() */
    @Override
    public String toString() {
        if (toString == null) {
            final StringBuilder buf = new StringBuilder(256);
            buf.append("Time(source=").append(source).append(",tickTime=")
                    .append(tickTime).append(",tickDuration=")
                    .append(tickDuration).append(",ticks=").append(ticks)
                    .append(",tickStep=").append(tickStep)
                    .append(",secondStart=").append(secondStart)
                    .append(",secondRest=").append(secondRest)
                    .append(",secondFraction=").append(secondFraction)
                    .append(",elapsedTime=").append(elapsedTime)
                    .append(",elapsedUnpausedTime=")
                    .append(elapsedUnpausedTime).append(",pausedTicks=")
                    .append(pausedTicks).append(",tickTimeInstant=")
                    .append(tickTimeInstant()).append(")");
            toString = buf.toString();
        }
        return toString;
    }

    /** hashCode() */
    @Override
    public final int hashCode() {
        return (int) (tickTime ^ (tickTime >>> 32));
    }

    /** equals(Object) */
    @Override
    public final boolean equals(final Object obj) {
        if (obj instanceof Time) {
            return tickTime == ((Time) obj).tickTime;
        }
        return false;
    }

    /**
     * Compares to another time instance.
     * Comparison is based strictly on the value of tickTime.
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public final int compareTo(final Time other) {
        return (other == null) ? 1 : Long.compare(tickTime, other.tickTime);
    }

    /** Returns the ClockService of the originating timeline. */
    @Override
    public final ClockService clockService() {
        return source.clockService();
    }

    /** Time of this tick, in nanoseconds, as an Instant. */
    public final Instant tickTimeInstant() {
        if (tickTimeInstant == null) {
            tickTimeInstant = Instant.ofEpochSecond(0, tickTime);
        }
        return tickTimeInstant;
    }
}
