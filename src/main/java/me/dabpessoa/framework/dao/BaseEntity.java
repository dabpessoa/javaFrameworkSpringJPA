package me.dabpessoa.framework.dao;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = -3729958615488101692L;

	public BaseEntity() {}

	/**
	 * Método para inicializar objetos que a entidade depende.
	 * as classes filhas devem especializar a implementação deste método
	 * caso seja necessário.
	 */
	public void init() {}
	
	public abstract Long getId();

	public boolean isUpdating() {
		return getId() != null;
	}

	public boolean isInserting() {
		return !isUpdating();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Entity [id=" + getId() + "]";
	}

}
