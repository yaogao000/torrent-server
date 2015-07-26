package com.drink.srv.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.drink.srv.support.SrvClientPoolFactory.PoolOperationCallBack;
import com.drink.srv.support.impl.SimpleSrvNodeProviderImpl;

public class SrvServiceClientProxyFactory implements FactoryBean,InitializingBean {
	
	private static final Logger logger = LoggerFactory.getLogger(SrvServiceClientProxyFactory.class);
	
    private String service;

    private String serverAddress;
    
	private Integer maxActive = 32;
	
	private Integer maxIdle = 16;
    
	//3 mins, -1表示关闭空闲检测
    private Integer idleTime = 3 * 60 * 1000;
    private SrvNodeProvider addressProvider;
    
    private String truststore;
    
	private String truststorePwd;

	private Object proxyClient;
	
	private Class objectClass;
    
    private GenericObjectPool<TServiceClient> pool;

    public void setTruststore(String truststore) {
		this.truststore = truststore;
	}


	public void setTruststorePwd(String truststorePwd) {
		this.truststorePwd = truststorePwd;
	}

    public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}

    public void setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setIdleTime(Integer idleTime) {
		this.idleTime = idleTime;
	}


	public void setService(String service) {
		this.service = service;
	}


    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }


    public void setAddressProvider(SrvNodeProvider addressProvider) {
		this.addressProvider = addressProvider;
	}    
    
    private PoolOperationCallBack callback = new PoolOperationCallBack() {
		
		@Override
		public void make(TServiceClient client) {
			logger.info("Create a thrift srv client pool!");
			
		}
		
		@Override
		public void destroy(TServiceClient client) {
			logger.info("Destroy a thrift srv client pool!");
			
		}
	};

    @Override
    public void afterPropertiesSet() throws Exception {
        if(serverAddress != null){
            addressProvider = new SimpleSrvNodeProviderImpl(serverAddress);
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        objectClass = classLoader.loadClass(service + "$Iface");
        Class<TServiceClientFactory<TServiceClient>> fi = (Class<TServiceClientFactory<TServiceClient>>)classLoader.loadClass(service + "$Client$Factory");
        TServiceClientFactory<TServiceClient> clientFactory = fi.newInstance();
        
        SrvClientPoolFactory clientPool = null;
        if(StringUtils.isNotBlank(truststore) && StringUtils.isNotBlank(truststorePwd)){
        	clientPool = new SrvClientPoolFactory(addressProvider, clientFactory,callback,truststore, truststorePwd);
        }
        else{
        	clientPool = new SrvClientPoolFactory(addressProvider, clientFactory,callback);
        }
        
        GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
        poolConfig.maxActive = maxActive;
        poolConfig.maxIdle = maxIdle;
        poolConfig.minIdle = 0;
        poolConfig.minEvictableIdleTimeMillis = idleTime;
        poolConfig.timeBetweenEvictionRunsMillis = idleTime/2L;
        pool = new GenericObjectPool<TServiceClient>(clientPool,poolConfig);
        //Frank: 当addressProvider发生变化(节点增加)时,清理连接池,以便重新生成; 
        addressProvider.setSrvNodeProviderChangeListener(new SrvNodeProviderChangeListener() {
			@Override
			public void onChanged(List<InetSocketAddress> srvs) {
				logger.info("Clean up the connection pool when the node changes, srv nodes=" + srvs.toString()); 
				pool.clear();
			}
		});
        
        proxyClient = Proxy.newProxyInstance(classLoader,new Class[]{objectClass},new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            	//
            	TServiceClient client = pool.borrowObject();
            	try{
            		return method.invoke(client, args);
            	}
            	catch(Exception e){
            		//如果是SrvException 表示业务异常，不处理回收业务对象，直接抛到上层
            		if(e instanceof java.lang.reflect.InvocationTargetException){
            			InvocationTargetException ite = (InvocationTargetException)e;
            			if( ite.getTargetException() instanceof SrvException){
            				SrvException ex = (SrvException)ite.getTargetException();
            				if(SrvRet.SYSTEM_ERROR.code == ex.code){
            					//服务调用时发生的系统错误
            					SrvSystemException exception= new SrvSystemException(ex.data, ex.msg);
            					//重新抛出SrvSystemException以便与SrvException区别
            					throw exception;
            					
            				}
            				throw ite.getTargetException();
            			}
            			//TODO 如果出现业务异常导致太频繁回收，将来可考虑将TTransportException.END_OF_FILE异常也在这里处理, 
            			//			只有TTransportException.NOT_OPEN,TTransportException.UNKNOWN 要做pool.invalidateObject
            		}
            		//调用失败,回收请求
            		pool.invalidateObject(client);
            		client = null;
            		throw e;
            	}finally{
            		if(client != null){
            			pool.returnObject(client);
            		}
            	}
            }
        });
      //等待5s, 初始化zookeeper结束
        Thread.sleep(5 * 1000); 
    }

    @Override
    public Object getObject() throws Exception {
        return proxyClient;
    }

    @Override
    public Class<?> getObjectType() {
        return objectClass;
    }

    @Override
    public boolean isSingleton() {
        return true;  
    }
    
    public void close(){
    	if(addressProvider != null){
    		addressProvider.close();
    	}
    }
}
