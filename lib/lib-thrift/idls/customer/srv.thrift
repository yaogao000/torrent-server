include "info.thrift"
include "../common/exp.thrift"
namespace java com.drink.srv

service CustomerSrv {
	map<string,string> login(1: required string phone, 2: required map<string,string> customerSession) throws(1: exp.SrvException ex);

	string getSecretByToken(1: required string token) throws(1: exp.SrvException ex);

	i16 signIn(1: required string phone, 2: required string password);
}
