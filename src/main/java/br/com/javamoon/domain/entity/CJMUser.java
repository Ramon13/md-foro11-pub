package br.com.javamoon.domain.entity;

import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.draw.Draw;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Getter
@Setter
@Entity
@Table(name="CJM_USER")
public class CJMUser extends User{
	
	@OneToMany(mappedBy = "cjmUser")
	private Set<Draw> drawList = new HashSet<>(0);
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private Auditorship auditorship;
}
