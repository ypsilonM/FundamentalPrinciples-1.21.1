package com.ypsi.fundamentalism.event;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpellMethodAnalyzer {
    private static final Map<Class<?>, Boolean> CACHE = new ConcurrentHashMap<>();
    private static final String TARGET_METHOD_NAME = "getSpellPower";

    public static boolean usesGetSpellPower(AbstractSpell spell) {
        return CACHE.computeIfAbsent(spell.getClass(), clazz -> {
            try {
                return analyzeClassBytecode(clazz);
            } catch (Exception e) {
                System.err.println("Error analyzing " + clazz.getName() + ": " + e.getMessage());
                return false;
            }
        });
    }

    private static boolean analyzeClassBytecode(Class<?> clazz) throws Exception {
        String className = clazz.getName().replace('.', '/');
        InputStream classStream = clazz.getClassLoader()
                .getResourceAsStream(className + ".class");

        if (classStream == null) return false;

        ClassReader reader = new ClassReader(classStream);
        SpellMethodFinder finder = new SpellMethodFinder();
        reader.accept(finder, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        return finder.foundGetSpellPower();
    }

    private static class SpellMethodFinder extends ClassVisitor {
        private boolean foundGetSpellPower = false;

        public SpellMethodFinder() {
            super(Opcodes.ASM9);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return new MethodVisitor(Opcodes.ASM9) {
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (name.equals(TARGET_METHOD_NAME)) {
                        foundGetSpellPower = true;
                    }
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }

                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            };
        }
        public boolean foundGetSpellPower() {
            return foundGetSpellPower;
        }
    }
}
