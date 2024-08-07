package online.talkandtravel.service;

import online.talkandtravel.model.entity.Avatar;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {
    Avatar save(Avatar avatar);

    Avatar findByUserId(Long userId);

    Avatar createDefaultAvatar(String username) throws IOException;

    Avatar uploadAvatar(MultipartFile imageFile, Long userId);
}
