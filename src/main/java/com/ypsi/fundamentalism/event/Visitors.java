package com.ypsi.fundamentalism.event;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.objectweb.asm.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Visitors {
    public static class SpellAnalysisVisitor extends ClassVisitor {
        private final Set<String> detectedCategories = new HashSet<>();
        private final Set<String> entityClassesToAnalyze = new HashSet<>();
        private final Set<String> analyzedClasses = new HashSet<>();

        public SpellAnalysisVisitor() {
            super(Opcodes.ASM9);
        }

        public void analyzeType(String internalClassName) {

            if (internalClassName == null || analyzedClasses.contains(internalClassName) ) return;

            analyzedClasses.add(internalClassName);

            if (isProjectile(internalClassName)) detectedCategories.add("createsProjectile");
            if (isAoeEntity(internalClassName)) detectedCategories.add("createsAoeEntity");
            if (isImpulseData(internalClassName)) detectedCategories.add("usesImpulseCastData");

            if(isEntityClass(internalClassName)) {
                detectedCategories.add("createsEntity");
                entityClassesToAnalyze.add(internalClassName);
                analyzeChildEntity(internalClassName);
            }
        }

        public void analyzeChildEntity(String internalClassName) {
            try {
                //System.out.println(" Analyzing Children C: " + internalClassName);

                ClassReader classReader = new ClassReader(internalClassName);
                ChildEntityAnalyzer childVisitor = new ChildEntityAnalyzer();
                classReader.accept(childVisitor, 0);

                Set<String> childCats = childVisitor.getDetectedCategories();
                //System.out.println(" RESULT: " + internalClassName + " -> " + childCats);

                detectedCategories.addAll(childCats);

            } catch (Exception e) {
                System.out.println(" ERROR " + internalClassName + ": " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if ("getRecastCount".equals(name) && descriptor.equals("(ILnet/minecraft/world/entity/LivingEntity;)I")) {
                detectedCategories.add("hasRecasts");
            }
            return new MethodAnalyzer();
        }

        public class MethodAnalyzer extends MethodVisitor {
            public MethodAnalyzer() {
                super(Opcodes.ASM9);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                var categoryMapping = Map.ofEntries(
                        Map.entry("shoot", "usesShoot"),
                        Map.entry("initSummon", "usesSummon"),
                        Map.entry("preCastTargetHelper", "usesTargeting"),
                        Map.entry("handleSpellTeleport", "usesTeleport"),
                        Map.entry("teleportTo", "usesTeleport"),
                        Map.entry("addEffect", "usesAddEffect"),
                        Map.entry("addFreshEntity", "createsEntity"),
                        Map.entry("raycastForEntity", "usesRaycast"),
                        Map.entry("getTargetBlock", "usesRaycast"),
                        Map.entry("doPostAttackEffects","usesPotentiation"),
                        Map.entry("setDeltaMovement","usesImpulseCastData"),
                        Map.entry("getWeaponDamage", "usesPotentiation")
                );

                String category = categoryMapping.get(name);
                if (category != null && matchesOwner(owner, name)) {
                    detectedCategories.add(category);
                }
            }

            @Override
            public void visitTypeInsn(int opcode, String type) {
                if (opcode == Opcodes.NEW || opcode == Opcodes.INSTANCEOF) {
                    analyzeType(type);
                }
                if (opcode == Opcodes.INSTANCEOF && type.contains("IMagicSummon")) {
                    detectedCategories.add("usesSummon");
                }
                if (opcode == Opcodes.NEW && type.contains("PortalData")){
                    detectedCategories.add("usesTeleport");
                }
                if (type.contains("SpellHealEvent")) {
                    detectedCategories.add("usesHealing");
                }
            }
            private boolean matchesOwner(String owner, String methodName) {
                return switch (methodName) {
                    case "initSummon" -> owner.contains("SummonManager");
                    case "preCastTargetHelper", "handleSpellTeleport", "raycastForEntity", "getTargetBlock", "getWeaponDamage" -> owner.contains("Utils");
                    case "addEffect" -> owner.contains("LivingEntity") || owner.contains("Entity");
                    case "doPostAttackEffects" -> owner.contains("EnchantmentHelper");
                    default -> true;
                };
            }

        }

        private class ChildEntityAnalyzer extends ClassVisitor {
            private final Set<String> childDetectedCategories = new HashSet<>();

            public ChildEntityAnalyzer() {
                super(Opcodes.ASM9);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM9) {

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                        if ("addEffect".equals(name)) {
                            childDetectedCategories.add("usesAddEffect");
                        }
                        if ("heal".equals(name)) {
                            childDetectedCategories.add("usesHealing");
                        }
                        if ("addFreshEntity".equals(name)) {
                            childDetectedCategories.add("createsEntity");
                        }
                        if ("doPostAttackEffectsWithItemSource".equals(name)) {
                            childDetectedCategories.add("usesPotentiation");
                        }
                    }

                    @Override
                    public void visitTypeInsn(int opcode, String type) {
                        if (opcode == Opcodes.NEW) {
                            if (isAoeEntity(type)) {
                                childDetectedCategories.add("createsAoeEntity");
                            }
                            if (type.contains("SpellHealEvent")) {
                                childDetectedCategories.add("usesHealing");
                            }
                            if (type.contains("MobEffectInstance") || type.contains("EffectInstance") || type.contains("PotionEffect")) {
                                childDetectedCategories.add("usesAddEffect");
                            }
                        }
                    }

                };
            }

            public Set<String> getDetectedCategories() {
                return childDetectedCategories;
            }
        }

        private boolean isProjectile(String internalName) {
            return checkClassHierarchy(internalName, "net.minecraft.world.entity.projectile.Projectile");
        }

        private boolean isAoeEntity(String internalName) {
            return internalName.toUpperCase().contains("AOE") || checkClassHierarchy(internalName, "io.redspace.ironsspellbooks.entity.spells.AoeEntity");
        }

        private boolean isImpulseData(String internalName) {
            return internalName.contains("Impulse") || checkClassHierarchy(internalName, "ImpulseCastData");
        }

        private boolean isEntityClass(String internalName) {
            if (internalName == null || internalName.contains("java/lang") || internalName.contains("net/minecraft")) {
                return false;
            }
            return checkClassHierarchy(internalName, "net.minecraft.world.entity.Entity");
        }

        private boolean checkClassHierarchy(String internalName, String targetClassName) {
            if (internalName == null) return false;
            try {
                String normalName = internalName.replace('/', '.');
                Class<?> clazz = Class.forName(normalName, false, getClass().getClassLoader());

                while (clazz != null && clazz != Object.class) {
                    if (clazz.getName().equals(targetClassName)) return true;
                    clazz = clazz.getSuperclass();
                }
            } catch (Exception e) {

            }
            return false;
        }
        public Set<String> getDetectedCategories() { return detectedCategories; }
        public Set<String> getEntityClassesToAnalyze() { return entityClassesToAnalyze; }
    }
}
