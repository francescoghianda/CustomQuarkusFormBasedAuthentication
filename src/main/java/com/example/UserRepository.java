package com.example;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class UserRepository {

    private EntityManager em;

    @Inject
    public UserRepository(EntityManager em){
        this.em = em;
    }

    public User findUser(String username){
        return em.createQuery("select u from User u where u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
    }

}
