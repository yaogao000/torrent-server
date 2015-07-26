namespace java com.drink.srv.info


struct Response {
    1: required i32 code;
    2: optional string msg;
    3: optional string data;
}
