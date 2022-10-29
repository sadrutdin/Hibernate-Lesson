package lesson.hibernate.dao.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lesson.hibernate.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserDaoImpl implements UserDao {
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    private final EntityManager em = Persistence.createEntityManagerFactory("default")
            .createEntityManager();

    @Override
    public List<User> findAll() {
        em.getTransaction().begin();
        List<User> list = em.createQuery("from User u where u.enabled is true order by u.id", User.class)
                .getResultList();
        em.getTransaction().commit();
        return list;
    }

    @Override
    public List<User> findAll(int limit) {
        em.getTransaction().begin();
        List<User> list = em.createQuery("from User u where u.enabled is true order by u.id", User.class)
                .setMaxResults(limit)
                .getResultList();
        em.getTransaction().commit();
        return list;
    }

    @Override
    public User findById(Long id) {
        em.getTransaction().begin();
        User result = em.find(User.class, id);
        em.getTransaction().commit();
        return result.isEnabled() ? result : null;
    }

    @Override
    public User findByLogin(String login) {
        em.getTransaction().begin();
        User result = em.createQuery("from User u where u.login = :login and u.enabled is true", User.class)
                .setParameter("login", login)
                .getSingleResult();
        em.getTransaction().commit();
        return result;
    }

    @Override
    public User create(String login, String password) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        return user;
    }

    @Override
    public void disable(String login) {
        em.getTransaction().begin();

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaUpdate<User> query = builder.createCriteriaUpdate(User.class);
        Root<User> root = query.from(User.class);

        Predicate predicate = builder.and(
                builder.equal(root.get("login"), login),
                builder.isTrue(root.get("enabled"))
        );

        query.set(root.get("enabled"), false)
                .where(predicate);


        int updatedCount = em.createQuery(query)
                .executeUpdate();// Возвращает количество обновленных элементов
        log.trace("Updated count for user by login: login = '{}', count = '{}'", login, updatedCount);

        /*
        Альтернативная реализация
        --
        User user = em.createQuery("from User u where u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult();
        user.setEnabled(false);
        em.merge(user);
        */

        em.getTransaction().commit();
    }
}
