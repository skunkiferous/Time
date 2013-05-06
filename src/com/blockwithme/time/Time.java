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
import org.threeten.bp.jdk8.Jdk8Methods;

/**
 * Represents the time as an application logical clock tick.
 * We try to offer all possible measurements of time, useful for a real
 * time application / game.
 *
 * Comparison is based strictly on the value of tickTime.
 *
 * All *time* values as long are in microseconds. After experimenting with
 * the Java methods to get the real time, and to sleep, I came to the
 * conclusion that achieving better then microsecond precision was basically
 * impossible. Also, I want to be able to express the time-in-seconds as a
 * double, and so we would loose the nanosecond precision anyway.
 *
 * Since both milliseconds and microseconds start with "m", I will use MUS as
 * shortcut for microseconds, aka mu-seconds, because the symbol for "micro"-
 * something is called "mu".
 *
 * It also contains one or two static helper methods.
 *
 * @author monster
 */
public class Time implements Comparable<Time>, ClockServiceSource,
        TimeConstants {

    /** The source of this time instance. */
    public final Timeline source;

    /**
     * The last tick, if any. Will be cleared, when this instance becomes the
     * new last tick, to prevent an infinite chain of Time instances.
     */
    public volatile Time lastTick;

    /** The time, in microseconds, at which this Time instance was created. */
    public final long creationTime;

    /** The time, in seconds, at which this Time instance was created. */
    public final double creationTimeSec;

    /** Microseconds elapsed, since last tick. */
    public final long tickDuration;

    /** Seconds elapsed, since last tick. */
    public final double tickDurationSec;

    /**
     * Returns the total microseconds-time spent in running state, since this
     * timeline was created. This could also be called the relative, or
     * elapsed, time. It will be reset to 0, after a reset() or "loop".
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

    /** Is this the first (since reset) tick? */
    public final boolean startTick;

    /** Is this the last tick (only possible if we have a fixed duration)? */
    public final boolean endTick;

    /** Number of core ticks, since source creation. */
    public final long elapsedCoreTicks;

    /** Returns the number of time the timeline was reset. */
    public final long resetCount;

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

    /** Time of this tick, in microseconds, as an Instant. */
    private Instant creationInstant;

    /** toString */
    private String toString;

    /** Creates a Time instance. */
    public Time(final Timeline theSource, final long theTicks,
            final long timeMicros, final long theRunningElapsedTime,
            final double theProgress, final double theTime,
            final long theRunningElapsedTicks, final boolean theEndTick,
            final long theResetCount, final Time theLastTick) {
        source = Objects.requireNonNull(theSource, "theSource");
        lastTick = theLastTick;
        runningElapsedTime = theRunningElapsedTime;
        runningElapsedTimeSec = ((double) runningElapsedTime) / Time.SECOND_MUS;
        progress = theProgress;
        time = theTime;
        creationTime = timeMicros;
        elapsedCoreTicks = theTicks;
        tickDuration = runningElapsedTime
                - ((lastTick == null) ? 0 : lastTick.runningElapsedTime);
        tickDurationSec = ((double) tickDuration) / Time.SECOND_MUS;
        creationTimeSec = ((double) creationTime) / Time.SECOND_MUS;
        runningElapsedTicks = theRunningElapsedTicks;
        startTick = (runningElapsedTicks == 0);
        endTick = theEndTick;
        resetCount = theResetCount;
    }

    /** toString() */
    @Override
    public String toString() {
        if (toString == null) {
            final StringBuilder buf = new StringBuilder(256);
            buf.append("Time(source=").append(source.name())
                    .append(",creationTime=").append(creationTime)
                    .append(",tickDuration=").append(tickDurationSec)
                    .append(",runningElapsedTime=")
                    .append(runningElapsedTimeSec)
                    .append(",runningElapsedTicks=")
                    .append(runningElapsedTicks).append(",elapsedCoreTicks=")
                    .append(elapsedCoreTicks).append(",progress=")
                    .append(progress).append(",time=").append(time)
                    .append(",instant=").append(creationInstant()).append(")");
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

    /** Time of this tick, in microseconds, as an Instant. */
    public final Instant creationInstant() {
        if (creationInstant == null) {
            creationInstant = toInstant(creationTime);
        }
        return creationInstant;
    }

    //////////////////////////////////////////////////////////////////////////
    // Helper methods
    //////////////////////////////////////////////////////////////////////////

    /** Converts the time-in-microseconds to an Instant. */
    public static Instant toInstant(final long timeInMicroseconds) {
        final long secs = Jdk8Methods.floorDiv(timeInMicroseconds,
                Time.SECOND_MUS);
        final long nos = Jdk8Methods.floorMod(timeInMicroseconds,
                Time.SECOND_MUS) * MICROSECOND_NANOS;
        return Instant.ofEpochSecond(secs, nos);
    }

    /** Converts a time in seconds, to a time in microseconds. */
    public static long secondsToMicros(final double seconds) {
        return Math.round(SECOND_MUS * seconds);
    }
}
