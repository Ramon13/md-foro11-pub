package br.com.javamoon.unit.validator;

import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_PASSWORD;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_USERNAME;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_USERNAME_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.PASSWORD_DOES_NOT_HAVE_LOWERCASE;
import static br.com.javamoon.validator.ValidationConstants.PASSWORD_DOES_NOT_HAVE_NUMBER;
import static br.com.javamoon.validator.ValidationConstants.PASSWORD_DOES_NOT_HAVE_UPPERCASE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.repository.CJMUserRepository;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.mapper.UserDTO;
import br.com.javamoon.util.Constants;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.UserAccountValidator;
import br.com.javamoon.validator.ValidationError;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountValidatorUnitTest {

	private UserAccountValidator victim;
	
	@Mock
	private GroupUserRepository groupUserRepository;
	
	@Mock
	private CJMUserRepository cjmUserRepository;
	
	@BeforeEach
	void setupEach() {
		victim = TestDataCreator.newUserAccountValidator(groupUserRepository, cjmUserRepository);
	}
	
	@Test
	void testCreateGroupUserAccountSuccessfully() {
		UserDTO userDTO = TestDataCreator.newUserDTO();
		
		Mockito.when(groupUserRepository.findActiveByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
		Mockito.when(groupUserRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
		victim.validateCreateGroupUserAccount(userDTO);
	}

	@Test
	void testCreateCJMUserAccountSuccessfully() {
		UserDTO userDTO = TestDataCreator.newUserDTO();
		
		Mockito.when(cjmUserRepository.findActiveByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
		Mockito.when(cjmUserRepository.findActiveByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
		victim.validateCreateCJMUserAccount(userDTO);
	}
	
	@Test
	void testCreateAccountWhenPasswordHasNoUpperCase() {
		UserDTO userDTO = TestDataCreator.newUserDTO();
		userDTO.setPassword(Constants.PASSWORD_NO_UPPERCASE);
		
		AccountValidationException exception = 
				assertThrows(AccountValidationException.class,() -> victim.validateCreateGroupUserAccount(userDTO));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
				new ValidationError(ACCOUNT_PASSWORD, PASSWORD_DOES_NOT_HAVE_UPPERCASE),
				exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testCreateAccountWhenPasswordHasNoLowerCase() {
		UserDTO userDTO = TestDataCreator.newUserDTO();
		userDTO.setPassword(Constants.PASSWORD_NO_LOWER);
		
		AccountValidationException exception = 
				assertThrows(AccountValidationException.class,() -> victim.validateCreateGroupUserAccount(userDTO));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
				new ValidationError(ACCOUNT_PASSWORD, PASSWORD_DOES_NOT_HAVE_LOWERCASE),
				exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testCreateAccountWhenPasswordHasNoNumbers() {
		UserDTO userDTO = TestDataCreator.newUserDTO();
		userDTO.setPassword(Constants.PASSWORD_NO_NUMBER);
		
		AccountValidationException exception = 
				assertThrows(AccountValidationException.class,() -> victim.validateCreateGroupUserAccount(userDTO));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
				new ValidationError(ACCOUNT_PASSWORD, PASSWORD_DOES_NOT_HAVE_NUMBER),
				exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testCreateGroupAccountWhenUsernameAlreadyExists() {
		UserDTO userDTO = TestDataCreator.newUserDTO();
		
		Mockito.when(groupUserRepository.findActiveByUsername(userDTO.getUsername())).thenReturn(Optional.of(new GroupUser()));
		Mockito.when(groupUserRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
		
		AccountValidationException exception = 
				assertThrows(AccountValidationException.class,() -> victim.validateCreateGroupUserAccount(userDTO));
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
				new ValidationError(ACCOUNT_USERNAME, ACCOUNT_USERNAME_ALREADY_EXISTS),
				exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testCreateCjmAccountWhenUsernameAlreadyExists() {
		UserDTO userDTO = TestDataCreator.newUserDTO();
		
		Mockito.when(cjmUserRepository.findActiveByUsername(userDTO.getUsername())).thenReturn(Optional.of(new CJMUser()));
		Mockito.when(cjmUserRepository.findActiveByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
		
		AccountValidationException exception = 
				assertThrows(AccountValidationException.class,() -> victim.validateCreateCJMUserAccount(userDTO));
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
				new ValidationError(ACCOUNT_USERNAME, ACCOUNT_USERNAME_ALREADY_EXISTS),
				exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testCreateGroupAccountWhenEmailAlreadyExists() {
		UserDTO userDTO = TestDataCreator.newUserDTO();
		
		Mockito.when(groupUserRepository.findActiveByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
		Mockito.when(groupUserRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(new GroupUser()));
		
		AccountValidationException exception = 
				assertThrows(AccountValidationException.class,() -> victim.validateCreateGroupUserAccount(userDTO));
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
				new ValidationError(ACCOUNT_EMAIL, ACCOUNT_EMAIL_ALREADY_EXISTS),
				exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testCreateCjmAccountWhenEmailAlreadyExists() {
		UserDTO userDTO = TestDataCreator.newUserDTO();
		
		Mockito.when(cjmUserRepository.findActiveByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
		Mockito.when(cjmUserRepository.findActiveByEmail(userDTO.getEmail())).thenReturn(Optional.of(new CJMUser()));
		
		AccountValidationException exception = 
				assertThrows(AccountValidationException.class,() -> victim.validateCreateCJMUserAccount(userDTO));
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
				new ValidationError(ACCOUNT_EMAIL, ACCOUNT_EMAIL_ALREADY_EXISTS),
				exception.getValidationErrors().getError(0));
	}
}
