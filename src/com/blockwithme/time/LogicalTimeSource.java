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
 * The LogicalTimeSource represents a source for the application logical time.
 * The logical time is represented as a long, and it is expected to move
 * forward, but does not need to keep in sync with the real time. Also, in is
 * conceivable that not all parts of the application are at the same logical
 * time, within the same real-time moment.
 *
 * @author monster
 */
public interface LogicalTimeSource {
    /** Returns the logical, application, time. */
    long logicalTime();

    /** Sets the logical time. */
    void setLogicalTime(long logicalTime);

    /** Increments the logical time. */
    long incrementLogicalTime();

    /** Decrements the logical time. */
    long decrementLogicalTime();
}
