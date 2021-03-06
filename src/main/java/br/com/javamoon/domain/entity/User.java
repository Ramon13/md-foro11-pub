package br.com.javamoon.domain.entity;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.util.StringUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
public class User implements Serializable{

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 20, nullable = false, unique = true)
	private String username;
	
	@Column(length = 64, nullable = true, unique = true)
	private String email;
	
	@Column(name="user_password", nullable = false, length = 255)
	private String password;
	
	@Column(name="credentials_expired", nullable = false)
	private Boolean credentialsExpired;
	
	@Column(name="recovery_token", nullable=true)
	private String recoveryToken;
	
	@Column(name="active", nullable = false)
	private Boolean active;
	
	@Min(value = 1)
    @Max(value = 7)
    @Column(name="permission_level", nullable = false)
    private Integer permissionLevel;
	
	private transient List<String> permissionRoles = new ArrayList<String>(0);
	
	private transient CJM cjm;
	
	@PrePersist
	public void prePersist() {
	    if (Objects.isNull(credentialsExpired))
	        credentialsExpired = true;
	    if (Objects.isNull(active))
	        active = true;
	    if (Objects.isNull(permissionLevel))
	    	permissionLevel = 1;
	}
	
	public void encryptPassword() {
		this.password = StringUtils.encrypt(this.password);
	}
}
