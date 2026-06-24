package ding.co.hellojpa.week3;

import ding.co.hellojpa.UniMember;
import ding.co.hellojpa.UniTeam;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit() {
            UniTeam team = new UniTeam();
            team.setName("Team 1");
            em.persist(team);

            UniMember member = new UniMember();
            member.setTeam(team);
            member.setUsername("Member 1");
            em.persist(member);
        }
    }
}