package lesson.hibernate.dao.user;

import lesson.hibernate.domain.User;

import java.util.List;

public interface UserDao {
    List<User> findAll();

    List<User> findAll(int limit);

    User findById(Long id);

    User findByLogin(String login);

    User create(String login, String password);

    void disable(String login);
}
