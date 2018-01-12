/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.jiguang.imuisample.util

import android.os.Handler
import android.os.Looper
import android.support.annotation.VisibleForTesting

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Global executor pools for the whole application.
 *
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
class AppExecutors @VisibleForTesting
internal constructor(private val diskIO: Executor, private val networkIO: Executor, private val mainThread: Executor) {

    constructor() : this(DiskIOThreadExecutor(), Executors.newFixedThreadPool(THREAD_COUNT),
            MainThreadExecutor()) {
    }

    fun diskIO(): Executor {
        return diskIO
    }

    fun networkIO(): Executor {
        return networkIO
    }

    fun mainThread(): Executor {
        return mainThread
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    private class ThreadExecutor : Executor {
        private val threadHandler = Handler(Looper.myLooper())

        override fun execute(runnable: Runnable) {
            threadHandler.post(runnable)
        }
    }

    companion object {

        private val THREAD_COUNT = 3
    }
}
