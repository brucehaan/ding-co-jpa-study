package ding.co.hellojpa.week3;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class Week3BiDirectionalTest {
    @Autowired
    EntityManagerFactory emf;

    @Test
    void 역방향_탐색_테스트_팀에서_멤버조회() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. 데이터 저장
            BiTeam team = new BiTeam();
            team.setName("TeamA");
            em.persist(team);

            BiMember member1 = new BiMember();
            member1.setUsername("회원1");
            member1.setTeam(team); // 주인에게 값 설정 (fk 저장)
            em.persist(member1);

            BiMember member2 = new BiMember();
            member2.setUsername("회원2");
            member2.setTeam(team); // 주인에게 값 설정 (fk 저장)
            em.persist(member2);

            // 영속성 컨텍스트 비우기 (DB에서 역방향으로 잘 가져오는지 확인하기 위함)
            em.flush();
            em.clear();

            log.info("2. 역방향 조회 시작");
            // 팀만 조회 (이때 멤버는 아직 안 가져옴 - LAZY)
            BiTeam findTeam = em.find(BiTeam.class, team.getId());

            // 핵심 : 팀 객체에서 멤버 리스트를 꺼내봄
            List<BiMember> members = findTeam.getMembers();

            log.info("팀 이름 = {}", findTeam.getName());

            // members.size()를 호출하는 순간 실제 db에 select 쿼리가 나감
            log.info("팀원 수 = {}", members.size());

            for (BiMember m : members) {
                log.info("팀원 이름 : {} " , m.getUsername());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    void 연관관계의_주인을_잘못_설정한_경우의_참사() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. 팀 저장
            BiTeam team = new BiTeam();
            team.setName("TeamA");
            em.persist(team);

            // 2. 회원 저장
            BiMember member = new BiMember();
            member.setUsername("회원1");

            // 치명적 실수 : 주인이 아닌 '가짜(mappedBy)'에만 값을 넣음
            // 팀아, 네 멤버 리스트에 이 회원 추가해!
            team.getMembers().add(member);

            // 진짜 주인인 member.setTeam(team)은 빼먹음
            em.persist(member);
            em.flush();
            em.clear();

            log.info("결과 확인");
            BiMember findMember = em.find(BiMember.class, member.getId());

            // DB에 TEAM_ID가 어떻게 들어갔는지 확인
            if (findMember.getTeam() == null) {
                log.info("DB에 TEAM_ID가 NULL로 저장됨");
                log.info("이유 : Team.members는 주인이 아니기 때문에 JPA가 DB저장 시 철저히 무시함");
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    void 연관관계의_주인에게_올바르게_값을_설정한_경우() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. 팀 저장
            BiTeam team = new BiTeam();
            team.setName("TeamA");
            em.persist(team);

            // 2. 회원 저장
            BiMember member = new BiMember();
            member.setUsername("회원1");

            // 연관관계의 주인(Member.team)에게 값을 설정
            // 외래 키(TEAM_ID)를 관리하는 주인이므로 DB에 정상적으로 INSERT/UPDATE됨
            member.setTeam(team);

            em.persist(member);

            // DB에 쿼리를 강제로 날리고 영속성 컨텍스트를 비움
            em.flush();
            em.clear();

            log.info("결과 확인");
            BiMember findMember = em.find(BiMember.class, member.getId());

            // DB에 TEAM_ID가 정상적으로 들어갔는지 확인
            if (findMember.getTeam() != null) {
                log.info("성공 : DB에 TEAM_ID가 정상적으로 저장됨");
                log.info("저장된 팀 이름 : {}", findMember.getTeam().getName());
            } else {
                log.info("실패 : 팀 정보가 없음");
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Test
    void 편의메스드를_이용하여_순수객체_상태_동기화하기() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            BiTeam team = new BiTeam();
            team.setName("TeamA");
            em.persist(team);

            BiMember member = new BiMember();
            member.setUsername("회원1");

            log.info("편의 메서드 호출");
            // 정답 : 편의 메서드 단 하나만 호출
            // 내부에서 member.team = team도 하고, team.members.add(member)도 해줌
            member.changeTeam(team);
            em.persist(member);

            // 중요 : flush, clear를 하지 않는다. 순수 메모리 객체 상태 확인용

            log.info("메모리 객체 상태 확인");
            // DB에 안 갔다 와도 메모리에 있는 team 객체의 리스트에 회원이 잘 들어있는지 확인하기
            int memberCount = team.getMembers().size();
            log.info("TeamA의 현재 소속 팀원 수 : {}", memberCount + "명");

            if (memberCount == 1) {
                log.info("성공 : DB에 가기 전 순수 객체 상태에서도 양방향 동기화가 완벽하다");
            } else {
                log.info("실패 : 편의 메서드를 안 썼다면 여기가 0 명이 나온다.");
            }
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }  finally {
            em.close();
        }
    }
}
