package com.doveaccelerator.common.constant;

public class Constants {
    // Agent Role Constants
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_PROJECT_MANAGER = "PROJECT_MANAGER";
    public static final String ROLE_PRODUCT_MANAGER = "PRODUCT_MANAGER";
    public static final String ROLE_ARCHITECT = "ARCHITECT";
    public static final String ROLE_DEVELOPER = "DEVELOPER";
    public static final String ROLE_TESTER = "TESTER";
    public static final String ROLE_OPERATOR = "OPERATOR";

    // Prompt Types
    public static final String PROMPT_ROLE_PLAYING = "ROLE_PLAYING";
    public static final String PROMPT_STEP_BY_STEP = "STEP_BY_STEP";
    public static final String PROMPT_EXAMPLE_COMPARISON = "EXAMPLE_COMPARISON";
    public static final String PROMPT_CHAIN_OF_THOUGHT = "CHAIN_OF_THOUGHT";
    public static final String PROMPT_REACT = "REACT";

    // Model Types
    public static final String MODEL_GPT4 = "GPT4";
    public static final String MODEL_CLAUDE = "CLAUDE";
    public static final String MODEL_WENXIN = "WENXIN";
    public static final String MODEL_SPARK = "SPARK";

    // Project Status
    public static final String STATUS_PLANNING = "PLANNING";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_PAUSED = "PAUSED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // Task Status
    public static final String TASK_PENDING = "PENDING";
    public static final String TASK_IN_PROGRESS = "IN_PROGRESS";
    public static final String TASK_COMPLETED = "COMPLETED";
    public static final String TASK_FAILED = "FAILED";

    // Common Constants
    public static final int MAX_RETRY_ATTEMPTS = 3;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String DEFAULT_LANGUAGE = "en_US";
}