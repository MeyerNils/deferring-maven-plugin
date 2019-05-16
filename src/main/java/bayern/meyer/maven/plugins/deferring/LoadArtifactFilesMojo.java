package bayern.meyer.maven.plugins.deferring;

/*
 * Copyright 2019 IT-Consulting Nils Meyer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProjectHelper;

import bayern.meyer.maven.plugins.deferring.model.AttachedArtifact;
import bayern.meyer.maven.plugins.deferring.model.AttachedArtifactsHolder;

/**
 * Goal that reads the artifact file and attachedArtifacts of the given project
 * from a file within the projects build directory written there by the
 * save-artifacts goal before triggering the maven-install or
 * maven-deploy-plugin
 *
 * @author Nils Meyer <a href="mailto:nils@meyer.bayern">nils@meyer.bayern</a>
 */
@Mojo(name = "load-artifacts", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true)
public class LoadArtifactFilesMojo extends AbstractArtifactFilesMojo {

	@Component
	private MavenProjectHelper mavenProjectHelper;

	@Parameter(required = false, defaultValue = "600")
	private int maxAge;

	public void execute() throws MojoExecutionException {

		if (mavenProject.getArtifact().getFile() != null) {
			throw new MojoExecutionException(
					"Project had already an artifact file set. load-artifacts Mojo is expected to be the first Mojo called during a maven run to set artifact file based on results from a former maven run by the save-artifacts Mojo");
		}

		if (!mavenProject.getAttachedArtifacts().isEmpty()) {
			throw new MojoExecutionException(
					"Project had already attachedArtifacts set. load-artifacts Mojo is expected to be the first Mojo called during a maven run to set artifact file based on results from a former maven run by the save-artifacts Mojo");
		}

		File attachedArtifactsHolderFile = getAttachedArtifactHolderFile();

		if (!attachedArtifactsHolderFile.exists()) {
			getLog().info("No file " + DEFERRING_JSON_FILENAME
					+ " found. Probably save-artifacts has not been called by a previous run. Continuing without adding attachedArtifacts.");
			return;
		}

		AttachedArtifactsHolder attachedArtifactsHolder;
		try {
			attachedArtifactsHolder = getObjectMapper().readValue(attachedArtifactsHolderFile,
					AttachedArtifactsHolder.class);

			if (attachedArtifactsHolder.getGenerationTimestamp().until(LocalDateTime.now(),
					ChronoUnit.SECONDS) > maxAge) {
				getLog().warn(DEFERRING_JSON_FILENAME + " from " + attachedArtifactsHolder.getGenerationTimestamp()
						+ " is older than " + maxAge
						+ " seconds. load-artifacts usually should be called in a maven run shortly after the one where save-artifacts was called.");
			}

			mavenProject.getArtifact().setFile(attachedArtifactsHolder.getFile());
			getLog().info("Read and set artifact file " + attachedArtifactsHolder.getFile());
			for (AttachedArtifact artifact : attachedArtifactsHolder.getAttachedArtifacts()) {
				mavenProjectHelper.attachArtifact(mavenProject, artifact.getArtifactType(),
						artifact.getArtifactClassifier(), artifact.getArtifactFile());
				getLog().info("Read and added attachedArtifact file " + artifact.getArtifactFile());
			}
		} catch (IOException e) {
			getLog().error(e);
			throw new MojoExecutionException("Could not load attachedArtifacts from " + DEFERRING_JSON_FILENAME, e);
		}

	}
	
	/**
	 * Set the MavenProjectHelper in tests
	 * @param mavenProjectHelper
	 */
	void setMavenProjectHelper(MavenProjectHelper mavenProjectHelper) {
		this.mavenProjectHelper = mavenProjectHelper;
	}
}
