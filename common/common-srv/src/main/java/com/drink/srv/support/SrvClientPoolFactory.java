package com.drink.srv.support;

import java.net.InetSocketAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;

/**
 * 客户端连接池
 *
 */
public class SrvClientPoolFactory extends BasePoolableObjectFactory<TServiceClient>{

    private final SrvNodeProvider addressProvider;
    
    private final TServiceClientFactory<TServiceClient> clientFactory;
    
    private PoolOperationCallBack callback;
    
    private String truststore;
    
    private String truststorePwd;

    protected SrvClientPoolFactory(SrvNodeProvider addressProvider,TServiceClientFactory<TServiceClient> clientFactory) throws Exception {
        this.addressProvider = addressProvider;
        this.clientFactory = clientFactory;
    }
    
    protected SrvClientPoolFactory(SrvNodeProvider addressProvider,TServiceClientFactory<TServiceClient> clientFactory,PoolOperationCallBack callback) throws Exception {
        this.addressProvider = addressProvider;
        this.clientFactory = clientFactory;
        this.callback = callback;
    }

    protected SrvClientPoolFactory(SrvNodeProvider addressProvider,TServiceClientFactory<TServiceClient> clientFactory,PoolOperationCallBack callback,String truststore,String truststorePwd) throws Exception {
        this.addressProvider = addressProvider;
        this.clientFactory = clientFactory;
        this.callback = callback;
        this.truststore = truststore;
        this.truststorePwd = truststorePwd;
        
    }


    @Override
    public TServiceClient makeObject() throws Exception {
    	TServiceClient client = null;
        InetSocketAddress address = addressProvider.get();
    	if(StringUtils.isNotBlank(truststore) && StringUtils.isNotBlank(truststorePwd)){
            TSSLTransportParameters params = new TSSLTransportParameters();
            params.setTrustStore(truststore, truststorePwd, "SunX509", "JKS");
            /*
             * Get a client transport instead of a server transport. The connection is opened on
             * invocation of the factory method, no need to specifically call open()
             */
            TTransport transport = TSSLTransportFactory.getClientSocket(address.getHostName(),address.getPort(), 0, params);
            TProtocol protocol = new  TBinaryProtocol(transport);
	        client = this.clientFactory.getClient(protocol);
    	}
    	else{
	        TSocket tsocket = new TSocket(address.getHostName(),address.getPort());
	        TProtocol protocol = new TBinaryProtocol(tsocket);
	        client = this.clientFactory.getClient(protocol);
	        tsocket.open();
    	}
        if(callback != null){
        	try{
        		callback.make(client);
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        return client;
    }

    public void destroyObject(TServiceClient client) throws Exception {
    	if(callback != null){
    		try{
    			callback.destroy(client);
    		}catch(Exception e){
    			//
    			e.printStackTrace();
    		}
        }
    	TTransport pin = client.getInputProtocol().getTransport();
    	pin.close();
    }

    public boolean validateObject(TServiceClient client) {
    	TTransport pin = client.getInputProtocol().getTransport();
    	return pin.isOpen();
    }
    
    
    static interface PoolOperationCallBack {
    	//销毁client之前执行
    	void destroy(TServiceClient client);
    	//创建成功是执行
    	void make(TServiceClient client);
    }

}
