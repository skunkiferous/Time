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
package com.blockwithme.time.implapi;

import com.blockwithme.time.ClockService;
import com.blockwithme.time.Scheduler.Handler;
import com.blockwithme.time.Task;

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
    Task<Runnable> scheduleAtFixedRateMUS(final Runnable task,
            final Handler errorHandler, final long delayMUS,
            final long periodMUS);

    /** @see java.util.Timer.schedule(TimerTask,long,long) */
    Task<Runnable> scheduleAtFixedPeriodMUS(final Runnable task,
            final Handler errorHandler, final long delayMUS,
            final long periodMUS);

    /** @see java.util.Timer.schedule(TimerTask,long) */
    Task<Runnable> scheduleMUS(final Runnable task, final Handler errorHandler,
            final long delayMUS);

    /** Register a Ticker, which is called at every clock tick. */
    Task<Ticker> scheduleTicker(final Ticker task);
}
