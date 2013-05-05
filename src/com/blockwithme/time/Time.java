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

    /** The time, in nanoseconds, at which this Time instance was created. */
    public final long creationTime;

    /** The time, in seconds, at which this Time instance was created. */
    public final double creationTimeSec;

    /** Nanoseconds elapsed, since last tick. */
    public final long tickDuration;

    /** Seconds elapsed, since last tick. */
    public final double tickDurationSec;

    /**
     * Returns the total nano-time spent in running state, since this timeline
     * was created. This could also be called the relative, or elapsed, time.
     * It will be reset to 0, after a reset() or "loop".
     */
    public final long runningElapsedTime;

    /**
     * Returns the total time in seconds spent in running state, since this
     * timeline was created. This could also be called the relative, or
     * elapsed, time. It will be reset to 0, after a reset() or "loop".
     */
    public final double runningElapsedTimeSec;

    /**
     * The total ticks spent in running state, since this timeline was created.
     * It will be reset to 0, after a reset() or "loop".
     */
    public final long runningElapsedTicks;

    /**
     * If fixedDurationTicks() is not 0, then we can compute how far we got
     * along this timeline, and return it as a fraction, within [0,1].
     * Returns -1 if fixedDurationTicks() is 0.
     */
    public final double progress;

    /**
     * Returns the "time" of this timeline. This is the value that most parts
     * of the application are interested in. It's meaning is arbitrary. It is
     * obtained by: (timeOffset() + globalTickScaling() * runningElapsedTicks()).
     * Being a double, it is not as precise as a long would be, but this is
     * required, to allow any kind of scaling.
     */
    public final double time;

    /** Number of core ticks, since source creation. */
    public final long ticks;

    /** Time of this tick, in nanoseconds, as an Instant. */
    private Instant tickTimeInstant;

    /** toString */
    private String toString;

    /** Creates a Time instance. */
    public Time(final Timeline theSource, final long theTicks,
            final long timeNanos, final long theRunningElapsedTime,
            final double theProgress, final double theTime,
            final long theRunningElapsedTicks, final Time theLastTick) {
        source = Objects.requireNonNull(theSource, "theSource");
        lastTick = theLastTick;
        runningElapsedTime = theRunningElapsedTime;
        runningElapsedTimeSec = ((double) runningElapsedTime) / Time.SECOND_NS;
        progress = theProgress;
        time = theTime;
        creationTime = timeNanos;
        ticks = theTicks;
        tickDuration = runningElapsedTime
                - ((lastTick == null) ? 0 : lastTick.runningElapsedTime);
        tickDurationSec = ((double) tickDuration) / Time.SECOND_NS;
        creationTimeSec = ((double) creationTime) / Time.SECOND_NS;
        runningElapsedTicks = theRunningElapsedTicks;
    }

    /** toString() */
    @Override
    public String toString() {
        if (toString == null) {
            final StringBuilder buf = new StringBuilder(256);
            buf.append("Time(source=").append(source.name())
                    .append(",tickDuration=").append(tickDuration)
                    .append(",runningElapsedTime=").append(runningElapsedTime)
                    .append(",progress=").append(progress).append(",time=")
                    .append(time).append(tickTimeInstant()).append(")");
            toString = buf.toString();
        }
        return toString;
    }

    /** hashCode() */
    @Override
    public final int hashCode() {
        return (int) (runningElapsedTime ^ (runningElapsedTime >>> 32));
    }

    /** equals(Object) */
    @Override
    public final boolean equals(final Object obj) {
        if (obj instanceof Time) {
            return runningElapsedTime == ((Time) obj).runningElapsedTime;
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
        return (other == null) ? 1 : Long.compare(runningElapsedTime,
                other.runningElapsedTime);
    }

    /** Returns the ClockService of the originating timeline. */
    @Override
    public final ClockService clockService() {
        return source.clockService();
    }

    /** Time of this tick, in nanoseconds, as an Instant. */
    public final Instant tickTimeInstant() {
        if (tickTimeInstant == null) {
            tickTimeInstant = Instant.ofEpochSecond(0, creationTime);
        }
        return tickTimeInstant;
    }
}
