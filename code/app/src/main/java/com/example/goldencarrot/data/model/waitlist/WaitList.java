package com.example.goldencarrot.data.model.waitlist;

import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.UserImpl;

import java.util.ArrayList;

/**
 * The {@code WaitList} class represents a waiting list for an event.
 * It stores the event associated with the waitlist and the list of users
 * who are on the waitlist.
 */
public class WaitList implements WaitListConfigurator{
    private Event event;
    private ArrayList<UserImpl> userArrayList;
    private int limitNumber;

    /**
     * Constructs a new {@code WaitList} object with the specified event and user list.
     *
     * @param event the event associated with the waitlist
     *
     */
    public WaitList(Event event) {
        this.event = event;
        this.userArrayList = new ArrayList<UserImpl>();

    }


    @Override
    public ArrayList<UserImpl> getUserArrayList() {
        return userArrayList;
    }

    @Override
    public Event getEvent() {
        return event;
    }

    @Override
    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void setUserArrayList(ArrayList<UserImpl> userArrayList) {
        this.userArrayList = userArrayList;
    }

    @Override
    public void setLimitNumber(int limitNumber) {
        this.limitNumber = limitNumber;
    }

    @Override
    public int getLimitNumber() {
        return limitNumber;
    }


    /**
     * Returns True if User was added, False if waitList is Full.
     * @param user to add to the list
     * @return boolean
     */
    @Override
    public boolean addUserToWaitList(final UserImpl user) {
        if (isFull()){
            return Boolean.FALSE;
        } else {
            userArrayList.add(user);
            return Boolean.TRUE;
        }
    }

    public boolean isFull(){
        return this.userArrayList.size() == this.limitNumber;
    }

    public void removeUserFromWaitList(UserImpl user){
        this.userArrayList.remove(user);
    }


}
