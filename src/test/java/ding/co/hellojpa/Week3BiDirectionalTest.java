package ding.co.hellojpa;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

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
            member1.setTeam(team); // 주인에게 값 설정 (FK 저장)
            em.persist(member1);

            BiMember member2 = new BiMember();
            member2.setUsername("회원2");
            member2.setTeam(team); // 주인에게 값 설정 (FK 저장)
            em.persist(member2);

            // ⭐️ 영속성 컨텍스트 비우기 (DB에서 역방향으로 잘 가져오는지 확인하기 위함)
            em.flush();
            em.clear();

            System.out.println("============== 2. 역방향 조회 시작 ==============");
            // 팀만 조회 (이때 멤버는 아직 안 가져옴 - LAZY)
            BiTeam findTeam = em.find(BiTeam.class, team.getId());

            // ✨ 핵심: 팀 객체에서 멤버 리스트를 꺼내봄!
            List<BiMember> members = findTeam.getMembers();

            System.out.println("팀 이름 = " + findTeam.getName());

            // members.size()를 호출하는 순간 실제 DB에 SELECT 쿼리가 나감!
            System.out.println("팀원 수 = " + members.size());

            for (BiMember m : members) {
                System.out.println(" -> 팀원 이름: " + m.getUsername());
            }
            System.out.println("===============================================");

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

            // ❌ [치명적 실수] 주인이 아닌 '가짜(mappedBy)'에만 값을 넣음
            // "팀아, 네 멤버 리스트에 이 회원 추가해!"
            team.getMembers().add(member);

            // (진짜 주인인 member.setTeam(team) 은 빼먹음!)
            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("============== 결과 확인 ==============");
            BiMember findMember = em.find(BiMember.class, member.getId());

            // DB에 TEAM_ID가 어떻게 들어갔는지 확인해봅시다.
            if (findMember.getTeam() == null) {
                System.out.println("🚨 맙소사! DB에 TEAM_ID가 NULL로 저장되었습니다!");
                System.out.println("이유: Team.members는 주인이 아니기 때문에 JPA가 DB 저장 시 철저히 무시합니다.");
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

            // ✅ [정답] 연관관계의 주인(Member.team)에게 값을 설정!
            // 외래 키(TEAM_ID)를 관리하는 주인이므로 DB에 정상적으로 INSERT/UPDATE 됩니다.
            member.setTeam(team);

            em.persist(member);

            // DB에 쿼리를 강제로 날리고 영속성 컨텍스트를 비웁니다.
            em.flush();
            em.clear();

            System.out.println("============== 결과 확인 ==============");
            BiMember findMember = em.find(BiMember.class, member.getId());

            // DB에 TEAM_ID가 정상적으로 들어갔는지 확인해봅시다.
            if (findMember.getTeam() != null) {
                System.out.println("🎉 성공! DB에 TEAM_ID가 정상적으로 저장되었습니다.");
                System.out.println("저장된 팀 이름: " + findMember.getTeam().getName());
            } else {
                System.out.println("🚨 실패! 팀 정보가 없습니다.");
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
    void 편의메서드를_사용하여_순수객체_상태_동기화하기() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            BiTeam team = new BiTeam();
            team.setName("TeamA");
            em.persist(team);

            BiMember member = new BiMember();
            member.setUsername("회원1");

            System.out.println("============== 편의 메서드 호출 ==============");
            // ✅ 정답: 편의 메서드 단 하나만 호출!
            // 내부에서 member.team = team 도 하고, team.members.add(member) 도 해줌
            member.changeTeam(team);
            em.persist(member);

            // ⭐️ 중요: flush, clear를 하지 않습니다! (순수 메모리 객체 상태 확인용)
            // em.flush();
            // em.clear();

            System.out.println("============== 메모리 객체 상태 확인 ==============");
            // DB에 안 갔다 와도 메모리에 있는 team 객체의 리스트에 회원이 잘 들어있을까?
            int memberCount = team.getMembers().size();
            System.out.println("TeamA의 현재 소속 팀원 수: " + memberCount + "명");

            if (memberCount == 1) {
                System.out.println("🎉 성공! DB에 가기 전 순수 객체 상태에서도 양방향 동기화가 완벽합니다.");
            } else {
                System.out.println("❌ 실패! 편의 메서드를 안 썼다면 여기가 0명이 나옵니다.");
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}