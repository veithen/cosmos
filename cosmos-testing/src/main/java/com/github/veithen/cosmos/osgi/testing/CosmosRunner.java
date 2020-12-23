/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2020 Andreas Veithen
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

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.github.veithen.cosmos.osgi.runtime.CosmosRuntime;

public class CosmosRunner extends BlockJUnit4ClassRunner {
    public CosmosRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        Object object = super.createTest();
        Class<?> testClass = object.getClass();
        Class<?> clazz = testClass;
        do {
            for (Field field : clazz.getDeclaredFields()) {
                Inject injectAnnotation = field.getAnnotation(Inject.class);
                if (injectAnnotation != null) {
                    final CosmosRuntime runtime = CosmosRuntime.getInstance();
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    Object value;
                    if (type == BundleContext.class) {
                        value = FrameworkUtil.getBundle(testClass).getBundleContext();
                    } else if (type == Provider.class) {
                        final Class<?> serviceClass =
                                (Class<?>)
                                        ((ParameterizedType) field.getGenericType())
                                                .getActualTypeArguments()[0];
                        value =
                                new Provider<Object>() {
                                    @Override
                                    public Object get() {
                                        return runtime.getService(serviceClass);
                                    }
                                };
                    } else {
                        value = runtime.getService(type);
                    }
                    field.set(object, value);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != Object.class);
        return object;
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        return new RunWithCosmosRuntime(super.classBlock(notifier));
    }
}
