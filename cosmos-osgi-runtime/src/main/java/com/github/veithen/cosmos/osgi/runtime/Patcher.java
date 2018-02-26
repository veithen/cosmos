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
package com.github.veithen.cosmos.osgi.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

final class Patcher {
    static void patch() throws CosmosException {
        try {
            ClassLoader classLoader = Patcher.class.getClassLoader();
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            ClassNode classNode;
            try (InputStream in = classLoader.getResourceAsStream("com/github/veithen/cosmos/osgi/runtime/FrameworkUtil.class")) {
                classNode = new ClassNode();
                new ClassReader(in).accept(classNode, 0);
            }
            ClassWriter classWriter;
            try (InputStream in = classLoader.getResourceAsStream("org/osgi/framework/FrameworkUtil.class")) {
                ClassReader classReader = new ClassReader(in);
                classWriter = new ClassWriter(classReader, 0);
                classReader.accept(new MethodInjector(classWriter, classNode.methods), 0);
            }
            byte[] bytes = classWriter.toByteArray();
            defineClass.invoke(classLoader, "org.osgi.framework.FrameworkUtil", bytes, 0, bytes.length);
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            throw new CosmosException("Failed to patch FrameworkUtil", ex);
        }
    }
}
