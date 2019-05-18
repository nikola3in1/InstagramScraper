package com.nikola2934.db;

import com.nikola2934.model.entities.Follower;
import com.nikola2934.model.entities.Model;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.HashSet;

public class Db <T extends Model>{

    private static Session ssObj;
    private static SessionFactory sessionFactoryObj;
    private static Db intance = new Db();

    public static Db getInstace() {
        return intance;
    }

    private Db() {
    }

    private static SessionFactory buildSessionFactory() {
        // Creating Configuration Instance & Passing Hibernate Configuration File
        Configuration configObj = new Configuration();
        configObj.configure("hibernate.cfg.xml");

        // Since Hibernate Version 4.x, ServiceRegistry Is Being Used
        ServiceRegistry serviceRegistryObj = new StandardServiceRegistryBuilder().applySettings(configObj.getProperties()).build();

        // Creating Hibernate SessionFactory Instance
        sessionFactoryObj = configObj.buildSessionFactory(serviceRegistryObj);
        return sessionFactoryObj;
    }

    public void save(Model obj) {
        try {
            ssObj = buildSessionFactory().openSession();
            ssObj.beginTransaction();
            ssObj.saveOrUpdate(obj);
//            ssObj.evict(obj);
            ssObj.getTransaction().commit();
            System.out.println("\n\n.......Records Saved Successfully To The Database.......\n\n");
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
            if (null != ssObj.getTransaction()) {
                System.out.println("\n.......Transaction Is Being Rolled Back.......");
                ssObj.getTransaction().rollback();
            }
            sqlException.printStackTrace();
        } finally {
            if (ssObj != null) {
                ssObj.close();
            }
        }
    }

    public HashSet<T> read(Class type){
        //Gets objects from db by given type
        HashSet<T> targets = new HashSet<>();
        try {
            ssObj = buildSessionFactory().openSession();
            ssObj.beginTransaction();
            targets.addAll(ssObj.createCriteria(type).list());
        } catch (Exception sqlException) {
            if (null != ssObj.getTransaction()) {
                System.out.println("\n.......Transaction Is Being Rolled Back.......");
                ssObj.getTransaction().rollback();
            }
            sqlException.printStackTrace();
        }finally {
            if (ssObj != null) {
                ssObj.close();
            }
        }
        return targets;
    }

    public void saveFollowers(HashSet<Follower> followers) {
        System.out.println(".......Hibernate Maven Example.......\n");
        try {
            ssObj = buildSessionFactory().openSession();
            ssObj.beginTransaction();

            for (Follower f : followers) {
                ssObj.save(f);
            }

            System.out.println("\n.......Records Saved Successfully To The Database.......\n");

            // Committing The Transactions To The Database
            ssObj.getTransaction().commit();
        } catch (Exception sqlException) {
            if (null != ssObj.getTransaction()) {
                System.out.println("\n.......Transaction Is Being Rolled Back.......");
                ssObj.getTransaction().rollback();
            }
            sqlException.printStackTrace();
        } finally {
            if (ssObj != null) {
                ssObj.close();
            }
        }
    }

    public void stop(){
        sessionFactoryObj.close();
    }

}
