package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC -Connection Param
 */
@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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

    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "select * from member where member_id=?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
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
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            //connection은 여기서 닫지 않음.
        }

    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money); //첫 번째 파라미터 바인딩
            pstmt.setString(2, memberId); //두 번째 파라미터 바인딩
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }

    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        PreparedStatement pstmt = null;

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money); //첫 번째 파라미터 바인딩
            pstmt.setString(2, memberId); //두 번째 파라미터 바인딩
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        }finally {
            JdbcUtils.closeStatement(pstmt);
            //connection은 여기서 닫지 않음.
        }
    }



    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }


    private void close(Connection conn, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(conn);
    }

    private Connection getConnection() throws SQLException {
       Connection con =  dataSource.getConnection();
       log.info("get Connection={}, class={}", con, con.getClass());
        return con;
    }

}
