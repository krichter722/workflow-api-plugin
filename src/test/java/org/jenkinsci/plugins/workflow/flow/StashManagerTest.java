package org.jenkinsci.plugins.workflow.flow;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Failure;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import hudson.util.DirScanner;
import jenkins.model.ArtifactManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.io.IOException;
import java.io.PrintStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;


@PrepareForTest({Run.class, ArtifactManager.class})
@RunWith(PowerMockRunner.class)
public class StashManagerTest {

    @Test(expected = Failure.class)
    public void test_stash_badName() throws IOException, InterruptedException {
        // Arrange
        final Run run = mock(Run.class);
        final FilePath filePath = mockFilePath();
        final Launcher launcher = mock(Launcher.class);
        final EnvVars env = mock(EnvVars.class);
        final TaskListener listener = mock(TaskListener.class);
        final String includes = "";
        final String excludes = "";
        boolean useDefaultExcludes = false;
        boolean allowEmpty = false;

        // Act
        StashManager.stash(run, "a:b", filePath, launcher, env, listener, includes, excludes, useDefaultExcludes, allowEmpty);
    }

    @Test
    public void test_stash_stashAwareArtifactManager() throws IOException, InterruptedException {
        // Arrange
        final Run run = mock(Run.class);
        final FilePath filePath = mockFilePath();
        final Launcher launcher = mock(Launcher.class);
        final EnvVars env = mock(EnvVars.class);
        final TaskListener listener = mock(TaskListener.class);
        final String name = "ab";
        final String includes = "";
        final String excludes = "";
        boolean useDefaultExcludes = false;
        boolean allowEmpty = false;
        final StashManager.StashAwareArtifactManager artifactManager = mock(StashManager.StashAwareArtifactManager.class);
        doReturn(artifactManager).when(run).pickArtifactManager();

        // Act
        StashManager.stash(run, name, filePath, launcher, env, listener, includes, excludes, useDefaultExcludes, allowEmpty);

        // Assert
        verify(((StashManager.StashAwareArtifactManager)artifactManager)).stash(name, filePath, launcher, env, listener, includes, excludes, useDefaultExcludes, allowEmpty);
    }

    @Test
    public void test_stash_noStashAwareArtifactManager() throws IOException, InterruptedException {
        // Arrange
        final Run run = mock(Run.class);
        final FilePath filePath = mockFilePath();
        final Launcher launcher = mock(Launcher.class);
        final EnvVars env = mock(EnvVars.class);
        final TaskListener listener = mock(TaskListener.class);
        final String name = "ab";
        final String includes = "";
        final String excludes = "";
        boolean useDefaultExcludes = false;
        boolean allowEmpty = false;
        final PrintStream logger = mock(PrintStream.class);
        doReturn(null).when(run).pickArtifactManager();
        doReturn(logger).when(listener).getLogger();
        doReturn(0).when(filePath).archive(any(), any(), any(DirScanner.class));

        // Act
        StashManager.stash(run, name, filePath, launcher, env, listener, includes, excludes, useDefaultExcludes, allowEmpty);

        // Assert
        verify(listener).getLogger();
    }

    private FilePath mockFilePath() {
        final VirtualChannel virtualChannel = mock(VirtualChannel.class);
        final String remote = "";
        return new FilePath(virtualChannel, remote);
    }
}