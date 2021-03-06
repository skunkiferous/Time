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
 * A task dependent on the logical application time.
 *
 * The time listener must *never* do long running, or blocking, operations!
 * This would delay all the other time listeners, and cause fluctuation
 * in the global tick period.
 *
 * @author monster
 */
public interface TimeListener {
    /**
     * Called when the logical time changes.
     * Will be null, to signify closure of the timeline.
     */
    void onTimeChange(Time time);
}
