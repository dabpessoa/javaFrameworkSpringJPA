package me.dabpessoa.framework.dao;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntityVersion extends BaseEntity {
	private static final long serialVersionUID = 1L;
	
	@Column(name="version")
	private Long version;
	
	public BaseEntityVersion() {}
	
	public BaseEntityVersion(Long version) {
		this();
		this.version = version;
	}
	
	public Long getVersion() {
		return version;
	}
	
	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseEntityVersion other = (BaseEntityVersion) obj;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VersionEntity [version=" + version + "]";
	}

}
