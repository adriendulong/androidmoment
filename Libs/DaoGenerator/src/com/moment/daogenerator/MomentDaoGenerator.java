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

        moment.addIdProperty();

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
        moment.addStringProperty("uniqueUrl");

        moment.addStringProperty("dateDebut");
        moment.addStringProperty("dateFin");

        moment.addBooleanProperty("isOpenInvit");

        /**
         * User
         */

        Entity user = schema.addEntity("User");
        user.implementsInterface("Parcelable");
        user.setTableName("users");

        user.addIdProperty();

        user.addLongProperty("facebookId");

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
        user.addStringProperty("adress");

        user.addBooleanProperty("isSelect");

        /**
         * Chats
         */

        Entity chat = schema.addEntity("Chat");
        chat.setTableName("chats");
        chat.implementsInterface("Parcelable");

        chat.addIdProperty();

        chat.addStringProperty("message");

        chat.addDateProperty("date");

         /**
         * Photos
         */

        Entity photo = schema.addEntity("Photo");
        photo.setTableName("photos");
        photo.implementsInterface("Parcelable");
        photo.addIdProperty().index();

        photo.addIntProperty("nbLike");

        photo.addStringProperty("urlOriginal");
        photo.addStringProperty("urlThumbnail");
        photo.addStringProperty("urlUnique");

        photo.addDateProperty("time");

        /**
         * Notifications
         */

        Entity notification = schema.addEntity("Notification");
        notification.setTableName("notifications");

        notification.addIntProperty("typeNotif");

        notification.addDateProperty("time");

        /**
         * Relations
         */

        /*** Moment Relations ***/

        Property momentHasOneOwner = moment.addLongProperty("ownerId").notNull().getProperty();
        moment.addToOne(user, momentHasOneOwner);

        Property momentHasManyUsers = moment.addLongProperty("userId").getProperty();
        ToMany momentToUser = moment.addToMany(user, momentHasManyUsers);
        momentToUser.setName("users");

        Property momentHasManyPhotos = moment.addLongProperty("photoId").getProperty();
        ToMany momentToPhotos = moment.addToMany(photo, momentHasManyPhotos);
        momentToPhotos.setName("photos");

        Property momentHasManyChats = moment.addLongProperty("chatId").getProperty();
        ToMany momentToChat = moment.addToMany(chat, momentHasManyChats);
        momentToChat.setName("chats");

        /*** User Relations ***/

        Property userHasManyMoments = user.addLongProperty("momentId").getProperty();
        ToMany userToMoments = user.addToMany(moment, userHasManyMoments);
        userToMoments.setName("moments");

        Property userHasManyNotifications = user.addLongProperty("notificationId").getProperty();
        ToMany userToNotifications = user.addToMany(notification, userHasManyNotifications);
        userToNotifications.setName("notifications");

        Property userHasManyInvitations = user.addLongProperty("invitationsId").getProperty();
        ToMany userToInvitations = user.addToMany(notification, userHasManyInvitations);
        userToInvitations.setName("invitations");

        /*** Chat Relation ***/

        Property chatHasOneOwner = chat.addLongProperty("userId").notNull().getProperty();
        chat.addToOne(user, chatHasOneOwner);

        Property chatHasOneMoment = chat.addLongProperty("momentId").notNull().getProperty();
        chat.addToOne(moment, chatHasOneMoment);

        /*** Photo Relation ***/

        Property photoHasOneOwner = photo.addLongProperty("userId").notNull().getProperty();
        photo.addToOne(user, photoHasOneOwner);

        Property photoHasOneMoment = photo.addLongProperty("momentId").getProperty();
        photo.addToOne(moment, photoHasOneMoment);

        /*** Notification Relation ***/

        Property notificationHasOneOwner = notification.addLongProperty("userId").notNull().getProperty();
        notification.addToOne(user, notificationHasOneOwner);

        Property notificationHasOneMoment = notification.addLongProperty("momentId").notNull().getProperty();
        notification.addToOne(moment, notificationHasOneMoment);

        /**
         * EOD
         */

        new DaoGenerator().generateAll(schema, "../androidmoment/Moment/src/");
    }
}