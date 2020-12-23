package com.github.arlekinside;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Users implements Serializable{
    private long id;
    private String botStatus = "read";
    private static String path = "/tmp/";
    private String userPath = null;
    private String access_token = null;
    private String scope = null;
    private String refresh_token = null;
    private Date expirationDate = null;
    protected Users(long id) {
        setId(id);
        userPath = path + id + ".dat";
    }

    public void writeUser() {
        try{
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(userPath));
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Users getUser(long id) {
        Users user = null;
        try{
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(path + id + ".dat"));
            user = (Users) objectInputStream.readObject();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }
    public static boolean userExists(long id){
        boolean exists = false;
        File file = new File(path + id + ".dat");
        if(file.exists()){
            exists = true;
        }
        return exists;
    }
    public Users setId(long id) {
        this.id = id;
        return this;
    }

    public long getId() {
        return id;
    }

    public Users setBotStatus(String botStatus) {
        this.botStatus = botStatus;
        return this;
    }

    public String getBotStatus() {
        return botStatus;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public Users setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
        return this;
    }

    public String getAccess_token() {
        return access_token;
    }

    public Users setAccess_token(String access_token) {
        this.access_token = access_token;
        return this;
    }
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
