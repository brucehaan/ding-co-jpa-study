package ding.co.hellojpa;

import org.junit.jupiter.api.Test;
import java.sql.*;


public class JdbcProblemTest {

    // H2 DB 연결 정보 (인메모리 모드)
    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    @Test
    void JDBC_데이터_불일치_확인() throws SQLException {
        // [준비] 테이블 생성 및 데이터 삽입 (JDBC는 수동으로 다 해줘야 함 ㅠㅠ)
        initDatabase();

        System.out.println("=== 💀 JDBC 실험 시작 ===");

        // [A] 1. 서비스 로직 시작: 회원 조회 (등급: BASIC)
        // -> new Member()가 실행되어 힙 메모리 @100번지에 객체 생성
        Member member = findById(1L);
        System.out.println("1. 처음 조회된 등급: " + member.getGrade()); // BASIC

        // [B] 2. 등급 상향 메서드 호출 (ID만 넘김!)
        promoteToVip(1L);

        // [C] 3. 등급 확인 (과연 VIP로 바뀌었을까?)
        System.out.println("3. 메인 로직에서 다시 확인한 등급: " + member.getGrade());

        // [결과 검증]
        if (member.getGrade() == Grade.VIP) {
            System.out.println(">>> 🎉 VIP 혜택 적용 성공!");
        } else {
            System.out.println(">>> 😭 혜택 적용 실패... (객체가 따로 놈)");
        }
    }

    // [D] 별도 서비스라고 가정 (DB에 직접 update를 날림)
    private void promoteToVip(Long memberId) throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        String sql = "UPDATE MEMBER SET GRADE = 'VIP' WHERE ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setLong(1, memberId);
        pstmt.executeUpdate();

        System.out.println("2. [DiscountService] DB 업데이트 완료! (BASIC -> VIP)");
        conn.close();
    }

    // JDBC 조회 메서드 (매번 new Member를 함!)
    private Member findById(Long memberId) throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        String sql = "SELECT  FROM MEMBER WHERE ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setLong(1, memberId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            Member m = new Member(
                    rs.getLong("id"),
                    rs.getString("name"),
                    Grade.valueOf(rs.getString("grade"))
            );
            conn.close();
            return m; // 👈 항상 새로운 객체 리턴!
        }
        return null;
    }

    // 테스트용 DB 초기화
    private void initDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE MEMBER (ID BIGINT PRIMARY KEY, NAME VARCHAR(255), GRADE VARCHAR(255))");
        stmt.execute("INSERT INTO MEMBER VALUES (1, 'UserA', 'BASIC')");
        conn.close();
    }
}