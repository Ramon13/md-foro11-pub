package br.com.javamoon.domain.draw_exclusion;

import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.util.DateUtils;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Getter
@Setter
@ToString
@Entity
@Table(name = "DRAW_EXCLUSION")
public class DrawExclusion implements Serializable{

	public DrawExclusion() {}
	
	public DrawExclusion(LocalDate startDate, LocalDate endDate, String message) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.message = message;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Integer id;
	
	@Column(name = "start_date", nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	@Column(name = "end_date", nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	@Column(name="creation_date", nullable=false)
	private LocalDateTime creationDate;
	
	@Length(min = 3, max = 1024 * 10, message = "O campo deve conter entre 3 e 10 * 1024 caracteres")
	@Column(nullable = false)
	private String message;
	
	@ManyToOne
	@JoinColumn(name="group_user_id", nullable = false)
	@ToString.Exclude
	private GroupUser groupUser;
	
	@ManyToOne
	@JoinColumn(name="soldier_id", nullable = false)
	@ToString.Exclude
	private Soldier soldier;

	@PrePersist
	private void prePersist() {
		if (Objects.isNull(creationDate))
			creationDate = LocalDateTime.now();
	}
	
	public String getPeriodAsText() {
		String format = "dd/MM/yyyy";
		return String.format("De:%s; Até:%s", 
				DateUtils.convertToFormat(startDate, format),
				DateUtils.convertToFormat(endDate, format));
	}
}
