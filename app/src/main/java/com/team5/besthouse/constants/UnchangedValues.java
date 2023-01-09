package com.team5.besthouse.constants;

public class UnchangedValues {

    public static final String ACCOUNT_CREATED_INTENT = "accountCreated";
    public static final String LOGOUT_PERFORMED = "logout";


    public static final String USERS_TABLE = "users";
    public static final String PROPERTIES_TABLE = "properties";
    public static final String CONTRACTS_TABLE = "contracts";

    public static final String NAME_REGEX = "^[a-zA-Z\\s]+";
    public static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final String PASSWORD_REGEX ="^[a-zA-Z\\s]+";
    public static final String PHONE_REGEX = "(84|0[3|5|7|8|9])+([0-9]{8})\\b";

    public static final String USER_ID_COL = "userId";
    public static final String USER_NAME_COL =  "fullName";
    public static final String USER_EMAIL_COL = "email" ;
    public static final String USER_PASS_COL = "password";
    public static final String USER_PHONE_COL = "phoneNumber";
    public static final String USER_ROLE = "role";

    public static final String IS_LOGIN_TENANT = "tenantLoginSession";
    public static final String IS_LOGIN_LANDLORD = "landlordLoginSession";

    public static final String LOGIN_USER = "loginUser";


    //Landlord add property activity
    public static final String LOCATION_ADDRESS = "la";
    public static final String ACTIVITY_REQUEST_CODE = "requestCode";
    public static final String SELECT_LOCATION = "Select Location";
    public static final String PROPERTY_TABLE= "properties";


    public static final String PLACES_API_KEY = "AIzaSyBv1GNlSbCCAeBxZZIEBA1K9HH5UWgAdFw";

}
