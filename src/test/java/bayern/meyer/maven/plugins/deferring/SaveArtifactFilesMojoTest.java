package bayern.meyer.maven.plugins.deferring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import bayern.meyer.maven.plugins.deferring.model.AttachedArtifact;
import bayern.meyer.maven.plugins.deferring.model.AttachedArtifactsHolder;

public class SaveArtifactFilesMojoTest {

	private static final String MAIN_FILENAME = "mainFile.jar";
	private static final String ARTIFACTS_TYPE_1 = "type1";
	private static final String ARTIFACTS_TYPE_2 = "type2";
	private static final String ARTIFACT_CLASSIFIER_1 = "classifier1";
	private static final String ARTIFACT_CLASSIFIER_2 = "classifier2";
	private static final String ARTIFACT_FILENAME_2 = "attachedArtifact2.xml";
	private static final String ARTIFACT_FILENAME_1 = "attachedArtifact1.txt";

	private final class AbsolutePathComparator implements Comparator<File> {
		@Override
		public int compare(File o1, File o2) {
			return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
		}
	}

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	private SaveArtifactFilesMojo saveArtifactFilesMojo;
	private MavenProject mockedMavenProject;
	private ObjectMapper objectMapper;

	@Before
	public void setUp() throws Exception {
		// Prepare helpers
		objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
		// Prepare mocks
		mockedMavenProject = mock(MavenProject.class, RETURNS_DEEP_STUBS);

		// Prepare class under test
		saveArtifactFilesMojo = new SaveArtifactFilesMojo();
		saveArtifactFilesMojo.setMavenProject(mockedMavenProject);
	}

	@Test
	public void testSuccssfullSave()
			throws MojoExecutionException, JsonParseException, JsonMappingException, IOException {
		File mainFile = new File(MAIN_FILENAME);

		File attachedArtifact1File = new File(ARTIFACT_FILENAME_1);
		Artifact attachedArtifact1 = mock(Artifact.class);
		when(attachedArtifact1.getFile()).thenReturn(attachedArtifact1File);
		when(attachedArtifact1.getClassifier()).thenReturn(ARTIFACT_CLASSIFIER_1);
		when(attachedArtifact1.getType()).thenReturn(ARTIFACTS_TYPE_1);

		File attachedArtifact2File = new File(ARTIFACT_FILENAME_2);
		Artifact attachedArtifact2 = mock(Artifact.class);
		when(attachedArtifact2.getFile()).thenReturn(attachedArtifact2File);
		when(attachedArtifact2.getClassifier()).thenReturn(ARTIFACT_CLASSIFIER_2);
		when(attachedArtifact2.getType()).thenReturn(ARTIFACTS_TYPE_2);

		when(mockedMavenProject.getArtifact().getFile()).thenReturn(mainFile);
		when(mockedMavenProject.getAttachedArtifacts()).thenReturn(Arrays.asList(attachedArtifact1, attachedArtifact2));

		when(mockedMavenProject.getBuild().getDirectory())
				.thenReturn(testFolder.getRoot().getAbsolutePath().toString());

		LocalDateTime before = LocalDateTime.now();

		// Call method under test
		saveArtifactFilesMojo.execute();

		// Verify expectations
		File deferringJsonFile = new File(testFolder.getRoot(), SaveArtifactFilesMojo.DEFERRING_JSON_FILENAME);
		assertThat(deferringJsonFile).exists();

		AttachedArtifactsHolder actualWrittenValue = objectMapper.readValue(deferringJsonFile,
				AttachedArtifactsHolder.class);

		assertThat(actualWrittenValue.getGenerationTimestamp()).isBetween(before, LocalDateTime.now());

		AttachedArtifactsHolder expectedValue = new AttachedArtifactsHolder();
		expectedValue.setGenerationTimestamp(actualWrittenValue.getGenerationTimestamp());
		expectedValue.setFile(mainFile);
		expectedValue.setAttachedArtifacts(Sets.newLinkedHashSet(
				new AttachedArtifact(ARTIFACT_CLASSIFIER_1, ARTIFACTS_TYPE_1, attachedArtifact1File),
				new AttachedArtifact(ARTIFACT_CLASSIFIER_2, ARTIFACTS_TYPE_2, attachedArtifact2File)));

		assertThat(actualWrittenValue).usingComparatorForType(new AbsolutePathComparator(), File.class)
				.isEqualToComparingFieldByFieldRecursively(expectedValue);
	}

}
