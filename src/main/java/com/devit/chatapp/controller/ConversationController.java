package com.devit.chatapp.controller;

import com.devit.chatapp.dto.request.AddMemberRequest;
import com.devit.chatapp.dto.request.CreateConversationRequest;
import com.devit.chatapp.dto.response.ChatMessageResponse;
import com.devit.chatapp.dto.response.ConversationResponseDTO;
import com.devit.chatapp.dto.response.UserResponseDTO;
import com.devit.chatapp.service.ConversationService;
import com.devit.chatapp.service.MessageService;
import com.devit.chatapp.util.Pagination;
import com.devit.chatapp.util.ResponseAPI;
import com.devit.chatapp.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
@Slf4j
public class ConversationController {

    private final ConversationService conversationService;
    private final MessageService messageService;

    @GetMapping
    public ResponseAPI<List<ConversationResponseDTO>> getConversations(@AuthenticationPrincipal Jwt userJwt) {

        String creatorId = userJwt.getSubject();
        List<ConversationResponseDTO> myConversations = conversationService.findMyConversations(creatorId);
        return ResponseBuilder.success("Conversations fetched", myConversations);
    }


    @GetMapping("/{id}/members")
    public ResponseAPI<List<UserResponseDTO>> getConversationMembers(@PathVariable Long id) {
        List<UserResponseDTO> members = conversationService.getMembersByChatId(id);

        return ResponseBuilder.success("Members fetched", members);
    }

    @GetMapping("{conversationId}/messages")
    public ResponseAPI<List<ChatMessageResponse>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") Long page,
            @RequestParam(defaultValue = "20") Long size
    ) {
        Slice<ChatMessageResponse> messages = messageService.getMessagesByChatId(conversationId, page, size);

        Pagination pagination = new Pagination(
                messages.getNumber(),         // página actual
                messages.getSize(),           // tamaño de página
                messages.isFirst(),           // ¿es la primera página?
                !messages.hasNext(),          // ¿es la última página?
                messages.getNumberOfElements() // cuántos elementos contiene esta página
        );

        return ResponseBuilder.successPaginated("Messages fetched", messages.getContent(), pagination);
    }


    @PostMapping
    public void createConversation(@Valid @RequestBody CreateConversationRequest request, @AuthenticationPrincipal Jwt userJwt) {
        String creatorId = userJwt.getSubject();
        conversationService.createConversation(request,creatorId);
    }


    @PostMapping("{conversationId}/addMember")
    public void handleNewMember(@PathVariable Long conversationId, @RequestBody AddMemberRequest memberId) {
        conversationService.addMemberToConversation(conversationId, memberId);
    }
}
