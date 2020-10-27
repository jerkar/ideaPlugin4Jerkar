package dev.jeka.ide.intellij.extension;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.*;
import dev.jeka.ide.intellij.action.JekaRunMethodAction;
import dev.jeka.ide.intellij.common.ClassUtils;
import dev.jeka.ide.intellij.common.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;


public class JekaRunLineMarkerContributor extends RunLineMarkerContributor {

    @Nullable
    @Override
    public Info getInfo(@NotNull PsiElement psiEl) {
      ///  if (Registry.is("ide.jvm.run.marker")) return null;
        if (!isIdentifier(psiEl)) {
            return null;
        }
        PsiElement psiParentEl = psiEl.getParent();
        if (! (psiParentEl instanceof PsiMethod)) {
            return null;
        }
        PsiMethod psiMethod = (PsiMethod) psiParentEl;
        PsiClass psiClass = psiMethod.getContainingClass();
        if (!ClassUtils.isExtendingJkCommandSet(psiClass)) {
            return null;
        }
        if (psiMethod.hasParameters() || psiMethod.isConstructor()
                || psiMethod.getModifierList().hasExplicitModifier("static")
                || !psiMethod.getModifierList().hasExplicitModifier("public")
                || !psiMethod.getReturnType().equals(PsiType.VOID)) {
            return null;
        }
        final AnAction[] actions = new AnAction[] {JekaRunMethodAction.RUN_JEKA_INSTANCE,
                JekaRunMethodAction.DEBUG_JEKA_INSTANCE};
        Function<PsiElement, String> tooltipProvider = el -> "Run '" + el.getText() + "' as Jeka command";
        return new Info(Constants.JkIcons.JEKA_RUN, actions, tooltipProvider);
    }

    protected boolean isIdentifier(PsiElement e) {
        return e instanceof PsiIdentifier;
    }
}