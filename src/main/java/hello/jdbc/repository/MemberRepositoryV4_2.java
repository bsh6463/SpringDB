package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 *SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository{

    private final DataSource dataSource;
    private final SQLExceptionTranslator exTranslaor;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        exTranslaor = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {
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
            throw exTranslaor.translate("save", sql, e);
        }finally {
            close(conn, pstmt, null);
        }


    }

    public Member findById(String memberId){
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
            throw exTranslaor.translate("find", sql, e);
        }finally {//리소스 닫기.
            close(con, pstmt, rs);
        }

    }


    public void update(String memberId, int money) {
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
            throw exTranslaor.translate("update", sql, e);
        }finally {
            close(con, pstmt, null);
        }
    }





    public void delete(String memberId) {
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
            throw exTranslaor.translate("delete", sql, e);
        }finally {
            close(con, pstmt, null);
        }
    }


    private void close(Connection conn, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //주의! 트랜잭션 동기화 사용하려면 DataSourceUtils 사용해야함.
        DataSourceUtils.releaseConnection(conn, dataSource);

    }

    private Connection getConnection() throws SQLException {
        //주의! 트랜잭션 동기화 사용하려면 DataSourceUtils 사용해야함.
        Connection con = DataSourceUtils.getConnection(dataSource);
       log.info("get Connection={}, class={}", con, con.getClass());
        return con;
    }

}
