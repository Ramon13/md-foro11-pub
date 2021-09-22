package br.com.javamoon.domain.cjm_user;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.user.User;
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
	
	@NotNull(message = "È necessário selecionar uma auditoria")
	@ManyToOne
	@JoinColumn(nullable = false)
	private Auditorship auditorship;
}
