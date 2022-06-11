package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * transaction - parameter 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false); //수동커밋 지정 및 transaction 시작

            bizLogic(con, fromId, toId, money);

            con.commit(); //성공시 commit

        }catch (Exception e){
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        }finally {
            release(con);
        }


    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        //비즈니스 로직
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney()+ money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private void release(Connection con) {
        if (con != null){
            try{
                con.setAutoCommit(true); //autoCommit으로 바꿔놓고
                con.close(); //connection pool에 반환.
            }catch (Exception e){
                log.info("error", e);
            }
        }
    }
}



