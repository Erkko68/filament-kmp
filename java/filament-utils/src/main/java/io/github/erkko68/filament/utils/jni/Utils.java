/*
 * Copyright (C) 2020 The Android Open Source Project
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

package io.github.erkko68.filament.utils.jni;

import io.github.erkko68.filament.jni.Filament;

public class Utils {
    static {
        Filament.init();
        System.loadLibrary("filament-utils-jni");
    }

    /**
     * Initializes the utils JNI layer. Internal use only.
     */
    public static void init() {
        // The static block handles initialization.
    }
}
