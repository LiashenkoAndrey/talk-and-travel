package com.gmail.smaglenko.talkandtravel.service.impl;

import com.gmail.smaglenko.talkandtravel.model.GroupMessage;
import com.gmail.smaglenko.talkandtravel.model.dto.GroupMessageRequestDto;
import com.gmail.smaglenko.talkandtravel.repository.GroupMessageRepository;
import com.gmail.smaglenko.talkandtravel.service.CountryService;
import com.gmail.smaglenko.talkandtravel.service.GroupMessageService;
import com.gmail.smaglenko.talkandtravel.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupMessageServiceImpl implements GroupMessageService {
    private final GroupMessageRepository repository;
    private final UserService userService;
    private final CountryService countryService;

    @Override
    public GroupMessage save(GroupMessage groupMessage) {
        return repository.save(groupMessage);
    }

    @Override
    @Transactional
    public GroupMessage create(GroupMessageRequestDto groupMessageRequestDto) {
        var groupMessage = createGroupMessageFromRequest(groupMessageRequestDto);
        var savedGroupMessage = save(groupMessage);
        return detachGroupMessageFields(savedGroupMessage);
    }

    @Override
    public List<GroupMessage> findByCountryIdOrderByCreationDateDesc(Long countryId) {
        List<GroupMessage> countryGroupMessagesByIdOrderByCreationDateDesc
                = repository.findByCountryIdOrderByCreationDateDesc(countryId);
        return countryGroupMessagesByIdOrderByCreationDateDesc.stream()
                .map(this::detachGroupMessageFields)
                .toList();
    }

    private GroupMessage detachGroupMessageFields(GroupMessage groupMessage) {
        return GroupMessage.builder()
                .id(groupMessage.getId())
                .content(groupMessage.getContent())
                .creationDate(groupMessage.getCreationDate())
                .build();
    }

    private GroupMessage createGroupMessageFromRequest(GroupMessageRequestDto groupMessageRequestDto) {
        var user = userService.findById(groupMessageRequestDto.getSenderId());
        var country = countryService.findById(groupMessageRequestDto.getCountryId());
        return GroupMessage.builder()
                .content(groupMessageRequestDto.getContent())
                .user(user)
                .country(country)
                .build();
    }
}
