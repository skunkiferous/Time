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

import com.blockwithme.time.Scheduler.Handler;

/**
 * CoreScheduler is the interface required to implement the core (singleton)
 * scheduler. All the other schedulers are "lightweight" schedulers built on
 * top of it. This makes scheduler creation cheap. Lightweight means that
 * only the core scheduler needs it's own thread.
 *
 * @author monster
 */
public interface CoreScheduler extends AutoCloseable {

    /** Returns the number of clock ticks per second. */
    int ticksPerSecond();

    /** Sets the ClockService. Can only be called once. */
    void setClockService(final ClockService clockService);

    /** @see java.util.Timer.cancel() */
    @Override
    void close() throws Exception;

    /** @see java.util.Timer.scheduleAtFixedRat(TimerTask,long,long) */
    Task<Runnable> scheduleAtFixedRateNS(final Runnable task,
            final Handler errorHandler, final long delayNS, final long periodNS);

    /** @see java.util.Timer.schedule(TimerTask,long,long) */
    Task<Runnable> scheduleAtFixedPeriodNS(final Runnable task,
            final Handler errorHandler, final long delayNS, final long periodNS);

    /** @see java.util.Timer.schedule(TimerTask,long) */
    Task<Runnable> scheduleNS(final Runnable task, final Handler errorHandler,
            final long delayNS);

    /** Register a Runnable, which is called at every clock tick. */
    Task<Runnable> scheduleTicker(final Runnable task,
            final Handler errorHandler);
}
