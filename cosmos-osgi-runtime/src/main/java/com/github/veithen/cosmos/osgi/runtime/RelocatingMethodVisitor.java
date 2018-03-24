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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

final class RelocatingMethodVisitor extends MethodVisitor {
    private final String from;
    private final String to;

    RelocatingMethodVisitor(MethodVisitor mv, String from, String to) {
        super(Opcodes.ASM6, mv);
        this.from = from;
        this.to = to;
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        if (owner.equals(from)) {
            owner = to;
        }
        super.visitFieldInsn(opcode, owner, name, desc);
    }
}
