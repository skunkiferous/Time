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

import com.blockwithme.time.Task;
import com.blockwithme.time.Timeline;

/**
 * Internal Timeline interface.
 *
 * @author monster
 */
public interface _Timeline extends Timeline {

    /**
     * Register a Ticker, which is called at every core clock tick.
     * It will always be called from the same thread.
     *
     * The Ticker must *never* do long, or blocking, operations!
     * This would delay all the other time listeners, and cause fluctuation
     * in the global tick period.
     */
    Task<Ticker> registerListener(final Ticker listener);

}
