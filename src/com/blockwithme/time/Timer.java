/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.blockwithme.time;

import java.util.Date;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

/**
 * This class is a copy of java.util.Timer form the project Apache Harmony.
 *
 * It was only slightly modified to use CurrentTimeNanos.utcTimeNanos()
 * instead of System.currentTimeMillis(), and support the JSR 310 backport.
 * All Date input parameters are assumed to me in UTC time. Some methods accept
 * values in nano-seconds only for convenience. Internally, we still use
 * milli-seconds (might change in future).
 *
 * {@code Timer}s are used to schedule jobs for execution in a background process. A
 * single thread is used for the scheduling and this thread has the option of
 * being a daemon thread. By calling {@code cancel} you can terminate a
 * {@code Timer} and its associated thread. All tasks which are scheduled to run after
 * this point are cancelled. Tasks are executed sequentially but are subject to
 * the delays from other tasks run methods. If a specific task takes an
 * excessive amount of time to run it may impact the time at which subsequent
 * tasks may run.
 * <p>
 *
 * The {@code TimerTask} does not offer any guarantees about the real-time nature of
 * scheduling tasks as its underlying implementation relies on the
 * {@code Object.wait(long)} method.
 * <p>
 * Multiple threads can share a single {@code Timer} without the need for their own
 * synchronization.
 * <p>
 * A {@code Timer} can be set to schedule tasks either at a fixed rate or
 * with a fixed period. Fixed-period execution is the default.
 * <p>
 * The difference between fixed-rate and fixed-period execution
 * is the following:  With fixed-rate execution, the start time of each
 * successive run of the task is scheduled in absolute terms without regard for when the previous
 * task run actually took place. This can result in a series of bunched-up runs
 * (one launched immediately after another) if busy resources or other
 * system delays prevent the {@code Timer} from firing for an extended time.
 * With fixed-period execution, each successive run of the
 * task is scheduled relative to the start time of the previous run of the
 * task, so two runs of the task are never fired closer together in time than
 * the specified {@code period}.
 *
 * @see TimerTask
 * @see java.lang.Object#wait(long)
 */
public class Timer {

    private static final class TimerImpl extends Thread {

        private static final class TimerHeap {
            private final int DEFAULT_HEAP_SIZE = 256;

            private TimerTask[] timers = new TimerTask[DEFAULT_HEAP_SIZE];

            private int size = 0;

            private int deletedCancelledNumber = 0;

            public TimerTask minimum() {
                return timers[0];
            }

            public boolean isEmpty() {
                return size == 0;
            }

            public void insert(final TimerTask task) {
                if (timers.length == size) {
                    final TimerTask[] appendedTimers = new TimerTask[size * 2];
                    System.arraycopy(timers, 0, appendedTimers, 0, size);
                    timers = appendedTimers;
                }
                timers[size++] = task;
                upHeap();
            }

            public void delete(final int pos) {
                // posible to delete any position of the heap
                if (pos >= 0 && pos < size) {
                    timers[pos] = timers[--size];
                    timers[size] = null;
                    downHeap(pos);
                }
            }

            private void upHeap() {
                int current = size - 1;
                int parent = (current - 1) / 2;

                while (timers[current].when < timers[parent].when) {
                    // swap the two
                    final TimerTask tmp = timers[current];
                    timers[current] = timers[parent];
                    timers[parent] = tmp;

                    // update pos and current
                    current = parent;
                    parent = (current - 1) / 2;
                }
            }

            private void downHeap(final int pos) {
                int current = pos;
                int child = 2 * current + 1;

                while (child < size && size > 0) {
                    // compare the children if they exist
                    if (child + 1 < size
                            && timers[child + 1].when < timers[child].when) {
                        child++;
                    }

                    // compare selected child with parent
                    if (timers[current].when < timers[child].when) {
                        break;
                    }

                    // swap the two
                    final TimerTask tmp = timers[current];
                    timers[current] = timers[child];
                    timers[child] = tmp;

                    // update pos and current
                    current = child;
                    child = 2 * current + 1;
                }
            }

            public void reset() {
                timers = new TimerTask[DEFAULT_HEAP_SIZE];
                size = 0;
            }

