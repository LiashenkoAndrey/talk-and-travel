package online.talkandtravel.util;

public record FileDto(
    byte[] fileBytes,
    String contentType,
    String filename,
    Long size) {


}
