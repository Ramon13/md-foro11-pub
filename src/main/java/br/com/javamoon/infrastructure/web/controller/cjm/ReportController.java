package br.com.javamoon.infrastructure.web.controller.cjm;

import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.DrawRepository;
import br.com.javamoon.service.DrawService;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/mngmt")
public class ReportController {
    
	@Autowired
	private DrawService drawService;
	
	@Autowired
	private DrawRepository drawRepository;
	
	
	@GetMapping(path="/draw/export/pdf/{drawId}", produces=MediaType.APPLICATION_PDF_VALUE)
	@ResponseBody
	public byte[] generateDrawPdf(@PathVariable Integer drawId,
			HttpServletResponse response) {
		Draw draw = drawRepository.findById(drawId).orElseThrow();
		response.setHeader("Content-disposition", String.format("inline; filename=%s.pdf", draw.getJusticeCouncil().getName()));
		return drawService.generateDrawReport(draw);
	}
	
}
