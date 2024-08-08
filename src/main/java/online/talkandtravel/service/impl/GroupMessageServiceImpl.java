package online.talkandtravel.service.impl;

import lombok.extern.log4j.Log4j2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class GroupMessageServiceImpl{}/* implements GroupMessageService {
    private final GroupMessageRepository repository;
    private final UserService userService;
    private final CountryRepository countryRepo;
    private final CountryService countryService;
    private final UserRepository userRepo;
    private final AvatarRepository avatarRepository;

    @Override
    public GroupMessage save(GroupMessage groupMessage) {
        return repository.save(groupMessage);
    }

    @Override
    @Transactional
    public IMessageDto saveAndReturnDto(GroupMessageRequestDto groupMessageRequestDto) {
        GroupMessage groupMessage = dtoToEntity(groupMessageRequestDto);
        Long savedGroupMessageId = save(groupMessage).getId();
        log.info("savedGroupMessageId - {}", savedGroupMessageId);
        IMessageDto savedGroupMessage = repository.findDistinctById(savedGroupMessageId).orElseThrow(EntityNotFoundException::new);

        log.info("savedGroupMessage - {}", savedGroupMessage.getId());
        log.info("savedGroupMessage - {}", savedGroupMessage.getContent());
        log.info("savedGroupMessage - {}", savedGroupMessage);
        return savedGroupMessage;
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

    private GroupMessage dtoToEntity(GroupMessageRequestDto dto) {
        User user = userRepo.getReferenceById(dto.getSenderId());
        Country country = countryRepo.getReferenceById(dto.getCountryId());
        return GroupMessage.builder()
                .content(dto.getContent())
                .user(user)
                .country(country)
                .build();
    }
}
*/