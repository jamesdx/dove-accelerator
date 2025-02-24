package com.helix.dove.dove.accelerator.event;

import com.helix.dove.dove.accelerator.entity.Requirement;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RequirementEvent extends ApplicationEvent {
    private final Requirement requirement;

    public RequirementEvent(Object source, Requirement requirement) {
        super(source);
        this.requirement = requirement;
    }
} 