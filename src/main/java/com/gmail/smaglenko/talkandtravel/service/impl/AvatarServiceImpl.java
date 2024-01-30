package com.gmail.smaglenko.talkandtravel.service.impl;

import com.gmail.smaglenko.talkandtravel.exception.FileSizeExceededException;
import com.gmail.smaglenko.talkandtravel.exception.UnsupportedFormatException;
import com.gmail.smaglenko.talkandtravel.model.Avatar;
import com.gmail.smaglenko.talkandtravel.repository.AvatarRepository;
import com.gmail.smaglenko.talkandtravel.service.AvatarService;
import com.gmail.smaglenko.talkandtravel.service.ImageService;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {
    private static final String[] SUPPORTED_FORMAT_AVATAR = {"jpeg", "jpg", "png"};
    private static final int MAX_SIZE_AVATAR = 4 * 1024 * 1024; //Size in bytes (4MB)
    private final AvatarRepository repository;
    private final ImageService imageService;

    @Override
    public Avatar save(Avatar avatar) {
        return repository.save(avatar);
    }

    @Override
    @Transactional
    public Avatar findByUserId(Long userId) {
        return getExistingAvatar(userId);
    }

    @Override
    public Avatar createStandardAvatar(String username) {
        var standardAvatar = imageService.generateImage(username);
        return buildAvatar(standardAvatar);
    }

    @Override
    @Transactional
    public Avatar uploadAvatar(MultipartFile file, Long userId) {
        var image = getImage(file);
        var originalFilename = file.getOriginalFilename();
        isImageValid(file, originalFilename);
        var existingAvatar = getExistingAvatar(userId);
        existingAvatar.setContent(image);
        return repository.save(existingAvatar);
    }

    private Avatar getExistingAvatar(Long userId) {
        var avatar = repository.findByUserId(userId).orElseThrow(
                () -> new NoSuchElementException("Can not find avatar by user ID: " + userId)
        );
        avatar.setContent(avatar.getContent());
        return avatar;
    }

    private void isImageValid(MultipartFile imageFile, String originalFilename)
            throws UnsupportedFormatException, FileSizeExceededException {
        if (!isSupportedFormat(originalFilename)) {
            throw new UnsupportedFormatException("Your photo must be in JPEG, JPG, or PNG.");
        }
        if (imageFile.getSize() > MAX_SIZE_AVATAR) {
            throw new FileSizeExceededException("File size exceeds 4MB");
        }
    }

    private byte[] getImage(MultipartFile imageFile) {
        try {
            return imageFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error converting an image to an avatar. Please try again.");
        }
    }

    private boolean isSupportedFormat(String fileName) {
        var fileExtension
                = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return Arrays.asList(SUPPORTED_FORMAT_AVATAR).contains(fileExtension);
    }

    private Avatar buildAvatar(byte[] standardAvatar) {
        return Avatar.builder()
                .content(standardAvatar)
                .build();
    }
}
