/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2022 Andreas Veithen
 * %%
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
 * #L%
 */
package com.github.veithen.cosmos.osgi.runtime;

import org.osgi.framework.Bundle;

/**
 * Defines the state of a {@link BundleImpl}. A bundle is initially in state {@link #LOADED} or
 * {@link #LAZY_ACTIVATE} depending on its activation policy. The following state transitions are
 * possible:
 *
 * <ul>
 *   <li>{@link #LOADED} &rarr; {@link #READY} and {@link #LAZY_ACTIVATE} &rarr; {@link #ACTIVE}:
 *       This transition occurs when a depending bundle is activated. This allows the framework to
 *       emulate lazy activation (Since it doesn't have control over the class loader, it cannot
 *       fully implement this feature).
 *   <li>({@link #LOADED}, {@link #LAZY_ACTIVATE}, {@link #READY}) &rarr; {@link #ACTIVE}: Triggered
 *       by {@link Bundle#start()}.
 * </ul>
 */
enum BundleState {
    /** Initial state for a bundle that is not configured for lazy activation. */
    LOADED(Bundle.RESOLVED),

    /**
     * Initial state for a bundle configured with lazy activation policy. This is the case if the
     * bundle has one of the following headers:
     *
     * <ul>
     *   <li><tt>Bundle-ActivationPolicy</tt> with a value of <tt>lazy</tt>.
     *   <li><tt>Eclipse-LazyStart</tt> with a value of <tt>true</tt>.
     *   <li><tt>Eclipse-AutoStart</tt> with a value of <tt>true</tt>.
     * </ul>
     */
    LAZY_ACTIVATE(Bundle.STARTING),

    /**
     * The bundle is not active, but is ready for code execution. This is the case if all
     * dependencies (including transitive dependencies) are in state {@link #READY} or {@link
     * #ACTIVE}.
     */
    READY(Bundle.RESOLVED),

    /** The bundle is starting, i.e. the bundle activator is about to be invoked. */
    STARTING(Bundle.STARTING),

    ACTIVE(Bundle.ACTIVE);

    private final int osgiState;

    private BundleState(int osgiState) {
        this.osgiState = osgiState;
    }

    int getOsgiState() {
        return osgiState;
    }
}
