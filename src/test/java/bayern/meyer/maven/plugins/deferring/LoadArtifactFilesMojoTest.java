package bayern.meyer.maven.plugins.deferring;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LoadArtifactFilesMojoTest {

	private static final String MAIN_FILENAME = "mainFile.jar";
	private static final String ARTIFACTS_TYPE_1 = "type1";
	private static final String ARTIFACTS_TYPE_2 = "type2";
	private static final String ARTIFACT_CLASSIFIER_1 = "classifier1";
	private static final String ARTIFACT_CLASSIFIER_2 = "classifier2";
	private static final String ARTIFACT_FILENAME_2 = "attachedArtifact2.xml";
	private static final String ARTIFACT_FILENAME_1 = "attachedArtifact1.txt";

	private LoadArtifactFilesMojo loadArtifactFilesMojo;
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
		loadArtifactFilesMojo = new LoadArtifactFilesMojo();
		loadArtifactFilesMojo.setMavenProject(mockedMavenProject);
		loadArtifactFilesMojo.setMavenProjectHelper(mockedMavenProjectHelper);
	}

	@Test
	public void testSuccessfullLoad() throws IOException, MojoExecutionException {
		// Prepare JSON File
		File deferringJsonFile = testFolder.newFile(LoadArtifactFilesMojo.DEFERRING_JSON_FILENAME);
		try (FileOutputStream deferringJsonFileStream = new FileOutputStream(deferringJsonFile)) {
			IOUtils.copy(
					LoadArtifactFilesMojoTest.class.getResourceAsStream(LoadArtifactFilesMojo.DEFERRING_JSON_FILENAME),
					deferringJsonFileStream);
		}
		
		// Prepare mocks
		when(mockedMavenProject.getBuild().getDirectory()).thenReturn(testFolder.getRoot().getAbsolutePath().toString());
		when(mockedMavenProject.getArtifact().getFile()).thenReturn(null);
		when(mockedMavenProject.getAttachedArtifacts()).thenReturn(Collections.EMPTY_LIST);
		
		// Call method under test
		loadArtifactFilesMojo.execute();
		
		// Verify expectations
		verify(mockedMavenProject.getArtifact()).setFile(argThat(file -> MAIN_FILENAME.equals(file.getName())));
		verify(mockedMavenProjectHelper).attachArtifact(same(mockedMavenProject), eq(ARTIFACTS_TYPE_1), eq(ARTIFACT_CLASSIFIER_1), argThat(file -> ARTIFACT_FILENAME_1.equals(file.getName())));
		verify(mockedMavenProjectHelper).attachArtifact(same(mockedMavenProject), eq(ARTIFACTS_TYPE_2), eq(ARTIFACT_CLASSIFIER_2), argThat(file -> ARTIFACT_FILENAME_2.equals(file.getName())));
	}

	@Test
	public void testArtifactFileAlreadySet() {
		// Prepare mocks
		when(mockedMavenProject.getArtifact().getFile()).thenReturn(testFolder.getRoot());

		// Call method under test
		assertThatThrownBy(loadArtifactFilesMojo::execute).isInstanceOf(MojoExecutionException.class).hasMessageContaining("Project had already an artifact file set");
	}

	@Test
	public void testAttachedArtifactsAlreadySet() {
		// Prepare mocks
		when(mockedMavenProject.getArtifact().getFile()).thenReturn(null);
		when(mockedMavenProject.getAttachedArtifacts()).thenReturn(Arrays.asList(mock(Artifact.class)));

		// Call method under test
		assertThatThrownBy(loadArtifactFilesMojo::execute).isInstanceOf(MojoExecutionException.class).hasMessageContaining("Project had already attachedArtifacts set");
	}

	@Test
	public void testNoJsonFileExisting() throws MojoExecutionException {
		// Prepare mocks
		when(mockedMavenProject.getBuild().getDirectory()).thenReturn(testFolder.getRoot().getAbsolutePath().toString());
		when(mockedMavenProject.getArtifact().getFile()).thenReturn(null);
		when(mockedMavenProject.getAttachedArtifacts()).thenReturn(Collections.EMPTY_LIST);

		// Call method under test
		loadArtifactFilesMojo.execute();
		
		// Verify expectations
		verify(mockedMavenProject.getArtifact(), never()).setFile(any());
		verify(mockedMavenProjectHelper, never()).attachArtifact(any(), any(), any(), any());
	}
}
