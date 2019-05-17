package bayern.meyer.maven.plugins.deferring;

import java.util.Optional;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProjectHelper;

import bayern.meyer.maven.plugins.deferring.model.AttachedArtifact;
import bayern.meyer.maven.plugins.deferring.model.DeferringInformationHolder;

/**
 * Goal that reads the artifact file and attachedArtifacts of the given project
 * from a file within the projects build directory written there by the
 * save-artifacts goal before triggering the maven-install-plugin or
 * maven-deploy-plugin
 *
 * @author Nils Meyer <a href="mailto:nils@meyer.bayern">nils@meyer.bayern</a>
 */
@Mojo(name = "load-artifacts", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true)
public class LoadArtifactFilesMojo extends AbstractDeferringMojo {

	@Component
	private MavenProjectHelper mavenProjectHelper;

	public void execute() throws MojoExecutionException {

		if (mavenProject.getArtifact().getFile() != null) {
			throw new MojoExecutionException(
					"Project had already an artifact file set. load-artifacts Mojo is expected to be the first Mojo called during a maven run to set artifact file based on results from a former maven run by the save-artifacts Mojo");
		}

		if (!mavenProject.getAttachedArtifacts().isEmpty()) {
			throw new MojoExecutionException(
					"Project had already attachedArtifacts set. load-artifacts Mojo is expected to be the first Mojo called during a maven run to set artifact file based on results from a former maven run by the save-artifacts Mojo");
		}

		Optional<DeferringInformationHolder> deferringInformationHolder = readDeferringInformation();
		deferringInformationHolder.ifPresent(this::setArtifacts);

	}

	private void setArtifacts(DeferringInformationHolder deferringInformationHolder) {
		mavenProject.getArtifact().setFile(deferringInformationHolder.getArtifactFile());
		getLog().info("Read and set artifact file " + deferringInformationHolder.getArtifactFile());
		for (AttachedArtifact artifact : deferringInformationHolder.getAttachedArtifacts()) {
			mavenProjectHelper.attachArtifact(mavenProject, artifact.getArtifactType(),
					artifact.getArtifactClassifier(), artifact.getArtifactFile());
			getLog().info("Read and added attachedArtifact file " + artifact.getArtifactFile());
		}
	}

	/**
	 * Set the MavenProjectHelper in tests
	 * 
	 * @param mavenProjectHelper
	 */
	void setMavenProjectHelper(MavenProjectHelper mavenProjectHelper) {
		this.mavenProjectHelper = mavenProjectHelper;
	}
}
