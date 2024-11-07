package online.talkandtravel.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.MailFacade;
import online.talkandtravel.service.MailService;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

@Component
@Log4j2
@RequiredArgsConstructor
public class MailFacadeImpl implements MailFacade {

  private final MailService mailService;
  private final TemplateEngine templateEngine;


}
