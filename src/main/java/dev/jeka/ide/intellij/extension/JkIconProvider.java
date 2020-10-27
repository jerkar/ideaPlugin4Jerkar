package dev.jeka.ide.intellij.extension;

import com.intellij.ide.IconProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import dev.jeka.ide.intellij.common.Constants;
import dev.jeka.ide.intellij.common.ModuleUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class JkIconProvider extends IconProvider {

    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        if (element instanceof PsiDirectory) {
            PsiDirectory psiDirectory = (PsiDirectory) element;
            if (psiDirectory.getName().equals("jeka")) {
                VirtualFile dir = psiDirectory.getVirtualFile();
                if (ModuleUtils.isExistingModuleRoot(element.getProject(), dir.getParent())) {
                    return Constants.JkIcons.JEKA_GREYLIGHT_NAKED;
                }
            }
        }
        return null;
    }
}