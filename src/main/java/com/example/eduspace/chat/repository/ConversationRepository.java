package com.example.eduspace.chat.repository;

import com.example.eduspace.chat.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConversationRepository extends MongoRepository<Conversation, String> {

    Optional<Conversation> findByApplicationId(String applicationId);

    Page<Conversation> findByAuthorIdOrApplicantId(String authorId, String applicantId, Pageable pageable);
}