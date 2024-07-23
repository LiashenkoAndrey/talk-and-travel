package online.talkandtravel.service;

import online.talkandtravel.model.GroupMessage;
import online.talkandtravel.model.dto.GroupMessageRequestDto;

import java.util.List;

public interface GroupMessageService {
    GroupMessage save(GroupMessage groupMessage);

    GroupMessage create(GroupMessageRequestDto groupMessageRequestDto);

    List<GroupMessage> findByCountryIdOrderByCreationDateDesc(Long countryId);
}
