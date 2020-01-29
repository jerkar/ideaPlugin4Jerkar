package dev.jeka.ide.intellij;

import com.intellij.execution.*;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiIdentifier;
import org.jetbrains.annotations.NotNull;


public class JekaRunMethodAction extends AnAction {

    public static final JekaRunMethodAction RUN_JEKA_INSTANCE = new JekaRunMethodAction(false);

    public static final JekaRunMethodAction DEBUG_JEKA_INSTANCE = new JekaRunMethodAction(true);

    private final boolean debug;

    private JekaRunMethodAction(boolean debug) {
        super((debug ? "Debug" : "Run") +   " as Jeka command",
                (debug ? "Debug" : "Run") +   " as Jeka command",
                debug ? AllIcons.Actions.StartDebugger : AllIcons.RunConfigurations.TestState.Run);
        this.debug = debug;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        PsiLocation<PsiIdentifier> location = (PsiLocation<PsiIdentifier>)
                event.getDataContext().getData(Location.DATA_KEY);
        String methodName = location.getPsiElement().getText();
        ApplicationConfiguration configuration = new ApplicationConfiguration("toto", project);
        configuration.setWorkingDirectory("$MODULE_WORKING_DIR$");
        configuration.setMainClassName("dev.jeka.core.tool.Main");
        configuration.setModule(Utils.getModule(event));
        configuration.setProgramParameters(methodName);
        RunnerAndConfigurationSettings runnerAndConfigurationSettings =
                RunManager.getInstance(project).createConfiguration(configuration, configuration.getFactory());
        Executor executor = debug ?  DefaultDebugExecutor.getDebugExecutorInstance() :
                DefaultRunExecutor.getRunExecutorInstance();
        ProgramRunnerUtil.executeConfiguration(runnerAndConfigurationSettings, executor);
    }

}