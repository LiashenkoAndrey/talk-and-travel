package online.talkandtravel.service.impl;

import lombok.RequiredArgsConstructor;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.repository.ChatRepository;
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
  private final ChatMapper chatMapper;

  @Transactional(readOnly = true)
  @Override
  public Page<ChatDto> findAllChats(Pageable pageable) {
    return chatRepository.findAll(pageable).map(chatMapper::toDto);
  }
}
