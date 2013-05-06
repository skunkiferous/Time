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
package com.blockwithme.time.util;

import java.util.Objects;
import java.util.concurrent.Executor;

import com.blockwithme.time.Time;
import com.blockwithme.time.TimeListener;

/**
 * Allows a TimeListener task to be run on another thread, using an Executor.
 *
 * Note that the thread calling onTimeChange() on this class *does not block*
 * until the executor has executed the task (under the assumption, that it
 * is a "normal" executor, which runs the tasks asynchronously).
 *
 * @author monster
 */
public class ForkingTimeListener implements TimeListener {

    /** We need a Runnable, to run it in an Executor. */
    private static final class Wrapper implements Runnable {

        /** The real TimeListener. */
        private final TimeListener task;

        /** The clock tick. */
        private final Time time;

        /** Creates a Wrapper. */
        public Wrapper(final TimeListener theTask, final Time theTime) {
            task = Objects.requireNonNull(theTask, "theTask");
            time = Objects.requireNonNull(theTime, "theTime");
        }

        @Override
        public int hashCode() {
            return task.hashCode();
        }

        @Override
        public String toString() {
            return "ForkTimeListener.Wrapper(" + task + ")";
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            task.onTimeChange(time);
        }
    }

    /** The real TimeListener. */
    private final TimeListener task;

    /** The Executor that will run the task. */
    private final Executor executor;

    /** Creates a Fork, with the given task and Executor. */
    public ForkingTimeListener(final TimeListener theTask,
            final Executor theExecutor) {
        task = Objects.requireNonNull(theTask, "theTask");
        executor = Objects.requireNonNull(theExecutor, "theExecutor");
    }

    @Override
    public int hashCode() {
        return task.hashCode();
    }

    @Override
    public String toString() {
        return "ForkTimeListener(" + task + ")";
    }

    @Override
    public void onTimeChange(final Time time) {
        executor.execute(new Wrapper(task, time));
    }
}
