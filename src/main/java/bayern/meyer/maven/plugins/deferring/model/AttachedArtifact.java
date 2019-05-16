package bayern.meyer.maven.plugins.deferring.model;

import java.io.File;

public class AttachedArtifact {

	private String artifactClassifier;
	private File artifactFile;
	private String artifactType;

	public AttachedArtifact() {
		super();
		// Empty constructor for Jackson
	}

	public AttachedArtifact(String artifactClassifier, String artifactType, File artifactFile) {
		super();
		this.artifactClassifier = artifactClassifier;
		this.artifactType = artifactType;
		this.artifactFile = artifactFile;
	}

	public String getArtifactClassifier() {
		return artifactClassifier;
	}

	public File getArtifactFile() {
		return artifactFile;
	}

	public String getArtifactType() {
		return artifactType;
	}

	public void setArtifactClassifier(String artifactClassifier) {
		this.artifactClassifier = artifactClassifier;
	}

	public void setArtifactFile(File artifactFile) {
		this.artifactFile = artifactFile;
	}

	public void setArtifactType(String artifactType) {
		this.artifactType = artifactType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactClassifier == null) ? 0 : artifactClassifier.hashCode());
		result = prime * result + ((artifactFile == null) ? 0 : artifactFile.hashCode());
		result = prime * result + ((artifactType == null) ? 0 : artifactType.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AttachedArtifact other = (AttachedArtifact) obj;
		if (artifactClassifier == null) {
			if (other.artifactClassifier != null) {
				return false;
			}
		} else if (!artifactClassifier.equals(other.artifactClassifier)) {
			return false;
		}
		if (artifactFile == null) {
			if (other.artifactFile != null) {
				return false;
			}
		} else if (!artifactFile.equals(other.artifactFile)) {
			return false;
		}
		if (artifactType == null) {
			if (other.artifactType != null) {
				return false;
			}
		} else if (!artifactType.equals(other.artifactType)) {
			return false;
		}
		return true;
	}
}
