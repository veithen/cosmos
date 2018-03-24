/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2018 Andreas Veithen
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
package com.github.veithen.cosmos.osgi.testing;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.github.veithen.cosmos.osgi.runtime.Runtime;

public class CosmosRunner extends BlockJUnit4ClassRunner {
    private Class<?> klass;
    private BundleContext bundleContext;

    public CosmosRunner(Class<?> klass) throws InitializationError {
        super(klass);
        this.klass = klass;
    }

    private BundleContext getBundleContext() {
        if (bundleContext == null) {
            bundleContext = FrameworkUtil.getBundle(klass).getBundleContext();
        }
        return bundleContext;
    }

    @Override
    protected Object createTest() throws Exception {
        // Always execute this to force initialization of the runtime.
        final Runtime runtime = Runtime.getInstance();

        Object object = super.createTest();
        Class<?> clazz = object.getClass();
        do {
            for (Field field : object.getClass().getDeclaredFields()) {
                Inject injectAnnotation = field.getAnnotation(Inject.class);
                if (injectAnnotation != null) {
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    if (type == BundleContext.class) {
                        field.set(object, getBundleContext());
                    } else if (type == Provider.class) {
                        final Class<?> serviceClass = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                        field.set(object, new Provider<Object>() {
                            @Override
                            public Object get() {
                                return runtime.getService(serviceClass);
                            }
                        });
                    }
                }
            }
        } while ((clazz = clazz.getSuperclass()) != Object.class);
        return object;
    }
}
