package bayern.meyer.maven.plugins.deferring.model;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class AttachedArtifactsHolder {

	private Set<AttachedArtifact> attachedArtifacts;

	private LocalDateTime generationTimestamp = LocalDateTime.now();

	private File file;

	public AttachedArtifactsHolder() {
		super();
		// Empty constructor for Jackson
	}

	public AttachedArtifactsHolder(AttachedArtifact...artifacts) {
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
	
	public File getFile() {
		return file;
	}

	public LocalDateTime getGenerationTimestamp() {
		return generationTimestamp;
	}

	public void setAttachedArtifacts(Set<AttachedArtifact> attachedArtifacts) {
		this.attachedArtifacts = attachedArtifacts;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setGenerationTimestamp(LocalDateTime generationTimestamp) {
		this.generationTimestamp = generationTimestamp;
	}

}
