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
import java.util.Optional;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import bayern.meyer.maven.plugins.deferring.model.DeferringInformationHolder;

public abstract class AbstractDeferringMojo extends AbstractMojo {

	protected static final String DEFERRING_JSON_FILENAME = "deferring-maven-plugin.json";
	/**
	 * The project currently being built.
	 */
	@Parameter(required = true, readonly = true, defaultValue = "${project}")
	protected MavenProject mavenProject;
	@Parameter(required = false, defaultValue = "600")
	private int maxAge;
	
	public AbstractDeferringMojo() {
		super();
	}

	protected File getDeferringInformationHolderFile() {
		return new File(mavenProject.getBuild().getDirectory(), DEFERRING_JSON_FILENAME);
	}

	protected ObjectMapper getObjectMapper() {
		return new ObjectMapper().registerModule(new JavaTimeModule());
	}

	/**
	 * @return the mavenProject
	 */
	public MavenProject getMavenProject() {
		return mavenProject;
	}

	/**
	 * @param mavenProject the mavenProject to set
	 */
	public void setMavenProject(MavenProject mavenProject) {
		this.mavenProject = mavenProject;
	}

	protected Optional<DeferringInformationHolder> readDeferringInformation() throws MojoExecutionException {
		File deferringInformationHolderFile = getDeferringInformationHolderFile();
	
		if (!deferringInformationHolderFile.exists()) {
			getLog().info("No file " + DEFERRING_JSON_FILENAME
					+ " found. Probably save-artifacts has not been called by a previous run. Continuing without changing the version.");
			return Optional.empty();
		}
	
		try {
			DeferringInformationHolder deferringInformationHolder = getObjectMapper().readValue(deferringInformationHolderFile,
					DeferringInformationHolder.class);
	
			if (deferringInformationHolder.getGenerationTimestamp().until(LocalDateTime.now(),
					ChronoUnit.SECONDS) > maxAge) {
				getLog().warn(DEFERRING_JSON_FILENAME + " from " + deferringInformationHolder.getGenerationTimestamp()
						+ " is older than " + maxAge
						+ " seconds. load-artifacts usually should be called in a maven run shortly after the one where save-artifacts was called.");
			}
			return Optional.ofNullable(deferringInformationHolder);
		} catch (IOException e) {
			getLog().error(e);
			throw new MojoExecutionException("Could not load defering information from " + DEFERRING_JSON_FILENAME, e);
		}
	}

}