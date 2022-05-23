package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

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

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
           con =  getConnection();
           pstmt = con.prepareStatement(sql);
           pstmt.setString(1, memberId);
           rs = pstmt.executeQuery();//select는 executeQuery
            if (rs.next()){//데이터 있으면 true, 내부 커서를 한번 호출을 해야 데이터가 있는 곳으로 이동함.
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));

                return member;
            }else{//데이터 없는 경우
                 throw new NoSuchElementException("member not fond memberId="+memberId);
            }
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        }finally {//리소스 닫기.
            close(con, pstmt, rs);
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
