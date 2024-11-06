package online.talkandtravel.facade;

import online.talkandtravel.model.dto.avatar.AvatarDto;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarFacade {

  AvatarDto saveOrUpdateAvatar(MultipartFile file);

}
