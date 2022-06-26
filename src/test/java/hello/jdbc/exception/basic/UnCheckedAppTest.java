package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

public class UnCheckedAppTest {

    @Test
    void checked(){
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request()).isInstanceOf(Exception.class);
    }

    static class Controller{
        Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Service{
        Repository repository= new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient{
        public void call() throws ConnectException {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class Repository{
        public void call() throws SQLException {
            try{
                runSQL();
            }catch (SQLException e){
                //기존예외를 넣어줘야 stackTrace가능
                //runtimeException으로 바꿔버리기
                throw new RuntimeConnectException(e);
            }
        }

        public void runSQL() throws SQLException {
            //checkedException
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException{
        public RuntimeConnectException(Throwable cause) {
            super(cause);
        }

        public RuntimeConnectException(String message) {
            super(message);
        }
    }
}
