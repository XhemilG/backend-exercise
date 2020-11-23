package actions;

import models.User;
import play.libs.typedmap.TypedKey;

public class Attributes {
    //public static final TypedKey<Taxi> TAXI_TYPED_KEY = TypedKey.<Taxi>create("taxi");
    public static final TypedKey TYPED_KEY = TypedKey.create("user");
    public static final TypedKey<String> AUTHENTICATION_TYPED_KEY = TypedKey.create("token");
}