package lesson.hibernate;

import lesson.hibernate.dao.user.UserDao;
import lesson.hibernate.dao.user.UserDaoImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        UserDao userDao = new UserDaoImpl();

        String login = "alex_" + RandomUtils.nextInt();
        String password = "as1244sf_" + RandomUtils.nextInt();
        String hexPassword = DigestUtils.md5Hex(password);

        log.info("Creating user with login = '{}'", login);
        log.info(userDao.create(login, hexPassword).toString());
        log.info("--");

        log.info("Finding all users...");
        log.info(userDao.findAll().toString());
        log.info("--");

        log.info("Finding user by login...");
        log.info(userDao.findByLogin(login).toString());
        log.info("--");

        log.info("Disabling user by login...");
        userDao.disable(login);
        log.info("--");

        log.info("Finding all users with limit 100");
        log.info(userDao.findAll(100).toString());
        log.info("--");
    }
}
