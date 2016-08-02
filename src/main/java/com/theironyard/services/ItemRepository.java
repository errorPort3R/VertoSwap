package com.theironyard.services;

import com.theironyard.entities.Item;
import com.theironyard.entities.User;
import com.theironyard.entities.Work;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Dan on 7/19/16.
 */
public interface ItemRepository extends CrudRepository<Item, Integer> {
    public Iterable<Item> findByUser(User user);

    public Iterable<Item> findByServiceTrueOrderByTimeDesc();

    public Iterable<Item> findByServiceFalseOrderByTimeDesc();

    public Iterable<Item> findByUserAndStatus(User user, Item.Status status);

    public Iterable<Item> findByUserAndStatusOrderByTimeDesc(User user, Item.Status status);

    public Iterable<Item> findByTitleLikeOrLocationLikeOrDescriptionLikeOrAcceptableExchangeLike(String title, String location, String description, String acceptableExchange);

    public Iterable<Item> findByWork(Work work);
    //

    @Query("SELECT i from Item i WHERE LOWER(title) LIKE '%' || LOWER(?) || '%' OR LOWER(location) LIKE '%' || LOWER(?) || '%' OR LOWER(description) LIKE '%' || LOWER(?) || '%' OR LOWER(acceptableExchange) LIKE '%' || LOWER(?) || '%'")
    public Iterable<Item> searchText(String title, String location, String description, String acceptableExchange);

}