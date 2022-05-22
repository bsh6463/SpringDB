package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId()); //첫 번째 파라미터 바인딩
            pstmt.setInt(2, member.getMoney()); //두 번째 파라미터 바인딩
            pstmt.executeUpdate(); //쿼리가 db에서 실행됨.
            return member;
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        }finally {
            close(conn, pstmt, null);
        }


    }
    //statement : sql그대로 넣음
    //preparedStatement: 파라미터 바인딩 가능., statement상속받음.
    private void close(Connection conn, Statement stmt, ResultSet rs){

        if (rs != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

      if (stmt != null){
          try {
              stmt.close(); //SQLException 발생 가능. 예외 발생해도 아래 conn닫는데 영향 안줌.
          } catch (SQLException e) {
              log.info("error", e);
          }
      }

      if (conn != null){
          try {
              conn.close(); //coonection은 외부 resource(tcp/ip)사용함, 안닫아주면 계속 유지됨..
          } catch (SQLException e) {
              log.info("error", e); // 닫을 때 예외 터지만 할 수 있는게....ㅠ
          }
      }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }

}
