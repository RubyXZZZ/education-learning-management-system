package io.rubyxzzz.lms.backend.model;

public enum StudentType {
    FULL_TIME_ONLY,      // F-1, J-1, M-1 (must ≥18 hrs)
    PART_TIME_ONLY,      // B1/B2 Tourist visa (must <18 hrs)
    FLEXIBLE;             // US Citizen, PR (can be either)

}