            public void deleteIfCancelled() {
                for (int i = 0; i < size; i++) {
                    if (timers[i].cancelled) {
                        deletedCancelledNumber++;
                        delete(i);
                        // re-try this point
                        i--;
                    }
                }
            }

            private int getTask(final TimerTask task) {
                for (int i = 0; i < timers.length; i++) {
                    if (timers[i] == task) {
                        return i;
                    }
                }
                return -1;
            }

        }

        /**
         * True if the method cancel() of the Timer was called or the !!!stop()
         * method was invoked
         */
        private boolean cancelled;

        /**
         * True if the Timer has become garbage
         */
        private boolean finished;

        /**
         * Vector consists of scheduled events, sorted according to
         * {@code when} field of TaskScheduled object.
         */
        private final TimerHeap tasks = new TimerHeap();

        /**
         * Starts a new timer.
         *
         * @param name thread's name
         * @param isDaemon daemon thread or not
         */
        TimerImpl(final String name, final boolean isDaemon) {
            this.setName(name);
            this.setDaemon(isDaemon);
            this.start();
        }

        /**
         * This method will be launched on separate thread for each Timer
         * object.
         */
        @Override
        public void run() {
            while (true) {
                TimerTask task;
                synchronized (this) {
                    // need to check cancelled inside the synchronized block
                    if (cancelled) {
                        return;
                    }
                    if (tasks.isEmpty()) {
                        if (finished) {
                            return;
                        }
                        // no tasks scheduled -- sleep until any task appear
                        try {
                            this.wait();
                        } catch (final InterruptedException ignored) {
                        }
                        continue;
                    }

                    final long currentTime = currentTimeMillis();

                    task = tasks.minimum();
                    long timeToSleep;

                    synchronized (task.lock) {
                        if (task.cancelled) {
                            tasks.delete(0);
                            continue;
                        }

                        // check the time to sleep for the first task scheduled
                        timeToSleep = task.when - currentTime;
                    }

                    if (timeToSleep > 0) {
                        // sleep!
                        try {
                            this.wait(timeToSleep);
                        } catch (final InterruptedException ignored) {
                        }
                        continue;
                    }

                    // no sleep is necessary before launching the task

                    synchronized (task.lock) {
                        int pos = 0;
                        if (tasks.minimum().when != task.when) {
                            pos = tasks.getTask(task);
                        }
                        if (task.cancelled) {
                            tasks.delete(tasks.getTask(task));
                            continue;
                        }

                        // set time to schedule
                        task.setScheduledTime(task.when);

                        // remove task from queue
                        tasks.delete(pos);

                        // set when the next task should be launched
                        if (task.period >= 0) {
                            // this is a repeating task,
                            if (task.fixedRate) {
                                // task is scheduled at fixed rate
                                task.when = task.when + task.period;
                            } else {
                                // task is scheduled at fixed delay
                                task.when = currentTimeMillis() + task.period;
                            }

                            // insert this task into queue
                            insertTask(task);
                        } else {
                            task.when = 0;
                        }
                    }
                }

                boolean taskCompletedNormally = false;
                try {
                    task.run();
                    taskCompletedNormally = true;
                } finally {
                    if (!taskCompletedNormally) {
                        synchronized (this) {
                            cancelled = true;
                        }
                    }
                }
            }
        }

        private void insertTask(final TimerTask newTask) {
            // callers are synchronized
            tasks.insert(newTask);
            this.notify();
        }

        /**
         * Cancels timer.
         */
        public synchronized void cancel() {
            cancelled = true;
            tasks.reset();
            this.notify();
        }

        public int purge() {
            if (tasks.isEmpty()) {
                return 0;
            }
            // callers are synchronized
            tasks.deletedCancelledNumber = 0;
            tasks.deleteIfCancelled();
            return tasks.deletedCancelledNumber;
        }

    }

    private static final class FinalizerHelper {
        private final TimerImpl impl;

        FinalizerHelper(final TimerImpl impl) {
            super();
            this.impl = impl;
        }

        @Override
        protected void finalize() {
            synchronized (impl) {
                impl.finished = true;
                impl.notify();
            }
        }
    }

