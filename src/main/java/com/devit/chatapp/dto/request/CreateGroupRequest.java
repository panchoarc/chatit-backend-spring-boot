package com.devit.chatapp.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class CreateGroupRequest {
    private String name;
    private Set<Long> memberIds;
}