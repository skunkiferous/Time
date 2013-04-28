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
 * The clock synchronizers are used to find the difference between the current
 * system clock, and some (relatively) reliable, external source (normally some
 * Internet server). It is used to allow the ClockService to return approximately
 * correct time values, despite large errors in the settings of the local
 * system clock.
 *
 * ClockSynchronizers are provided by the default API implementation, and do
 * not normally need to be implemented by API users.
 *
 * If a clock service is created without clock synchronizers, it will be forced
 * to use the (possibly inaccurate)  time of the local host.
 *
 * @author monster
 */
public interface ClockSynchronizer {

    /**
     * Returns the expected precision, in milliseconds.
     * It is only used for "sorting purpose*.
     */
    long expectedPrecision();

    /**
     * Computes the difference between the local system clock, and some
     * reliable external source, in milliseconds. It should throw an Exception
     * on failure.
     */
    long getLocalToUTCTimeOffset() throws Exception;

}
