package com.drink.srv.support;


import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownHookCase implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ShutdownHookCase.class);
    private CuratorFramework zooKeeper;
    private String nodePath;

    public ShutdownHookCase(CuratorFramework zooKeeper,String nodePath){
        this.zooKeeper = zooKeeper;
        this.nodePath = nodePath;
    }

    @Override
    public void run() {
        try {

            logger.info("---Close the service from zookeeper---");
            long st = System.currentTimeMillis();

            //直接关闭实例，虽然可以达到关闭zk节点的效果，但同时也会影响正在使用实例远程访问接口的业务
            //zooKeeper.close();

            logger.info("Delete ZkNode from ZK:[" +nodePath+"]" );
            zooKeeper.delete().forPath(nodePath);


            long second = 40;

            Thread.sleep(second*1000);

            logger.info((System.currentTimeMillis()-st)/1000f + "s");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
