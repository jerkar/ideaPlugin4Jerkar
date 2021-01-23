package dev.jeka.ide.intellij.panel.explorer.model;

import com.intellij.openapi.module.Module;
import com.intellij.psi.*;
import dev.jeka.ide.intellij.common.PsiClassHelper;
import dev.jeka.ide.intellij.common.PsiMethodHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.util.*;

@RequiredArgsConstructor
public abstract class JekaCommandHolder implements JekaModelNode {

    @Getter
    private final JekaModelNode parent;

    @Getter
    private final PsiClass containingClass;

    private List<JekaPlugin> cachedPlugins;

    @Override
    public NodeInfo getNodeInfo() {
        return NodeInfo.simple(this, getIcon(),
                this::getName, this::getParent, this::getChildren);
    }

    protected abstract Icon getIcon();

    public abstract Module getModule();

    protected abstract String getName();

    public abstract PsiClass getCommandClass();

    protected List<JekaModelNode> getChildren() {
        PsiMethod[] methods = containingClass.getAllMethods();
        List<JekaModelNode> result = new LinkedList<>();
        result.addAll(plugins());
        result.add(new JekaFieldSet(this));
        Set<String> methodNames = new HashSet<>();
        for (PsiMethod method : methods) {
            if (PsiMethodHelper.isInstancePublicVoidNoArgsNotFromObject(method)) {
                String methodName = method.getName();
                result.add(new JekaCommand(this, methodName, method));
                methodNames.add(methodName);
            }
        }
        return result;
    }

    public List<JekaPlugin> plugins() {
        if (cachedPlugins != null) {
            return cachedPlugins;
        }
        PsiClass commandPsiClass = getContainingClass();
        PsiField[] psiFields = commandPsiClass.getAllFields();
        List<JekaPlugin> result = new LinkedList<>();
        for (PsiField psiField : psiFields) {
            PsiType psiType = psiField.getType();
            if (psiType instanceof PsiClassType) {
                PsiClassType classType = (PsiClassType) psiType;
                PsiClass psiClass = classType.resolve();
                if (PsiClassHelper.isExtendingJkPlugin(psiClass)) {
                    JekaPlugin plugin = JekaPlugin.fromPsiClass(this, psiClass);
                    result.add(plugin);
                }
            }
        }
        cachedPlugins = Collections.unmodifiableList(result);
        return result;
    }

}
