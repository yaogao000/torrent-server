package com.drink.srv.support;

import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.annotation.processing.Processor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.drink.srv.support.impl.LocalNetworkIpTransfer;

/**
 * 服务提供者Factory
 */
public class SrvServiceServerFactory implements InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(SrvServiceServerFactory.class);

	private Integer port;

	private Integer priority = 1;// default

	private Object service;// service 实现类

	private SrvServerIpTransfer ipTransfer;

	private SrvNodeRegister addressRegister;

	private ServerThread serverThread;

	private String keystore;

	private String keystorePwd;

	public void setKeystorePwd(String keystorePwd) {
		this.keystorePwd = keystorePwd;
	}

	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}

	public void setService(Object service) {
		this.service = service;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setIpTransfer(SrvServerIpTransfer ipTransfer) {
		this.ipTransfer = ipTransfer;
	}

	public void setAddressRegister(SrvNodeRegister addressReporter) {
		this.addressRegister = addressReporter;
	}


    @Override
    public void afterPropertiesSet() throws Exception  {
		if (ipTransfer == null) {
			ipTransfer = new LocalNetworkIpTransfer();
		}
		
		if(this.port <= 0){
			throw new Exception("Exception: port is " + this.port);
		}
		
		String ip = ipTransfer.getIp();

		if (ip == null) { throw new NullPointerException("Can't find server ip!"); }
		String hostname = ip + ":" + port + ":" + priority;
		Class serviceClass = service.getClass();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Class<?>[] interfaces = serviceClass.getInterfaces();
		if (interfaces.length == 0) { throw new IllegalClassFormatException("Service-class should implements Iface"); }

		// reflect,load "Processor";
		TProcessor processor = null;
		for (final Class clazz : interfaces) {
            String cname = clazz.getSimpleName();
            if (!cname.equals("Iface")) {
                continue;
            }
            String pname = clazz.getEnclosingClass().getName() + "$Processor";
            try {
                Class pclass =  classLoader.loadClass(pname);

                if(!TProcessor.class.isAssignableFrom(pclass)){
                    continue;
                }

                Constructor constructor = pclass.getConstructor(clazz);
                /**加入代理对象，进行错误处理*/
//				break;
				
				Object proxyService = Proxy.newProxyInstance(classLoader,new Class[]{clazz},new InvocationHandler() {
		            @Override
		            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		            	//
		            	try{
		            		Object ret = method.invoke(service, args);
		            		return ret;
		            	}catch(Throwable e){
		            		//过滤TProcessor方法
		            		
		            		Throwable realEx = null;
		            		//如果是SrvException 表示业务异常，不处理回收业务对象，直接抛到上层
		            		if(e instanceof java.lang.reflect.InvocationTargetException){
		            			InvocationTargetException ite = (InvocationTargetException)e;
		            			realEx = ite.getTargetException();
		            			/**如果本身即是SrvException即刻抛出错误*/
		            			if( realEx != null && realEx instanceof SrvException){
		            				throw ite.getTargetException();
		            			}
		            			
		            		}else{
		            			realEx = e;
		            		}
		            		logger.error("thrift 服务实例["+clazz.getName()+"]调用时发生系统错误", e);
		            		//打印所有的异常栈
		            		String str = ExceptionUtils.getFullStackTrace(realEx);
		            		if(str == null)str = "empty exception stack!!";
		            		SrvException retEx = new SrvException();
		            		retEx.code = SrvRet.SYSTEM_ERROR.code;
		            		retEx.msg = e.getMessage();
		            		retEx.data = str;
		            		throw retEx;
		            	}
		            }
		        });
				processor= (TProcessor) constructor.newInstance(proxyService);
//                processor = (TProcessor)constructor.newInstance(service);

                break;
            } catch (ClassNotFoundException e) {
                //logger.error(e);
            }

        }


		if (processor == null) { throw new IllegalClassFormatException("Service-class should implements Iface"); }
		// 需要单独的线程,因为serve方法是阻塞的.
		if(StringUtils.isNotBlank(keystore) && StringUtils.isNotBlank(keystorePwd)){
			serverThread = new SSLServerThread(processor, port, keystore, keystorePwd);
			serverThread.start();
			logger.info("Starting thrift ssl service, listen to port " + port + "..");
		}
		else{
			serverThread = new ServerThread(processor, port);
			serverThread.start();
			logger.info("Starting thrift service, listen to port " + port + "..");
		}
		
		if (addressRegister != null) {
			addressRegister.register(hostname);

		}

	}


    class ServerThread extends Thread {
		protected TThreadPoolServer server;

		protected ServerThread() {}

		protected ServerThread(TProcessor processor, int port) throws Exception {
			// Frank: 这里不使用TNonblockingServer,因为client与server都做pool，那么就不需要使用非阻塞做分发
			TServerSocket serverTransport = new TServerSocket(port);
			// 待TCompactProtocol压测
			Factory portFactory = new Factory(true, true);
			TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
			args.processor(processor);
			args.protocolFactory(portFactory);
			server = new TThreadPoolServer(args);

		}

		@Override
		public void run() {
			try {
				server.serve();
			}
			catch (Exception e) {
				//
			}
		}

		public void stopServer() {
			server.stop();
		}
	}

	    class SSLServerThread extends ServerThread {
        protected SSLServerThread(TProcessor processor, int port, String keystore, String keystorePwd) throws Exception {
            TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
            params.setKeyStore(keystore, keystorePwd, null, null);
            TServerTransport serverTransport = TSSLTransportFactory.getServerSocket(port, 0, null, params);

            // 待TCompactProtocol压测
            Factory portFactory = new Factory(true, true);
            TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
            args.processor(processor);
            args.protocolFactory(portFactory);
            server = new TThreadPoolServer(args);
        }

        @Override
        public void run() {
            try {
                server.serve();
            }
            catch (Exception e) {
                //
            }
        }

        public void stopServer() {
            server.stop();
        }

    }

	public void close() {
        System.out.println("------close method-----");
        serverThread.stopServer();
	}


}
