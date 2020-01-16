/*
 * Copyright 2018-2019 original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.jeka.ide.intellij;

import b.b.b.R;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import dev.jeka.core.api.java.project.JkJavaProjectIde;
import dev.jeka.core.api.java.project.JkJavaProjectIdeSupplier;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.system.JkProcess;
import dev.jeka.core.api.utils.JkUtilsSystem;
import dev.jeka.core.tool.JkCommands;
import dev.jeka.core.tool.JkPlugin;
import dev.jeka.ide.intellij.platform.ShellConfigurationProducer;
import sun.management.AgentConfigurationError;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Jerome Angibaud
 */
public class CmdJekaDoer implements JekaDoer {

    static final CmdJekaDoer INSTANCE = new CmdJekaDoer();

    private ConsoleView view = null;
    private ToolWindow window = null;

    static {
        JkLog.registerHierarchicalConsoleHandler();
    }

    public void generateIml(Project project, Path moduleDir, String qualifiedClassName) {
        GeneralCommandLine cmd = new GeneralCommandLine(jekaCmd(moduleDir));
        cmd.addParameter("intellij#iml");
        cmd.setWorkDirectory(moduleDir.toFile());
        if (qualifiedClassName != null) {
            cmd.addParameter("-CC=" + qualifiedClassName);
        }
        start(cmd, project);
    }

    public void scaffoldModule(Project project, Path moduleDir) {
        GeneralCommandLine cmd = new GeneralCommandLine(jekaCmd(moduleDir));
        cmd.addParameters("scaffold#run", "scaffold#wrap", "java#", "intellij#iml" );
        cmd.setWorkDirectory(moduleDir.toFile());
        start(cmd, project);
    }


    private void start(GeneralCommandLine cmd, Project project) {
        OSProcessHandler handler = null;
        try {
            handler = new OSProcessHandler(cmd);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        if (view == null) {
            TextConsoleBuilderFactory factory = TextConsoleBuilderFactory.getInstance();
            TextConsoleBuilder builder = factory.createBuilder(project);
            view = builder.getConsole();
        }
        view.attachToProcess(handler);
        view.clear();
        handler.startNotify();
        if (window == null) {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            window = manager.registerToolWindow("Jeka console", true, ToolWindowAnchor.BOTTOM);
            final ContentManager contentManager = window.getContentManager();
            Content content = contentManager
                    .getFactory()
                    .createContent(view.getComponent(), "", false);
            contentManager.addContent(content);
        }
        window.show(() -> {});
    }

    private String jekaCmd(Path moduleDir) {
        if (JkUtilsSystem.IS_WINDOWS) {
            return Files.exists(moduleDir.resolve("jekaw.bat")) ?
                    moduleDir.toAbsolutePath().resolve("jekaw.bat").toString() : "jeka.bat";
        }
        return Files.exists(moduleDir.resolve("jekaw")) ? "./jekaw" : "jeka";
    }

/*

   public void generateImlOld(Path moduleDir, String qualifiedClassName) {
        JkProcess iml = jeka(moduleDir)
                .andParams("intellij#iml").withWorkingDir(moduleDir).withLogCommand(true).withLogOutput(true);
        if (qualifiedClassName != null) {
            iml = iml.andParams("-CC=" + qualifiedClassName);
        }
        int result = iml.runSync();
        if (result != 0) {
            iml.andParams("-CC=dev.jeka.core.tool.JkCommands", "java#").withFailOnError(true).runSync();
        }
    }

    public void generateIml3(Project project, Path moduleDir, String qualifiedClassName) {
        RunManager runManager = RunManager.getInstance(project);
        ConfigurationType type = ConfigurationTypeUtil.findConfigurationType("ShConfigurationType");
        String name = name(moduleDir);

              List<RunConfiguration> runConfigurations = runManager.getAllConfigurationsList();
        RunConfiguration shellConfigurationTemplate = runConfigurations.stream()
                .filter(runConfiguration -> runManager.isTemplate(runConfiguration))
                .filter(runConfiguration -> runConfiguration.getName().equals("Shell Script"))
                .findFirst().get();


        RunnerAndConfigurationSettings settings = runManager.createConfiguration(name, type.getClass());
        System.out.println(settings);
    }




    private static String name(Path moduleDir) {
        return "[Jeka " + moduleDir.getFileName() + "] intellij#iml";
    }


    private JkJavaProjectIde findProjectIde(JkCommands jkCommands) {
        if (jkCommands instanceof JkJavaProjectIdeSupplier) {
            JkJavaProjectIdeSupplier javaProjectIdeSupplier = (JkJavaProjectIdeSupplier) jkCommands;
            return javaProjectIdeSupplier.getJavaProjectIde();
        }
        for (JkPlugin plugin : jkCommands.getPlugins().getAll()) {
            if (plugin instanceof JkJavaProjectIdeSupplier) {
                JkJavaProjectIdeSupplier javaProjectIdeSupplier = (JkJavaProjectIdeSupplier) plugin;
                return javaProjectIdeSupplier.getJavaProjectIde();
            }
        }
        return null;
    }

    */



}
