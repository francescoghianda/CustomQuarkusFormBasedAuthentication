package com.example;

import com.security.service.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class UserRepository implements com.security.service.UserRepository {

    private final EntityManager em;

    @Inject
    public UserRepository(EntityManager em){
        this.em = em;
    }

    @Override
    public UserEntity findByUsername(String username) {
        return em.createQuery("select u from User u where u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
    }
}
