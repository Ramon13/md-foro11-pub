package br.com.javamoon.domain.repository;

import br.com.javamoon.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{

}
