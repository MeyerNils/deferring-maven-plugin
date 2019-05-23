package bayern.meyer.maven.plugins.deferring;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LoadVersionMojoTest {

	private static final String ARTIFACT_VERSION = "1.0.0-SNAPSHOT";

	private LoadVersionMojo loadVersionMojo;
	private MavenProject mockedMavenProject;
	private MavenProjectHelper mockedMavenProjectHelper;

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		// Prepare mocks
		mockedMavenProject = mock(MavenProject.class, RETURNS_DEEP_STUBS);
		mockedMavenProjectHelper = mock(MavenProjectHelper.class);

		// Prepare class under test
		loadVersionMojo = new LoadVersionMojo();
		loadVersionMojo.setMavenProject(mockedMavenProject);
		loadVersionMojo.setMavenProjectHelper(mockedMavenProjectHelper);
	}

	@Test
	public void testSuccessfullLoad() throws IOException, MojoExecutionException {
		// Prepare JSON File
		File deferringJsonFile = testFolder.newFile(LoadArtifactFilesMojo.DEFERRING_JSON_FILENAME);
		try (FileOutputStream deferringJsonFileStream = new FileOutputStream(deferringJsonFile)) {
			IOUtils.copy(
					LoadVersionMojoTest.class.getResourceAsStream(LoadArtifactFilesMojo.DEFERRING_JSON_FILENAME),
					deferringJsonFileStream);
		}
		
		// Prepare mocks
		when(mockedMavenProject.getBuild().getDirectory()).thenReturn(testFolder.getRoot().getAbsolutePath().toString());
		when(mockedMavenProject.getArtifact().getFile()).thenReturn(null);
		when(mockedMavenProject.getAttachedArtifacts()).thenReturn(Collections.EMPTY_LIST);
		
		// Call method under test
		loadVersionMojo.execute();
		
		// Verify expectations
		verify(mockedMavenProject.getArtifact()).setVersion(ARTIFACT_VERSION);
		verify(mockedMavenProject.getArtifact()).setVersionRange(eq(VersionRange.createFromVersion(ARTIFACT_VERSION)));
		verify(mockedMavenProject.getModel()).setVersion(ARTIFACT_VERSION);
	}

	@Test
	public void testNoJsonFileExisting() throws MojoExecutionException {
		// Prepare mocks
		when(mockedMavenProject.getBuild().getDirectory()).thenReturn(testFolder.getRoot().getAbsolutePath().toString());
		when(mockedMavenProject.getArtifact().getFile()).thenReturn(null);
		when(mockedMavenProject.getAttachedArtifacts()).thenReturn(Collections.EMPTY_LIST);

		// Call method under test
		loadVersionMojo.execute();
		
		// Verify expectations
		verify(mockedMavenProject.getArtifact(), never()).setFile(any());
		verify(mockedMavenProjectHelper, never()).attachArtifact(any(), any(), any(), any());
	}
}
