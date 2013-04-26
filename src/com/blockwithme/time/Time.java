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

/**
 * Represents the time as an application logical clock tick.
 *
 * Comparison is based strictly on the value of tickTime.
 *
 * @author monster
 */
public class Time implements Comparable<Time> {

    /** *UTC* time of this tick, in nanoseconds. */
    public final long tickTime;

    /** *UTC* time of last tick, in nanoseconds. */
    public final long lastTickTime;

    /** Nanoseconds elapsed, since last tick. */
    public final long tickDuration;

    /** Ticks count since start of clock. */
    public final long ticks;

    /** The beginning of this second, in nanoseconds. */
    public final long secondStart;

    /** The elapsed time in nanoseconds, since the start of this second. */
    public final long secondRest;

    /** The elapsed time as a fraction, since the start of this second. */
    public final double secondFraction;

    /** The start *UTC* time, in nanoseconds, when the clock was created. */
    public final long startTimeNanos;

    /** The elapsed time, in nanoseconds, since the clock was created. */
    public final long elapsedTimeNanos;

    /** toString */
    private String toString;

    /** Creates a Time instance. */
    public Time(final long now, final long theLastTickTime,
            final long theTicks, final long theStartTimeNanos) {
        lastTickTime = theLastTickTime;
        tickTime = now;
        tickDuration = tickTime - lastTickTime;
        ticks = theTicks;
        startTimeNanos = theStartTimeNanos;
        elapsedTimeNanos = now - startTimeNanos;
        secondStart = (now / Scheduler.SECOND_NS) * Scheduler.SECOND_NS;
        secondRest = now - secondStart;
        secondFraction = ((double) secondRest) / Scheduler.SECOND_NS;
    }

    /** toString() */
    @Override
    public String toString() {
        if (toString == null) {
            final StringBuilder buf = new StringBuilder(256);
            buf.append("Time(tickTime=").append(tickTime)
                    .append(",lastTickTime=").append(lastTickTime)
                    .append(",tickDuration=").append(tickDuration)
                    .append(",ticks=").append(ticks).append(",secondStart=")
                    .append(secondStart).append(",secondRest=")
                    .append(secondRest).append(",secondFraction")
                    .append(secondFraction).append(",startTimeNanos")
                    .append(startTimeNanos).append(",elapsedTimeNanos=")
                    .append(elapsedTimeNanos).append(")");
            toString = buf.toString();
        }
        return toString;
    }

    /** hashCode() */
    @Override
    public int hashCode() {
        return (int) (tickTime ^ (tickTime >>> 32));
    }

    /** equals(Object) */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Time) {
            return tickTime == ((Time) obj).tickTime;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Time other) {
        return (other == null) ? 1 : Long.compare(tickTime, other.tickTime);
    }
}