    /** NS im MN. */
    private static final long MS2NS = 1000000L;

    private static long timerId;

    /* This object will be used in synchronization purposes */
    private final TimerImpl impl;

    // Used to finalize thread
    @SuppressWarnings("unused")
    private final FinalizerHelper finalizer;

    private synchronized static long nextId() {
        return timerId++;
    }

    /**
     * Converts an Instant, assumed to be UTC, to a Date.
     */
    private static Date toUTCDate(final Instant instantUTC) {
        return new Date(instantUTC.toEpochMilli());
    }

    /**
     * Converts an ZonedDateTime, to a Date.
     */
    private static Date toUTCDate(final ZonedDateTime dateTime) {
        return toUTCDate(dateTime.with(ZoneOffset.UTC).toInstant());
    }

    /**
     * Converts an LocalDateTime, to a Date.
     */
    private static Date toUTCDate(final LocalDateTime dateTime) {
        return toUTCDate(dateTime.atZone(ZoneOffset.UTC).toInstant());
    }

    private static long currentTimeMillis() {
        return CurrentTimeNanos.utcTimeNanos() / MS2NS;
    }

    /**
     * Creates a new named {@code Timer} which does run as a daemon thread.
     *
     * @param name the name of the Timer.
     * @throws NullPointerException is {@code name} is {@code null}
     */
    public Timer(final String name) {
        super();
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        this.impl = new TimerImpl(name, true);
        this.finalizer = new FinalizerHelper(impl);
    }

    /**
     * Creates a new daemon {@code Timer}.
     */
    public Timer() {
        this("Timer-" + Timer.nextId());
    }

    /**
     * Cancels the {@code Timer} and removes any scheduled tasks. If there is a
     * currently running task it is not affected. No more tasks may be scheduled
     * on this {@code Timer}. Subsequent calls do nothing.
     */
    public void cancel() {
        impl.cancel();
    }

    /**
     * Removes all canceled tasks from the task queue. If there are no
     * other references on the tasks, then after this call they are free
     * to be garbage collected.
     *
     * @return the number of canceled tasks that were removed from the task
     *         queue.
     */
    public int purge() {
        synchronized (impl) {
            return impl.purge();
        }
    }

    /**
     * Schedule a task for single execution. If {@code when} is less than the
     * current time, it will be scheduled to be executed as soon as possible.
     *
     * @param task
     *            the task to schedule.
     * @param timeUTC
     *            time of execution.
     * @throws IllegalArgumentException
     *                if {@code when.getTime() < 0}.
     * @throws IllegalStateException
     *                if the {@code Timer} has been canceled, or if the task has been
     *                scheduled or canceled.
     */
    public void schedule(final TimerTask task, final Date timeUTC) {
        if (timeUTC.getTime() < 0) {
            throw new IllegalArgumentException();
        }
        final long delay = timeUTC.getTime() - currentTimeMillis();
        scheduleImpl(task, delay < 0 ? 0 : delay, -1, false);
    }

