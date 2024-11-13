package online.talkandtravel.model.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import online.talkandtravel.model.entity.attachment.AttachmentType;
import org.springframework.web.multipart.MultipartFile;

public record SendMessageWithAttachmentRequest(
    String content,
    @NotNull @Positive Long chatId,
    Long repliedMessageId,
    @NotNull AttachmentType attachmentType,
    @NotNull MultipartFile file
) {

}
