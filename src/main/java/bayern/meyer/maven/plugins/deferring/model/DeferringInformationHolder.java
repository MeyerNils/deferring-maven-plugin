package bayern.meyer.maven.plugins.deferring.model;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class DeferringInformationHolder {

	private Set<AttachedArtifact> attachedArtifacts;

	private LocalDateTime generationTimestamp = LocalDateTime.now();

	private File artifactFile;
	
	private String artifactVersion;

	public DeferringInformationHolder() {
		super();
		// Empty constructor for Jackson
	}

	public DeferringInformationHolder(AttachedArtifact...artifacts) {
		this();
		for (AttachedArtifact attachedArtifact : artifacts) {
			addAttachedArtifact(attachedArtifact);
		}
	}

	public void addAttachedArtifact(AttachedArtifact attachedArtifact) {
		// Use the getter to avoid null access
		getAttachedArtifacts().add(attachedArtifact);		
	}

	public Set<AttachedArtifact> getAttachedArtifacts() {
		if (attachedArtifacts == null) {
			attachedArtifacts = new HashSet<AttachedArtifact>();
		}
		return attachedArtifacts;
	}
	
	public File getArtifactFile() {
		return artifactFile;
	}

	public LocalDateTime getGenerationTimestamp() {
		return generationTimestamp;
	}

	public String getArtifactVersion() {
		return artifactVersion;
	}

	public void setAttachedArtifacts(Set<AttachedArtifact> attachedArtifacts) {
		this.attachedArtifacts = attachedArtifacts;
	}

	public void setArtifactFile(File file) {
		this.artifactFile = file;
	}

	public void setGenerationTimestamp(LocalDateTime generationTimestamp) {
		this.generationTimestamp = generationTimestamp;
	}

	public void setArtifactVersion(String version) {
		this.artifactVersion = version;
	}

}