    /**
     * Schedule a task for single execution after a specified delay.
     *
     * @param task
     *            the task to schedule.
     * @param delay
     *            amount of time in milliseconds before execution.
     * @throws IllegalArgumentException
     *                if {@code delay < 0}.
     * @throws IllegalStateException
     *                if the {@code Timer} has been canceled, or if the task has been
     *                scheduled or canceled.
     */
    public void schedule(final TimerTask task, final long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException();
        }
        scheduleImpl(task, delay, -1, false);
    }

    /**
     * Schedule a task for repeated fixed-delay execution after a specific delay.
     *
     * @param task
     *            the task to schedule.
     * @param delay
     *            amount of time in milliseconds before first execution.
     * @param period
     *            amount of time in milliseconds between subsequent executions.
     * @throws IllegalArgumentException
     *                if {@code delay < 0} or {@code period < 0}.
     * @throws IllegalStateException
     *                if the {@code Timer} has been canceled, or if the task has been
     *                scheduled or canceled.
     */
    public void schedule(final TimerTask task, final long delay,
            final long period) {
        if (delay < 0 || period <= 0) {
            throw new IllegalArgumentException();
        }
        scheduleImpl(task, delay, period, false);
    }

    /**
     * Schedule a task for repeated fixed-delay execution after a specific time
     * has been reached.
     *
     * @param task
     *            the task to schedule.
     * @param timeUTC
     *            time of first execution.
     * @param period
     *            amount of time in milliseconds between subsequent executions.
     * @throws IllegalArgumentException
     *                if {@code when.getTime() < 0} or {@code period < 0}.
     * @throws IllegalStateException
     *                if the {@code Timer} has been canceled, or if the task has been
     *                scheduled or canceled.
     */
    public void schedule(final TimerTask task, final Date timeUTC,
            final long period) {
        if (period <= 0 || timeUTC.getTime() < 0) {
            throw new IllegalArgumentException();
        }
        final long delay = timeUTC.getTime() - currentTimeMillis();
        scheduleImpl(task, delay < 0 ? 0 : delay, period, false);
    }

    /**
     * Schedule a task for repeated fixed-rate execution after a specific delay
     * has passed.
     *
     * @param task
     *            the task to schedule.
     * @param delay
     *            amount of time in milliseconds before first execution.
     * @param period
     *            amount of time in milliseconds between subsequent executions.
     * @throws IllegalArgumentException
     *                if {@code delay < 0} or {@code period < 0}.
     * @throws IllegalStateException
     *                if the {@code Timer} has been canceled, or if the task has been
     *                scheduled or canceled.
     */
    public void scheduleAtFixedRate(final TimerTask task, final long delay,
            final long period) {
        if (delay < 0 || period <= 0) {
            throw new IllegalArgumentException();
        }
        scheduleImpl(task, delay, period, true);
    }

    /**
     * Schedule a task for repeated fixed-rate execution after a specific time
     * has been reached.
     *
     * @param task
     *            the task to schedule.
     * @param firstTimeUTC
     *            time of first execution.
     * @param period
     *            amount of time in milliseconds between subsequent executions.
     * @throws IllegalArgumentException
     *                if {@code when.getTime() < 0} or {@code period < 0}.
     * @throws IllegalStateException
     *                if the {@code Timer} has been canceled, or if the task has been
     *                scheduled or canceled.
     */
    public void scheduleAtFixedRate(final TimerTask task,
            final Date firstTimeUTC, final long period) {
        if (period <= 0 || firstTimeUTC.getTime() < 0) {
            throw new IllegalArgumentException();
        }
        final long delay = firstTimeUTC.getTime() - currentTimeMillis();
        scheduleImpl(task, delay, period, true);
    }

    /*
     * Schedule a task.
     */
    private void scheduleImpl(final TimerTask task, final long delay,
            final long period, final boolean fixed) {
        synchronized (impl) {
            if (impl.cancelled) {
                throw new IllegalStateException("Timer already cancelled.");
            }

            final long when = delay + currentTimeMillis();

            if (when < 0) {
                throw new IllegalArgumentException("Illegal execution time.");
            }

            synchronized (task.lock) {
                if (task.isScheduled()) {
                    throw new IllegalStateException(
                            "Task already scheduled or cancelled");
                }

                if (task.cancelled) {
                    throw new IllegalStateException(
                            "Task already scheduled or cancelled");
                }

                task.when = when;
                task.period = period;
                task.fixedRate = fixed;
            }

            // insert the newTask into queue
            impl.insertTask(task);
        }
    }

    /** @see schedule(TimerTask,java.util.Date) */
    public void schedule(final TimerTask task, final Instant timeUTC) {
        schedule(task, toUTCDate(timeUTC));
    }

    /** @see schedule(TimerTask,java.util.Date) */
    public void schedule(final TimerTask task, final ZonedDateTime dateTime) {
        schedule(task, toUTCDate(dateTime));
    }

    /** @see schedule(TimerTask,java.util.Date) */
    public void schedule(final TimerTask task, final LocalDateTime dateTime) {
        schedule(task, toUTCDate(dateTime));
    }

    /** @see schedule(TimerTask,java.util.Date,long) */
    public void schedule(final TimerTask task, final Instant firstTimeUTC,
            final long periodMS) {
        schedule(task, toUTCDate(firstTimeUTC), periodMS);
    }

    /** @see schedule(TimerTask,java.util.Date,long) */
    public void schedule(final TimerTask task, final ZonedDateTime firstTime,
            final long periodMS) {
        schedule(task, toUTCDate(firstTime), periodMS);
    }

    /** @see schedule(TimerTask,java.util.Date,long) */
    public void schedule(final TimerTask task, final LocalDateTime firstTime,
            final long periodMS) {
        schedule(task, toUTCDate(firstTime), periodMS);
    }

    /** @see schedule(TimerTask,java.util.Date,long) */
    public void scheduleNS(final TimerTask task, final Date firstTimeUTC,
            final long periodNS) {
        schedule(task, firstTimeUTC, periodNS / MS2NS);
    }

    /** @see schedule(TimerTask,java.util.Date,long) */
    public void scheduleNS(final TimerTask task, final Instant firstTimeUTC,
            final long periodNS) {
        schedule(task, toUTCDate(firstTimeUTC), periodNS / MS2NS);
    }

    /** @see schedule(TimerTask,java.util.Date,long) */
    public void scheduleNS(final TimerTask task, final ZonedDateTime firstTime,
            final long periodNS) {
        schedule(task, toUTCDate(firstTime), periodNS / MS2NS);
    }

    /** @see schedule(TimerTask,java.util.Date,long) */
    public void scheduleNS(final TimerTask task, final LocalDateTime firstTime,
            final long periodNS) {
        schedule(task, toUTCDate(firstTime), periodNS / MS2NS);
    }

    /** @see schedule(TimerTask,long) */
    public void scheduleNS(final TimerTask task, final long delayNS) {
        schedule(task, delayNS / MS2NS);
    }

    /** @see schedule(TimerTask,long,long) */
    public void scheduleNS(final TimerTask task, final long delayNS,
            final long periodNS) {
        schedule(task, delayNS / MS2NS, periodNS / MS2NS);
    }

    /** @see scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    public void scheduleAtFixedRate(final TimerTask task,
            final Instant firstTimeUTC, final long periodMS) {
        scheduleAtFixedRate(task, toUTCDate(firstTimeUTC), periodMS);
    }

    /** @see scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    public void scheduleAtFixedRate(final TimerTask task,
            final ZonedDateTime firstTime, final long periodMS) {
        scheduleAtFixedRate(task, toUTCDate(firstTime), periodMS);
    }

    /** @see scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    public void scheduleAtFixedRate(final TimerTask task,
            final LocalDateTime firstTime, final long periodMS) {
        scheduleAtFixedRate(task, toUTCDate(firstTime), periodMS);
    }

    /** @see scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    public void scheduleAtFixedRateNS(final TimerTask task,
            final Date firstTimeUTC, final long periodNS) {
        scheduleAtFixedRate(task, firstTimeUTC, periodNS / MS2NS);
    }

    /** @see scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    public void scheduleAtFixedRateNS(final TimerTask task,
            final Instant firstTimeUTC, final long periodNS) {
        scheduleAtFixedRate(task, toUTCDate(firstTimeUTC), periodNS / MS2NS);
    }

    /** @see scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    public void scheduleAtFixedRateNS(final TimerTask task,
            final ZonedDateTime firstTime, final long periodNS) {
        scheduleAtFixedRate(task, toUTCDate(firstTime), periodNS / MS2NS);
    }

    /** @see scheduleAtFixedRate(TimerTask,java.util.Date,long) */
    public void scheduleAtFixedRateNS(final TimerTask task,
            final LocalDateTime firstTime, final long periodNS) {
        scheduleAtFixedRate(task, toUTCDate(firstTime), periodNS / MS2NS);
    }

    /** @see scheduleAtFixedRate(TimerTask,long,long) */
    public void scheduleAtFixedRateNS(final TimerTask task, final long delayNS,
            final long periodNS) {
        scheduleAtFixedRate(task, delayNS / MS2NS, periodNS / MS2NS);
    }
}
