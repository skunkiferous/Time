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
 * The LogicalScheduler is a Scheduler that can also schedule tasks based on
 * the logical application time.
 *
 * @author monster
 */
public interface LogicalScheduler extends Scheduler, LogicalTimeSource {

    /** Schedules a task to be executed in delay cycles. */
    Task<LogicalTimeListener> schedule(final LogicalTimeListener task,
            final long delay);

    /** Schedules a task to be executed every period cycles, starting in delay cycles. */
    Task<LogicalTimeListener> scheduleAtFixedRate(
            final LogicalTimeListener task, final long delay, final long period);
}
