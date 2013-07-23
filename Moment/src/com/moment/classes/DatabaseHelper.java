package com.moment.classes;

import com.moment.AppMoment;
import com.moment.models.Moment;
import com.moment.models.User;

import java.util.List;

public class DatabaseHelper {

    public static Moment getMomentByIdFromDataBase(Long id){
        return AppMoment.getInstance().momentDao.load(id);
    }

    public static List<Moment> getMomentsFromDataBase(){
        List queryMoments =  AppMoment.getInstance().momentDao.queryBuilder().list();
        return queryMoments;
    }

    public static User getUserByIdFromDataBase(Long id){
        return AppMoment.getInstance().userDao.load(id);
    }

    public static List<User> getUsersFromDataBase(){
        List queryUser = AppMoment.getInstance().userDao.queryBuilder().list();
        return queryUser;
    }

    public static void addMoment(Moment moment){
        AppMoment.getInstance().daoSession.insert(moment);
        AppMoment.getInstance().user.getMoments().add(moment);
    }

    public static  void removeMoment(Moment moment){
        AppMoment.getInstance().momentDao.delete(moment);
    }

    public static  void removeUser(User user){
        AppMoment.getInstance().userDao.delete(user);
    }

}
