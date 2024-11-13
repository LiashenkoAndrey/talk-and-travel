package online.talkandtravel.model.dto.attachment;

public record ImageAttachmentDto (
    String id,
    String originalImageUrl,
    String thumbnailImageUrl
) implements AttachmentDto {

}
