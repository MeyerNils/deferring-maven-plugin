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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class AbstractArtifactFilesMojo extends AbstractMojo {

	protected static final String DEFERRING_JSON_FILENAME = "deferring-maven-plugin.json";
	/**
	 * The project currently being built.
	 */
	@Parameter(required = true, readonly = true, defaultValue = "${project}")
	protected MavenProject mavenProject;
	
	public AbstractArtifactFilesMojo() {
		super();
	}

	protected File getAttachedArtifactHolderFile() {
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

}