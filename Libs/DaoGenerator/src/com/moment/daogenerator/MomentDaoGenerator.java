package com.moment.daogenerator;

import de.greenrobot.daogenerator.*;

public class MomentDaoGenerator {

    public static void main(String[] args) throws Exception {

        Schema schema = new Schema(1, "com.moment.models");
        schema.enableKeepSectionsByDefault();

        /**
         * Moment
         */

        Entity moment = schema.addEntity("Moment");
        moment.setTableName("moments");

        moment.addIntProperty("id").index();

        moment.addIntProperty("state");
        moment.addIntProperty("guestNumber");
        moment.addIntProperty("guestComing");
        moment.addIntProperty("guestNotComing");
        moment.addIntProperty("privacy");

        moment.addStringProperty("name");
        moment.addStringProperty("description");
        moment.addStringProperty("placeInformations");
        moment.addStringProperty("infoTransport");
        moment.addStringProperty("hashtag");
        moment.addStringProperty("adresse");
        moment.addStringProperty("keyBitmap");
        moment.addStringProperty("urlCover");

        moment.addDateProperty("dateDebut");
        moment.addDateProperty("dateFin");

        moment.addBooleanProperty("isOpenInvit");

        /**
         * User
         */

        Entity user = schema.addEntity("User");
        user.setTableName("users");
        user.addIdProperty().index();

        user.addIntProperty("facebookId");
        user.addIntProperty("nbFollows");
        user.addIntProperty("nbFollowers");

        user.addStringProperty("email");
        user.addStringProperty("secondEmail");
        user.addStringProperty("firstName");
        user.addStringProperty("lastName");
        user.addStringProperty("pictureProfileUrl");
        user.addStringProperty("keyBitmap");
        user.addStringProperty("numTel");
        user.addStringProperty("secondNumTel");
        user.addStringProperty("fbPhotoUrl");
        user.addStringProperty("idCarnetAdresse");
        user.addStringProperty("description");

        user.addBooleanProperty("isSelect");

/*        *//**//**
         * Photos
         *//**//*

        Entity photo = schema.addEntity("Photo");
        photo.setTableName("photos");
        photo.addIdProperty().index();

        photo.addIntProperty("nbLike");

        photo.addStringProperty("urlOriginal");
        photo.addStringProperty("urlThumbnail");

        *//**//**
         * Chats
         *//**//*

        Entity chat = schema.addEntity("Chat");
        chat.setTableName("chats");
        chat.addIdProperty();

        chat.addStringProperty("message");

        chat.addDateProperty("date");

        *//**//**
         * Adresses
         *//**//*

        Entity adresse = schema.addEntity("Adresse");
        adresse.setTableName("adresses");

        adresse.addIntProperty("codePostal");

        adresse.addStringProperty("numeroRue");
        adresse.addStringProperty("Ville");

        *//**//**
         * FbEvent
         *//**//*

        Entity fbEvent = schema.addEntity("FbEvent");
        fbEvent.setTableName("fbevents");

        fbEvent.addStringProperty("id");
        fbEvent.addStringProperty("title");
        fbEvent.addStringProperty("startTime");
        fbEvent.addStringProperty("location");

        *//**//**
         * Place
         *//**//*

        Entity place = schema.addEntity("Place");
        place.setTableName("places");

        place.addStringProperty("placeOne");
        place.addStringProperty("placeTwo");
        place.addStringProperty("placeThree");

        *//**//**
         * Relations
         *//**//*

        setRelationToOne(photo, user, "userId");
        setRelationToMany(user, photo, "photoId");

        setRelationToOne(chat, user, "userId");
        setRelationToMany(user, chat, "chatId");
     */
        setRelationToOne(moment, user, "userId");
        setRelationToMany(user, moment, "momentId");


        //new DaoGenerator().generateAll(schema, "../momentandroid/Moment/src/");
        new DaoGenerator().generateAll(schema, "../momentandroid/Libs/DaoGenerator/src-gen/");
    }

    private static void setRelationToOne(Entity entity, Entity entity2, String propertyName) {
        Property.PropertyBuilder propertyBuilder = entity.addProperty(PropertyType.Long, propertyName).notNull();
        Property property = propertyBuilder.getProperty();
        entity.addToOne(entity2, property);
    }

    private static void setRelationToMany(Entity entity, Entity entity2, String propertyName) {
        Property.PropertyBuilder propertyBuilder = entity.addProperty(PropertyType.Long, propertyName).notNull();
        Property property = propertyBuilder.getProperty();
        entity.addToMany(entity2, property);
    }
}