package bayern.meyer.maven.plugins.deferring;

import java.util.Optional;

import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProjectHelper;

import bayern.meyer.maven.plugins.deferring.model.DeferringInformationHolder;

/**
 * Goal that reads the artifacts version of the given project from a file within
 * the projects build directory written there by the save-artifacts goal before
 * triggering the maven-install-plugin or maven-deploy-plugin
 *
 * @author Nils Meyer <a href="mailto:nils@meyer.bayern">nils@meyer.bayern</a>
 */
@Mojo(name = "load-version", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true)
public class LoadVersionMojo extends AbstractDeferringMojo {

	@Component
	private MavenProjectHelper mavenProjectHelper;

	public void execute() throws MojoExecutionException {
		Optional<DeferringInformationHolder> deferringInformationHolder = readDeferringInformation();
		deferringInformationHolder.ifPresent(this::setVersion);
	}

	private void setVersion(DeferringInformationHolder deferringInformationHolder) {
		mavenProject.getArtifact().setVersion(deferringInformationHolder.getArtifactVersion());
		mavenProject.getArtifact()
				.setVersionRange(VersionRange.createFromVersion(deferringInformationHolder.getArtifactVersion()));
		getLog().info("Read and set artifact version " + deferringInformationHolder.getArtifactVersion());
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
