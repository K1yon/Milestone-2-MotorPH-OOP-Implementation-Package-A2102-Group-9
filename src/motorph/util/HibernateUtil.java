package motorph.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class HibernateUtil {

    private static final EntityManagerFactory EMF;

    static {
        try {
            EMF = Persistence.createEntityManagerFactory("motorph-pu");
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManager getEM() {
        return EMF.createEntityManager();
    }

    public static void close() {
        if (EMF != null && EMF.isOpen()) EMF.close();
    }

    private HibernateUtil() {}
}