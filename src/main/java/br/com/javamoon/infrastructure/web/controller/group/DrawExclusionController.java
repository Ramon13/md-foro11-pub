package br.com.javamoon.infrastructure.web.controller.group;

import static br.com.javamoon.infrastructure.web.controller.ControllerHelper.getArmy;
import static br.com.javamoon.infrastructure.web.controller.ControllerHelper.getCJM;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.exception.DrawExclusionNotFoundException;
import br.com.javamoon.exception.DrawExclusionValidationException;
import br.com.javamoon.exception.SoldierNotFoundException;
import br.com.javamoon.mapper.DrawExclusionDTO;
import br.com.javamoon.service.DrawExclusionService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gp/sd/exclusion")
public class DrawExclusionController {

	private SoldierService soldierService;
	
	private DrawExclusionService drawExclusionService;
	
	public DrawExclusionController(SoldierService soldierService, DrawExclusionService drawExclusionService) {
		this.soldierService = soldierService;
		this.drawExclusionService = drawExclusionService;
	}

	@SuppressWarnings("rawtypes")
	@PostMapping
	public ResponseEntity save(@RequestBody DrawExclusionDTO exclusionDTO) {
		try {
			Soldier soldier = soldierService.getSoldier(exclusionDTO.getSoldierId(), getArmy(), getCJM());
		
			DrawExclusionDTO newExclusion = 
					drawExclusionService.save(exclusionDTO, SecurityUtils.groupUser(), soldier);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(newExclusion);
		} catch (DrawExclusionValidationException e) {
			return ResponseEntity.unprocessableEntity().body( e.getErrorList() );
		} catch (SoldierNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping("/{exclusionId}")
	public ResponseEntity delete(@PathVariable Integer exclusionId) {
		try {
			GroupUser loggedUser = SecurityUtils.groupUser();
			drawExclusionService.delete(exclusionId, loggedUser);
			
			return ResponseEntity.noContent().build();
		} catch(DrawExclusionNotFoundException e) {
			return ResponseEntity.notFound().build();
		
		} catch(DrawExclusionValidationException e) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body( e.getErrorList() );
		}
	}
}
