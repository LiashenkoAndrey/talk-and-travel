package online.talkandtravel.model.dto.attachment;

public record ImageAttachmentDto (
    String type,
    String id,
    String originalImageUrl,
    String thumbnailImageUrl
) implements AttachmentDto {

}
