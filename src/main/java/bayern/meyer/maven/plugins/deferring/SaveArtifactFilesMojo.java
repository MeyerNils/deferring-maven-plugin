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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import bayern.meyer.maven.plugins.deferring.model.AttachedArtifact;
import bayern.meyer.maven.plugins.deferring.model.AttachedArtifactsHolder;

/**
 * Goal that stores the artifacts file and attachedArtifacts of the given
 * project into a file within the projects build directory to be re-used in a
 * later run by the load-artifacts goal before triggering the
 * maven-install or maven-deploy-plugin
 *
 * @author Nils Meyer <a href="mailto:nils@meyer.bayern">nils@meyer.bayern</a>
 */
@Mojo(name = "save-artifacts", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true)
public class SaveArtifactFilesMojo extends AbstractArtifactFilesMojo {

	public void execute() throws MojoExecutionException {

		AttachedArtifactsHolder attachedArtifactsHolder = new AttachedArtifactsHolder();

		attachedArtifactsHolder.setFile(mavenProject.getArtifact().getFile());
		getLog().info("Saving artifact file " + mavenProject.getArtifact().getFile());
		for (Artifact artifact : mavenProject.getAttachedArtifacts()) {
			attachedArtifactsHolder.addAttachedArtifact(
					new AttachedArtifact(artifact.getClassifier(), artifact.getType(), artifact.getFile()));
			getLog().info("Saving attachedArtifact " + artifact.getFile());
		}
		attachedArtifactsHolder.setGenerationTimestamp(LocalDateTime.now());

		File attachedArtifactsHolderFile = getAttachedArtifactHolderFile();

		if (attachedArtifactsHolderFile.exists()) {
			attachedArtifactsHolderFile.delete();
		} else if (!attachedArtifactsHolderFile.getParentFile().exists()) {
			attachedArtifactsHolderFile.getParentFile().mkdirs();
		}

		try {
			getObjectMapper().writeValue(attachedArtifactsHolderFile, attachedArtifactsHolder);
		} catch (IOException e) {
			getLog().error(e);
			throw new MojoExecutionException("Could not save attachedArtifacts into " + DEFERRING_JSON_FILENAME, e);
		}
	}
}
