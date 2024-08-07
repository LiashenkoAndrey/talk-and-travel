package online.talkandtravel.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import online.talkandtravel.exception.chat.MainCountryChatNotFoundException;
import online.talkandtravel.exception.country.CountryNotFoundException;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.CountryRepository;
import online.talkandtravel.service.ChatService;
import online.talkandtravel.util.mapper.ChatMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final ChatRepository chatRepository;
  private final CountryRepository countryRepository;
  private final ChatMapper chatMapper;

  @Transactional(readOnly = true)
  @Override
  public Page<ChatDto> findAllChats(Pageable pageable) {
    return chatRepository.findAll(pageable).map(chatMapper::toDto);
  }

  @Override
  public ChatDto findMainChat(String countryName) {
    Country country = getCountry(countryName);
    Optional<Chat> optionalChat =
        country.getChats().stream().filter(chat -> chat.getName().equals(countryName)).findFirst();
    Chat chat = optionalChat.orElseThrow(() -> new MainCountryChatNotFoundException(countryName));
    return chatMapper.toDto(chat);
  }

  private Country getCountry(String countryName) {
    return countryRepository
        .findById(countryName)
        .orElseThrow(() -> new CountryNotFoundException(countryName));
  }
}
