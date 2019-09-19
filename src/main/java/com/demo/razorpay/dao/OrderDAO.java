package com.demo.razorpay.dao;

import com.demo.razorpay.models.Order;

import javax.persistence.Query;
import java.util.List;

public class OrderDAO extends AbstractDAO<Order>{

    public OrderDAO() {
        super(Order.class);
    }

    public Order findById(String id){
        return this.getEntityManager().find(Order.class, id);
    }

    public List<Order> findByUserId(String userId){
        Query query = this.getEntityManager().createQuery("SELECT o FROM Order AS o WHERE o.user.id = :userId");
        query.setParameter("userId", userId);

        return query.getResultList();
    }
}
