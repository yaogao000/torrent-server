namespace java com.drink.srv.info
struct Customer {
    1: i64 cid;
    2: i16 countryCode = 86;
    3: i32 cityId;
    4: string mobile;
    5: string nickname;
    6: byte gender = 0;	//性别(0-无; 1-男; 2-女)
    7: string password;
    8: string photo;
    9: string email;
}
struct CustomerSession {
     1: i64	cid;
     2: string	token;
     3: string	secret;//与token成对出现, 唯一对应关系, 用于做MD5计算
     4: string  aeskey;
     5: byte client;
     6: i32 cityId;
     7: double lat;
     8: double lng;
     9: i64 expireAt;
     10: byte status;
}