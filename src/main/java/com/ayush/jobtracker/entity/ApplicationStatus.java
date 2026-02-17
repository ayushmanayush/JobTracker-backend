package com.ayush.jobtracker.entity;

public enum ApplicationStatus{
    APPLIED{
        @Override
        public boolean canTransitionTo(ApplicationStatus newStatus) {
            return newStatus == INTERVIEW || newStatus == REJECTED;
        }
    },
    INTERVIEW{
        @Override
        public boolean canTransitionTo(ApplicationStatus newStatus){
            return newStatus == OFFERED || newStatus == REJECTED;
        }
    },
    OFFERED{
        @Override
        public boolean canTransitionTo(ApplicationStatus newStatus) {
            return newStatus == ACCEPTED || newStatus == DECLINED;
        }
    },
    ACCEPTED{
        @Override
        public boolean canTransitionTo(ApplicationStatus newStatus){
            return false;
        }
    },
    DECLINED{
        @Override
        public boolean canTransitionTo(ApplicationStatus newStatus){
            return false;
        }
    },
    REJECTED{
        @Override
        public boolean canTransitionTo(ApplicationStatus newStatus){
            return false;
        }
    };
    public abstract boolean canTransitionTo(ApplicationStatus newStatus);
}
