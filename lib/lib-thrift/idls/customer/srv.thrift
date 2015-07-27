include "info.thrift"
include "../common/exp.thrift"
namespace java com.drink.srv

service CustomerSrv {
	info.CustomerSession login(1: required string phone, 2: optional string password, 3: required i16 countryCode, 4: required info.CustomerSession customerSession) throws(1: exp.SrvException ex);

	string getSecretByToken(1: required string token) throws(1: exp.SrvException ex);

	#i16 signIn(1: required string phone, 2: required string password);
}
