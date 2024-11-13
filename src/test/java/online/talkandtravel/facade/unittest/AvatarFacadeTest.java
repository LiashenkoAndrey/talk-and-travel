package online.talkandtravel.facade.unittest;

import static online.talkandtravel.util.constants.AvatarDimensionsConstants.X256;
import static online.talkandtravel.util.constants.AvatarDimensionsConstants.X50;
import static online.talkandtravel.util.constants.S3Constants.AVATAR_X256_FOLDER_NAME;
import static online.talkandtravel.util.constants.S3Constants.AVATAR_X50_FOLDER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import online.talkandtravel.facade.impl.AvatarFacadeImpl;
import online.talkandtravel.model.dto.avatar.AvatarDto;
import online.talkandtravel.service.AvatarService;
import online.talkandtravel.service.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class AvatarFacadeTest {

  @Mock private ImageService imageService;

  @Mock private AvatarService avatarService;

  @InjectMocks AvatarFacadeImpl underTest;

  @Test
  void saveOrUpdateAvatar_shouldSave() throws IOException {
    byte[] fileBytes = new byte[] {61,45,5,52,54,55,65};
    String contentType = "png";
    MultipartFile file = Mockito.mock(MultipartFile.class);
    byte[] smallThumbnailBytes = new byte[] {1,2,4,5,6};
    byte[] bigThumbnailBytes = new byte[] {61,42,54,55,65};
    AvatarDto avatarDtoExpected = new AvatarDto("1", "2");

    when(file.getBytes()).thenReturn(fileBytes);
    when(file.getContentType()).thenReturn(contentType);
    when(imageService.generateThumbnail(fileBytes, contentType, X50)).thenReturn(smallThumbnailBytes);
    when(imageService.generateThumbnail(fileBytes, contentType, X256)).thenReturn(bigThumbnailBytes);
    when(avatarService.saveOrUpdateUserAvatar(smallThumbnailBytes, AVATAR_X50_FOLDER_NAME)).thenReturn(avatarDtoExpected);
    when(avatarService.saveOrUpdateUserAvatar(bigThumbnailBytes, AVATAR_X256_FOLDER_NAME)).thenReturn(avatarDtoExpected);

    AvatarDto actual = underTest.saveOrUpdateAvatar(file);
    assertNotNull(actual);
    assertEquals(avatarDtoExpected, actual);

    verify(imageService).generateThumbnail(fileBytes, contentType, X50);
    verify(imageService).generateThumbnail(fileBytes, contentType, X256);
    verify(avatarService).saveOrUpdateUserAvatar(smallThumbnailBytes, AVATAR_X50_FOLDER_NAME);
    verify(avatarService).saveOrUpdateUserAvatar(bigThumbnailBytes, AVATAR_X256_FOLDER_NAME);
  }
}
